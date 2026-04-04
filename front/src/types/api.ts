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
}

export interface FeedbackItem {
  id: number;
  userId: number | null;
  scooterId: number | null;
  content: string;
  priority: string;
  resolved: boolean;
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
