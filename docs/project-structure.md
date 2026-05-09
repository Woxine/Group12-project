# 项目结构与业务职责说明

本文说明仓库中主要目录和文件负责的具体业务，便于后续开发、测试和答辩时快速定位功能实现。

## 顶层结构

| 路径 | 业务职责 |
| --- | --- |
| `backend/` | Spring Boot 后端，负责用户、车辆、预约、支付卡、折扣、反馈、轨迹、管理端统计和计费规则等核心业务 API。 |
| `pages/` | UniApp X 移动端页面，负责用户侧找车、下单、支付、订单、个人资料、反馈、折扣认证和店员代客预约流程。 |
| `common/` | 移动端共享业务工具，集中维护 API 地址、订单时长、提示文案、Toast/Modal 事件和轨迹采集。 |
| `components/` | 移动端通用 UI 组件，承载底部导航、车辆介绍浮层、全局提示、弹窗和操作菜单。 |
| `auth_web_front/` | Vue 管理员网页，负责车辆运维、反馈处理、折扣审核、营收统计、计费设置和车型文案维护。 |
| `docs/` | 项目级文档。当前保留测试验证清单和本文档。 |
| `testing_sys/` | Sprint 级系统测试脚本、测试用例和报告。 |
| `testing_scripts/` | 人工或半自动测试辅助脚本。 |
| `static/` | 移动端静态资源，如头像、车辆图片、支付卡品牌图标等。 |
| `nativeResources/` | Android 原生打包资源，例如启动图。 |
| `uni_modules/` | UniApp 生态模块和云函数公共依赖。 |
| `uniCloud-aliyun/` | 阿里云 UniCloud 相关占位或平台资源。 |

## 移动端 App

### 工程入口和配置

| 文件 | 业务职责 |
| --- | --- |
| `manifest.json` | 定义 App 名称、版本、平台能力、地图定位模块、Android 图标和启动图等原生能力。 |
| `pages.json` | 注册所有移动端页面路径、导航栏标题和页面路由，是 App 页面结构的总入口。 |
| `App.uvue` | 处理 App 生命周期和全局交互，承载全局 Toast、Modal、ActionSheet 等组件挂载。 |
| `uni.scss` | 移动端全局样式变量，统一颜色、间距、阴影、圆角等视觉基础。 |

### `pages/`

| 文件 | 业务职责 |
| --- | --- |
| `pages/index/index.uvue` | 首页地图找车，展示附近车辆、安全提示、当前订单入口和车型说明入口。 |
| `pages/escooters/escooters.uvue` | 车辆列表和预约入口，按车型展示可租车辆，选择开始时间和租期后创建预约。 |
| `pages/orders/orders.uvue` | 我的订单列表，展示订单状态，支持取消、结束骑行、延长租期、跳转支付和查看路线。 |
| `pages/orders/route.uvue` | 订单路线地图，展示骑行起终点和后端保存的 GPS 轨迹。 |
| `pages/payment/payment.uvue` | 订单支付页，展示金额、折扣明细和确认支付流程。 |
| `pages/me/me.uvue` | 个人中心，展示登录状态、用户身份、折扣资格和功能菜单入口。 |
| `pages/login/login.uvue` | 用户登录页，完成账号认证并保存登录态。 |
| `pages/register/register.uvue` | 用户注册页，收集账号信息并创建普通用户。 |
| `pages/price/price.uvue` | 价格说明页，向用户解释租赁价格和费用规则。 |
| `pages/feedback/feedback.uvue` | 用户反馈页，提交问题描述和相关图片给后台处理。 |
| `pages/discount-verification/discount-verification.uvue` | 折扣认证页，上传学生、老年等折扣资格材料并查看审核状态。 |
| `pages/payment-card/payment-card.uvue` | 支付卡管理页，展示已绑定银行卡、默认卡和删除入口。 |
| `pages/payment-card/payment-card-add.uvue` | 添加支付卡页，录入卡信息并绑定到用户或 Guest。 |
| `pages/guest-booking/guest-booking.uvue` | 店员代客预约页，为未注册 Guest 创建租车订单。 |
| `pages/guest-profile/guest-profile.uvue` | Guest 资料页，维护或查看 Guest 联系方式和相关订单信息。 |
| `pages/account/account.uvue` | 账户设置总入口，汇总用户名、邮箱、密码和头像等资料修改项。 |
| `pages/username/username.uvue` | 修改用户名页。 |
| `pages/email/email.uvue` | 修改邮箱页。 |
| `pages/password/password.uvue` | 修改密码页。 |
| `pages/portrait/portrait.uvue` | 修改头像页。 |
| `pages/security/security.uvue` | 安全设置页，维护账户安全和通知相关偏好。 |
| `pages/about/about.uvue` | 关于页面，展示项目或应用介绍。 |

### `common/`

| 文件 | 业务职责 |
| --- | --- |
| `common/api.uts` | 集中定义后端 `BASE_URL` 和移动端常用 API 路径，区分调试与发布环境。 |
| `common/trajectoryTracker.uts` | 骑行中采集 GPS 点位，按距离和时间阈值缓冲并批量上传到轨迹接口。 |
| `common/bookingTime.uts` | 维护租赁时长预设、时间格式化和订单时长显示逻辑。 |
| `common/messages.uts` | 集中管理登录、注册、预约、支付、网络错误等用户提示文案。 |
| `common/appToast.uts` | 封装成功、失败、普通信息和长文本提示，统一替代分散的原生提示调用。 |
| `common/notifier.uts` | 通过全局事件驱动 Toast、Modal、ActionSheet，实现跨页面统一通知。 |

### `components/`

| 文件 | 业务职责 |
| --- | --- |
| `components/TabBar/TabBar.uvue` | App 底部主导航，切换首页、车辆列表和个人中心。 |
| `components/VehicleIntroOverlay/VehicleIntroOverlay.uvue` | 车辆介绍浮层，复用展示车型特点和用车说明。 |
| `components/AppToast/AppToast.uvue` | 全局 Toast 展示组件。 |
| `components/AppModal/AppModal.uvue` | 全局确认弹窗组件。 |
| `components/AppActionSheet/AppActionSheet.uvue` | 全局操作菜单组件。 |

## 管理员网页 `auth_web_front/`

### 入口、路由和状态

| 文件 | 业务职责 |
| --- | --- |
| `auth_web_front/src/main.ts` | 管理端 Vue 应用入口，挂载 Element Plus、Pinia、路由和全局样式。 |
| `auth_web_front/src/App.vue` | 管理端根组件，承载路由出口。 |
| `auth_web_front/src/router/index.ts` | 定义登录、未授权和管理员子路由，并通过路由守卫限制 ADMIN 访问。 |
| `auth_web_front/src/layouts/AdminLayout.vue` | 管理端侧边栏和页面布局，组织营收、车辆、反馈、折扣、计费等业务菜单。 |
| `auth_web_front/src/stores/auth.ts` | 管理员登录态、token、角色、用户名和用户 ID 的本地持久化。 |
| `auth_web_front/src/adminStatus.ts` | 管理端状态映射工具，统一反馈、车辆、审核等状态的标签含义。 |
| `auth_web_front/src/styles.css` | 管理端全局 UI token、页面卡片、表格、弹窗和状态样式。 |
| `auth_web_front/src/env.d.ts` | Vite/Vue 类型声明。 |

### API 和类型

| 文件 | 业务职责 |
| --- | --- |
| `auth_web_front/src/api/client.ts` | Axios 客户端，统一后端地址、认证头和错误处理。 |
| `auth_web_front/src/api/auth.ts` | 管理员登录和认证相关请求。 |
| `auth_web_front/src/api/admin.ts` | 管理端核心 API，覆盖车辆 CRUD、批量更新、反馈处理、折扣审核、营收统计、计费设置和车型文案。 |
| `auth_web_front/src/types/api.ts` | 管理端请求和响应类型定义，对齐后端 DTO。 |

### 页面和组件

| 文件 | 业务职责 |
| --- | --- |
| `auth_web_front/src/views/LoginView.vue` | 管理员登录页面。 |
| `auth_web_front/src/views/UnauthorizedView.vue` | 非管理员或无权限访问时的提示页面。 |
| `auth_web_front/src/views/admin/RevenueView.vue` | 营收统计页，展示收入、租期分布和趋势数据。 |
| `auth_web_front/src/views/admin/AnalyticsView.vue` | 管理看板分析页，展示经营概览、车辆和订单分析。 |
| `auth_web_front/src/views/admin/ScootersView.vue` | 车辆管理页，支持车辆列表、状态、可见性、坐标、费率和批量维护。 |
| `auth_web_front/src/views/admin/FeedbacksView.vue` | 反馈管理页，查看用户反馈、附件和处理状态。 |
| `auth_web_front/src/views/admin/HighPriorityIssuesView.vue` | 高优先级问题页，集中处理紧急或升级反馈。 |
| `auth_web_front/src/views/admin/DiscountVerificationsView.vue` | 折扣认证审核页，批准或驳回用户提交的资格材料。 |
| `auth_web_front/src/views/admin/BillingSettingsView.vue` | 计费设置页，维护长租倍率、折扣率和计费配置变更日志。 |
| `auth_web_front/src/views/admin/VehicleContentEditorView.vue` | 车型文案编辑页，维护前台展示的车辆描述内容。 |
| `auth_web_front/src/components/admin/DiscountOverviewCard.vue` | 计费设置中的折扣概览卡片。 |
| `auth_web_front/src/components/admin/LongRentDiscountDialog.vue` | 长租折扣配置弹窗。 |
| `auth_web_front/src/components/admin/SimpleDiscountRateDialog.vue` | 学生、老年、常旅客等简单折扣率配置弹窗。 |

### 测试和文档

| 文件 | 业务职责 |
| --- | --- |
| `auth_web_front/src/tests/accessibility/revenue.a11y.spec.ts` | 营收页无障碍测试。 |
| `auth_web_front/src/tests/accessibility/billing.a11y.spec.ts` | 计费设置页无障碍测试。 |
| `auth_web_front/src/tests/accessibility/feedbacks.a11y.spec.ts` | 反馈页无障碍测试。 |
| `auth_web_front/docs/ui-modernization.md` | 管理端 UI 现代化规范，约束颜色、表格、弹窗、状态标签和布局复用。 |

## 后端 `backend/`

### 启动和配置

| 文件 | 业务职责 |
| --- | --- |
| `backend/src/main/java/com/group12/backend/BackendApplication.java` | Spring Boot 后端启动入口。 |
| `backend/src/main/resources/application.yaml` | 后端默认配置，定义数据库、文件存储、计费和安全相关配置入口。 |
| `backend/init.sql` | 本地数据库初始化脚本，创建业务表并写入演示数据。 |
| `backend/src/main/java/com/group12/backend/config/WebConfig.java` | Web 层配置，注册拦截器和跨域等请求处理规则。 |
| `backend/src/main/java/com/group12/backend/config/OpenApiConfig.java` | Swagger/OpenAPI 文档配置。 |
| `backend/src/main/java/com/group12/backend/config/PasswordEncoderConfig.java` | 密码加密器配置。 |
| `backend/src/main/java/com/group12/backend/config/AsyncConfig.java` | 异步任务线程池配置，用于邮件等后台任务。 |
| `backend/src/main/java/com/group12/backend/config/DataInitializer.java` | 启动时初始化或修复业务数据。 |
| `backend/src/main/java/com/group12/backend/config/BillingProperties.java` | 计费规则默认参数配置。 |
| `backend/src/main/java/com/group12/backend/config/DiscountProperties.java` | 折扣策略默认参数配置。 |
| `backend/src/main/java/com/group12/backend/config/FeedbackStorageProperties.java` | 反馈图片本地存储配置。 |
| `backend/src/main/java/com/group12/backend/config/DiscountVerificationStorageProperties.java` | 折扣认证材料本地存储配置。 |
| `backend/src/main/java/com/group12/backend/config/PaymentCardBinLookupProperties.java` | 银行卡 BIN 查询服务和限流配置。 |

### Controller

| 文件 | 业务职责 |
| --- | --- |
| `backend/src/main/java/com/group12/backend/controller/AuthController.java` | 登录、认证检查和管理员登录态相关接口。 |
| `backend/src/main/java/com/group12/backend/controller/UserController.java` | 用户注册、资料查询和修改、个人订单、Guest 订单历史等用户接口。 |
| `backend/src/main/java/com/group12/backend/controller/ScooterController.java` | 用户端车辆查询、车辆位置，以及管理员车辆更新接口。 |
| `backend/src/main/java/com/group12/backend/controller/BookingController.java` | 预约创建、Guest 预约、支付、取消和结束骑行业务接口。 |
| `backend/src/main/java/com/group12/backend/controller/BookingExtensionController.java` | 订单延长租期接口。 |
| `backend/src/main/java/com/group12/backend/controller/TrajectoryController.java` | 骑行轨迹点上传和查询接口。 |
| `backend/src/main/java/com/group12/backend/controller/PaymentCardController.java` | 用户和 Guest 支付卡绑定、查询、删除、默认卡和 BIN 查询接口。 |
| `backend/src/main/java/com/group12/backend/controller/FeedbackController.java` | 用户反馈提交、图片上传、管理员反馈列表和状态处理接口。 |
| `backend/src/main/java/com/group12/backend/controller/AdminFeedbackController.java` | 管理员下载反馈附件接口。 |
| `backend/src/main/java/com/group12/backend/controller/DiscountVerificationController.java` | 用户提交折扣认证材料和查看个人认证记录接口。 |
| `backend/src/main/java/com/group12/backend/controller/AdminDiscountVerificationController.java` | 管理员折扣认证列表、附件下载、批准和驳回接口。 |
| `backend/src/main/java/com/group12/backend/controller/AdminController.java` | 管理员看板、营收、车辆批量维护、计费设置和日志接口。 |
| `backend/src/main/java/com/group12/backend/controller/SecurityController.java` | 账户安全设置读取和更新接口。 |
| `backend/src/main/java/com/group12/backend/controller/VehicleDescriptionController.java` | 车型介绍内容查询和管理员维护接口。 |

### Service 和业务实现

| 文件 | 业务职责 |
| --- | --- |
| `backend/src/main/java/com/group12/backend/service/impl/AuthServiceImpl.java` | 校验账号密码、生成登录结果并处理认证业务。 |
| `backend/src/main/java/com/group12/backend/service/impl/UserServiceImpl.java` | 注册用户、维护资料、修改账号信息并查询用户相关订单。 |
| `backend/src/main/java/com/group12/backend/service/impl/ScooterServiceImpl.java` | 查询可租车辆、维护车辆状态、坐标、费率、可见性和批量更新。 |
| `backend/src/main/java/com/group12/backend/service/impl/BookingServiceImpl.java` | 预约生命周期核心实现，包含创建、Guest 下单、支付、取消、结束骑行和结算。 |
| `backend/src/main/java/com/group12/backend/service/impl/BookingExtensionServiceImpl.java` | 延长租期的校验、时长更新和费用重算。 |
| `backend/src/main/java/com/group12/backend/service/impl/BillingServiceImpl.java` | 计费设置读取、更新、日志记录和长租/折扣参数维护。 |
| `backend/src/main/java/com/group12/backend/service/impl/DiscountServiceImpl.java` | 根据用户身份和计费规则计算订单折扣。 |
| `backend/src/main/java/com/group12/backend/service/impl/DiscountVerificationServiceImpl.java` | 折扣认证提交、审核状态流转和材料文件关联。 |
| `backend/src/main/java/com/group12/backend/service/impl/PaymentCardServiceImpl.java` | 支付卡绑定、默认卡、删除、Guest 卡管理和卡号展示脱敏。 |
| `backend/src/main/java/com/group12/backend/service/impl/PaymentCardBinLookupService.java` | 银行卡 BIN 查询和查询频控。 |
| `backend/src/main/java/com/group12/backend/service/impl/FeedbackServiceImpl.java` | 用户反馈创建、图片关联、优先级和管理员处理状态维护。 |
| `backend/src/main/java/com/group12/backend/service/impl/LocalFeedbackDocumentStorage.java` | 反馈图片本地保存和读取。 |
| `backend/src/main/java/com/group12/backend/service/impl/LocalDiscountDocumentStorage.java` | 折扣认证材料本地保存和读取。 |
| `backend/src/main/java/com/group12/backend/service/impl/TrajectoryServiceImpl.java` | 骑行轨迹点批量保存、订单权限校验和轨迹查询。 |
| `backend/src/main/java/com/group12/backend/service/impl/AdminServiceImpl.java` | 管理端营收、看板、热门日期和统计聚合。 |
| `backend/src/main/java/com/group12/backend/service/impl/SecurityServiceImpl.java` | 账户安全设置读取和更新。 |
| `backend/src/main/java/com/group12/backend/service/impl/VehicleDescriptionServiceImpl.java` | 车型介绍文案查询和更新。 |
| `backend/src/main/java/com/group12/backend/service/impl/EmailNotificationServiceImpl.java` | 预约确认等邮件通知发送。 |
| `backend/src/main/java/com/group12/backend/service/BillingRule.java` | 计费规则模型，承载价格倍率和折扣计算参数。 |
| `backend/src/main/java/com/group12/backend/service/UploadRateLimiter.java` | 限制反馈图片等上传频率，避免接口滥用。 |

### 定时任务、鉴权和横切逻辑

| 文件 | 业务职责 |
| --- | --- |
| `backend/src/main/java/com/group12/backend/task/BookingScheduler.java` | 定时处理超时未支付订单和到期订单，释放车辆并闭合订单状态。 |
| `backend/src/main/java/com/group12/backend/interceptor/AuthenticationInterceptor.java` | 解析 token 并注入用户身份，保护需要登录的 API。 |
| `backend/src/main/java/com/group12/backend/security/AdminAccessGuard.java` | 管理员权限校验，保护后台管理接口。 |
| `backend/src/main/java/com/group12/backend/annotation/LogAction.java` | 标记需要记录审计日志的业务操作。 |
| `backend/src/main/java/com/group12/backend/aspect/LogAspect.java` | 拦截 `LogAction` 并写入审计日志。 |
| `backend/src/main/java/com/group12/backend/exception/BusinessException.java` | 业务异常封装。 |
| `backend/src/main/java/com/group12/backend/exception/GlobalExceptionHandler.java` | 统一异常到 HTTP 响应的映射。 |
| `backend/src/main/java/com/group12/backend/exception/ErrorMessages.java` | 统一业务错误文案。 |

### Entity

| 文件 | 业务职责 |
| --- | --- |
| `backend/src/main/java/com/group12/backend/entity/User.java` | 用户账号、角色、资料和折扣身份。 |
| `backend/src/main/java/com/group12/backend/entity/Scooter.java` | 车辆基础信息、车型、状态、费率、位置和可见性。 |
| `backend/src/main/java/com/group12/backend/entity/Booking.java` | 预约订单，记录用户、车辆、时段、状态、费用、折扣和还车信息。 |
| `backend/src/main/java/com/group12/backend/entity/Payment.java` | 支付记录，关联订单金额和支付状态。 |
| `backend/src/main/java/com/group12/backend/entity/PaymentCard.java` | 用户或 Guest 的支付卡令牌、品牌、尾号和默认卡状态。 |
| `backend/src/main/java/com/group12/backend/entity/Feedback.java` | 用户反馈、优先级、处理状态、图片和管理员处理结果。 |
| `backend/src/main/java/com/group12/backend/entity/DiscountVerificationSubmission.java` | 折扣认证申请、材料路径、类型、状态和审核意见。 |
| `backend/src/main/java/com/group12/backend/entity/TrajectoryPoint.java` | 骑行 GPS 轨迹点。 |
| `backend/src/main/java/com/group12/backend/entity/LocationPoint.java` | 车辆或订单涉及的位置点。 |
| `backend/src/main/java/com/group12/backend/entity/BillingSettings.java` | 当前生效的计费和折扣配置。 |
| `backend/src/main/java/com/group12/backend/entity/BillingSettingsLog.java` | 计费配置变更日志。 |
| `backend/src/main/java/com/group12/backend/entity/VehicleDescription.java` | 车型展示文案。 |
| `backend/src/main/java/com/group12/backend/entity/AuditLog.java` | 关键业务操作审计记录。 |

### Repository

| 文件 | 业务职责 |
| --- | --- |
| `backend/src/main/java/com/group12/backend/repository/UserRepository.java` | 用户数据查询和持久化。 |
| `backend/src/main/java/com/group12/backend/repository/ScooterRepository.java` | 车辆数据查询和持久化。 |
| `backend/src/main/java/com/group12/backend/repository/BookingRepository.java` | 预约订单查询和持久化。 |
| `backend/src/main/java/com/group12/backend/repository/PaymentRepository.java` | 支付记录查询和持久化。 |
| `backend/src/main/java/com/group12/backend/repository/PaymentCardRepository.java` | 支付卡查询和持久化。 |
| `backend/src/main/java/com/group12/backend/repository/FeedbackRepository.java` | 反馈查询和持久化。 |
| `backend/src/main/java/com/group12/backend/repository/DiscountVerificationSubmissionRepository.java` | 折扣认证申请查询和持久化。 |
| `backend/src/main/java/com/group12/backend/repository/TrajectoryPointRepository.java` | 轨迹点查询和持久化。 |
| `backend/src/main/java/com/group12/backend/repository/LocationPointRepository.java` | 位置点查询和持久化。 |
| `backend/src/main/java/com/group12/backend/repository/BillingSettingsRepository.java` | 当前计费配置查询和持久化。 |
| `backend/src/main/java/com/group12/backend/repository/BillingSettingsLogRepository.java` | 计费配置变更日志查询和持久化。 |
| `backend/src/main/java/com/group12/backend/repository/VehicleDescriptionRepository.java` | 车型文案查询和持久化。 |
| `backend/src/main/java/com/group12/backend/repository/AuditLogRepository.java` | 审计日志查询和持久化。 |

### DTO、工具和测试

| 路径 | 业务职责 |
| --- | --- |
| `backend/src/main/java/com/group12/backend/dto/` | 后端 API 入参和出参对象，例如登录、注册、创建预约、支付、反馈、轨迹、营收统计、折扣认证等契约。 |
| `backend/src/main/java/com/group12/backend/util/` | 纯工具逻辑，例如预约时间窗口和格式化辅助。 |
| `backend/src/main/java/com/group12/backend/service/pricing/` | 订单价格计算相关的纯业务算法。 |
| `backend/src/test/java/` | 后端单元和集成测试，覆盖预约、支付卡、折扣、反馈、计费、安全、邮件和并发等业务。 |

## 文档和测试资产

| 文件或目录 | 业务职责 |
| --- | --- |
| `docs/accessibility-checklist.md` | 无障碍测试和人工验收清单，覆盖管理端和移动端交互。 |
| `docs/project-structure.md` | 本文档，维护项目结构与业务职责说明。 |
| `testing_sys/sprint*/` | Sprint 系统测试用例、执行脚本和报告。 |
| `testing_scripts/sprint*/` | Sprint 测试辅助脚本。 |

## 维护约定

新增页面、Controller、Service、管理端视图或重要测试资产时，请在本文档补充一行“路径 -> 业务职责”。如果文件只是 DTO、样式、资源或平台生成内容，可按目录归类，避免把业务说明写成低价值文件清单。
