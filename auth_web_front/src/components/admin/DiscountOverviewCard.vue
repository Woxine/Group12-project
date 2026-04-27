<template>
  <el-card shadow="hover" class="discount-card" @click="emit('click')">
    <div class="card-header">
      <span class="title">{{ title }}</span>
      <el-tag size="small" type="info">{{ tag }}</el-tag>
    </div>
    <el-text class="summary">{{ summary }}</el-text>
  </el-card>
</template>

<script setup lang="ts">
defineProps<{
  title: string;
  summary: string;
  tag: string;
}>();

const emit = defineEmits<{
  (e: "click"): void;
}>();
</script>

<style scoped>
.discount-card {
  cursor: pointer;
  min-height: 132px;
  border-radius: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.title {
  font-size: 16px;
  font-weight: 600;
}

.summary {
  color: #606266;
  line-height: 1.6;
  white-space: pre-line;
}
</style>
