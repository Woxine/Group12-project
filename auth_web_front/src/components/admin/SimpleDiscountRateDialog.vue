<template>
  <el-dialog :model-value="visible" :title="title" width="560px" class="simple-rate-dialog" @close="emit('update:visible', false)">
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

const props = defineProps<{
  visible: boolean;
  title: string;
  description: string;
  label: string;
  rate: number;
  saving: boolean;
}>();

const emit = defineEmits<{
  (e: "update:visible", value: boolean): void;
  (e: "save", value: number): void;
}>();

const localRate = ref(0.8);
const DEFAULT_RATE = 0.8;

function sanitizeRate(value: unknown) {
  const num = typeof value === "number" ? value : Number(value);
  if (!Number.isFinite(num) || num <= 0) return DEFAULT_RATE;
  if (num < 0.0001) return 0.0001;
  if (num > 1) return 1;
  return Math.round(num * 10000) / 10000;
}

watch(
  () => props.visible,
  (opened) => {
    if (opened) {
      localRate.value = sanitizeRate(props.rate);
    }
  }
);

watch(
  () => props.rate,
  (nextRate) => {
    if (props.visible) {
      localRate.value = sanitizeRate(nextRate);
    }
  }
);

const isRateValid = computed(() => localRate.value >= 0.0001 && localRate.value <= 1);
</script>

<style scoped>
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

:deep(.el-alert) {
  margin-top: var(--ui-space-2);
}

:deep(.el-dialog__body) {
  padding-bottom: var(--ui-space-4);
}
</style>
