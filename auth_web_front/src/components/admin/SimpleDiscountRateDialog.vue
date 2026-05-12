<template>
  <!-- 单项折扣率编辑弹窗 / Dialog for editing one discount rate. -->
  <el-dialog :model-value="visible" :title="title" width="560px" class="simple-rate-dialog" @close="emit('update:visible', false)">
    <!-- 说明文字与折扣率输入 / Description and rate input. -->
    <el-text class="description">{{ description }}</el-text>
    <el-form label-position="top" class="form admin-dialog-form">
      <el-form-item :label="label">
        <el-input-number
          v-model="localRate"
          :min="0.0001"
          :max="1"
          :step="0.01"
          :precision="4"
          class="full-width"
        />
      </el-form-item>
    </el-form>
    <!-- 输入范围校验提示 / Inline validation for the allowed rate range. -->
    <el-alert
      v-if="!isRateValid"
      title="Enter a discount rate between 0.0001 and 1."
      type="warning"
      :closable="false"
      show-icon
      class="admin-hint"
    />
    <template #footer>
      <el-button @click="emit('update:visible', false)">Cancel</el-button>
      <el-button type="primary" :loading="saving" :disabled="!isRateValid" @click="emit('save', localRate)">
        Save
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";

/** 父组件控制弹窗内容、当前值和保存状态 / Parent-controlled dialog content, current value, and saving state. */
const props = defineProps<{
  visible: boolean;
  title: string;
  description: string;
  label: string;
  rate: number;
  saving: boolean;
}>();

/** 关闭弹窗或提交新折扣率 / Close the dialog or submit the edited rate. */
const emit = defineEmits<{
  (e: "update:visible", value: boolean): void;
  (e: "save", value: number): void;
}>();

/** 本地输入副本，避免未保存输入直接改动父组件状态 / Local input copy so unsaved edits do not mutate parent state. */
const localRate = ref(0.8);
const DEFAULT_RATE = 0.8;

/** 将后端或表单值规整到可保存范围 / Normalize backend or form values into the saveable range. */
function sanitizeRate(value: unknown) {
  const num = typeof value === "number" ? value : Number(value);
  if (!Number.isFinite(num) || num <= 0) return DEFAULT_RATE;
  if (num < 0.0001) return 0.0001;
  if (num > 1) return 1;
  return Math.round(num * 10000) / 10000;
}

/** 打开弹窗时同步最新折扣率 / Sync the latest rate when the dialog opens. */
watch(
  () => props.visible,
  (opened) => {
    if (opened) {
      localRate.value = sanitizeRate(props.rate);
    }
  }
);

/** 弹窗打开期间跟随父组件的异步刷新值 / Follow parent async refreshes while the dialog is open. */
watch(
  () => props.rate,
  (nextRate) => {
    if (props.visible) {
      localRate.value = sanitizeRate(nextRate);
    }
  }
);

/** 保存按钮的最终输入门禁 / Final input gate for the Save button. */
const isRateValid = computed(() => localRate.value >= 0.0001 && localRate.value <= 1);
</script>

<style scoped>
/* 弹窗正文排版 / Dialog body typography. */
.description {
  display: block;
  color: var(--ui-text-muted);
  line-height: 1.55;
}

.form {
  margin-top: var(--ui-space-4);
}

.full-width {
  width: 100%;
}

/* Element Plus 弹窗间距微调 / Element Plus dialog spacing adjustments. */
:deep(.el-alert) {
  margin-top: var(--ui-space-2);
}

:deep(.el-dialog__body) {
  padding-bottom: var(--ui-space-4);
}
</style>
