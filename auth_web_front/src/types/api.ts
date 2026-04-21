export interface ApiEnvelope<T> {
  data: T;
  total?: number;
}

export interface LoginResponse {
  token: string;
  userId: string;
  role: string;
  name: string;
}

export interface RevenueStats {
  totalRevenue: number;
  totalOrders: number;
  averageOrderValue: number;
}

export interface DurationRevenue {
  durationType: string;
  totalRevenue: number;
  totalOrders: number;
}

export interface Scooter {
  id: number;
  status: string;
  locationLat: number | null;
  locationLng: number | null;
  hourRate: number;
  locationName: string;
  /** false = hidden from client app map/list */
  visible?: boolean;
  longRentThresholdHours?: number;
  extraLongRentThresholdHours?: number;
  longRentHourRateMultiplier?: number;
  extraLongRentHourRateMultiplier?: number;
}

export interface BillingSettings {
  longRentThresholdHours: number;
  extraLongRentThresholdHours: number;
  longRentHourRateMultiplier: number;
  extraLongRentHourRateMultiplier: number;
  updatedAt?: string;
}

export interface BillingSettingsLog {
  id: number;
  oldLongRentHourRateMultiplier: number;
  newLongRentHourRateMultiplier: number;
  oldExtraLongRentHourRateMultiplier: number;
  newExtraLongRentHourRateMultiplier: number;
  operatorUserId?: number | null;
  createdAt: string;
}

export interface FeedbackItem {
  id: number;
  userId: number | null;
  scooterId: number | null;
  content: string;
  priority: string;
  resolved: boolean;
  // ID14 TODO fields
  escalated?: boolean;
  escalatedTo?: string | null;
  escalationStatus?: string | null;
}

export interface ProcessFeedbackPayload {
  action: "DIRECT_HANDLE" | "ESCALATE";
  escalateTo?: string;
  note?: string;
}

export interface EscalatedFeedbackResponse {
  feedbackId: number;
  priority: string;
  escalated: boolean;
  escalatedTo?: string | null;
  status: string;
}

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

export interface PopularRentalDate {
  date: string;
  rank: number;
  orderCount: number;
  revenue: number;
}

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

export interface OrderStats {
  totalOrders: number;
  validOrders: number;
  cancelledOrders: number;
  cancellationRate: number;
}

export interface VehicleStats {
  totalScooters: number;
  rentedScooters: number;
  maintenanceScooters: number;
  availableScooters: number;
  usageRate: number;
}

export interface FaultStats {
  totalFeedbacks: number;
  resolvedFeedbacks: number;
  unresolvedFeedbacks: number;
  priorityDistribution: Record<string, number>;
}

export interface DailyTrendPoint {
  date: string;
  orderCount: number;
  revenue: number;
}

export interface DashboardOverview {
  orderStats: OrderStats;
  revenueStats: RevenueStats;
  vehicleStats: VehicleStats;
  faultStats: FaultStats;
  dailyTrend: DailyTrendPoint[];
}
