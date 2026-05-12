import { http } from "./client";
import type {
  ApiEnvelope,
  DashboardOverview,
  BillingSettings,
  BillingSettingsLog,
  BatchCreateScooterPayload,
  DiscountVerificationSubmission,
  DurationRevenue,
  EscalatedFeedbackResponse,
  FeedbackItem,
  BulkScooterUpdatePayload,
  HighPriorityIssue,
  PopularRentalDate,
  ProcessFeedbackPayload,
  RevenueStats,
  Scooter,
  ScooterBulkApplyResult,
  ScooterBulkPreview,
  VehicleDescription
} from "@/types/api";

/** 收入与仪表盘统计接口 / Revenue and dashboard statistics APIs. */
/** 获取指定日期范围的收入统计 / Fetch revenue statistics for the selected date range. */
export async function getRevenueStats(params: { start_date?: string; end_date?: string }) {
  const response = await http.get<ApiEnvelope<RevenueStats>>("/api/v1/admin/revenue", { params });
  return response.data.data;
}

/** 获取指定日期范围内按租期分组的收入 / Fetch revenue grouped by rental duration for the selected date range. */
export async function getRevenueByDuration(params: { start_date?: string; end_date?: string }) {
  const response = await http.get<ApiEnvelope<DurationRevenue[]>>("/api/v1/admin/revenue/duration", { params });
  return response.data.data;
}

/** 获取本周按租期分组的收入 / Fetch this week's revenue grouped by rental duration. */
export async function getWeeklyRevenueByDuration() {
  const response = await http.get<ApiEnvelope<DurationRevenue[]>>("/api/v1/admin/revenue/duration-week");
  return response.data.data;
}

/** 获取管理端仪表盘概览数据 / Fetch dashboard overview data for the admin panel. */
export async function getDashboardOverview(params: { start_date?: string; end_date?: string }) {
  const response = await http.get<ApiEnvelope<DashboardOverview>>("/api/v1/admin/dashboard/overview", { params });
  return response.data.data;
}

/** 车辆管理接口 / Scooter management APIs. */
/** 获取管理端车辆列表，包括客户端隐藏车辆 / Fetch the admin scooter list, including vehicles hidden from the client app. */
export async function getAdminScooters(params: { status?: string; page?: number; size?: number }) {
  const response = await http.get<{ data: Scooter[]; total: number }>("/api/v1/admin/scooters", { params });
  return response.data;
}

/** 创建新的车辆记录 / Create a new scooter record. */
export async function createScooter(payload: {
  status?: string;
  type?: string;
  hour_rate: number;
  location_lat?: number;
  location_lng?: number;
  location_name?: string;
  location_point_id?: number;
}) {
  const response = await http.post<ApiEnvelope<Scooter>>("/api/v1/admin/scooters", payload);
  return response.data.data;
}

/** 删除指定车辆 / Delete the specified scooter. */
export async function deleteScooter(scooterId: number) {
  await http.delete(`/api/v1/admin/scooters/${scooterId}`);
}

/** 更新指定车辆信息 / Update the specified scooter details. */
export async function updateScooter(
  scooterId: number,
  payload: {
    type?: string;
    status?: string;
    hour_rate?: number;
    location_lat?: number;
    location_lng?: number;
    visible?: boolean;
  }
) {
  const response = await http.put<ApiEnvelope<Scooter>>(`/api/v1/admin/scooters/${scooterId}`, payload);
  return response.data.data;
}

/** 预览按车型批量更新车辆的影响 / Preview the impact of a bulk scooter update by type. */
export async function previewBulkUpdateByType(payload: BulkScooterUpdatePayload) {
  const response = await http.post<ApiEnvelope<ScooterBulkPreview>>("/api/v1/admin/scooters/bulk-by-type/preview", payload);
  return response.data.data;
}

/** 应用按车型批量更新车辆 / Apply a bulk scooter update by type. */
export async function applyBulkUpdateByType(payload: BulkScooterUpdatePayload) {
  const response = await http.post<ApiEnvelope<ScooterBulkApplyResult>>("/api/v1/admin/scooters/bulk-by-type/apply", payload);
  return response.data.data;
}

/** 批量创建车辆 / Batch create scooters. */
export async function batchCreateScooters(payload: BatchCreateScooterPayload) {
  const response = await http.post<{ data: Scooter[]; totalCreated: number }>("/api/v1/admin/scooters/batch", payload);
  return response.data;
}

/** 用户反馈与高优先级流程接口 / Feedback and high-priority workflow APIs. */
/** 获取用户反馈列表 / Fetch user feedback records. */
export async function getFeedbacks(params: {
  resolved?: boolean;
  priority?: string;
  page?: number;
  size?: number;
}) {
  const response = await http.get<{ data: FeedbackItem[]; total: number }>("/api/v1/feedbacks", { params });
  return response.data;
}

/** 更新反馈处理状态 / Update a feedback item's processing status. */
export async function updateFeedback(feedbackId: number, status: string) {
  const response = await http.put<ApiEnvelope<FeedbackItem>>(`/api/v1/feedbacks/${feedbackId}`, { status });
  return response.data.data;
}

/** 按优先级流程处理反馈 / Process feedback through the priority workflow. */
export async function processFeedbackByPriority(feedbackId: number, payload: ProcessFeedbackPayload) {
  const response = await http.put<ApiEnvelope<EscalatedFeedbackResponse>>(
    `/api/v1/feedbacks/${feedbackId}/process-priority`,
    payload
  );
  return response.data.data;
}

/** 获取高优先级问题列表 / Fetch high-priority issue records. */
export async function getHighPriorityIssues(params: {
  escalated?: boolean;
  page?: number;
  size?: number;
}) {
  const response = await http.get<{ data: HighPriorityIssue[]; total: number }>("/api/v1/feedbacks/high-priority", { params });
  return response.data;
}

/** 获取反馈附件的本地预览地址 / Fetch a local preview URL for a feedback attachment. */
export async function getFeedbackFileUrl(id: number): Promise<string> {
  const response = await http.get(`/api/v1/admin/feedbacks/${id}/file`, {
    responseType: "blob"
  });
  return URL.createObjectURL(response.data);
}

/** 折扣资格审核接口 / Discount verification review APIs. */
/** 获取折扣认证申请列表 / Fetch discount verification submissions. */
export async function getDiscountVerifications(params: {
  status?: "PENDING" | "APPROVED" | "REJECTED";
  type?: "STUDENT" | "SENIOR";
  page?: number;
  size?: number;
}) {
  const response = await http.get<{ data: DiscountVerificationSubmission[]; total: number }>(
    "/api/v1/admin/discount-verifications",
    { params }
  );
  return response.data;
}

/** 通过折扣认证申请 / Approve a discount verification submission. */
export async function approveDiscountVerification(id: number) {
  const response = await http.post<ApiEnvelope<DiscountVerificationSubmission>>(
    `/api/v1/admin/discount-verifications/${id}/approve`
  );
  return response.data.data;
}

/** 拒绝折扣认证申请并提交原因 / Reject a discount verification submission with a reason. */
export async function rejectDiscountVerification(id: number, reason: string) {
  const response = await http.post<ApiEnvelope<DiscountVerificationSubmission>>(
    `/api/v1/admin/discount-verifications/${id}/reject`,
    { reason }
  );
  return response.data.data;
}

/** 获取折扣认证附件的本地预览地址 / Fetch a local preview URL for a discount verification attachment. */
export async function getDiscountVerificationFileUrl(id: number): Promise<string> {
  const response = await http.get(`/api/v1/admin/discount-verifications/${id}/file`, {
    responseType: "blob"
  });
  return URL.createObjectURL(response.data);
}

/** 计费设置与折扣配置接口 / Billing settings and discount configuration APIs. */
/** 获取计费设置 / Fetch billing settings. */
export async function fetchBillingSettings() {
  const response = await http.get<ApiEnvelope<BillingSettings>>("/api/v1/admin/billing-settings");
  return response.data.data;
}

/** 更新计费设置 / Update billing settings. */
export async function updateBillingSettings(payload: {
  longRentHourRateMultiplier?: number;
  extraLongRentHourRateMultiplier?: number;
  studentDiscountRate?: number;
  seniorDiscountRate?: number;
  frequentDiscountRate?: number;
}) {
  const response = await http.put<ApiEnvelope<BillingSettings>>("/api/v1/admin/billing-settings", payload);
  return response.data.data;
}

/** 获取计费设置变更日志 / Fetch billing settings change logs. */
export async function fetchBillingSettingsLogs(limit = 20) {
  const response = await http.get<{ data: BillingSettingsLog[]; total: number }>("/api/v1/admin/billing-settings/logs", {
    params: { limit }
  });
  return response.data;
}

/** 获取指定日期范围内的热门租赁日期 / Fetch popular rental dates for the selected date range. */
export async function getPopularRentalDates(params: { start_date?: string; end_date?: string }) {
  const response = await http.get<ApiEnvelope<PopularRentalDate[]>>("/api/v1/admin/revenue/popular-dates", { params });
  return response.data.data;
}

/** 车辆文案配置接口 / Vehicle content configuration APIs. */
/** 获取车辆类型描述配置 / Fetch vehicle type description settings. */
export async function getVehicleDescriptions() {
  const response = await http.get<ApiEnvelope<VehicleDescription[]>>("/api/v1/vehicles");
  return response.data.data;
}

/** 更新指定车辆类型的描述配置 / Update description settings for the specified vehicle type. */
export async function updateVehicleDescription(
  type: string,
  payload: {
    display_name?: string;
    subtitle?: string;
    description?: string;
    range_text?: string;
    speed_text?: string;
    motor_text?: string;
    advice?: string;
  }
) {
  const response = await http.put<ApiEnvelope<VehicleDescription>>(`/api/v1/vehicles/${type}`, payload);
  return response.data.data;
}

// --- Announcements ---

export async function getAnnouncements() {
  const response = await http.get<{ data: any[] }>("/api/v1/admin/announcements");
  return response.data;
}

export async function createAnnouncement(data: {
  title: string;
  content: string;
  type?: string;
  startTime?: string;
  endTime?: string;
  enabled?: boolean;
}) {
  const response = await http.post<{ data: any }>("/api/v1/admin/announcements", data);
  return response.data;
}

export async function updateAnnouncement(id: number, data: {
  title?: string;
  content?: string;
  type?: string;
  startTime?: string;
  endTime?: string;
  enabled?: boolean;
}) {
  const response = await http.put<{ data: any }>(`/api/v1/admin/announcements/${id}`, data);
  return response.data;
}

export async function deleteAnnouncement(id: number) {
  const response = await http.delete<{ message: string }>(`/api/v1/admin/announcements/${id}`);
  return response.data;
}
