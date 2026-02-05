# 数据库架构与设计说明 (Database Schema Design)

本文档描述了 Group12 共享滑板车（Scooter Rental）系统的数据库设计结构。

## 1. 实体关系图 (ER Diagram 逻辑)

*   **User** (1) —— (N) **Booking**
*   **User** (1) —— (N) **Feedback**
*   **Scooter** (1) —— (N) **Booking**
*   **Scooter** (1) —— (N) **Feedback**
*   **Scooter** (N) —— (0..1) **LocationPoint**
*   **Booking** (1) —— (0..1) **Payment**

---

## 2. 详细数据表设计

### 2.1 用户表 (`users`)
**用途**：存储系统所有用户（顾客和员工）。

| 字段名 | 类型 (Java) | 类型 (SQL) | 约束 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `Long` | `BIGINT` | **PK**, Auto Inc | 用户唯一ID |
| `email` | `String` | `VARCHAR(255)` | **UK**, Not Null | 登录邮箱（唯一） |
| `password`| `String` | `VARCHAR(255)` | Not Null | 加密后的密码 |
| `role` | `String` | `VARCHAR` | Def: 'CUSTOMER' | 角色: `CUSTOMER`, `STAFF`, `ADMIN` |
| `student` | `Boolean` | `TINYINT(1)` | Def: 0 | 是否学生（用于计算优惠） |
| `age` | `Integer` | `INT` | | 年龄 |

### 2.2 滑板车表 (`scooters`)
**用途**：管理车辆资产、位置和费率。

| 字段名 | 类型 (Java) | 类型 (SQL) | 约束 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `Long` | `BIGINT` | **PK**, Auto Inc | 滑板车编号 |
| `status` | `String` | `VARCHAR` | Not Null | 状态: `AVAILABLE`, `RENTED`, `MAINTENANCE` |
| `location_lat` | `Double` | `DECIMAL(10,8)` | | 当前纬度 |
| `location_lng` | `Double` | `DECIMAL(11,8)` | | 当前经度 |
| `hour_rate` | `BigDecimal` | `DECIMAL` | Not Null | 时租费率 |
| `location_point_id`| `LocationPoint` | `BIGINT` | **FK** | 当前所在的站点ID |

### 2.3 订单表 (`bookings`)
**用途**：核心业务表，记录租赁全过程。

| 字段名 | 类型 (Java) | 类型 (SQL) | 约束 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `Long` | `BIGINT` | **PK**, Auto Inc | 订单ID |
| `user_id` | `User` | `BIGINT` | **FK** | 关联用户 |
| `scooter_id` | `Scooter` | `BIGINT` | **FK** | 关联滑板车 |
| `start_time` | `LocalDateTime` | `DATETIME` | Not Null | 开始时间 |
| `end_time` | `LocalDateTime` | `DATETIME` | Nullable | 结束时间 |
| `duration_hours`| `Double` | `FLOAT` | | 时长 |
| `total_price` | `BigDecimal` | `DECIMAL` | | 总价 |
| `status` | `String` | `VARCHAR` | Not Null | `CONFIRMED`, `COMPLETED`, `CANCELLED` |

### 2.4 支付表 (`payments`)
**用途**：记录交易资金流，与订单强关联。

| 字段名 | 类型 (Java) | 类型 (SQL) | 约束 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `Long` | `BIGINT` | **PK**, Auto Inc | 支付流水号 |
| `booking_id` | `Booking` | `BIGINT` | **FK**, **UK** | 关联订单 |
| `amount` | `BigDecimal` | `DECIMAL` | Not Null | 支付金额 |
| `payment_method`| `String` | `VARCHAR` | | `CREDIT_CARD`, `PAYPAL` |
| `timestamp` | `LocalDateTime` | `DATETIME` | Not Null | 支付时间 |

### 2.5 反馈表 (`feedbacks`)
**用途**：客服系统，用户提交报修或评价。

| 字段名 | 类型 (Java) | 类型 (SQL) | 约束 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `Long` | `BIGINT` | **PK**, Auto Inc | 反馈ID |
| `user_id` | `User` | `BIGINT` | **FK** | 提交人 |
| `scooter_id` | `Scooter` | `BIGINT` | **FK** | 关联车辆 |
| `content` | `String` | `TEXT` | Not Null | 内容 |
| `priority` | `String` | `VARCHAR` | Def: 'LOW' | 优先级 |
| `resolved` | `Boolean` | `TINYINT` | Def: 0 | 处理状态 |

### 2.6 站点表 (`location_points`)
**用途**：定义固定停车点或热点区域。

| 字段名 | 类型 (Java) | 类型 (SQL) | 约束 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `Long` | `BIGINT` | **PK**, Auto Inc | 站点ID |
| `name` | `String` | `VARCHAR` | Not Null | 站点名称 |
| `lat` | `Double` | `DECIMAL` | | 站点纬度 |
| `lng` | `Double` | `DECIMAL` | | 站点经度 |
