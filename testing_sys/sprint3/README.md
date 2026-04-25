# Sprint3 系统测试（Node.js 命令行）

本目录用于对 Sprint3 功能做端到端系统测试，覆盖：

- `state1`：`ID9`、`ID22`、`ID23`
- `state2`：`ID14`、`ID15`、`ID20`、`ID25`（以后台接口可测项为主）

## 运行前提

- Node.js `18+`
- 后端服务已启动（默认 `http://localhost:8080`）
- 后端已注入默认管理员：`admin@admin.com / admin123`

## 快速开始

在仓库根目录运行：

```bash
cd testing_sys/sprint3
npm run test:sprint3 -- --baseUrl http://localhost:8080
```

只跑 state1：

```bash
cd testing_sys/sprint3
npm run test:sprint3:state1 -- --baseUrl http://localhost:8080
```

只跑 state2：

```bash
cd testing_sys/sprint3
npm run test:sprint3:state2 -- --baseUrl http://localhost:8080
```

按关键字筛选：

```bash
cd testing_sys/sprint3
npm run test:sprint3 -- --suite state1 --grep id23
```

## 参数

- `--suite <state1|state2|all>`：选择套件（默认 `all`）
- `--baseUrl <url>`：后端地址（默认 `http://localhost:8080`）
- `--reportDir <path>`：报告输出目录（默认 `testing_sys/sprint3/reports/<timestamp>`）
- `--grep <keyword>`：按用例名/标签筛选
- `--verbose`：打印更详细的请求响应日志（自动脱敏）

## 报告输出

每次运行会在 `reports/<timestamp>/` 生成：

- `summary.json`：总体摘要
- `cases.jsonl`：逐用例结果
- `http.log`：请求日志
