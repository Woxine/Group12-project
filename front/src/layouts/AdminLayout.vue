<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="brand">Admin Console</div>
      <el-menu :default-active="route.path" router class="menu">
        <el-menu-item index="/admin/analytics">Analytics</el-menu-item>
        <el-menu-item index="/admin/revenue">Revenue</el-menu-item>
        <el-menu-item index="/admin/scooters">Scooters</el-menu-item>
        <el-menu-item index="/admin/feedbacks">Feedback</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div>{{ authStore.name || "Admin" }}</div>
        <el-button type="danger" plain @click="logout">Sign Out</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";

import { useAuthStore } from "@/stores/auth";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

function logout() {
  authStore.signOut();
  router.push("/login");
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.aside {
  border-right: 1px solid #e5e7eb;
  background: #ffffff;
}

.brand {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  border-bottom: 1px solid #e5e7eb;
}

.menu {
  border-right: none;
}

.header {
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
