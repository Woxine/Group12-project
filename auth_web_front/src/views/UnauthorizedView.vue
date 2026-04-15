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
}
</style>
