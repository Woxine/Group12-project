# Sprint2 Testing Scripts

仅用于当前 Sprint2（ID2 / ID3 / ID7 / ID10_11）的后端单元测试。

## 目录说明

- `run-all.ps1`：运行 Sprint2 全部单元测试（Maven Surefire）
- `run-by-feature.ps1`：按功能 ID 运行测试
- `export-report.ps1`：导出 Surefire 报告到本目录 `reports/`
- `config/features.psd1`：功能 ID 与测试类名模式映射
- `reports/`：脚本运行日志和导出报告

## 快速开始（PowerShell）

在项目根目录执行：

```powershell
.\testing_scripts\sprint2\run-all.ps1
```

按功能执行：

```powershell
.\testing_scripts\sprint2\run-by-feature.ps1 -Feature id2
.\testing_scripts\sprint2\run-by-feature.ps1 -Feature id3
.\testing_scripts\sprint2\run-by-feature.ps1 -Feature id7
.\testing_scripts\sprint2\run-by-feature.ps1 -Feature id10_11
```

导出报告：

```powershell
.\testing_scripts\sprint2\export-report.ps1
```

## 日志与详细输出

脚本默认启用：

- `-DtrimStackTrace=false`
- `-Dsurefire.printSummary=true`
- `-DforkCount=1`
- `-DreuseForks=false`

每次运行会在 `reports/` 下生成时间戳日志文件，便于排查失败原因。

## 前置条件

- 已安装 JDK 21（`backend/pom.xml` 使用 `java.version=21`）
- PowerShell 可执行本地脚本
- 如果没有全局 `mvn`，脚本会自动使用 `backend/mvnw.cmd`

## 命名约定建议

- 测试类命名：`<Feature><Layer>Test`
- 示例：`PaymentCardServiceTest`、`BookingExtensionControllerTest`
- 方法命名：`should_<expected>_when_<condition>`
