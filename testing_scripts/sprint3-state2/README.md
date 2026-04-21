# Sprint3-State2 Testing Scripts

Sprint3-State2 backend unit/integration test runners for `ID14`, `ID15`, `ID20`.

## Structure

- `run-all.ps1`: run all backend tests for this sprint.
- `run-by-feature.ps1`: run tests by feature id.
- `export-report.ps1`: copy Surefire reports into this folder.
- `config/features.psd1`: feature-to-test-pattern mapping.
- `reports/`: timestamped logs and exported reports.

## Quick Start (PowerShell)

From project root:

```powershell
.\testing_scripts\sprint3-state2\run-all.ps1
```

Run by feature:

```powershell
.\testing_scripts\sprint3-state2\run-by-feature.ps1 -Feature id14_priority_feedback
.\testing_scripts\sprint3-state2\run-by-feature.ps1 -Feature id15_high_priority_issues
.\testing_scripts\sprint3-state2\run-by-feature.ps1 -Feature id20_popular_rental_dates
```

Export report:

```powershell
.\testing_scripts\sprint3-state2\export-report.ps1
```

## Notes

- Uses `JUnit 5 + Mockito` through Maven Surefire (`mvn test` / `mvnw.cmd test`).
- Script output is both printed and saved to `reports/*.log`.
- If global `mvn` is unavailable, scripts automatically use `backend/mvnw.cmd`.
