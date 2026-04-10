# Sprint2 系统测试（Node.js 命令行）

本目录用于对 Sprint2 功能（ID2/3/7/10&11）做端到端系统测试。测试脚本与测试结果都落在 `testing_sys/sprint2/` 下，并按时间戳写入 `reports/`。

## 运行前提

- Node.js **18+**
- 后端服务已启动（默认 `http://localhost:8080`）

## 快速开始

在仓库根目录运行：

```bash
cd testing_sys/sprint2
npm run test:sprint2 -- --baseUrl http://localhost:8080
```

你也可以仅跑部分用例：

```bash
cd testing_sys/sprint2
npm run test:sprint2 -- --grep paymentcard
```

## 参数

- `--baseUrl <url>`：后端地址（默认 `http://localhost:8080`）
- `--reportDir <path>`：报告输出目录（默认 `testing_sys/sprint2/reports/<timestamp>`）
- `--grep <keyword>`：按关键字筛选用例（匹配用例名/标签）
- `--verbose`：打印更详细的请求/响应信息（仍会脱敏）

## 报告输出

每次运行会生成一个目录：

- `summary.json`：总体摘要（总数/通过/失败/耗时）
- `cases.jsonl`：逐用例结果（JSON Lines）
- `http.log`：请求/响应日志（自动脱敏）

## 注意

- `Authorization: Bearer <token>` 会被脱敏写盘。
- 卡号会被脱敏写盘（仅保留末 4 位）。

