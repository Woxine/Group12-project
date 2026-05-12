export type AdminSemanticType = "success" | "warning" | "danger" | "info";
export type AdminChartColorKey = AdminSemanticType | "primary" | "neutral";

/** 图表颜色读取 CSS 设计令牌时需要的 token 和兜底色 / Token and fallback color used when charts read CSS design tokens. */
type ChartToken = {
  token: string;
  fallback: string;
};

/** 管理端图表语义色与设计系统颜色的映射 / Mapping between admin chart semantic colors and design-system colors. */
export const ADMIN_CHART_COLOR_TOKENS: Record<AdminChartColorKey, ChartToken> = {
  success: { token: "--ui-color-success-600", fallback: "#16a34a" },
  warning: { token: "--ui-color-warning-600", fallback: "#d97706" },
  danger: { token: "--ui-color-danger-600", fallback: "#dc2626" },
  info: { token: "--ui-color-primary-600", fallback: "#2563eb" },
  primary: { token: "--ui-color-primary-600", fallback: "#2563eb" },
  neutral: { token: "--ui-text-muted", fallback: "#64748b" }
};

/** 统一大小写、空格和布尔值，便于后续状态匹配 / Normalize case, spacing, and boolean values for status matching. */
export function normalizeAdminStatus(value?: string | boolean | null) {
  if (typeof value === "boolean") {
    return value ? "YES" : "NO";
  }
  return (value ?? "").trim().replace(/[\s-]+/g, "_").toUpperCase();
}

/** 通用业务状态到 Element Plus 标签类型的映射 / Map general business statuses to Element Plus tag types. */
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

/** 反馈优先级到标签类型的映射 / Map feedback priority values to tag types. */
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

/** 车辆状态到标签类型的映射 / Map scooter statuses to tag types. */
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
