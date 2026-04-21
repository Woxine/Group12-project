import { http } from "./client";
import type {
  ApiEnvelope,
  DashboardOverview,
  BillingSettings,
  BillingSettingsLog,
  DiscountVerificationSubmission,
  DurationRevenue,
  EscalatedFeedbackResponse,
  FeedbackItem,
  HighPriorityIssue,
  PopularRentalDate,
  ProcessFeedbackPayload,
  RevenueStats,
  Scooter
} from "@/types/api";

export async function getRevenueStats(params: { start_date?: string; end_date?: string }) {
  const response = await http.get<ApiEnvelope<RevenueStats>>("/api/v1/admin/revenue", { params });
  return response.data.data;
}

export async function getRevenueByDuration(params: { start_date?: string; end_date?: string }) {
  const response = await http.get<ApiEnvelope<DurationRevenue[]>>("/api/v1/admin/revenue/duration", { params });
  return response.data.data;
}

export async function getWeeklyRevenueByDuration() {
  const response = await http.get<ApiEnvelope<DurationRevenue[]>>("/api/v1/admin/revenue/duration-week");
  return response.data.data;
}

export async function getDashboardOverview(params: { start_date?: string; end_date?: string }) {
  const response = await http.get<ApiEnvelope<DashboardOverview>>("/api/v1/admin/dashboard/overview", { params });
  return response.data.data;
}

export async function getScooters(params: { status?: string; page?: number; size?: number }) {
  const response = await http.get<{ data: Scooter[]; total: number }>("/api/v1/scooters", { params });
  return response.data;
}

/** Admin list: includes vehicles hidden from the client app. */
export async function getAdminScooters(params: { status?: string; page?: number; size?: number }) {
  const response = await http.get<{ data: Scooter[]; total: number }>("/api/v1/admin/scooters", { params });
  return response.data;
}

export async function createScooter(payload: {
  status?: string;
  hour_rate: number;
  location_lat?: number;
  location_lng?: number;
  location_name?: string;
  location_point_id?: number;
}) {
  const response = await http.post<ApiEnvelope<Scooter>>("/api/v1/admin/scooters", payload);
  return response.data.data;
}

export async function deleteScooter(scooterId: number) {
  await http.delete(`/api/v1/admin/scooters/${scooterId}`);
}

export async function updateScooter(
  scooterId: number,
  payload: {
    status?: string;
    hour_rate?: number;
    location_lat?: number;
    location_lng?: number;
    visible?: boolean;
  }
) {
  const response = await http.put<ApiEnvelope<Scooter>>(`/api/v1/scooters/${scooterId}`, payload);
  return response.data.data;
}

export async function getFeedbacks(params: {
  resolved?: boolean;
  priority?: string;
  page?: number;
  size?: number;
}) {
  const response = await http.get<{ data: FeedbackItem[]; total: number }>("/api/v1/feedbacks", { params });
  return response.data;
}

export async function updateFeedback(feedbackId: number, status: string) {
  const response = await http.put<ApiEnvelope<FeedbackItem>>(`/api/v1/feedbacks/${feedbackId}`, { status });
  return response.data.data;
}

export async function processFeedbackByPriority(feedbackId: number, payload: ProcessFeedbackPayload) {
  const response = await http.put<ApiEnvelope<EscalatedFeedbackResponse>>(
    `/api/v1/feedbacks/${feedbackId}/process-priority`,
    payload
  );
  return response.data.data;
}

export async function getHighPriorityIssues(params: {
  escalated?: boolean;
  page?: number;
  size?: number;
}) {
  const response = await http.get<{ data: HighPriorityIssue[]; total: number }>("/api/v1/feedbacks/high-priority", { params });
  return response.data;
}

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

export async function approveDiscountVerification(id: number) {
  const response = await http.post<ApiEnvelope<DiscountVerificationSubmission>>(
    `/api/v1/admin/discount-verifications/${id}/approve`
  );
  return response.data.data;
}

export async function rejectDiscountVerification(id: number, reason: string) {
  const response = await http.post<ApiEnvelope<DiscountVerificationSubmission>>(
    `/api/v1/admin/discount-verifications/${id}/reject`,
    { reason }
  );
  return response.data.data;
}

export async function fetchBillingSettings() {
  const response = await http.get<ApiEnvelope<BillingSettings>>("/api/v1/admin/billing-settings");
  return response.data.data;
}

export async function updateBillingMultipliers(payload: {
  longRentHourRateMultiplier: number;
  extraLongRentHourRateMultiplier: number;
}) {
  const response = await http.put<ApiEnvelope<BillingSettings>>("/api/v1/admin/billing-settings", payload);
  return response.data.data;
}

export async function fetchBillingSettingsLogs(limit = 20) {
  const response = await http.get<{ data: BillingSettingsLog[]; total: number }>("/api/v1/admin/billing-settings/logs", {
    params: { limit }
  });
  return response.data;
}

export async function getPopularRentalDatesThisWeek() {
  const response = await http.get<ApiEnvelope<PopularRentalDate[]>>("/api/v1/admin/revenue/popular-dates-week");
  return response.data.data;
}
