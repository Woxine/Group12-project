import { createRouter, createWebHistory } from "vue-router";

import AdminLayout from "@/layouts/AdminLayout.vue";
import LoginView from "@/views/LoginView.vue";
import UnauthorizedView from "@/views/UnauthorizedView.vue";
import { useAuthStore } from "@/stores/auth";

const AnalyticsView = () => import("@/views/admin/AnalyticsView.vue");
const RevenueView = () => import("@/views/admin/RevenueView.vue");
const ScootersView = () => import("@/views/admin/ScootersView.vue");
const FeedbacksView = () => import("@/views/admin/FeedbacksView.vue");
const HighPriorityIssuesView = () => import("@/views/admin/HighPriorityIssuesView.vue");
const DiscountVerificationsView = () => import("@/views/admin/DiscountVerificationsView.vue");
const BillingSettingsView = () => import("@/views/admin/BillingSettingsView.vue");
const VehicleContentEditorView = () => import("@/views/admin/VehicleContentEditorView.vue");

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", redirect: "/admin/revenue" },
    { path: "/login", component: LoginView },
    { path: "/unauthorized", component: UnauthorizedView },
    {
      path: "/admin",
      component: AdminLayout,
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        { path: "", redirect: "/admin/revenue" },
        { path: "analytics", component: AnalyticsView },
        { path: "revenue", component: RevenueView },
        { path: "scooters", component: ScootersView },
        { path: "feedbacks", component: FeedbacksView },
        { path: "high-priority-issues", component: HighPriorityIssuesView },
        { path: "discount-verifications", component: DiscountVerificationsView },
        { path: "billing", component: BillingSettingsView },
        { path: "vehicle-content", component: VehicleContentEditorView }
      ]
    }
  ]
});

router.beforeEach((to) => {
  const authStore = useAuthStore();
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return "/login";
  }
  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    return "/unauthorized";
  }
  if (to.path === "/login" && authStore.isAuthenticated && authStore.isAdmin) {
    return "/admin/revenue";
  }
  return true;
});

export default router;
