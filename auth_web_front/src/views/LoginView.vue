<template>
  <!-- 管理员登录页面 / Admin login page. -->
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        <div class="login-title-wrap">
          <strong class="login-title">Admin Login</strong>
          <span class="login-subtitle">Sign in to continue to the operations workspace.</span>
        </div>
      </template>
      <!-- 登录表单，提交后写入认证 Store / Login form that writes the authenticated session into the auth store. -->
      <el-form :model="form" label-position="top" class="admin-dialog-form" @submit.prevent>
        <el-form-item label="Email">
          <el-input v-model="form.email" autocomplete="off" />
        </el-form-item>
        <el-form-item label="Password">
          <el-input v-model="form.password" show-password />
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="submit">Sign In</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";

import { useAuthStore } from "@/stores/auth";

/** 登录页依赖的路由、认证状态和表单状态 / Router, auth store, and form state used by the login page. */
const router = useRouter();
const authStore = useAuthStore();
const loading = ref(false);
const form = reactive({
  email: "",
  password: ""
});

/** 校验输入、登录并拒绝非管理员账号进入后台 / Validate input, sign in, and block non-admin accounts. */
async function submit() {
  if (!form.email || !form.password) {
    ElMessage.warning("Please enter email and password");
    return;
  }

  loading.value = true;
  try {
    await authStore.signIn(form.email, form.password);
    if (!authStore.isAdmin) {
      ElMessage.error("This account is not an admin");
      authStore.signOut();
      router.push("/unauthorized");
      return;
    }
    router.push("/admin/revenue");
  } catch (error: any) {
    const message = error?.response?.data?.message ?? "Login failed";
    ElMessage.error(message);
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
/* 登录页居中背景与卡片 / Centered login background and card. */
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at 20% 20%, var(--ui-color-primary-50) 0%, var(--ui-bg-app) 42%, var(--ui-bg-subtle) 100%);
  padding: 20px;
}

.login-card {
  width: 420px;
  border-radius: var(--ui-radius-lg);
  border: 1px solid var(--ui-border-soft);
  box-shadow: var(--ui-shadow-lg);
}

/* 登录标题排版 / Login title typography. */
.login-title-wrap {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.login-title {
  font-size: 20px;
  color: var(--ui-text-strong);
}

.login-subtitle {
  font-size: 13px;
  color: var(--ui-text-muted);
}

/* 提交按钮撑满卡片宽度 / Submit button spans the card width. */
.login-card :deep(.el-button) {
  width: 100%;
}
</style>
