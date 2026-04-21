# Sprint3 Feature Dev Checklist (Step 1)

> 目标：先确认新功能从头到尾会涉及哪些文件。  
> 本次只做第一步：文件范围识别，不写实现代码。  
> 标记说明：`[已有]` = 当前仓库已存在、需要修改；`[新增]` = 建议新增。

---

## ID 14：根据优先级处理反馈（Process feedback based on priority）

> 依赖 ID 13（反馈基础提交流程）。  
> 业务目标：低优先级可由一线/直接负责人直接处理；高优先级需要上报给更高级别管理层或专业团队。

### 后端（优先级判定、上报状态、处理流）

- [ ] [已有] `backend/src/main/java/com/group12/backend/entity/Feedback.java`（补“上报状态/上报目标/上报时间/处理人角色”等字段）
- [ ] [已有] `backend/src/main/java/com/group12/backend/dto/FeedbackRequest.java`（补优先级输入规范，必要时增加分类字段）
- [ ] [新增] `backend/src/main/java/com/group12/backend/dto/ProcessFeedbackRequest.java`（处理动作入参：directHandle / escalate）
- [ ] [新增] `backend/src/main/java/com/group12/backend/dto/EscalatedFeedbackResponse.java`（上报后回传结构）
- [ ] [已有] `backend/src/main/java/com/group12/backend/controller/FeedbackController.java`（新增“按优先级处理/上报”接口）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/FeedbackService.java`（新增 processByPriority 方法签名）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/impl/FeedbackServiceImpl.java`（实现低优先级直处理与高优先级上报逻辑）
- [ ] [已有] `backend/src/main/java/com/group12/backend/repository/FeedbackRepository.java`（新增按上报状态/优先级查询方法）
- [ ] [已有] `backend/src/main/java/com/group12/backend/exception/ErrorMessages.java`（新增优先级处理与上报失败错误文案）
- [ ] [已有] `backend/src/main/java/com/group12/backend/security/AdminAccessGuard.java`（高优先级查看/处理权限控制）

### 数据库

- [ ] [已有] `backend/init.sql`（`feedbacks` 表补充 escalated 相关字段，或新增流转表）
- [ ] [已有] `backend/DATABASE_SCHEMA.md`（更新反馈优先级处理与上报模型说明）

### 前端（管理端）

- [ ] [已有] `auth_web_front/src/views/admin/FeedbacksView.vue`（增加“直接处理/上报”动作入口与状态展示）
- [ ] [已有] `auth_web_front/src/api/admin.ts`（新增 process / escalate API 方法）
- [ ] [已有] `auth_web_front/src/types/api.ts`（扩展 FeedbackItem 类型：上报状态与处理链路字段）

### 前端（uni-app 客户端，可选联动）

- [ ] [已有] `pages/feedback/feedback.uvue`（补优先级提示和提交约束文案）
- [ ] [已有] `common/messages.uts`（新增优先级处理流程文案）

---

## ID 15：查看高优先级问题（View high-priority issues）

> 依赖 ID 14。  
> 业务目标：高优先级反馈上报后，后台管理员可快速查看和筛选。

### 后端（查询与筛选）

- [ ] [已有] `backend/src/main/java/com/group12/backend/controller/FeedbackController.java`（新增“仅高优先级+已上报”查询接口，或强化现有筛选）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/FeedbackService.java`（新增 getHighPriorityIssues 方法）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/impl/FeedbackServiceImpl.java`（实现高优先级问题查询、排序、分页）
- [ ] [已有] `backend/src/main/java/com/group12/backend/repository/FeedbackRepository.java`（新增高优先级+上报状态复合查询）
- [ ] [新增] `backend/src/main/java/com/group12/backend/dto/HighPriorityIssueItemDTO.java`（管理员列表项结构）

### 前端（管理端）

- [ ] [已有] `auth_web_front/src/views/admin/FeedbacksView.vue`（增加“High Priority”快捷筛选/tab）
- [ ] [新增] `auth_web_front/src/views/admin/HighPriorityIssuesView.vue`（如采用独立页面）
- [ ] [已有] `auth_web_front/src/router/index.ts`（注册高优先级页面路由）
- [ ] [已有] `auth_web_front/src/layouts/AdminLayout.vue`（菜单入口）
- [ ] [已有] `auth_web_front/src/api/admin.ts`（新增高优先级列表 API）
- [ ] [已有] `auth_web_front/src/types/api.ts`（新增 HighPriorityIssue 类型）

---

## ID 20：追踪热门租赁日期（Track popular rental dates）

> 业务目标：后台可查看一周内累计日收入，形成“热门租赁日期”周榜。

### 后端（统计聚合）

- [ ] [已有] `backend/src/main/java/com/group12/backend/controller/AdminController.java`（新增“weekly popular dates leaderboard”接口）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/AdminService.java`（声明周榜统计方法）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/impl/AdminServiceImpl.java`（复用 dailyTrend + 新增按日收入排序周榜）
- [ ] [新增] `backend/src/main/java/com/group12/backend/dto/PopularRentalDateDTO.java`（周榜项：date/revenue/orders/rank）
- [ ] [已有] `backend/src/main/java/com/group12/backend/repository/BookingRepository.java`（必要时新增按日聚合查询优化）
- [ ] [已有] `backend/src/main/java/com/group12/backend/dto/DailyTrendPointDTO.java`（若需要补充字段用于周榜）
- [ ] [已有] `backend/src/main/java/com/group12/backend/dto/DashboardOverviewDTO.java`（若将周榜并入总览返回）

### 前端（管理端）

- [ ] [已有] `auth_web_front/src/views/admin/RevenueView.vue`（增加“热门租赁日期周榜”表格）
- [ ] [已有] `auth_web_front/src/views/admin/AnalyticsView.vue`（可视化展示周榜/热点日期）
- [ ] [已有] `auth_web_front/src/api/admin.ts`（新增 popular rental dates API）
- [ ] [已有] `auth_web_front/src/types/api.ts`（新增 PopularRentalDate 类型）

### 前端（uni-app 管理页，可选联动）

- [ ] [已有] `pages/admin/revenue.uvue`（增加周榜区域，供移动端管理员查看）

---

## ID 25：无障碍/辅助功能（Accessibility / Assistive Features）

> 横切需求：主要作用于前端交互层，保障键盘可用性、语义可读性、颜色对比与可感知反馈。

### 管理端 Web（重点）

- [ ] [已有] `auth_web_front/src/views/admin/FeedbacksView.vue`（筛选、按钮、表格语义与键盘可达性）
- [ ] [已有] `auth_web_front/src/views/admin/RevenueView.vue`（数据表与筛选控件可读性）
- [ ] [已有] `auth_web_front/src/views/admin/AnalyticsView.vue`（图表辅助文本/无障碍描述）
- [ ] [已有] `auth_web_front/src/layouts/AdminLayout.vue`（导航可聚焦顺序、active 状态可辨识）
- [ ] [已有] `auth_web_front/src/styles.css`（颜色对比、focus 样式、可读字号）
- [ ] [已有] `auth_web_front/src/App.vue`（全局可访问性基础设置）

### uni-app 客户端（建议）

- [ ] [已有] `pages/feedback/feedback.uvue`（表单标签、错误提示可感知）
- [ ] [已有] `pages/admin/home.uvue`（按钮可读性与触达区域）
- [ ] [已有] `pages/admin/revenue.uvue`（输入框/结果信息的易读性）
- [ ] [已有] `common/messages.uts`（统一、清晰、可被辅助阅读器读懂的提示文案）

### 测试与验收（建议补充）

- [ ] [新增] `auth_web_front/src/tests/accessibility/feedbacks.a11y.spec.ts`（反馈管理页可访问性检查）
- [ ] [新增] `auth_web_front/src/tests/accessibility/revenue.a11y.spec.ts`（营收页可访问性检查）
- [ ] [新增] `docs/accessibility-checklist.md`（无障碍验收条目：键盘、对比度、语义、错误提示）

---

## Sprint3 共用横切文件（ID14/15/20/25）

- [ ] [已有] `docs/feature-dev-checklist.md`（当前文件；第一步完成后继续补第二步类/函数 TODO）
- [ ] [已有] `docs/sprint3-state1-todo-details.md`（后续同步本 sprint 新增功能的函数级任务）
- [ ] [已有] `backend/src/main/resources/application.yaml`（如新增统计接口或权限开关配置）

---

## Step 2 + Step 3：类与函数 TODO 代码框架（已落代码骨架）

> 说明：以下是“只写 TODO 框架，不实现内容”的类/函数清单，已在代码中创建或声明。

### ID 14：根据优先级处理反馈

#### 新增类（后端）

- [x] `backend/src/main/java/com/group12/backend/dto/ProcessFeedbackRequest.java`
  - [ ] `action`（`DIRECT_HANDLE` / `ESCALATE`）TODO 校验规则
  - [ ] `escalateTo` TODO 目标团队字段
  - [ ] `note` TODO 处理备注字段
- [x] `backend/src/main/java/com/group12/backend/dto/EscalatedFeedbackResponse.java`
  - [ ] `feedbackId/priority/escalated/escalatedTo/status` TODO 返回规范

#### 既有类新增函数（后端）

- [x] `backend/src/main/java/com/group12/backend/service/FeedbackService.java`
  - [ ] `processFeedbackByPriority(String feedbackId, ProcessFeedbackRequest request, Long operatorUserId)`
- [x] `backend/src/main/java/com/group12/backend/service/impl/FeedbackServiceImpl.java`
  - [ ] `processFeedbackByPriority(...)`（已放 `UnsupportedOperationException` TODO 占位）
- [x] `backend/src/main/java/com/group12/backend/controller/FeedbackController.java`
  - [ ] `processByPriority(...)`（`PUT /api/v1/feedbacks/{feedbackId}/process-priority`）

#### 前端 TODO 框架

- [x] `auth_web_front/src/api/admin.ts`
  - [ ] `processFeedbackByPriority(feedbackId, payload)`
- [x] `auth_web_front/src/types/api.ts`
  - [ ] `ProcessFeedbackPayload`
  - [ ] `EscalatedFeedbackResponse`
  - [ ] `FeedbackItem` 扩展上报字段

### ID 15：查看高优先级问题

#### 新增类（后端）

- [x] `backend/src/main/java/com/group12/backend/dto/HighPriorityIssueItemDTO.java`
  - [ ] 高优先级问题列表字段 TODO 细化

#### 既有类新增函数（后端）

- [x] `backend/src/main/java/com/group12/backend/service/FeedbackService.java`
  - [ ] `getHighPriorityIssues(Boolean escalated, Integer page, Integer size)`
- [x] `backend/src/main/java/com/group12/backend/service/impl/FeedbackServiceImpl.java`
  - [ ] `getHighPriorityIssues(...)`（已放 `UnsupportedOperationException` TODO 占位）
- [x] `backend/src/main/java/com/group12/backend/controller/FeedbackController.java`
  - [ ] `getHighPriorityIssues(...)`（`GET /api/v1/feedbacks/high-priority`）

#### 前端 TODO 框架

- [x] `auth_web_front/src/views/admin/HighPriorityIssuesView.vue`
  - [ ] `load()`
  - [ ] `directHandle(feedbackId)`
  - [ ] `escalate(feedbackId)`
- [x] `auth_web_front/src/router/index.ts`
  - [ ] 注册 `/admin/high-priority-issues`
- [x] `auth_web_front/src/layouts/AdminLayout.vue`
  - [ ] 增加 High Priority Issues 菜单入口
- [x] `auth_web_front/src/api/admin.ts`
  - [ ] `getHighPriorityIssues(params)`
- [x] `auth_web_front/src/types/api.ts`
  - [ ] `HighPriorityIssue`

### ID 20：追踪热门租赁日期

#### 新增类（后端）

- [x] `backend/src/main/java/com/group12/backend/dto/PopularRentalDateDTO.java`
  - [ ] `date/rank/orderCount/revenue` TODO 规则细化

#### 既有类新增函数（后端）

- [x] `backend/src/main/java/com/group12/backend/service/AdminService.java`
  - [ ] `getPopularRentalDatesThisWeek()`
- [x] `backend/src/main/java/com/group12/backend/service/impl/AdminServiceImpl.java`
  - [ ] `getPopularRentalDatesThisWeek()`（已放 `UnsupportedOperationException` TODO 占位）
- [x] `backend/src/main/java/com/group12/backend/controller/AdminController.java`
  - [ ] `getPopularRentalDatesThisWeek(...)`（`GET /api/v1/admin/revenue/popular-dates-week`）

#### 前端 TODO 框架

- [x] `auth_web_front/src/api/admin.ts`
  - [ ] `getPopularRentalDatesThisWeek()`
- [x] `auth_web_front/src/types/api.ts`
  - [ ] `PopularRentalDate`

### ID 25：无障碍/辅助功能（先建任务框架）

- [ ] `auth_web_front/src/views/admin/FeedbacksView.vue`
  - [ ] TODO: 补可键盘触达的操作流与语义提示
- [ ] `auth_web_front/src/views/admin/RevenueView.vue`
  - [ ] TODO: 补筛选/表格可访问性标签
- [ ] `auth_web_front/src/views/admin/AnalyticsView.vue`
  - [ ] TODO: 补图表可读描述与替代文本
- [ ] `auth_web_front/src/styles.css`
  - [ ] TODO: 补 focus visible 与对比度样式基线
