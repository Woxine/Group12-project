<template>
  <el-container class="layout">
    <div
      v-if="sidebarOpen"
      class="aside-backdrop"
      @click="sidebarOpen = false"
      aria-hidden="true"
    />
    <el-aside width="248px" :class="['aside', { 'aside--open': sidebarOpen }]" aria-label="Admin sidebar">
      <div class="brand">
        <el-icon class="brand-icon"><Platform /></el-icon>
        <div class="brand-text-wrap">
          <span class="brand-title">Admin Console</span>
          <span class="brand-subtitle">Operations Center</span>
        </div>
      </div>
      <nav aria-label="Admin primary navigation">
        <el-menu :default-active="route.path" router class="menu" @select="sidebarOpen = false">
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

    <el-container class="layout-main-shell">
      <el-header class="header" role="banner">
        <div class="header-left">
          <button
            class="sidebar-toggle"
            @click="sidebarOpen = !sidebarOpen"
            :aria-label="sidebarOpen ? 'Close navigation menu' : 'Open navigation menu'"
            :aria-expanded="sidebarOpen"
          >
            <el-icon :size="20"><Close v-if="sidebarOpen" /><Expand v-else /></el-icon>
          </button>
          <span class="header-title">Management Workspace</span>
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
        <div class="content-inner">
          <router-view />
        </div>
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
  SwitchButton,
  Expand,
  Close
} from '@element-plus/icons-vue';

import { useAuthStore } from "@/stores/auth";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const mainContentRef = ref<HTMLElement | null>(null);
const sidebarOpen = ref(false);

function logout() {
  authStore.signOut();
  router.push("/login");
}

watch(
  () => route.fullPath,
  async () => {
    await nextTick();
    document.getElementById("main-content")?.focus();
  }
);
</script>

<style scoped>
.layout {
  min-height: 100vh;
  background: var(--ui-bg-app);
}

.aside {
  border-right: 1px solid var(--ui-border-soft);
  background: linear-gradient(180deg, var(--ui-bg-surface) 0%, var(--ui-bg-surface-soft) 100%);
  box-shadow: var(--ui-shadow-sm);
  z-index: 10;
}

.brand {
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: var(--ui-space-3);
  padding: 0 18px;
  border-bottom: 1px solid var(--ui-border-soft);
}

.brand-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: var(--ui-radius-md);
  background: var(--ui-color-primary-50);
  font-size: 22px;
  color: var(--ui-color-primary-600);
}

.brand-text-wrap {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--ui-text-strong);
}

.brand-subtitle {
  font-size: 12px;
  color: var(--ui-text-muted);
}

.menu {
  border-right: none;
  margin-top: 10px;
  padding: 0 8px;
}

:deep(.menu .el-menu-item) {
  margin: 4px var(--ui-space-3);
  border-radius: var(--ui-radius-md);
  height: 48px;
  line-height: 48px;
  border: 1px solid transparent;
  color: var(--ui-text-muted);
  font-weight: 500;
  transition: background-color 0.2s ease, border-color 0.2s ease, color 0.2s ease, box-shadow 0.2s ease;
}

:deep(.menu .el-menu-item:hover),
:deep(.menu .el-menu-item:focus-visible) {
  background: var(--ui-color-primary-50);
  border-color: var(--ui-color-primary-100);
  color: var(--ui-color-primary-700);
}

:deep(.menu .el-menu-item:focus-visible) {
  outline: 3px solid var(--ui-color-primary-600);
  outline-offset: 2px;
}

:deep(.menu .el-menu-item.is-active) {
  background-color: var(--ui-color-primary-50);
  border-color: var(--ui-color-primary-100);
  color: var(--ui-color-primary-700);
  font-weight: 600;
  box-shadow: inset 4px 0 0 var(--ui-color-primary-700);
}


.layout-main-shell {
  min-width: 0;
}

.header {
  background: linear-gradient(180deg, var(--ui-bg-surface) 0%, var(--ui-bg-surface-soft) 100%);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--ui-border-soft);
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--ui-shadow-sm);
  z-index: 5;
  padding: 0 22px;
}

.header-title {
  font-size: 14px;
  color: var(--ui-text-muted);
  font-weight: 600;
  letter-spacing: 0.2px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--ui-space-2);
  border: 1px solid var(--ui-border-soft);
  border-radius: 999px;
  background: var(--ui-bg-surface);
  padding: 4px 10px 4px 4px;
}

.avatar {
  background: var(--ui-color-primary-600);
  color: #ffffff;
  font-weight: bold;
  box-shadow: var(--ui-shadow-sm);
}

.greeting {
  font-size: 14px;
  color: var(--ui-text-muted);
  font-weight: 500;
}

.main-content {
  background-color: var(--ui-bg-app);
  padding: 18px;
}

.content-inner {
  max-width: 1440px;
  margin: 0 auto;
}

.sidebar-toggle {
  display: none;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: 1px solid var(--ui-border-default);
  border-radius: var(--ui-radius-sm);
  background: var(--ui-bg-surface);
  color: var(--ui-text-default);
  cursor: pointer;
  transition: background-color 0.2s ease, border-color 0.2s ease;
  flex-shrink: 0;
}

.sidebar-toggle:hover {
  background: var(--ui-color-primary-50);
  border-color: var(--ui-color-primary-100);
}

.aside-backdrop {
  display: none;
}

@media (max-width: 768px) {
  .sidebar-toggle {
    display: inline-flex;
  }

  .aside {
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    z-index: 1000;
    transform: translateX(-100%);
    transition: transform 0.3s ease;
    width: 260px !important;
  }

  .aside--open {
    transform: translateX(0);
  }

  .aside-backdrop {
    display: block;
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.4);
    z-index: 999;
  }
}

@media (max-width: 1080px) {
  .aside {
    width: 220px !important;
  }
}

@media (max-width: 880px) {
  .header-title {
    display: none;
  }

  .header {
    padding: 0 var(--ui-space-4);
  }

  .greeting {
    max-width: 140px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
