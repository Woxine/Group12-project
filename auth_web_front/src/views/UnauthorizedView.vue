<template>
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

function toLogin() {
  router.push("/login");
}
</script>

<style scoped>
.unauthorized-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: linear-gradient(180deg, var(--ui-bg-app) 0%, var(--ui-bg-subtle) 100%);
}

.unauthorized-page :deep(.el-result) {
  width: min(560px, calc(100% - 24px));
  padding: 24px 18px;
  border-radius: var(--ui-radius-lg);
  border: 1px solid var(--ui-border-soft);
  background: var(--ui-bg-surface);
  box-shadow: var(--ui-shadow-md);
}
</style>
