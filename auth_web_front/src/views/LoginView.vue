<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        <strong>Admin Login</strong>
      </template>
      <el-form :model="form" label-position="top" @submit.prevent>
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
}

.login-card {
  width: 420px;
}
</style>
