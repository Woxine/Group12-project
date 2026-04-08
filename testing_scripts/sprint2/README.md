# Sprint2 Testing Scripts

仅用于当前 Sprint2（ID2 / ID3 / ID7 / ID10_11）的后端单元测试。

## CI 策略（实现前保持失败）

`backend/src/test/java/.../sprint2/` 下的用例断言的是**功能实现完成后**的行为（Service 正常返回、Controller 委托 Service、`createBooking` 路径触发确认邮件等）。**仅有骨架时 `mvn test` 会失败**，这是刻意的；全部实现并满足断言后构建才会绿。

- 显式门禁：`BookingServiceEmailNotificationGateTest` 使用 `fail(...)`，在 `BookingServiceImpl` 中接线邮件后请删除该用例或改为 `verify`。
- 与实现无关的校验仍会通过（如 DTO Bean Validation、`AuthServiceImpl#login` 的 Mockito 测试）。

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

## PowerShell 说明

若使用 `$ErrorActionPreference = Stop`，不要把 Maven 的 `stderr` 直接当成致命错误：JVM（如 ByteBuddy）会输出 WARNING 到 stderr。脚本在调用 Maven 时会临时改为 `Continue`，避免管道被中断。

## 日志与详细输出

脚本默认启用：

- `-DtrimStackTrace=false`
- `-Dsurefire.printSummary=true`
- `-DforkCount=1`
- `-DreuseForks=false`

每次运行会在 `reports/` 下生成时间戳日志文件，便于排查失败原因。

### 按功能跑时「还没有测试类」会怎样？

`run-by-feature.ps1` 会通过 `-Dtest=...` 只跑命名匹配的测试。Surefire 3.x 默认在「指定了过滤条件但一个测试都没匹配到」时报错（防止类名写错）。脚本已加上 `-Dsurefire.failIfNoSpecifiedTests=false`，因此在尚未添加 `*PaymentCard*Test` 等类时，构建仍会通过；一旦补上对应测试类，就会真正执行它们。

## 前置条件

- 已安装 JDK 21（`backend/pom.xml` 使用 `java.version=21`）
- PowerShell 可执行本地脚本
- 如果没有全局 `mvn`，脚本会自动使用 `backend/mvnw.cmd`

## 命名约定建议

- 测试类命名：`<Feature><Layer>Test`
- 示例：`PaymentCardServiceTest`、`BookingExtensionControllerTest`
- 方法命名：`should_<expected>_when_<condition>`
