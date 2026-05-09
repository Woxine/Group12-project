# 上云部署指南

本文说明如何把 Group12 共享滑板车项目部署到阿里云 ECS。内容基于 `.claude/skills/deploy-to-cloud.md`，并补充仓库内实际构建、环境变量和安全约束。

## 适用范围

部署对象包括三部分：

| 组件 | 本地位置 | 服务器位置 | 端口或入口 |
| --- | --- | --- | --- |
| 后端 API | `backend/` | `/opt/scooter/backend-0.0.1-SNAPSHOT.jar` | `8080`，systemd 服务 `scooter-backend` |
| 管理端 Web | `auth_web_front/` | `/opt/scooter-web/` | Nginx 静态站点，`80` |
| 移动端 APK | UniApp X 根目录 | `unpackage/release/` 产物手动分发 | Release 包直接请求公网 API |

当前生产示例地址：

- 管理端：`http://8.137.174.238/`
- 后端 API：`http://8.137.174.238:8080`

部署顺序固定为：后端 Jar -> 管理端 Web -> UniApp APK。不要先发新版前端再发后端，否则新页面可能调用旧后端不存在的接口。

## 前置条件

### 本地工具

- JDK 21。
- Maven 3.9+，或使用仓库内 `backend/mvnw.cmd`。
- Node.js 和 npm，用于构建 `auth_web_front`。
- HBuilderX，用于 UniApp Android 云打包。
- WSL，用于在 Windows 上使用 `sshpass` 执行 SSH/SCP。

### 服务器环境

- MySQL 8.x，监听本机 `3306`。
- Java 21 运行时。
- Nginx，静态站点目录为 `/opt/scooter-web/`。
- systemd 服务 `scooter-backend`，启动 `/opt/scooter/backend-0.0.1-SNAPSHOT.jar`。
- 安全组放通 22、80、8080。

### 机密管理

不要把任何真实口令写入仓库、文档或提交记录。以下内容必须保存在仓库外：

| 机密 | 用途 | 推荐保存方式 |
| --- | --- | --- |
| ECS root/SSH 密码或私钥 | 上传和重启服务 | 本机安全位置或密码管理器。 |
| `SPRING_DATASOURCE_PASSWORD` | 生产数据库密码 | systemd 环境变量或权限为 600 的 env 文件。 |
| `JWT_SECRET` | JWT 签名 | systemd 环境变量或专用 env 文件。 |
| `SPRING_MAIL_PASSWORD` | 邮件 SMTP 密码 | systemd 环境变量或专用 env 文件。 |
| `ADMIN_INITIAL_PASSWORD` | 初始管理员密码 | 首次部署时注入，之后妥善轮换。 |

`.gitignore` 已忽略 `*.env`、`*.local`、`backend/src/main/resources/application-local.yaml`、`target/`、`node_modules/`、`unpackage/`、`.tmp/` 等。不要强行提交这些文件。

## SSH/SCP 方式

Windows 环境建议通过 WSL + `sshpass` 执行命令。命令中的 `<ECS_PASSWORD>` 必须替换为仓库外保存的密码，不要写进文件。

```bash
wsl -e bash -c 'sshpass -p "<ECS_PASSWORD>" ssh -o StrictHostKeyChecking=no root@8.137.174.238 "<REMOTE_COMMAND>"'
```

上传文件：

```bash
wsl -e bash -c 'sshpass -p "<ECS_PASSWORD>" scp -o StrictHostKeyChecking=no "<LOCAL_WSL_PATH>" root@8.137.174.238:<REMOTE_PATH>'
```

Windows 路径映射规则：

```text
C:\Users\...\Group12-project
=> /mnt/c/Users/.../Group12-project
```

如有条件，优先改用 SSH key，减少命令中出现口令的机会。

## 后端部署

### 1. 构建 Jar

在仓库根目录执行：

```powershell
cd backend
.\mvnw.cmd clean package -DskipTests
```

产物路径：

```text
backend/target/backend-0.0.1-SNAPSHOT.jar
```

如需在部署前运行测试，可改为：

```powershell
cd backend
.\mvnw.cmd test
.\mvnw.cmd clean package
```

### 2. 上传 Jar

将 `<PROJECT_WSL_PATH>` 替换为本机项目的 WSL 路径。

```bash
wsl -e bash -c 'sshpass -p "<ECS_PASSWORD>" scp -o StrictHostKeyChecking=no \
  "<PROJECT_WSL_PATH>/backend/target/backend-0.0.1-SNAPSHOT.jar" \
  root@8.137.174.238:/opt/scooter/'
```

### 3. 重启服务

```bash
wsl -e bash -c 'sshpass -p "<ECS_PASSWORD>" ssh -o StrictHostKeyChecking=no \
  root@8.137.174.238 "systemctl restart scooter-backend"'
```

### 4. 验证后端

检查 systemd 状态：

```bash
wsl -e bash -c 'sshpass -p "<ECS_PASSWORD>" ssh -o StrictHostKeyChecking=no \
  root@8.137.174.238 "systemctl status scooter-backend --no-pager"'
```

检查 API：

```bash
wsl -e bash -c 'sshpass -p "<ECS_PASSWORD>" ssh -o StrictHostKeyChecking=no \
  root@8.137.174.238 "curl -s -o /dev/null -w \"%{http_code}\" http://localhost:8080/api/v1/scooters"'
```

期望结果：

- `systemctl status` 显示 `active (running)`。
- `curl` 返回 `200`。

### 5. 后端运行时配置

`backend/src/main/resources/application.yaml` 会从环境变量读取生产配置。服务器上至少应提供：

```text
JWT_SECRET
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
SPRING_JPA_HIBERNATE_DDL_AUTO
SPRING_MAIL_HOST
SPRING_MAIL_USERNAME
SPRING_MAIL_PASSWORD
```

本地开发默认 `SPRING_JPA_HIBERNATE_DDL_AUTO=update`。生产更推荐 `validate`，但在改为 `validate` 前必须确认数据库结构已和实体完全一致。当前结构差异见 `docs/database-structure.md`。

## 管理端 Web 部署

### 1. 确认生产 API 地址

`auth_web_front/src/api/client.ts` 通过 `VITE_API_BASE_URL` 决定后端地址。当前 `auth_web_front/.env.production` 示例为：

```text
VITE_API_BASE_URL=http://8.137.174.238:8080
```

Vite 会在构建时把该值写入静态文件。修改 API 地址后必须重新构建并重新上传 `dist`。

### 2. 构建 dist

```powershell
cd auth_web_front
npm install
npm run build
```

`npm run build` 会依次执行：

- `guard:no-generated-js`
- `vue-tsc --noEmit -p tsconfig.app.json`
- `vite build`

产物目录：

```text
auth_web_front/dist/
```

### 3. 清空服务器旧文件

```bash
wsl -e bash -c 'sshpass -p "<ECS_PASSWORD>" ssh -o StrictHostKeyChecking=no \
  root@8.137.174.238 "rm -rf /opt/scooter-web/*"'
```

### 4. 上传 dist 内容

注意上传的是 `dist/*` 的内容，不是 `dist` 目录本身。

```bash
wsl -e bash -c 'sshpass -p "<ECS_PASSWORD>" scp -o StrictHostKeyChecking=no -r \
  "<PROJECT_WSL_PATH>/auth_web_front/dist/"* \
  root@8.137.174.238:/opt/scooter-web/'
```

### 5. 修复 Nginx 访问权限

```bash
wsl -e bash -c 'sshpass -p "<ECS_PASSWORD>" ssh -o StrictHostKeyChecking=no \
  root@8.137.174.238 "chmod -R o+rX /opt/scooter-web"'
```

### 6. 验证管理端

浏览器打开：

```text
http://8.137.174.238/
```

如果看到旧页面，使用 Ctrl+F5 强制刷新。登录后重点验证车辆列表、反馈列表和计费设置页，因为它们能同时验证静态站点、API 地址、token 和后端权限。

## UniApp APK 发布

移动端 API 地址在 `common/api.uts` 中自动切换：

```text
Debug 包：   http://localhost:8080
Release 包： http://8.137.174.238:8080
```

本地运行时需要：

```text
adb reverse tcp:8080 tcp:8080
```

Release 云打包步骤：

1. 打开 HBuilderX。
2. 选择“发行 -> 原生 App-云打包”。
3. 选择 Android。
4. 使用公共测试证书或团队约定证书。
5. 构建完成后在 `unpackage/release/` 获取 APK。
6. 安装到手机，验证登录、车辆列表、预约、支付和订单列表。

如果生产 IP 或域名变化，必须先修改 `common/api.uts` 的 `RELEASE_URL`，再重新云打包。

## 推荐完整部署顺序

```text
1. 确认后端数据库和环境变量
2. 构建后端 Jar
3. 上传 Jar 并重启 scooter-backend
4. curl 验证后端 API 返回 200
5. 设置管理端 VITE_API_BASE_URL
6. 构建管理端 dist
7. 上传 dist/* 到 /opt/scooter-web/
8. 浏览器验证管理端
9. HBuilderX 云打包 APK
10. 手机安装并验证核心 App 流程
```

## 回滚策略

部署前建议在服务器备份上一版：

```bash
wsl -e bash -c 'sshpass -p "<ECS_PASSWORD>" ssh -o StrictHostKeyChecking=no \
  root@8.137.174.238 "cp /opt/scooter/backend-0.0.1-SNAPSHOT.jar /opt/scooter/backend-0.0.1-SNAPSHOT.jar.bak.$(date +%Y%m%d%H%M%S)"'
```

管理端静态文件备份：

```bash
wsl -e bash -c 'sshpass -p "<ECS_PASSWORD>" ssh -o StrictHostKeyChecking=no \
  root@8.137.174.238 "cp -a /opt/scooter-web /opt/scooter-web.bak.$(date +%Y%m%d%H%M%S)"'
```

回滚后端：

1. 将备份 Jar 复制回 `/opt/scooter/backend-0.0.1-SNAPSHOT.jar`。
2. 执行 `systemctl restart scooter-backend`。
3. 用 `curl http://localhost:8080/api/v1/scooters` 验证。

回滚管理端：

1. 清空 `/opt/scooter-web/`。
2. 将备份目录内容复制回 `/opt/scooter-web/`。
3. 执行 `chmod -R o+rX /opt/scooter-web`。
4. 浏览器 Ctrl+F5 验证。

## 常见问题

| 现象 | 排查方式 | 处理 |
| --- | --- | --- |
| 后端无法启动 | `journalctl -u scooter-backend -n 200` | 检查数据库、环境变量、端口和 Jar 文件。 |
| API 返回 500 | 查看后端日志 | 常见原因是数据库字段缺失、JWT_SECRET 缺失或文件目录权限不足。 |
| API 返回 404 | 核对前后端版本 | 确认先部署了新版后端，再部署新版前端。 |
| 管理端 403 | 检查 Nginx 权限 | 执行 `chmod -R o+rX /opt/scooter-web`。 |
| 管理端请求 localhost | 检查构建时 `VITE_API_BASE_URL` | 修改 `.env.production` 后重新 `npm run build`。 |
| CORS 报错 | 检查 `WebConfig.java` | 添加允许的 Origin 后重新部署后端。 |
| 页面仍是旧版本 | 浏览器缓存 | Ctrl+F5 强刷，必要时清理缓存。 |
| SSH 或 scp 失败 | 检查安全组和服务器状态 | 确认 22 端口开放，服务器在线。 |
| 上传中断 | `df -h` 查看磁盘 | 清理空间后重新上传。 |

## 不要提交的内容

- ECS 密码、SSH 私钥、数据库密码、JWT 密钥、SMTP 密码。
- `NonProjectFile/online.txt` 或任何保存线上口令的文件。
- `backend/src/main/resources/application-local.yaml`。
- `auth_web_front/dist/`。
- `backend/target/`。
- `unpackage/` 下的 APK 或临时打包产物。
- `node_modules/`。
- `.tmp/`。

部署完成后，如果需要提交文档或代码，先用 `git status` 检查是否误加入了构建产物或本地配置。
