export type AdminSemanticType = "success" | "warning" | "danger" | "info";
export type AdminChartColorKey = AdminSemanticType | "primary" | "neutral";

type ChartToken = {
  token: string;
  fallback: string;
};

export const ADMIN_CHART_COLOR_TOKENS: Record<AdminChartColorKey, ChartToken> = {
  success: { token: "--ui-color-success-600", fallback: "#16a34a" },
  warning: { token: "--ui-color-warning-600", fallback: "#d97706" },
  danger: { token: "--ui-color-danger-600", fallback: "#dc2626" },
  info: { token: "--ui-color-primary-600", fallback: "#2563eb" },
  primary: { token: "--ui-color-primary-600", fallback: "#2563eb" },
  neutral: { token: "--ui-text-muted", fallback: "#64748b" }
};

export function normalizeAdminStatus(value?: string | boolean | null) {
  if (typeof value === "boolean") {
    return value ? "YES" : "NO";
  }
  return (value ?? "").trim().replace(/[\s-]+/g, "_").toUpperCase();
}

export function getAdminStatusTagType(value?: string | boolean | null): AdminSemanticType {
  switch (normalizeAdminStatus(value)) {
    case "COMPLETED":
    case "COMPLETE":
    case "RESOLVED":
    case "APPROVED":
    case "AVAILABLE":
    case "VISIBLE":
    case "HEALTHY":
    case "YES":
      return "success";
    case "PENDING":
    case "WAITING":
    case "UNRESOLVED":
    case "OPEN":
    case "ESCALATED":
    case "NEEDS_REVIEW":
    case "NEEDS_ATTENTION":
    case "AT_RISK":
    case "RISKY":
      return "warning";
    case "REJECTED":
    case "FAILED":
    case "FAILURE":
    case "ERROR":
    case "CRITICAL":
    case "MAINTENANCE":
    case "DELETED":
    case "DESTRUCTIVE":
      return "danger";
    default:
      return "info";
  }
}

export function getAdminPriorityTagType(value?: string | null): AdminSemanticType {
  switch (normalizeAdminStatus(value)) {
    case "HIGH":
    case "CRITICAL":
      return "danger";
    case "MEDIUM":
      return "warning";
    case "LOW":
    case "UNKNOWN":
    default:
      return "info";
  }
}

export function getScooterStatusTagType(value?: string | null): AdminSemanticType {
  switch (normalizeAdminStatus(value)) {
    case "AVAILABLE":
      return "success";
    case "RESERVED":
      return "warning";
    case "MAINTENANCE":
      return "danger";
    case "RENTED":
    default:
      return "info";
  }
}
