# Sprint Feature Dev Checklist (Step 1)

> 目标：先确认新功能从头到尾会涉及哪些文件。  
> 范围：本次仅完成文件范围识别，不做业务实现。  
> 标记约定：`[已有]` = 当前仓库已有、需要修改；`[新增]` = 建议新建。

---

## ID 9：为未注册用户办理预订 + 店员录入并绑定用户信用卡

### 后端（API / 业务 / 持久化）

- [ ] [已有] `backend/src/main/java/com/group12/backend/controller/BookingController.java`（新增店员代下单入口，支持未注册用户场景）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/BookingService.java`（扩展“代客预订”服务接口）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/impl/BookingServiceImpl.java`（补未注册用户预订逻辑、绑定卡联动）
- [ ] [已有] `backend/src/main/java/com/group12/backend/dto/CreateBookingRequest.java`（扩展 guest / salesperson 相关字段）
- [ ] [新增] `backend/src/main/java/com/group12/backend/dto/CreateGuestBookingRequest.java`（未注册用户预订专用入参）
- [ ] [新增] `backend/src/main/java/com/group12/backend/dto/GuestProfileResponse.java`（游客信息回传结构）
- [ ] [已有] `backend/src/main/java/com/group12/backend/controller/PaymentCardController.java`（新增店员代绑定卡入口）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/PaymentCardService.java`（支持游客或临时用户绑卡接口）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/impl/PaymentCardServiceImpl.java`（实现店员录卡与关联逻辑）
- [ ] [已有] `backend/src/main/java/com/group12/backend/repository/PaymentCardRepository.java`（补按游客标识/临时账号查询）
- [ ] [已有] `backend/src/main/java/com/group12/backend/entity/PaymentCard.java`（补 guest 绑定字段或关联关系）
- [ ] [已有] `backend/src/main/java/com/group12/backend/entity/Booking.java`（支持 guest 预订关联策略）
- [ ] [已有] `backend/src/main/java/com/group12/backend/repository/BookingRepository.java`（补 guest 预订检索条件）
- [ ] [已有] `backend/src/main/java/com/group12/backend/controller/UserController.java`（店员为未注册对象创建临时档案/查询订单）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/impl/UserServiceImpl.java`（临时用户资料创建、预订历史聚合）
- [ ] [已有] `backend/src/main/java/com/group12/backend/exception/ErrorMessages.java`（新增 guest booking / bind card 错误文案）
- [ ] [已有] `backend/src/main/java/com/group12/backend/exception/GlobalExceptionHandler.java`（统一异常映射）
- [ ] [已有] `backend/src/main/java/com/group12/backend/interceptor/AuthenticationInterceptor.java`（店员权限识别）
- [ ] [已有] `backend/src/main/java/com/group12/backend/config/WebConfig.java`（路由鉴权与白名单策略）

### 数据库

- [ ] [已有] `backend/init.sql`（补 guest 预订、卡片绑定所需字段/表）
- [ ] [已有] `backend/DATABASE_SCHEMA.md`（更新数据模型说明）

### 前端（店员端 / 订单端 / 支付端）

- [ ] [已有] `pages/price/price.uvue`（支持未注册用户下单流程入口）
- [ ] [已有] `pages/payment/payment.uvue`（店员代录信用卡与支付确认）
- [ ] [已有] `pages/payment-card/payment-card.uvue`（店员维护代录卡片）
- [ ] [已有] `pages/orders/orders.uvue`（显示 guest 预订与后续操作）
- [ ] [新增] `pages/guest-booking/guest-booking.uvue`（店员创建未注册用户预订页面）
- [ ] [新增] `pages/guest-profile/guest-profile.uvue`（店员录入临时用户资料）
- [ ] [已有] `pages.json`（注册新页面路由）
- [ ] [已有] `common/api.uts`（新增 guest booking / bind card API 常量）
- [ ] [已有] `common/messages.uts`（新增 guest booking / salesperson 文案）

### 测试与脚本

- [ ] [新增] `backend/src/test/java/com/group12/backend/sprint2/guestbooking/GuestBookingServiceTest.java`
- [ ] [新增] `backend/src/test/java/com/group12/backend/sprint2/guestbooking/GuestBookingControllerMvcTest.java`
- [ ] [新增] `backend/src/test/java/com/group12/backend/sprint2/paymentcard/SalespersonBindCardTest.java`
- [ ] [已有] `testing_scripts/sprint2/config/features.psd1`（加入 ID9 测试分组）
- [ ] [已有] `testing_sys/sprint2/scripts/src/suites/sprint2.mjs`（加入 ID9 测试套件）

### 新增文件职责说明（ID 9）

- `CreateGuestBookingRequest.java`：定义未注册用户预订入参（店员标识、guest 基本信息、车辆与时长、可选联系方式）及校验规则。
- `GuestProfileResponse.java`：返回 guest 档案摘要（guestId、显示名、联系方式掩码、关联预订数）供前端渲染。
- `pages/guest-booking/guest-booking.uvue`：店员为未注册用户创建预订的专用页面，承载资料录入、车辆选择、提交与异常提示。
- `pages/guest-profile/guest-profile.uvue`：展示并维护临时用户资料及其关联支付卡、预订记录。
- `GuestBookingServiceTest.java`：覆盖未注册用户预订核心业务（创建、冲突校验、权限校验、失败回滚）。
- `GuestBookingControllerMvcTest.java`：覆盖 guest 预订接口的请求校验、鉴权、状态码与响应体结构。
- `SalespersonBindCardTest.java`：覆盖店员代绑定信用卡流程（归属校验、重复卡校验、默认卡策略）。

---

## ID 22：新增折扣功能（频繁使用者、学生、老年人）

### 后端（折扣规则与计费）

- [ ] [已有] `backend/src/main/java/com/group12/backend/service/impl/BookingServiceImpl.java`（接入折扣规则并重算总价）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/BookingService.java`（暴露折扣计算/回传信息）
- [ ] [已有] `backend/src/main/java/com/group12/backend/entity/Booking.java`（记录折扣类型、折扣金额、实付金额）
- [ ] [已有] `backend/src/main/java/com/group12/backend/dto/BookingResponse.java`（回传原价/折扣/实付）
- [ ] [已有] `backend/src/main/java/com/group12/backend/entity/User.java`（使用 `isStudent`、`age` 判定资格）
- [ ] [已有] `backend/src/main/java/com/group12/backend/repository/BookingRepository.java`（统计近 7 日使用时长，识别 frequent user）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/impl/UserServiceImpl.java`（对外提供优惠资格基础信息）
- [ ] [新增] `backend/src/main/java/com/group12/backend/service/DiscountService.java`（抽离折扣策略）
- [ ] [新增] `backend/src/main/java/com/group12/backend/service/impl/DiscountServiceImpl.java`（实现 frequent/student/senior 规则）
- [ ] [新增] `backend/src/main/java/com/group12/backend/dto/DiscountBreakdownResponse.java`（折扣明细结构）
- [ ] [已有] `backend/src/main/java/com/group12/backend/exception/ErrorMessages.java`（折扣资格/规则冲突提示）

### 数据库

- [ ] [已有] `backend/init.sql`（补 bookings 折扣字段）
- [ ] [已有] `backend/DATABASE_SCHEMA.md`（补折扣相关 schema 文档）

### 前端（价格展示与订单展示）

- [ ] [已有] `pages/price/price.uvue`（下单前展示可用折扣与预估实付）
- [ ] [已有] `pages/payment/payment.uvue`（支付页展示折扣明细）
- [ ] [已有] `pages/orders/orders.uvue`（历史订单展示折扣信息）
- [ ] [已有] `common/messages.uts`（新增 discount 文案）
- [ ] [已有] `common/api.uts`（新增折扣查询/试算 API）

### 测试与脚本

- [ ] [新增] `backend/src/test/java/com/group12/backend/sprint2/discount/DiscountServiceTest.java`
- [ ] [新增] `backend/src/test/java/com/group12/backend/sprint2/booking/BookingDiscountIntegrationTest.java`
- [ ] [已有] `testing_scripts/sprint2/config/features.psd1`（加入 ID22 测试分组）
- [ ] [已有] `testing_sys/sprint2/scripts/src/suites/sprint2.mjs`（加入 ID22 测试套件）

### 新增文件职责说明（ID 22）

- `DiscountService.java`：声明折扣资格判断与折扣计算接口（frequent/student/senior 规则统一出口）。
- `DiscountServiceImpl.java`：实现折扣优先级、互斥/叠加策略、每周时长统计、折扣金额计算与回传结构组装。
- `DiscountBreakdownResponse.java`：定义折扣明细响应（原价、命中规则、折扣金额、实付金额、说明文案 key）。
- `DiscountServiceTest.java`：覆盖折扣规则单测（资格边界、优先级、金额精度、异常输入）。
- `BookingDiscountIntegrationTest.java`：覆盖下单流程中的折扣集成行为（创建订单时自动应用折扣并持久化）。

---

## ID 23：支持多个客户端同时使用

### 后端（并发一致性与容量）

- [ ] [已有] `backend/src/main/java/com/group12/backend/service/impl/BookingServiceImpl.java`（并发创建订单冲突保护）
- [ ] [已有] `backend/src/main/java/com/group12/backend/repository/ScooterRepository.java`（`findByIdForUpdate` 并发锁语义确认）
- [ ] [已有] `backend/src/main/java/com/group12/backend/repository/BookingRepository.java`（重叠预订冲突查询优化）
- [ ] [已有] `backend/src/main/java/com/group12/backend/task/BookingScheduler.java`（并发下状态收敛与幂等）
- [ ] [已有] `backend/src/main/java/com/group12/backend/service/BookingCompletionService.java`（并发完成订单幂等）
- [ ] [已有] `backend/src/main/java/com/group12/backend/config/AsyncConfig.java`（线程池参数调优）
- [ ] [已有] `backend/src/main/java/com/group12/backend/config/WebConfig.java`（多端并发请求的跨域策略）
- [ ] [已有] `backend/src/main/java/com/group12/backend/interceptor/AuthenticationInterceptor.java`（并发请求下鉴权稳定性）
- [ ] [已有] `backend/src/main/java/com/group12/backend/controller/BookingController.java`（高并发下返回码与错误语义稳定）
- [ ] [已有] `backend/src/main/java/com/group12/backend/exception/GlobalExceptionHandler.java`（并发冲突统一错误映射）
- [ ] [已有] `backend/src/main/java/com/group12/backend/exception/ErrorMessages.java`（新增并发冲突提示文案）

### 前端（多端操作体验）

- [ ] [已有] `pages/bikelist/bikelist.uvue`（车辆可用状态刷新与冲突提示）
- [ ] [已有] `pages/price/price.uvue`（提交前后状态二次确认）
- [ ] [已有] `pages/orders/orders.uvue`（轮询刷新与冲突处理）
- [ ] [已有] `pages/payment/payment.uvue`（并发取消/支付兜底提示）
- [ ] [已有] `common/messages.uts`（并发冲突、重试建议文案）

### 测试与脚本

- [ ] [新增] `backend/src/test/java/com/group12/backend/sprint2/concurrency/BookingConcurrentCreateTest.java`
- [ ] [新增] `backend/src/test/java/com/group12/backend/sprint2/concurrency/BookingConcurrentStateTransitionTest.java`
- [ ] [已有] `testing_sys/sprint2/scripts/src/runner.mjs`（支持并发用户压测参数）
- [ ] [已有] `testing_sys/sprint2/scripts/src/targets/bookings.mjs`（并发预订场景）
- [ ] [已有] `testing_scripts/sprint2/config/features.psd1`（加入 ID23 测试分组）

### 新增文件职责说明（ID 23）

- `BookingConcurrentCreateTest.java`：验证多客户端同时抢同一车辆时仅允许一个成功预订，其余请求返回冲突或业务失败。
- `BookingConcurrentStateTransitionTest.java`：验证并发执行取消/完成/延长等状态流转时的数据一致性与幂等行为。

---

## Sprint 共用横切文件（ID 9/22/23 都要评估）

- [ ] [已有] `backend/src/main/resources/application.yaml`（线程池、日志、超时等配置）
- [ ] [已有] `backend/src/test/resources/application-test.yaml`（测试环境并发/数据配置）
- [ ] [已有] `docs/sprint2-todo-details.md`（后续同步函数级 TODO）
- [ ] [已有] `docs/feature-dev-checklist.md`（本文件，持续维护）

---

## 函数级 TODO（方法名 + 输入输出 + 校验点）

> 说明：本节聚焦上文“新增文件职责说明”，给出可直接落代码的函数任务拆分。

### ID 9：未注册用户预订 + 店员代绑卡

#### `CreateGuestBookingRequest.java`

- [ ] `getSalespersonId()/setSalespersonId(String)`
  - 输入：店员用户 ID（字符串）
  - 输出：字段读写
  - 校验点：非空、可解析为数值 ID
- [ ] `getGuestName()/setGuestName(String)`
  - 输入：guest 显示名
  - 输出：字段读写
  - 校验点：非空、长度范围、去首尾空格
- [ ] `getGuestContact()/setGuestContact(String)`
  - 输入：手机号或邮箱
  - 输出：字段读写
  - 校验点：至少满足手机号/邮箱一种格式
- [ ] `getScooterId()/setScooterId(String)`, `getDuration()/setDuration(String)`
  - 输入：车辆 ID、时长档位
  - 输出：字段读写
  - 校验点：时长仅允许 `10M/1H/4H/1D/1W`

#### `GuestProfileResponse.java`

- [ ] `fromGuestEntity(...)`（静态工厂，建议新增）
  - 输入：guest 实体 + 聚合统计数据
  - 输出：`GuestProfileResponse`
  - 校验点：敏感字段掩码、null 安全处理
- [ ] `maskContact(String contact)`（私有工具，建议新增）
  - 输入：联系方式
  - 输出：掩码后的展示字符串
  - 校验点：手机号与邮箱分别处理；短字符串兜底

#### `pages/guest-booking/guest-booking.uvue`

- [ ] `loadInitialData()`
  - 输入：无（内部读取 token、店员信息）
  - 输出：页面初始状态（车辆列表/默认时长/店员上下文）
  - 校验点：未登录跳转、接口失败容错
- [ ] `validateGuestForm(): boolean`
  - 输入：页面表单状态
  - 输出：是否通过校验
  - 校验点：姓名、联系方式、车辆、时长必填；文案统一
- [ ] `submitGuestBooking()`
  - 输入：`CreateGuestBookingRequest` 对应前端 payload
  - 输出：创建成功后的 bookingId 与跳转动作
  - 校验点：防重复提交、401/403/409 分流提示
- [ ] `handleGuestBookingError(res)`
  - 输入：后端错误响应
  - 输出：toast/modal 提示
  - 校验点：复用 `getResponseMessage`，错误码映射一致

#### `pages/guest-profile/guest-profile.uvue`

- [ ] `loadGuestProfile(guestId: string)`
  - 输入：guestId
  - 输出：guest 基础资料 + 关联卡片 + 订单摘要
  - 校验点：guestId 合法性、404 空态
- [ ] `bindCardForGuest()`
  - 输入：卡片表单 + guestId
  - 输出：绑卡结果与卡片列表刷新
  - 校验点：卡号格式、重复卡提示、请求中禁用按钮
- [ ] `goToGuestBookings()`
  - 输入：guestId
  - 输出：跳转到订单列表筛选态
  - 校验点：参数透传完整

#### `GuestBookingServiceTest.java`

- [ ] `createGuestBooking_success()`
  - 输入：合法 guest 预订请求
  - 输出：断言返回 booking DTO
  - 校验点：状态为 `CONFIRMED`、车辆状态更新
- [ ] `createGuestBooking_conflictWhenScooterUnavailable()`
  - 输入：不可用车辆
  - 输出：断言业务异常
  - 校验点：错误码/消息与 API 契约一致
- [ ] `createGuestBooking_forbiddenWhenSalespersonInvalid()`
  - 输入：无权限店员上下文
  - 输出：断言 403
  - 校验点：不写入订单数据

#### `GuestBookingControllerMvcTest.java`

- [ ] `createGuestBooking_returns201()`
  - 输入：有效 JSON 请求体 + 鉴权头
  - 输出：HTTP 201 + `data.id`
  - 校验点：响应结构稳定
- [ ] `createGuestBooking_returns400WhenInvalidPayload()`
  - 输入：缺失必填字段
  - 输出：HTTP 400
  - 校验点：校验错误信息可读
- [ ] `createGuestBooking_returns401WithoutToken()`
  - 输入：无 token
  - 输出：HTTP 401
  - 校验点：统一错误体

#### `SalespersonBindCardTest.java`

- [ ] `bindCard_success()`
  - 输入：合法卡信息 + guest 归属
  - 输出：保存成功并返回 `PaymentCardResponse`
  - 校验点：返回中不暴露敏感卡号
- [ ] `bindCard_rejectDuplicateCard()`
  - 输入：重复卡片信息
  - 输出：冲突异常
  - 校验点：错误码 409
- [ ] `bindCard_setDefaultRuleWorks()`
  - 输入：多卡绑定场景
  - 输出：默认卡唯一
  - 校验点：仅 1 张 `isDefault=true`

### ID 22：折扣功能

#### `DiscountService.java`

- [ ] `calculateDiscount(Long userId, Long scooterId, String duration): DiscountBreakdownResponse`
  - 输入：用户 ID、车辆 ID、时长档位
  - 输出：折扣明细对象
  - 校验点：空值保护、未知档位处理
- [ ] `resolveDiscountType(Long userId): String`
  - 输入：用户 ID
  - 输出：`FREQUENT/STUDENT/SENIOR/NONE`
  - 校验点：优先级策略固定且可测试

#### `DiscountServiceImpl.java`

- [ ] `isFrequentUser(Long userId): boolean`
  - 输入：用户 ID
  - 输出：是否满足每周 8+ 小时
  - 校验点：统计窗口为近 7 天、仅统计有效订单
- [ ] `isStudent(Long userId): boolean`
  - 输入：用户 ID
  - 输出：布尔值
  - 校验点：读取 `User.isStudent`，null 视为 false
- [ ] `isSenior(Long userId): boolean`
  - 输入：用户 ID
  - 输出：布尔值
  - 校验点：年龄阈值常量化（如 >= 60）
- [ ] `applyDiscount(BigDecimal origin, String discountType): BigDecimal`
  - 输入：原价、折扣类型
  - 输出：折后价
  - 校验点：金额精度、最小值不小于 0

#### `DiscountBreakdownResponse.java`

- [ ] `of(...)`（静态构造，建议新增）
  - 输入：原价、折扣类型、折扣率、折后价、文案 key
  - 输出：`DiscountBreakdownResponse`
  - 校验点：字段完整性，数值不为负

#### `DiscountServiceTest.java`

- [ ] `calculateDiscount_studentApplied()`
  - 输入：学生用户样本
  - 输出：命中 STUDENT 折扣
  - 校验点：折扣率与金额正确
- [ ] `calculateDiscount_seniorApplied()`
  - 输入：老年用户样本
  - 输出：命中 SENIOR 折扣
  - 校验点：边界年龄正确（阈值前后）
- [ ] `calculateDiscount_frequentApplied()`
  - 输入：近 7 天使用 >= 8h 的样本
  - 输出：命中 FREQUENT
  - 校验点：小时累计逻辑正确
- [ ] `calculateDiscount_priorityRuleStable()`
  - 输入：同时满足多个资格
  - 输出：命中优先级最高规则
  - 校验点：规则顺序不随实现波动

#### `BookingDiscountIntegrationTest.java`

- [ ] `createBooking_withDiscountPersistsPriceFields()`
  - 输入：创建订单请求
  - 输出：订单创建成功
  - 校验点：原价/折扣/实付字段都被写入
- [ ] `createBooking_withoutDiscountKeepsOriginalPrice()`
  - 输入：不满足折扣用户请求
  - 输出：无折扣
  - 校验点：折后价 = 原价

### ID 23：多客户端并发

#### `BookingConcurrentCreateTest.java`

- [ ] `concurrentCreate_sameScooter_onlyOneSuccess()`
  - 输入：并发 N 个创建请求（同一 scooter、同一时间窗口）
  - 输出：成功与失败统计
  - 校验点：仅 1 个成功，其余冲突失败
- [ ] `concurrentCreate_differentScooters_multiSuccess()`
  - 输入：并发请求（不同 scooter）
  - 输出：多请求成功
  - 校验点：无误报冲突
- [ ] `concurrentCreate_noDuplicateConfirmedBooking()`
  - 输入：高并发压力请求
  - 输出：数据库最终状态校验
  - 校验点：同一车同一时间无重复 `CONFIRMED`

#### `BookingConcurrentStateTransitionTest.java`

- [ ] `concurrentCancelAndComplete_sameBooking_idempotent()`
  - 输入：并发取消与完成请求
  - 输出：最终单一合法状态
  - 校验点：状态机不出现非法组合
- [ ] `concurrentExtendAndComplete_consistentEndTime()`
  - 输入：并发延长与完成
  - 输出：结束时间与状态一致
  - 校验点：不存在已完成却被再次延长
- [ ] `concurrentOperations_scooterStatusConsistent()`
  - 输入：多操作并发
  - 输出：车辆状态收敛
  - 校验点：订单状态与 scooter 状态匹配
