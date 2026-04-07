# 功能开发 TODO 清单

## 0）功能输入（编码前必须先定）

- 功能名：
- 触发入口页面（`.uvue`）：
- API 合同：
  - URL：
  - 方法：
  - 请求体：
  - 响应体：
- 权限级别：
  - [ ] 公开
  - [ ] 登录用户
  - [ ] 管理员

---

## 1）现有功能域映射（参考骨架）

### 认证与登录

- 后端：`AuthController`、`AuthService`、`AuthServiceImpl`、`JwtUtil`
- 前端：`pages/login/login.uvue`
- 当前实现特点：
  - 登录接口返回 token + 用户信息
  - 前端把 token/userId/role/userInfo 存到 storage
  - 拦截器校验 `Authorization: Bearer ...`

### 用户资料与账号设置

- 后端：`UserController`、`UserService`、`UserServiceImpl`、`UserRepository`
- DTO：`RegisterRequest`、`ChangePasswordRequest`、`ChangeEmailRequest`、`ChangeNameRequest`
- 前端：`pages/me/me.uvue`、`pages/password/password.uvue`、`pages/email/email.uvue`、`pages/username/username.uvue`
- 当前实现特点：
  - 修改资料前校验 path `userId` 与当前登录用户一致
  - DTO 字段使用 `@Valid` 校验
  - 错误信息统一走 `ErrorMessages`

### 订单（核心交易流）

- 后端：`BookingController`、`BookingService`、`BookingServiceImpl`、`BookingRepository`
- 相关：`BookingCompletionService`、`BookingScheduler`
- 前端：`pages/price/price.uvue`（创建）、`pages/orders/orders.uvue`（取消/完成/分页）
- 当前实现特点：
  - 创建订单并发控制：`ScooterRepository.findByIdForUpdate`
  - 状态机：`CONFIRMED -> COMPLETED/CANCELLED`
  - 订单与车辆状态在同一事务内同步更新

### 车辆管理

- 后端：`ScooterController`、`ScooterService`、`ScooterServiceImpl`、`ScooterRepository`
- 前端：`pages/index/index.uvue`、`pages/bikelist/bikelist.uvue`、`pages/admin/modify.uvue`
- 当前实现特点：
  - 公开查询车辆列表/位置
  - 管理端可更新状态、费率、位置

### 反馈与管理统计

- 反馈：`FeedbackController`、`FeedbackService`、`FeedbackServiceImpl`
- 统计：`AdminController`、`AdminService`、`AdminServiceImpl`
- 前端：`pages/feedback/feedback.uvue`、`pages/admin/revenue.uvue`
- 当前实现特点：
  - 统计按时间范围 + 租期分类聚合
  - 前端同页拉取 summary + breakdown 两组数据

---

## 2）后端 TODO（固定顺序）

### 2.1 DTO 层

- [ ] 新建 `backend/src/main/java/.../dto/<Feature>Request.java`
  - [ ] 添加字段校验（`@NotBlank`、`@Size` 等）
  - [ ] Controller 入参使用 `@Valid`
- [ ] 新建 `backend/src/main/java/.../dto/<Feature>Response.java`
  - [ ] 返回字段结构稳定、可供前端直接消费

### 2.2 Controller 层

- [ ] 在 `/api/v1/...` 下新增/扩展路由
- [ ] 增加接口方法：
  - [ ] `create(...)`
  - [ ] `query(...)`
  - [ ] `update(...)`
  - [ ] `delete(...)`（如需要）
- [ ] 权限校验与功能权限级别保持一致

### 2.3 Service 接口层

- [ ] 在 `service/<Feature>Service.java` 定义方法签名
  - [ ] `create...(...)`
  - [ ] `query...(...)`
  - [ ] `update...(...)`

### 2.4 Service 实现层

- [ ] 在 `service/impl/<Feature>ServiceImpl.java` 实现业务逻辑
- [ ] 增加状态校验
- [ ] 增加幂等处理（若存在重试场景）
- [ ] 需要时加事务边界（`@Transactional`）

### 2.5 Repository 层

- [ ] 新增/扩展 `repository/<Feature>Repository.java`
- [ ] 增加必要查询方法
- [ ] 复杂查询按需添加 `@Query`

### 2.6 错误与审计

- [ ] 在 `exception/ErrorMessages.java` 增加功能错误常量
  - [ ] `FEATURE_X_...`
- [ ] 需要审计时增加 `@LogAction`

### 2.7 定时处理（可选）

- [ ] 若功能需要延迟/周期处理，按 `BookingScheduler` 模式接入
  - [ ] 调度类
  - [ ] 独立业务执行服务
  - [ ] 失败隔离策略

---

## 3）前端 TODO（按页面动作拆）

### 3.1 页面实现

- [ ] 新增或修改页面：`pages/<feature>/<feature>.uvue`
- [ ] 新增页面状态变量
- [ ] 新增表单与提交函数

### 3.2 API 请求

- [ ] 使用项目现有风格编写 `uni.request`
- [ ] 受保护接口带 token 头
  - [ ] `Authorization: Bearer <token>`
- [ ] 使用 `getResponseMessage(...)` 统一错误映射

### 3.3 文案与交互一致性

- [ ] 在 `common/messages.uts` 增加文案 key
- [ ] 使用 `getAppMessage(...)` 读取统一文案
- [ ] 成功/失败 toast 风格与现有页面一致

### 3.4 本地状态与页面注册

- [ ] 成功后同步 storage 或页面局部状态
- [ ] 新页面在 `pages.json` 注册路由

---

## 4）验证 TODO（不要遗漏）

- [ ] 正常路径（`200/201`）
- [ ] 参数错误（`400`）
- [ ] 未登录（`401`）
- [ ] 权限不足（`403`）
- [ ] 资源不存在（`404`）
- [ ] 状态冲突（`409` 或业务冲突错误）

---

## 5）可直接套用的 FeatureX 文件清单

将 `FeatureX` 替换为真实功能名。

- [ ] `backend/.../dto/FeatureXRequest.java`（字段 + 校验）
- [ ] `backend/.../dto/FeatureXResponse.java`（返回结构）
- [ ] `backend/.../controller/FeatureXController.java`（`@RequestMapping("/api/v1/feature-x")`）
- [ ] `backend/.../service/FeatureXService.java`（`create/query/update`）
- [ ] `backend/.../service/impl/FeatureXServiceImpl.java`（核心业务规则）
- [ ] `backend/.../repository/FeatureXRepository.java`（查询与唯一性校验）
- [ ] `backend/.../exception/ErrorMessages.java`（`FEATURE_X_...`）
- [ ] `pages/featurex/featurex.uvue`（页面交互 + 请求）
- [ ] `pages.json`（页面路由注册）
- [ ] `common/messages.uts`（文案 key）
- [ ] 联调检查：鉴权、异常映射、前端提示一致性

---

## 6）Sprint 功能文件影响清单（第一步：先识别涉及文件）

> 说明：本节只记录“从头到尾会涉及哪些文件”，用于开发前范围确认。  
> 约定：`[已有]` 表示当前仓库已存在、需要修改；`[新增]` 表示建议新建文件。

### ID 2：存储支付卡片信息（Store Payment Card Information）

#### 后端

- [ ] [已有] `backend/src/main/java/com/group12/backend/controller/UserController.java`（账户相关入口扩展）  
      或 [新增] `backend/src/main/java/com/group12/backend/controller/PaymentCardController.java`
- [ ] [新增] `backend/src/main/java/com/group12/backend/dto/StorePaymentCardRequest.java`
- [ ] [新增] `backend/src/main/java/com/group12/backend/dto/PaymentCardResponse.java`
- [ ] [新增] `backend/src/main/java/com/group12/backend/service/PaymentCardService.java`
- [ ] [新增] `backend/src/main/java/com/group12/backend/service/impl/PaymentCardServiceImpl.java`
- [ ] [新增] `backend/src/main/java/com/group12/backend/repository/PaymentCardRepository.java`  
      （或评估复用现有 `PaymentRepository.java`）
- [ ] [新增] `backend/src/main/java/com/group12/backend/entity/PaymentCard.java`（如当前无实体）
- [ ] [已有] `backend/src/main/java/com/group12/backend/exception/ErrorMessages.java`（卡片相关错误常量）
- [ ] [已有] `backend/src/main/java/com/group12/backend/security/AdminAccessGuard.java`（如涉及管理权限校验）
- [ ] [已有] `backend/src/main/java/com/group12/backend/config/WebConfig.java`（若新增白名单/鉴权路径）

#### 前端

- [ ] [新增] `pages/payment-card/payment-card.uvue`（卡片管理页）
- [ ] [已有] `pages/me/me.uvue`（新增入口）
- [ ] [已有] `pages/payment/payment.uvue`（支付时复用已存卡片）
- [ ] [已有] `common/messages.uts`（paymentCard 文案组）
- [ ] [已有] `pages.json`（注册新页面路由）

#### 新增文件职责说明（ID 2）

- `StorePaymentCardRequest.java`：定义卡片入参（持卡人、卡号掩码前字段、到期时间、品牌等）与 `@Valid` 校验规则。
- `PaymentCardResponse.java`：定义返回结构（卡片 id、品牌、尾号、默认卡标记、创建时间），避免向前端暴露敏感字段。
- `PaymentCardService.java`：声明卡片新增/查询/删除/设默认等业务接口。
- `PaymentCardServiceImpl.java`：实现卡片保存规则（去重、归属校验、默认卡策略、敏感信息处理）。
- `PaymentCardRepository.java`：提供按用户查询、默认卡查询、唯一性检查等持久化方法。
- `PaymentCard.java`：定义支付卡实体及与用户的关联关系（`userId` 归属）。
- `pages/payment-card/payment-card.uvue`：提供卡片列表、添加卡片、删除卡片、设默认卡页面交互。

### ID 3：账户安全增强（Enhance Account Security）

#### 后端

- [ ] [已有] `backend/src/main/java/com/group12/backend/controller/AuthController.java`
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/AuthService.java`
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/impl/AuthServiceImpl.java`
- [ ] [已有] `backend/src/main/java/com/group12/backend/utils/JwtUtil.java`
- [ ] [已有] `backend/src/main/java/com/group12/backend/interceptor/AuthenticationInterceptor.java`
- [ ] [已有] `backend/src/main/java/com/group12/backend/controller/UserController.java`（安全相关接口扩展）
- [ ] [新增] `backend/src/main/java/com/group12/backend/dto/SecuritySettingsRequest.java`（按需求可拆分）
- [ ] [新增] `backend/src/main/java/com/group12/backend/dto/SecuritySettingsResponse.java`
- [ ] [已有] `backend/src/main/java/com/group12/backend/exception/ErrorMessages.java`

#### 前端

- [ ] [新增] `pages/security/security.uvue`（安全设置页，若需要）
- [ ] [已有] `pages/login/login.uvue`（登录策略增强）
- [ ] [已有] `pages/password/password.uvue`（强密码规则/交互）
- [ ] [已有] `pages/me/me.uvue`（安全中心入口）
- [ ] [已有] `common/messages.uts`（security/auth 文案补齐）
- [ ] [已有] `pages.json`（新页面路由）

#### 新增文件职责说明（ID 3）

- `SecuritySettingsRequest.java`：承载安全策略配置入参（如登录风控开关、会话策略、二次验证参数等，按最终需求裁剪）。
- `SecuritySettingsResponse.java`：返回当前安全配置状态，供前端安全页渲染。
- `pages/security/security.uvue`：安全中心页面，展示并提交账户安全配置，统一提示处理。

### ID 7：邮件预订确认（Email Reservation Confirmation）

#### 后端

- [ ] [已有] `backend/src/main/java/com/group12/backend/controller/BookingController.java`
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/BookingService.java`
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/impl/BookingServiceImpl.java`
- [ ] [新增] `backend/src/main/java/com/group12/backend/service/EmailNotificationService.java`
- [ ] [新增] `backend/src/main/java/com/group12/backend/service/impl/EmailNotificationServiceImpl.java`
- [ ] [新增] `backend/src/main/java/com/group12/backend/dto/BookingConfirmationEmailPayload.java`（可选）
- [ ] [已有] `backend/src/main/java/com/group12/backend/repository/UserRepository.java`（获取收件人邮箱）
- [ ] [已有] `backend/src/main/java/com/group12/backend/exception/ErrorMessages.java`（邮件发送失败类错误）
- [ ] [已有] `backend/src/main/resources/application.yml` 或 `application.properties`（邮件配置）

#### 前端

- [ ] [已有] `pages/price/price.uvue`（创建后提示“邮件已发送/发送失败”）
- [ ] [已有] `pages/orders/orders.uvue`（必要时补状态提示）
- [ ] [已有] `common/messages.uts`（email booking 文案）

#### 新增文件职责说明（ID 7）

- `EmailNotificationService.java`：抽象邮件发送能力，定义“发送预订确认邮件”等接口。
- `EmailNotificationServiceImpl.java`：实现邮件模板拼装与发送（预订号、车辆、租期、价格、起止时间等）。
- `BookingConfirmationEmailPayload.java`：封装邮件模板所需字段，降低 `BookingServiceImpl` 与邮件实现的耦合。

### ID 10 & 11：预订状态管理与延长（Reservation Status Management & Extension）

#### 后端

- [ ] [已有] `backend/src/main/java/com/group12/backend/controller/BookingController.java`（新增延长/状态接口）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/BookingService.java`
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/impl/BookingServiceImpl.java`
- [ ] [新增] `backend/src/main/java/com/group12/backend/dto/ExtendBookingRequest.java`
- [ ] [新增] `backend/src/main/java/com/group12/backend/dto/BookingStatusResponse.java`（可选）
- [ ] [已有] `backend/src/main/java/com/group12/backend/repository/BookingRepository.java`（冲突查询扩展）
- [ ] [已有] `backend/src/main/java/com/group12/backend/repository/ScooterRepository.java`（必要时并发控制）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/BookingCompletionService.java`
- [ ] [已有] `backend/src/main/java/com/group12/backend/task/BookingScheduler.java`（与延长后的到期逻辑一致）
- [ ] [已有] `backend/src/main/java/com/group12/backend/exception/ErrorMessages.java`

#### 前端

- [ ] [已有] `pages/orders/orders.uvue`（新增“延长预订/状态展示”动作）
- [ ] [已有] `pages/orders/route.uvue`（如状态影响路线展示）
- [ ] [可选新增] `pages/extend-booking/extend-booking.uvue`（独立延长页）
- [ ] [已有] `common/messages.uts`（booking status/extension 文案）
- [ ] [已有] `pages.json`（如新增页面）

#### 新增文件职责说明（ID 10 & 11）

- `ExtendBookingRequest.java`：定义延长预订入参（延长时长/目标结束时间）及合法性校验。
- `BookingStatusResponse.java`：统一返回预订状态视图（当前状态、可执行动作、结束时间、费用变化等）。
- `pages/extend-booking/extend-booking.uvue`：提供延长预订交互页（时长选择、价格预估、提交延长请求）。

### Sprint 通用横切文件（以上功能均需按需评估）

- [ ] [已有] `common/api.uts`（前端 API 路径常量）
- [ ] [已有] `backend/src/main/java/com/group12/backend/config/WebConfig.java`（路由鉴权策略）
- [ ] [已有] `backend/src/main/java/com/group12/backend/exception/GlobalExceptionHandler.java`（如有统一异常映射）
- [ ] [已有] `backend/src/main/java/com/group12/backend/annotation/LogAction.java`（需要审计时）
- [ ] [测试] 后端/前端对应测试文件（按团队测试规范补齐）

---

## 7）函数级 TODO（方法名 + 输入输出 + 校验点）

> 说明：本节为第二步编码前的函数颗粒度任务拆分。  
> 命名可按现有项目风格微调，但建议保持语义不变。

### ID 2：存储支付卡片信息（Store Payment Card Information）

#### 后端函数 TODO

- [ ] `PaymentCardController.createCard(...)`
  - 输入：`userId`（path）、`StorePaymentCardRequest`（body）
  - 输出：`ResponseEntity<Map<String,Object>>`（`data: PaymentCardResponse`）
  - 校验点：登录态存在；path `userId` 与当前用户一致；请求体 `@Valid` 通过

- [ ] `PaymentCardController.listCards(...)`
  - 输入：`userId`（path）
  - 输出：`ResponseEntity<Map<String,Object>>`（`data: List<PaymentCardResponse>`）
  - 校验点：仅本人可查看；空列表返回 `[]` 不报错

- [ ] `PaymentCardController.deleteCard(...)`
  - 输入：`userId`（path）、`cardId`（path）
  - 输出：`ResponseEntity<Map<String,Object>>`（成功消息）
  - 校验点：卡片存在且归属当前用户；删除默认卡后的默认卡重选策略

- [ ] `PaymentCardController.setDefaultCard(...)`
  - 输入：`userId`（path）、`cardId`（path）
  - 输出：`ResponseEntity<Map<String,Object>>`（更新后的 `PaymentCardResponse`）
  - 校验点：同一用户仅允许 1 张默认卡；目标卡必须属于该用户

- [ ] `PaymentCardService.createCard(String userId, StorePaymentCardRequest req)`
  - 输入：用户 id + 卡片入参
  - 输出：`PaymentCardResponse`
  - 校验点：卡号合法性（Luhn 可选）；去重（同用户同尾号+到期月年+品牌）；敏感字段不明文返回

- [ ] `PaymentCardService.listCards(String userId)`
  - 输入：用户 id
  - 输出：`List<PaymentCardResponse>`
  - 校验点：排序规则（默认卡优先、创建时间倒序）

- [ ] `PaymentCardService.deleteCard(String userId, String cardId)`
  - 输入：用户 id、卡片 id
  - 输出：`Map<String,Object>` 或 `void`
  - 校验点：不存在返回 404；越权返回 403

- [ ] `PaymentCardService.setDefaultCard(String userId, String cardId)`
  - 输入：用户 id、卡片 id
  - 输出：`PaymentCardResponse`
  - 校验点：事务内先取消旧默认再设置新默认；幂等（重复设置同一张默认卡）

- [ ] `PaymentCardRepository.findByUserIdOrderByCreatedAtDesc(...)`
  - 输入：`userId`
  - 输出：实体列表
  - 校验点：结果稳定排序

- [ ] `PaymentCardRepository.findByIdAndUserId(...)`
  - 输入：`cardId`、`userId`
  - 输出：`Optional<PaymentCard>`
  - 校验点：用于越权隔离

- [ ] `PaymentCardRepository.existsDuplicateCard(...)`（可 `@Query`）
  - 输入：`userId` + 去重维度字段
  - 输出：`boolean`
  - 校验点：避免重复绑卡

#### 前端函数 TODO

- [ ] `loadCards()`
  - 输入：无（内部读取 `userId`/`token`）
  - 输出：页面状态 `cards[]`
  - 校验点：401 跳登录；空态展示

- [ ] `submitAddCard()`
  - 输入：表单状态（卡号/持卡人/到期）
  - 输出：添加结果 toast + 刷新列表
  - 校验点：前端基础格式校验；提交节流防重复点击

- [ ] `onDeleteCard(cardId: string)`
  - 输入：卡片 id
  - 输出：删除结果 + 列表刷新
  - 校验点：二次确认弹窗；删除失败提示统一

- [ ] `onSetDefault(cardId: string)`
  - 输入：卡片 id
  - 输出：默认卡切换结果 + 列表局部更新
  - 校验点：重复点击幂等提示；请求中禁用按钮

### ID 3：账户安全增强（Enhance Account Security）

#### 后端函数 TODO

- [ ] `AuthController.getSecuritySettings(...)`
  - 输入：当前登录用户上下文
  - 输出：`SecuritySettingsResponse`
  - 校验点：未登录 401；仅本人可查

- [ ] `AuthController.updateSecuritySettings(...)`
  - 输入：`SecuritySettingsRequest`
  - 输出：更新后的 `SecuritySettingsResponse`
  - 校验点：`@Valid`；字段范围校验；敏感配置变更审计

- [ ] `AuthService.getSecuritySettings(Long userId)`
  - 输入：用户 id
  - 输出：`SecuritySettingsResponse`
  - 校验点：用户存在性

- [ ] `AuthService.updateSecuritySettings(Long userId, SecuritySettingsRequest req)`
  - 输入：用户 id + 配置请求
  - 输出：`SecuritySettingsResponse`
  - 校验点：事务一致性；配置变更幂等

- [ ] `AuthenticationInterceptor.preHandle(...)`（按需求增强）
  - 输入：`HttpServletRequest`
  - 输出：是否放行
  - 校验点：token 格式、过期、黑名单/会话策略（如引入）

#### 前端函数 TODO

- [ ] `loadSecuritySettings()`
  - 输入：无
  - 输出：安全设置表单初始值
  - 校验点：401/403 分流提示

- [ ] `submitSecuritySettings()`
  - 输入：页面安全配置表单
  - 输出：保存成功提示
  - 校验点：开关依赖校验（例如启用 A 必须配置 B）

- [ ] `handleSecurityRiskPrompt(...)`（可选）
  - 输入：风险状态码/消息
  - 输出：统一弹窗提示
  - 校验点：防重复弹窗

### ID 7：邮件预订确认（Email Reservation Confirmation）

#### 后端函数 TODO

- [ ] `BookingServiceImpl.createBooking(...)`（扩展）
  - 输入：`CreateBookingRequest`
  - 输出：`BookingResponse`
  - 校验点：订单创建成功后触发邮件；邮件失败不回滚订单（建议异步/降级）

- [ ] `EmailNotificationService.sendBookingConfirmation(BookingConfirmationEmailPayload payload)`
  - 输入：邮件载荷（用户邮箱、订单号、车辆、租期、价格、时间）
  - 输出：`boolean` 或 `void`
  - 校验点：邮箱为空不发送；模板字段完整性

- [ ] `EmailNotificationServiceImpl.buildBookingTemplate(...)`
  - 输入：`BookingConfirmationEmailPayload`
  - 输出：邮件主题+正文
  - 校验点：金额/时间格式统一；避免 null 文本

- [ ] `EmailNotificationServiceImpl.sendMail(...)`
  - 输入：收件人、主题、正文
  - 输出：发送结果
  - 校验点：异常捕获并记录日志；防止抛出影响主交易链路

#### 前端函数 TODO

- [ ] `confirm()`（`pages/price/price.uvue`，扩展）
  - 输入：预订信息
  - 输出：下单后提示信息
  - 校验点：区分“下单成功+邮件已发送”与“下单成功+邮件稍后补发”文案

### ID 10 & 11：预订状态管理与延长（Reservation Status Management & Extension）

#### 后端函数 TODO

- [ ] `BookingController.extendBooking(...)`
  - 输入：`bookingId`（path）、`ExtendBookingRequest`（body）
  - 输出：`BookingStatusResponse` 或 `BookingResponse`
  - 校验点：仅 `CONFIRMED` 可延长；当前用户与订单归属一致

- [ ] `BookingService.extendBooking(String bookingId, ExtendBookingRequest req)`
  - 输入：订单 id + 延长请求
  - 输出：延长后的订单视图
  - 校验点：新结束时间必须晚于当前结束时间；与其他订单不冲突；金额重算

- [ ] `BookingServiceImpl.extendBooking(...)`
  - 输入：同上
  - 输出：同上
  - 校验点：事务内更新 `endTime`、`duration`、`totalPrice`；幂等请求处理

- [ ] `BookingRepository.findOverlappingBookings(...)`（复用/扩展）
  - 输入：`scooterId`、新区间
  - 输出：冲突订单列表
  - 校验点：排除当前订单自身

- [ ] `BookingScheduler.checkAndCompleteExpiredBookings()`（联动校验）
  - 输入：系统当前时间
  - 输出：自动完成结果日志
  - 校验点：延长后订单不应被提前自动完成

#### 前端函数 TODO

- [ ] `onExtendBooking(booking)`（`pages/orders/orders.uvue`）
  - 输入：订单对象
  - 输出：跳转延长页或弹窗选择延长时长
  - 校验点：仅可延长状态显示按钮

- [ ] `submitExtendBooking()`
  - 输入：`bookingId` + 延长参数
  - 输出：延长成功后刷新订单列表
  - 校验点：请求进行中禁用重复提交；失败提示统一

- [ ] `getOrderActions(booking)`（扩展）
  - 输入：订单对象
  - 输出：动作列表（`endRide/cancelOrder/viewRoute/extendBooking`）
  - 校验点：状态机与后端规则一致，避免前后端动作不一致
