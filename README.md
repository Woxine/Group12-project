# Group12 Project

## 项目说明

本项目包含一个 Spring Boot 后端和一个 uni-app 前端。团队开发阶段建议每位组员各自使用本地 MySQL，并导入同一份初始化脚本，避免共享数据库带来的权限、安全和数据互相覆盖问题。

## 环境要求

- JDK 21
- Maven 3.9+，或直接使用仓库内的 Maven Wrapper
- MySQL 8.x

### 数据库初始化

后端约定的本地数据库名为 `scooter_db`。

### 方式一：直接导入初始化脚本

在 `backend` 目录执行：

```powershell
mysql -u your_mysql_username -p < init.sql
```

该脚本会：

- 自动创建 `scooter_db`
- 创建后端当前使用的表结构
- 插入一组基础站点和滑板车演示数据

如果你先手动建库，也可以先登录 MySQL 再执行：

```sql
CREATE DATABASE scooter_db;
USE scooter_db;
SOURCE init.sql;
```

### 本地数据库配置**二选一**

仓库中的共享配置文件是 `backend/src/main/resources/application.yaml`。
其中不保存真实数据库账号密码，组员需要用自己的本地配置覆盖。

| 方案 | 需要准备什么 | 什么时候需要重新设置 | 适合谁 |
| :--- | :--- | :--- | :--- |
| 环境变量 | 在当前 PowerShell 中设置 `SPRING_DATASOURCE_*` | 每次换新终端、重启电脑后再次启动时 | 想临时使用、不想创建本地配置文件的人 |
| `application-local.yaml` | 复制模板并填写自己的本地账号密码，再启用 `local` profile | 本地 YAML 只需配置一次；但若仍用 PowerShell 临时设置 `SPRING_PROFILES_ACTIVE`，换新终端后要重新设置 | 想长期保留本地数据库配置的人 |

### 推荐方式：环境变量

在 PowerShell 中执行以下命令，然后在同一个终端窗口里启动后端：

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/scooter_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:SPRING_DATASOURCE_USERNAME="你的MySQL用户名"
$env:SPRING_DATASOURCE_PASSWORD="你的MySQL密码"
```

说明：

- 以上写法在当前 PowerShell 会话中临时生效。
- 如果关闭当前终端、重新打开新的终端，或重启电脑后再次启动后端，需要重新执行这三行命令。
- 如果只是停止后端后又在同一个终端窗口重新启动，一般不需要重复输入。
- 使用环境变量方案时，不需要创建 `application-local.yaml`。

### 可选方式：本地 `application-local.yaml`

本方案与环境变量方案二选一。如果不想每次在新终端里重新设置数据库环境变量，可以改用本地 YAML 配置。

1. 复制 `backend/src/main/resources/application-local.example.yaml`
2. 重命名为 `backend/src/main/resources/application-local.yaml`
3. 把其中的 `username` 和 `password` 改成你自己的本地 MySQL 账号
4. 启动前启用 `local` 配置档（profile）

PowerShell 示例：

```powershell
$env:SPRING_PROFILES_ACTIVE="local"
```

说明：

- `application-local.yaml` 配好后会一直保留在你本地，不需要重复创建。
- 如果你仍然通过 PowerShell 临时设置 `SPRING_PROFILES_ACTIVE`，那么换一个新的终端窗口后，需要重新执行这行命令。
- 如果你把 `local` profile 配到 IDE 运行配置或系统环境变量里，就可以接近“一劳永逸”。
- `application-local.yaml` 已加入忽略规则，不应该提交到仓库。

### 启动后端

在 `backend` 目录执行：

```powershell
.\mvnw.cmd spring-boot:run
```

如果本地已经装好了 Maven，也可以执行：

```powershell
mvn spring-boot:run
```

默认情况下，前端使用的后端地址是 `http://localhost:8080`，定义在 `common/api.uts`，本地联调通常不需要修改。

### 启动ADB

在启动前，需要在任意控制台输入

```text
adb reverse tcp:8080 tcp:8080
```

以启动ADB端口的反向映射，否则前后端通信无法正常进行。

## 额外说明

- 数据库结构设计文档位于 `backend/DATABASE_SCHEMA.md`，其内容应与当前实体定义和 `backend/init.sql` 保持一致
- `init.sql` 是团队统一的可执行初始化脚本
- 如果需要插入默认用户，请注意后端登录逻辑要求密码使用 BCrypt 哈希，而不是明文。

## 首次使用建议

- 导入 `backend/init.sql`
- 配置自己的 MySQL 连接信息
- 启动后端
- 再启动前端进行联调
