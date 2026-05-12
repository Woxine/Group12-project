<template>
  <!-- 非管理员账号访问后台时展示的拒绝页 / Access-denied page shown when a non-admin account enters the console. -->
  <div class="unauthorized-page">
    <el-result icon="warning" title="Access Denied" sub-title="This account is not an admin and cannot access the admin console">
      <template #extra>
        <el-button type="primary" @click="toLogin">Back to Login</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";

const router = useRouter();

/** 返回登录页，方便切换管理员账号 / Return to login so the user can switch to an admin account. */
function toLogin() {
  router.push("/login");
}
</script>

<style scoped>
/* 拒绝访问页居中布局 / Centered access-denied page layout. */
.unauthorized-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: linear-gradient(180deg, var(--ui-bg-app) 0%, var(--ui-bg-subtle) 100%);
}

/* Element Plus 结果卡片视觉边界 / Visual boundary for the Element Plus result card. */
.unauthorized-page :deep(.el-result) {
  width: min(560px, calc(100% - 24px));
  padding: 24px 18px;
  border-radius: var(--ui-radius-lg);
  border: 1px solid var(--ui-border-soft);
  background: var(--ui-bg-surface);
  box-shadow: var(--ui-shadow-md);
}
</style>
