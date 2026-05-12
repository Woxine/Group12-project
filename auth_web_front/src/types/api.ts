/** 后端统一响应包装 / Shared backend response envelope. */
export interface ApiEnvelope<T> {
  data: T;
  total?: number;
}

/** 登录成功后返回的管理员会话信息 / Admin session information returned after login. */
export interface LoginResponse {
  token: string;
  userId: string;
  role: string;
  name: string;
}

/** 收入统计接口返回值 / Revenue statistics API response. */
export interface RevenueStats {
  totalRevenue: number;
  totalOrders: number;
  averageOrderValue: number;
}

/** 按租期拆分的收入数据 / Revenue grouped by rental duration. */
export interface DurationRevenue {
  durationType: string;
  totalRevenue: number;
  totalOrders: number;
}

/** 管理端车辆记录 / Admin scooter record. */
export interface Scooter {
  id: number;
  status: string;
  type: 'GEN1' | 'GEN2' | 'GEN3' | 'GEN3PRO';
  locationLat: number | null;
  locationLng: number | null;
  hourRate: number;
  locationName: string;
  /** false 表示客户端地图/列表隐藏 / false = hidden from client app map/list. */
  visible?: boolean;
  longRentThresholdHours?: number;
  extraLongRentThresholdHours?: number;
  longRentHourRateMultiplier?: number;
  extraLongRentHourRateMultiplier?: number;
}

/** 按车型批量更新车辆的请求体 / Request payload for bulk scooter updates by type. */
export interface BulkScooterUpdatePayload {
  type: 'GEN1' | 'GEN2' | 'GEN3' | 'GEN3PRO';
  hour_rate?: number;
  status?: 'AVAILABLE' | 'RESERVED' | 'RENTED' | 'MAINTENANCE';
  visible?: boolean;
  confirm_risky?: boolean;
}

/** 批量更新预览结果 / Bulk update preview result. */
export interface ScooterBulkPreview {
  type: string;
  matchedCount: number;
  hiddenCount: number;
  statusBreakdown: Record<string, number>;
  risky: boolean;
  riskWarnings: string[];
}

/** 批量更新应用结果 / Bulk update apply result. */
export interface ScooterBulkApplyResult {
  type: string;
  matchedCount: number;
  updatedCount: number;
  hiddenCount: number;
  statusBreakdown: Record<string, number>;
  risky: boolean;
  riskWarnings: string[];
}

/** 管理端计费与折扣设置 / Admin billing and discount settings. */
export interface BillingSettings {
  longRentThresholdHours: number;
  extraLongRentThresholdHours: number;
  longRentHourRateMultiplier: number;
  extraLongRentHourRateMultiplier: number;
  studentDiscountRate: number;
  seniorDiscountRate: number;
  frequentDiscountRate: number;
  updatedAt?: string;
}

/** 计费设置变更审计日志 / Billing settings change audit log. */
export interface BillingSettingsLog {
  id: number;
  oldLongRentHourRateMultiplier: number;
  newLongRentHourRateMultiplier: number;
  oldExtraLongRentHourRateMultiplier: number;
  newExtraLongRentHourRateMultiplier: number;
  operatorUserId?: number | null;
  createdAt: string;
}

/** 用户反馈记录 / User feedback record. */
export interface FeedbackItem {
  id: number;
  userId: number | null;
  scooterId: number | null;
  bookingId: number | null;
  content: string;
  priority: string;
  resolved: boolean;
  escalated: boolean;
  escalatedTo: string | null;
  escalationStatus: string | null;
  imageMimeType: string | null;
}

/** 反馈优先级工作流请求体 / Feedback priority workflow payload. */
export interface ProcessFeedbackPayload {
  action: "DIRECT_HANDLE" | "ESCALATE" | "RESOLVE";
  escalateTo?: string;
  note?: string;
}

/** 反馈处理后的后端响应 / Backend response after processing feedback. */
export interface EscalatedFeedbackResponse {
  feedbackId: number;
  priority: string;
  escalated: boolean;
  escalatedTo?: string | null;
  status: string;
}

/** 高优先级问题列表项 / High-priority issue list item. */
export interface HighPriorityIssue {
  feedbackId: number;
  userId: number | null;
  scooterId: number | null;
  content: string;
  priority: string;
  escalated: boolean;
  escalatedTo?: string | null;
  resolved: boolean;
}

/** 热门租赁日期排行项 / Popular rental date ranking item. */
export interface PopularRentalDate {
  date: string;
  rank: number;
  orderCount: number;
  revenue: number;
}

/** 学生/老人折扣认证申请记录 / Student or senior discount verification submission. */
export interface DiscountVerificationSubmission {
  id: number;
  userId: number;
  type: "STUDENT" | "SENIOR";
  status: "PENDING" | "APPROVED" | "REJECTED";
  originalFilename: string;
  mimeType: string;
  sizeBytes: number;
  storagePath: string;
  submittedAt: string;
  reviewedAt: string | null;
  reviewerUserId: number | null;
  rejectReason: string | null;
  version: number;
}

/** 仪表盘订单统计 / Dashboard order statistics. */
export interface OrderStats {
  totalOrders: number;
  validOrders: number;
  cancelledOrders: number;
  cancellationRate: number;
}

/** 仪表盘车辆统计 / Dashboard vehicle statistics. */
export interface VehicleStats {
  totalScooters: number;
  rentedScooters: number;
  maintenanceScooters: number;
  availableScooters: number;
  usageRate: number;
}

/** 仪表盘故障反馈统计 / Dashboard fault feedback statistics. */
export interface FaultStats {
  totalFeedbacks: number;
  resolvedFeedbacks: number;
  unresolvedFeedbacks: number;
  priorityDistribution: Record<string, number>;
}

/** 仪表盘每日趋势点 / Dashboard daily trend point. */
export interface DailyTrendPoint {
  date: string;
  orderCount: number;
  revenue: number;
}

/** 仪表盘总览聚合数据 / Aggregated dashboard overview data. */
export interface DashboardOverview {
  orderStats: OrderStats;
  revenueStats: RevenueStats;
  vehicleStats: VehicleStats;
  faultStats: FaultStats;
  dailyTrend: DailyTrendPoint[];
}

/** 车辆类型展示文案 / Vehicle type display copy. */
export interface VehicleDescription {
  id: number;
  vehicleType: string;
  displayName: string;
  subtitle: string;
  description: string;
  rangeText: string;
  speedText: string;
  motorText: string;
  advice: string;
}

/** 批量创建车辆的单行输入 / One row in a batch scooter creation request. */
export interface BatchCreateScooterItem {
  type: string;
  status: string;
  hour_rate: number;
  count: number;
  location_lat?: number;
  location_lng?: number;
  location_name?: string;
}

/** 批量创建车辆请求体 / Batch scooter creation payload. */
export interface BatchCreateScooterPayload {
  scooters: BatchCreateScooterItem[];
}
