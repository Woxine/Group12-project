<template>
  <el-container class="layout">
    <el-aside width="240px" class="aside" aria-label="Admin sidebar">
      <div class="brand">
        <el-icon class="brand-icon"><Platform /></el-icon>
        Admin Console
      </div>
      <nav aria-label="Admin primary navigation">
        <el-menu :default-active="route.path" router class="menu">
          <el-menu-item index="/admin/analytics">
            <el-icon><DataLine /></el-icon>
            <span>Analytics</span>
          </el-menu-item>
          <el-menu-item index="/admin/revenue">
            <el-icon><Money /></el-icon>
            <span>Revenue</span>
          </el-menu-item>
          <el-menu-item index="/admin/scooters">
            <el-icon><Bicycle /></el-icon>
            <span>Scooters</span>
          </el-menu-item>
          <el-menu-item index="/admin/feedbacks">
            <el-icon><ChatDotSquare /></el-icon>
            <span>Feedback</span>
          </el-menu-item>
          <el-menu-item index="/admin/high-priority-issues">
            <el-icon><WarningFilled /></el-icon>
            <span>High Priority Issues</span>
          </el-menu-item>
          <el-menu-item index="/admin/discount-verifications">
            <el-icon><DocumentChecked /></el-icon>
            <span>Verification Review</span>
          </el-menu-item>
          <el-menu-item index="/admin/billing">
            <el-icon><Setting /></el-icon>
            <span>Billing</span>
          </el-menu-item>
          <el-menu-item index="/admin/vehicle-content">
            <el-icon><EditPen /></el-icon>
            <span>Vehicle Content</span>
          </el-menu-item>
        </el-menu>
      </nav>
    </el-aside>

    <el-container>
      <el-header class="header" role="banner">
        <div class="header-left">
          <!-- Optional breadcrumb or title here in the future -->
        </div>
        <div class="header-right">
          <el-space :size="16">
            <div class="user-info">
              <el-avatar :size="32" class="avatar">
                {{ (authStore.name || "A").charAt(0).toUpperCase() }}
              </el-avatar>
              <span class="greeting">Hello, {{ authStore.name || "Admin" }}</span>
            </div>
            <el-button type="danger" plain @click="logout" size="small">
              <el-icon><SwitchButton /></el-icon>
              <span>Sign Out</span>
            </el-button>
          </el-space>
        </div>
      </el-header>
      <el-main id="main-content" ref="mainContentRef" class="main-content" tabindex="-1">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { nextTick, ref, watch } from "vue";
import {
  DataLine,
  Money,
  Bicycle,
  ChatDotSquare,
  WarningFilled,
  DocumentChecked,
  Setting,
  EditPen,
  Platform,
  SwitchButton
} from '@element-plus/icons-vue';

import { useAuthStore } from "@/stores/auth";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const mainContentRef = ref<HTMLElement | null>(null);

function logout() {
  authStore.signOut();
  router.push("/login");
}

watch(
  () => route.fullPath,
  async () => {
    await nextTick();
    mainContentRef.value?.focus();
  }
);
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.aside {
  border-right: 1px solid #e5e7eb;
  background: #ffffff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  z-index: 10;
}

.brand {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 18px;
  color: #303133;
  border-bottom: 1px solid #f0f2f5;
}

.brand-icon {
  margin-right: 8px;
  font-size: 20px;
  color: #409EFF;
}

.menu {
  border-right: none;
  margin-top: 8px;
}

.el-menu-item {
  margin: 4px 12px;
  border-radius: 8px;
  height: 48px;
  line-height: 48px;
}

.el-menu-item.is-active {
  background-color: #dbeafe;
  color: #1d4ed8;
  font-weight: 600;
  border-left: 4px solid #1d4ed8;
}

.el-menu-item.is-active::after {
  content: " (current)";
  font-size: 12px;
  font-weight: 500;
}

.header {
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.02);
  z-index: 5;
  padding: 0 24px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.avatar {
  background: #409EFF;
  color: white;
  font-weight: bold;
}

.greeting {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.main-content {
  background-color: #f5f7fa;
  padding: 24px;
}
</style>
