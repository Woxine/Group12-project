<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        <div class="login-title-wrap">
          <strong class="login-title">Admin Login</strong>
          <span class="login-subtitle">Sign in to continue to the operations workspace.</span>
        </div>
      </template>
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

const router = useRouter();
const authStore = useAuthStore();
const loading = ref(false);
const form = reactive({
  email: "",
  password: ""
});

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

.login-card :deep(.el-button) {
  width: 100%;
}
</style>
