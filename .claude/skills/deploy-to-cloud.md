---
name: deploy-to-cloud
description: Use when deploying the rideNow/scooter project to production cloud (阿里云 ECS). Covers backend Spring Boot jar, admin Vue web, and uniapp APK release. Triggers on keywords: deploy, 部署, upload to server, 发布, 云端更新, 打包上线.
---

# Deploy to Cloud (全平台部署)

## Overview

Deploy rideNow project to 阿里云 ECS (8.137.174.238). Three components: backend API (Spring Boot), admin web (Vue/Vite static), uniapp APK (Android). Always deploy backend first, then frontend/web.

## Server Info

| Component | Location | Port | Service |
|-----------|----------|------|---------|
| MySQL | localhost | 3306 | mysqld |
| Backend | /opt/scooter/backend-0.0.1-SNAPSHOT.jar | 8080 | scooter-backend (systemd) |
| Admin Web | /opt/scooter-web/ | 80 | nginx static |
| Public | http://8.137.174.238 | 80/8080 | - |

## SSH Access

**Always use WSL + sshpass** (Windows has no native sshpass):

```bash
wsl -e bash -c 'sshpass -p "<password>" ssh -o StrictHostKeyChecking=no root@8.137.174.238 "<command>"'
wsl -e bash -c 'sshpass -p "<password>" scp -o StrictHostKeyChecking=no <src> root@8.137.174.238:<dst>'
```

**WSL path mapping:** `C:\Users\...` → `/mnt/c/Users/...`

**Password:** Stored in `NonProjectFile/online.txt` (Section 1.1). Never commit to git.

## Deploy Backend

```bash
# 1. Build jar (project root: ProjectFile/Group12-project)
cd backend && ./mvnw.cmd clean package -DskipTests

# 2. Upload jar
wsl -e bash -c 'sshpass -p "<pw>" scp -o StrictHostKeyChecking=no \
  "/mnt/c/.../backend/target/backend-0.0.1-SNAPSHOT.jar" \
  root@8.137.174.238:/opt/scooter/'

# 3. Restart service
wsl -e bash -c 'sshpass -p "<pw>" ssh -o StrictHostKeyChecking=no \
  root@8.137.174.238 "systemctl restart scooter-backend"'

# 4. Verify (check status + API response)
wsl -e bash -c 'sshpass -p "<pw>" ssh -o StrictHostKeyChecking=no \
  root@8.137.174.238 "systemctl status scooter-backend --no-pager"'
wsl -e bash -c 'sshpass -p "<pw>" ssh -o StrictHostKeyChecking=no \
  root@8.137.174.238 "curl -s -o /dev/null -w \"%{http_code}\" http://localhost:8080/api/v1/scooters"'
```

**Expected:** `Active: active (running)`, API returns `200`.

## Deploy Admin Web

```bash
# 1. Build dist
cd auth_web_front && npm run build

# 2. Clear old files on server
wsl -e bash -c 'sshpass -p "<pw>" ssh -o StrictHostKeyChecking=no \
  root@8.137.174.238 "rm -rf /opt/scooter-web/*"'

# 3. Upload dist (注意: dist/* 不是 dist 目录本身)
wsl -e bash -c 'sshpass -p "<pw>" scp -o StrictHostKeyChecking=no -r \
  "/mnt/c/.../auth_web_front/dist/"* \
  root@8.137.174.238:/opt/scooter-web/'

# 4. Fix permissions (nginx needs o+rX)
wsl -e bash -c 'sshpass -p "<pw>" ssh -o StrictHostKeyChecking=no \
  root@8.137.174.238 "chmod -R o+rX /opt/scooter-web"'
```

**Verify:** `http://8.137.174.238/` with Ctrl+F5.

**Prerequisite:** `auth_web_front/.env.production` must contain:
```
VITE_API_BASE_URL=http://8.137.174.238:8080
```

## Build UniApp APK (Release)

API auto-switches: debug → localhost:8080, release → 8.137.174.238:8080 (see `common/api.uts`).

1. HBuilderX → **发行 → 原生App-云打包**
2. Android, use public test certificate
3. APK output: `unpackage/release/`
4. Transfer to phone and install

**No server-side work needed** — APK calls backend directly via public IP.

## Full Platform Deploy Order

```
1. Backend jar    → upload + restart
2. Admin Web dist → upload + permissions
3. UniApp APK     → build + install on phone
```

Always backend first. New frontend calling old backend = 404 errors.

## Git Workflow

```bash
# After deploy, commit and push
git add -A && git reset HEAD .tmp/ .claude/
git commit -m "feat: <description>"
git push origin master_sprint3
```

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| Backend won't start | `journalctl -u scooter-backend -n 200` — check DB, port, jar |
| Web 403 Forbidden | `chmod -R o+rX /opt/scooter-web` |
| CORS error | Add origin to `WebConfig.java` allowedOrigins, redeploy backend |
| Web shows old version | Ctrl+F5 force refresh |
| SSH connection refused | Check security group port 22, server status |
| scp upload incomplete | Re-scp, check disk space: `df -h` |

## Quick Reference

```
Server:     ssh root@8.137.174.238
Backend:    systemctl restart scooter-backend
Backend log: journalctl -u scooter-backend -f
Backend jar: /opt/scooter/backend-0.0.1-SNAPSHOT.jar
Web dir:    /opt/scooter-web
Web URL:    http://8.137.174.238/
API URL:    http://8.137.174.238:8080
Build jar:  cd backend && ./mvnw.cmd clean package -DskipTests
Build web:  cd auth_web_front && npm run build
```
