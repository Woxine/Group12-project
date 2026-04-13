# Sprint3-State1 Testing Scripts

Sprint3-State1 only (`ID9`, `ID22`, `ID23`) backend unit/integration test runners.

## Structure

- `run-all.ps1`: run all backend tests for this sprint.
- `run-by-feature.ps1`: run tests by feature id.
- `export-report.ps1`: copy Surefire reports into this folder.
- `config/features.psd1`: feature-to-test-pattern mapping.
- `reports/`: timestamped logs and exported reports.

## Quick Start (PowerShell)

From project root:

```powershell
.\testing_scripts\sprint3-state1\run-all.ps1
```

Run by feature:

```powershell
.\testing_scripts\sprint3-state1\run-by-feature.ps1 -Feature id9_guest_booking
.\testing_scripts\sprint3-state1\run-by-feature.ps1 -Feature id22_discount
.\testing_scripts\sprint3-state1\run-by-feature.ps1 -Feature id23_concurrency
```

Export report:

```powershell
.\testing_scripts\sprint3-state1\export-report.ps1
```

## Notes

- Uses `JUnit 5 + Mockito` through Maven Surefire (`mvn test` / `mvnw.cmd test`).
- Script output is both printed and saved to `reports/*.log`.
- If global `mvn` is unavailable, scripts automatically use `backend/mvnw.cmd`.
