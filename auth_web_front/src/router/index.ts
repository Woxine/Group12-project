import { createRouter, createWebHistory } from "vue-router";

import AdminLayout from "@/layouts/AdminLayout.vue";
import RevenueView from "@/views/admin/RevenueView.vue";
import ScootersView from "@/views/admin/ScootersView.vue";
import FeedbacksView from "@/views/admin/FeedbacksView.vue";
import HighPriorityIssuesView from "@/views/admin/HighPriorityIssuesView.vue";
import AnalyticsView from "@/views/admin/AnalyticsView.vue";
import DiscountVerificationsView from "@/views/admin/DiscountVerificationsView.vue";
import BillingSettingsView from "@/views/admin/BillingSettingsView.vue";
import VehicleContentEditorView from "@/views/admin/VehicleContentEditorView.vue";
import LoginView from "@/views/LoginView.vue";
import UnauthorizedView from "@/views/UnauthorizedView.vue";
import { useAuthStore } from "@/stores/auth";

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
