# 数据库架构与设计说明 (Database Schema Design)

本文档描述了 Group12 共享滑板车（Scooter Rental）系统当前使用的数据库结构。

说明：

- `backend/init.sql` 是团队统一使用的可执行初始化脚本。
- 本文档是对当前表结构的人工可读说明，应与实体定义和 `init.sql` 保持一致。

## 1. 实体关系图 (ER Diagram 逻辑)

- **User** (1) —— (N) **Booking**
- **User** (1) —— (N) **Feedback**
- **Scooter** (1) —— (N) **Booking**
- **Scooter** (1) —— (N) **Feedback**
- **Scooter** (N) —— (0..1) **LocationPoint**
- **Booking** (1) —— (0..1) **Payment**

---

## 2. 详细数据表设计

### 2.1 用户表 (`users`)

**用途**：存储系统所有用户（顾客和员工）。

| 字段名 | 类型 (Java) | 类型 (SQL) | 约束 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `Long` | `BIGINT` | **PK**, Auto Inc | 用户唯一ID |
| `email` | `String` | `VARCHAR(255)` | **UK**, Not Null | 登录邮箱（唯一） |
| `password` | `String` | `VARCHAR(255)` | Not Null | 加密后的密码 |
| `name` | `String` | `VARCHAR(255)` | Not Null | 用户姓名 |
| `role` | `String` | `VARCHAR(20)` | Def: 'CUSTOMER' | 角色: `CUSTOMER`, `STAFF`, `ADMIN` |
| `is_student` | `Boolean` | `TINYINT(1)` | Def: 0 | 是否学生（用于计算优惠） |
| `age` | `Integer` | `INT` | | 年龄 |

### 2.2 滑板车表 (`scooters`)

**用途**：管理车辆资产、位置和费率。

| 字段名 | 类型 (Java) | 类型 (SQL) | 约束 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `Long` | `BIGINT` | **PK**, Auto Inc | 滑板车编号 |
| `status` | `String` | `VARCHAR(255)` | Not Null | 状态: `AVAILABLE`, `RENTED`, `MAINTENANCE` |
| `location_lat` | `Double` | `DOUBLE` | | 当前纬度 |
| `location_lng` | `Double` | `DOUBLE` | | 当前经度 |
| `hour_rate` | `BigDecimal` | `DECIMAL(38,2)` | Not Null | 时租费率 |
| `location_point_id` | `LocationPoint` | `BIGINT` | **FK** | 当前所在的站点ID |

### 2.3 订单表 (`bookings`)

**用途**：核心业务表，记录租赁全过程。

| 字段名 | 类型 (Java) | 类型 (SQL) | 约束 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `Long` | `BIGINT` | **PK**, Auto Inc | 订单ID |
| `user_id` | `User` | `BIGINT` | **FK** | 关联用户 |
| `scooter_id` | `Scooter` | `BIGINT` | **FK** | 关联滑板车 |
| `start_time` | `LocalDateTime` | `DATETIME(6)` | Not Null | 开始时间 |
| `end_time` | `LocalDateTime` | `DATETIME(6)` | Nullable | 结束时间 |
| `duration_hours` | `Double` | `DOUBLE` | | 时长 |
| `total_price` | `BigDecimal` | `DECIMAL(38,2)` | | 总价 |
| `status` | `String` | `VARCHAR(255)` | Not Null | `CONFIRMED`, `COMPLETED`, `CANCELLED` |
| `start_lat` | `Double` | `DOUBLE` | Nullable | 开始用车时手机纬度（路线展示） |
| `start_lng` | `Double` | `DOUBLE` | Nullable | 开始用车时手机经度 |
| `end_lat` | `Double` | `DOUBLE` | Nullable | 结束还车时手机纬度 |
| `end_lng` | `Double` | `DOUBLE` | Nullable | 结束还车时手机经度 |

### 2.4 支付表 (`payments`)

**用途**：记录交易资金流，与订单强关联。

| 字段名 | 类型 (Java) | 类型 (SQL) | 约束 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `Long` | `BIGINT` | **PK**, Auto Inc | 支付流水号 |
| `booking_id` | `Booking` | `BIGINT` | **FK**, **UK** | 关联订单 |
| `amount` | `BigDecimal` | `DECIMAL(38,2)` | Not Null | 支付金额 |
| `payment_method` | `String` | `VARCHAR(255)` | | `CREDIT_CARD`, `PAYPAL` |
| `timestamp` | `LocalDateTime` | `DATETIME(6)` | Not Null | 支付时间 |

### 2.5 反馈表 (`feedbacks`)

**用途**：客服系统，用户提交报修或评价。

| 字段名 | 类型 (Java) | 类型 (SQL) | 约束 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `Long` | `BIGINT` | **PK**, Auto Inc | 反馈ID |
| `user_id` | `User` | `BIGINT` | **FK** | 提交人 |
| `scooter_id` | `Scooter` | `BIGINT` | **FK** | 关联车辆 |
| `content` | `String` | `TEXT` | Not Null | 内容 |
| `priority` | `String` | `VARCHAR(20)` | Def: 'LOW' | 优先级 |
| `resolved` | `Boolean` | `BIT(1)` | Def: 0 | 处理状态 |

### 2.6 站点表 (`location_points`)

**用途**：定义固定停车点或热点区域。

| 字段名 | 类型 (Java) | 类型 (SQL) | 约束 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `Long` | `BIGINT` | **PK**, Auto Inc | 站点ID |
| `name` | `String` | `VARCHAR` | Not Null | 站点名称 |
| `lat` | `Double` | `DOUBLE` | | 站点纬度 |
| `lng` | `Double` | `DOUBLE` | | 站点经度 |

### 2.7 审计日志表 (`audit_logs`)

**用途**：记录关键操作的审计信息，便于追踪业务变更与回滚参考。

| 字段名 | 类型 (Java) | 类型 (SQL) | 约束 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `Long` | `BIGINT` | **PK**, Auto Inc | 审计日志ID |
| `user_id` | `Long` | `BIGINT` | Nullable | 操作用户ID |
| `action` | `String` | `VARCHAR(255)` | Nullable | 操作类型，如 `CREATE_BOOKING` |
| `entity_name` | `String` | `VARCHAR(255)` | Nullable | 被操作实体名，如 `Booking` |
| `entity_id` | `String` | `VARCHAR(255)` | Nullable | 被操作实体ID |
| `request_params` | `String` | `TEXT` | Nullable | 请求参数快照，通常为 JSON 文本 |
| `timestamp` | `LocalDateTime` | `DATETIME(6)` | Nullable | 操作时间 |

---

## 3. 初始化脚本与演示数据

当前 `backend/init.sql` 除了创建以上表结构外，还会初始化一组便于联调的基础数据：

- `location_points`：3 条站点演示数据
- `scooters`：5 条滑板车演示数据

说明：

- 初始化脚本默认不插入用户数据。
- 如果需要手动插入默认用户，请使用 BCrypt 哈希后的密码，因为后端登录逻辑不会接受明文密码。
