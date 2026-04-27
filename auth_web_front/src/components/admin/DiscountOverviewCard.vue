<template>
  <el-card
    shadow="hover"
    class="discount-card admin-clickable-card"
    role="button"
    tabindex="0"
    :aria-label="`Open ${props.title} settings`"
    @click="emit('click')"
    @keydown.enter.prevent="emit('click')"
    @keydown.space.prevent="emit('click')"
  >
    <div class="card-header">
      <span class="title">{{ title }}</span>
      <el-tag size="small" type="info">{{ tag }}</el-tag>
    </div>
    <el-text class="summary">{{ summary }}</el-text>
  </el-card>
</template>

<script setup lang="ts">
const props = defineProps<{
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
  min-height: 132px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.title {
  font-size: 16px;
  font-weight: 700;
  color: var(--ui-text-strong);
}

.summary {
  color: var(--ui-text-muted);
  line-height: 1.6;
  white-space: pre-line;
}

:deep(.el-card__body) {
  padding: 16px;
}

:deep(.el-tag) {
  border-radius: 999px;
  border-color: var(--ui-color-primary-100);
  background: var(--ui-color-primary-50);
  color: var(--ui-color-primary-700);
}
</style>
