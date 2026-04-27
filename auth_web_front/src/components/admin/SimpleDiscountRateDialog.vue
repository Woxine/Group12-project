<template>
  <el-dialog :model-value="visible" :title="title" width="560px" @close="emit('update:visible', false)">
    <el-text class="description">{{ description }}</el-text>
    <el-form label-position="top" class="form">
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
    <el-alert v-if="!isRateValid" title="Discount rate must be within [0.0001, 1.0]." type="warning" :closable="false" show-icon />
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

watch(
  () => props.visible,
  (opened) => {
    if (opened) {
      localRate.value = Number(props.rate);
    }
  }
);

watch(
  () => props.rate,
  (nextRate) => {
    if (props.visible) {
      localRate.value = Number(nextRate);
    }
  }
);

const isRateValid = computed(() => localRate.value >= 0.0001 && localRate.value <= 1);
</script>

<style scoped>
.description {
  display: block;
  color: #606266;
}

.form {
  margin-top: 16px;
}

.full-width {
  width: 100%;
}
</style>
