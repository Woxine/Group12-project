<template>
  <!-- 折扣入口卡片 / Clickable entry card for a discount setting. -->
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
      <span class="title admin-card-title">{{ title }}</span>
      <el-tag size="small" type="info">{{ tag }}</el-tag>
    </div>
    <el-text class="summary">{{ summary }}</el-text>
  </el-card>
</template>

<script setup lang="ts">
/** 父页面传入的折扣展示内容 / Discount display content passed by the parent page. */
const props = defineProps<{
  title: string;
  summary: string;
  tag: string;
}>();

/** 通知父页面打开对应设置弹窗 / Ask the parent page to open the matching settings dialog. */
const emit = defineEmits<{
  (e: "click"): void;
}>();
</script>

<style scoped>
/* 卡片基础布局 / Base card layout. */
.discount-card {
  min-height: 132px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--ui-space-3);
  margin-bottom: var(--ui-space-3);
}

/* 文本层级与摘要换行 / Text hierarchy and summary line wrapping. */
.title {
  font-size: 16px;
}

.summary {
  color: var(--ui-text-muted);
  line-height: 1.6;
  white-space: pre-line;
}

/* Element Plus 内部样式微调 / Element Plus inner style adjustments. */
:deep(.el-card__body) {
  padding: var(--ui-space-4);
}

:deep(.el-tag) {
  border-radius: 999px;
  border-color: var(--ui-color-primary-100);
  background: var(--ui-color-primary-50);
  color: var(--ui-color-primary-700);
}
</style>
