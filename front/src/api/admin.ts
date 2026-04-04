import { http } from "./client";
import type {
  ApiEnvelope,
  DashboardOverview,
  DurationRevenue,
  FeedbackItem,
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

export async function updateScooter(
  scooterId: number,
  payload: {
    status?: string;
    hour_rate?: number;
    location_lat?: number;
    location_lng?: number;
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
