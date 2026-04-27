<template>
  <el-dialog :model-value="visible" title="Long-Rent Discount Adjustment" width="1080px" @close="handleClose">
    <el-row :gutter="24">
      <el-col :span="10">
        <el-form label-position="top" class="form-panel admin-dialog-form">
          <el-form-item label="Threshold T1 (hours)">
            <el-input-number v-model="state.longRentThresholdHours" :min="1" :max="200" :precision="0" disabled class="full-width" />
          </el-form-item>
          <el-form-item label="Threshold T2 (hours)">
            <el-input-number v-model="state.extraLongRentThresholdHours" :min="2" :max="300" :precision="0" disabled class="full-width" />
          </el-form-item>
          <el-form-item label="Multiplier m1 (24h~72h)">
            <el-input-number
              v-model="state.longRentHourRateMultiplier"
              :min="0.0001"
              :max="1"
              :step="0.01"
              :precision="4"
              class="full-width"
              @change="onInputChange('m1')"
            />
          </el-form-item>
          <el-form-item label="Multiplier m2 (>72h)">
            <el-input-number
              v-model="state.extraLongRentHourRateMultiplier"
              :min="0.0001"
              :max="1"
              :step="0.01"
              :precision="4"
              class="full-width"
              @change="onInputChange('m2')"
            />
          </el-form-item>
          <el-alert v-if="inlineValidationMessage" :title="inlineValidationMessage" type="warning" show-icon :closable="false" class="status-alert" />
          <el-alert
            v-else
            title="You can review and save after confirming both multipliers."
            type="success"
            show-icon
            :closable="false"
            class="status-alert"
          />
          <el-text v-if="state.updatedAt" class="updated-at">
            Last updated: {{ state.updatedAt }}
          </el-text>
        </el-form>
      </el-col>

      <el-col :span="14">
        <v-chart ref="chartRef" class="chart" :option="chartOption" autoresize />
      </el-col>
    </el-row>

    <el-divider />
    <div class="log-header">
      <span class="log-title">Multiplier Adjustment Logs</span>
      <el-button size="small" @click="emit('refresh-logs')">Refresh Logs</el-button>
    </div>
    <el-table :data="logs" stripe class="log-table admin-data-table" v-loading="logsLoading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="m1" width="220">
        <template #default="{ row }">
          {{ Number(row.oldLongRentHourRateMultiplier).toFixed(4) }} → {{ Number(row.newLongRentHourRateMultiplier).toFixed(4) }}
        </template>
      </el-table-column>
      <el-table-column label="m2" width="220">
        <template #default="{ row }">
          {{ Number(row.oldExtraLongRentHourRateMultiplier).toFixed(4) }} → {{ Number(row.newExtraLongRentHourRateMultiplier).toFixed(4) }}
        </template>
      </el-table-column>
      <el-table-column label="Operator" width="140">
        <template #default="{ row }">{{ row.operatorUserId ?? "-" }}</template>
      </el-table-column>
      <el-table-column label="Changed At" min-width="180">
        <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button @click="emit('update:visible', false)">Cancel</el-button>
      <el-button type="primary" :loading="saving" @click="openPreview">Save</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="previewVisible" title="Confirm Multiplier Update" width="720px" append-to-body class="preview-dialog">
    <el-alert
      title="Please review the pricing impact before applying the new multipliers."
      type="info"
      show-icon
      :closable="false"
      class="preview-alert"
    />
    <el-descriptions :column="2" border>
      <el-descriptions-item label="m1 current">
        {{ originalState.longRentHourRateMultiplier.toFixed(4) }}
      </el-descriptions-item>
      <el-descriptions-item label="m1 new">
        {{ state.longRentHourRateMultiplier.toFixed(4) }}
      </el-descriptions-item>
      <el-descriptions-item label="m2 current">
        {{ originalState.extraLongRentHourRateMultiplier.toFixed(4) }}
      </el-descriptions-item>
      <el-descriptions-item label="m2 new">
        {{ state.extraLongRentHourRateMultiplier.toFixed(4) }}
      </el-descriptions-item>
    </el-descriptions>

    <el-table :data="previewRows" size="small" class="preview-table admin-data-table">
      <el-table-column prop="hours" label="Duration" width="140">
        <template #default="{ row }">{{ row.hours }}h</template>
      </el-table-column>
      <el-table-column prop="before" label="Before (£)">
        <template #default="{ row }">{{ row.before.toFixed(2) }}</template>
      </el-table-column>
      <el-table-column prop="after" label="After (£)">
        <template #default="{ row }">{{ row.after.toFixed(2) }}</template>
      </el-table-column>
      <el-table-column prop="delta" label="Delta (£)">
        <template #default="{ row }">
          <span :class="row.delta >= 0 ? 'delta-up' : 'delta-down'">
            {{ row.delta >= 0 ? "+" : "" }}{{ row.delta.toFixed(2) }}
          </span>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <el-button @click="previewVisible = false">Cancel</el-button>
      <el-button type="primary" :loading="saving" @click="confirmSave">Confirm Save</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, reactive, ref, watch } from "vue";
import { ElMessage } from "element-plus";
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { LineChart } from "echarts/charts";
import { GridComponent, MarkLineComponent, TooltipComponent, LegendComponent } from "echarts/components";
import type { EChartsOption } from "echarts";
import VChart from "vue-echarts";
import type { BillingSettings, BillingSettingsLog } from "@/types/api";

use([CanvasRenderer, LineChart, GridComponent, MarkLineComponent, TooltipComponent, LegendComponent]);

const H_MAX = 168;
const PREVIEW_BASE_RATE = 3.5;
const PREVIEW_HOURS = [24, 48, 72, 100, 168];
const MIN_MULTIPLIER = 0.0001;
const MAX_MULTIPLIER = 1;
const DEFAULT_M1 = 0.85;
const DEFAULT_M2 = 0.75;

const props = defineProps<{
  visible: boolean;
  settings: BillingSettings | null;
  logs: BillingSettingsLog[];
  logsLoading: boolean;
  saving: boolean;
}>();

const emit = defineEmits<{
  (e: "update:visible", value: boolean): void;
  (e: "save", payload: { longRentHourRateMultiplier: number; extraLongRentHourRateMultiplier: number }): void;
  (e: "refresh-logs"): void;
}>();

const chartRef = ref<InstanceType<typeof VChart> | null>(null);
const previewVisible = ref(false);
const lastEditedField = ref<"m1" | "m2">("m1");
const state = reactive({
  longRentThresholdHours: 24,
  extraLongRentThresholdHours: 72,
  longRentHourRateMultiplier: DEFAULT_M1,
  extraLongRentHourRateMultiplier: DEFAULT_M2,
  updatedAt: ""
});
const originalState = reactive({
  longRentHourRateMultiplier: DEFAULT_M1,
  extraLongRentHourRateMultiplier: DEFAULT_M2
});

const m1RangeError = computed(() => {
  const m1 = state.longRentHourRateMultiplier;
  if (m1 < MIN_MULTIPLIER || m1 > MAX_MULTIPLIER) {
    return "The 24h-72h multiplier must be between 0.0001 and 1.";
  }
  return "";
});

const m2RangeError = computed(() => {
  const m2 = state.extraLongRentHourRateMultiplier;
  if (m2 < MIN_MULTIPLIER || m2 > MAX_MULTIPLIER) {
    return "The >72h multiplier must be between 0.0001 and 1.";
  }
  return "";
});

function uiColor(tokenName: string, fallback: string) {
  if (typeof window === "undefined") {
    return fallback;
  }
  return getComputedStyle(document.documentElement).getPropertyValue(tokenName).trim() || fallback;
}

const relationError = computed(() => {
  if (state.extraLongRentHourRateMultiplier > state.longRentHourRateMultiplier) {
    return "The >72h multiplier should not be higher than the 24h-72h multiplier.";
  }
  return "";
});

const inlineValidationMessage = computed(() => {
  if (lastEditedField.value === "m1") {
    return m1RangeError.value;
  }
  if (lastEditedField.value === "m2") {
    return m2RangeError.value;
  }
  return "";
});

function clampMultiplier(v: number) {
  if (Number.isNaN(v)) return MAX_MULTIPLIER;
  if (v < MIN_MULTIPLIER) return MIN_MULTIPLIER;
  if (v > MAX_MULTIPLIER) return MAX_MULTIPLIER;
  return Math.round(v * 10000) / 10000;
}

function readNumberOrFallback(value: unknown, fallback: number) {
  const num = typeof value === "number" ? value : Number(value);
  if (!Number.isFinite(num)) return fallback;
  if (num <= 0) return fallback;
  return clampMultiplier(num);
}

function computeSegmentedTotal(rate: number, hours: number, m1: number, m2: number) {
  const t1 = state.longRentThresholdHours;
  const t2 = state.extraLongRentThresholdHours;
  if (hours <= t1) return rate * hours;
  if (hours <= t2) return rate * t1 + rate * m1 * (hours - t1);
  return rate * t1 + rate * m1 * (t2 - t1) + rate * m2 * (hours - t2);
}

const previewRows = computed(() => {
  return PREVIEW_HOURS.map((hours) => {
    const before = computeSegmentedTotal(
      PREVIEW_BASE_RATE,
      hours,
      originalState.longRentHourRateMultiplier,
      originalState.extraLongRentHourRateMultiplier
    );
    const after = computeSegmentedTotal(
      PREVIEW_BASE_RATE,
      hours,
      state.longRentHourRateMultiplier,
      state.extraLongRentHourRateMultiplier
    );
    return {
      hours,
      before,
      after,
      delta: after - before
    };
  });
});

function secondAnchorX() {
  return state.longRentThresholdHours + (state.extraLongRentThresholdHours - state.longRentThresholdHours) / 2;
}

function thirdAnchorX() {
  return state.extraLongRentThresholdHours + (H_MAX - state.extraLongRentThresholdHours) / 2;
}

const stepSeriesData = computed(() => {
  const t1 = state.longRentThresholdHours;
  const t2 = state.extraLongRentThresholdHours;
  const m1 = state.longRentHourRateMultiplier;
  const m2 = state.extraLongRentHourRateMultiplier;
  return [
    [0, 1],
    [t1, 1],
    [t1, m1],
    [t2, m1],
    [t2, m2],
    [H_MAX, m2]
  ];
});

const chartOption = computed<EChartsOption>(() => {
  const primary = uiColor("--ui-color-primary-600", "#2563eb");
  const success = uiColor("--ui-color-success-600", "#16a34a");
  const warning = uiColor("--ui-color-warning-500", "#f59e0b");
  const surface = uiColor("--ui-bg-surface", "#ffffff");

  return {
    tooltip: {
      trigger: "axis",
      formatter: (params: any) => {
        const point = params?.[0]?.data;
        if (!point) return "";
        return `${point[0]}h<br/>Multiplier: ${Number(point[1]).toFixed(4)}`;
      }
    },
    legend: { data: ["Effective Multiplier"] },
    grid: { left: 40, right: 30, top: 40, bottom: 40 },
    xAxis: { type: "value", min: 0, max: H_MAX, name: "Hours" },
    yAxis: { type: "value", min: 0, max: 1, name: "Multiplier" },
    series: [
      {
        name: "Effective Multiplier",
        type: "line",
        step: "end",
        smooth: false,
        data: stepSeriesData.value,
        lineStyle: { width: 3, color: primary },
        symbol: "none",
        markLine: {
          symbol: "none",
          lineStyle: { type: "dashed" },
          data: [{ xAxis: state.longRentThresholdHours }, { xAxis: state.extraLongRentThresholdHours }]
        }
      }
    ],
    graphic: [
      {
        id: "m1-handle",
        type: "circle",
        shape: { r: 9 },
        position: [0, 0],
        draggable: true,
        style: { fill: success, stroke: surface, lineWidth: 2 },
        ondrag: function (this: any) {
          onHandleDrag("m1", this);
        }
      },
      {
        id: "m2-handle",
        type: "circle",
        shape: { r: 9 },
        position: [0, 0],
        draggable: true,
        style: { fill: warning, stroke: surface, lineWidth: 2 },
        ondrag: function (this: any) {
          onHandleDrag("m2", this);
        }
      }
    ]
  };
});

function refreshHandlePositions() {
  const chart = (chartRef.value as any)?.chart;
  if (!chart) return;
  const p1 = chart.convertToPixel({ xAxisIndex: 0, yAxisIndex: 0 }, [secondAnchorX(), state.longRentHourRateMultiplier]) as number[];
  const p2 = chart.convertToPixel({ xAxisIndex: 0, yAxisIndex: 0 }, [thirdAnchorX(), state.extraLongRentHourRateMultiplier]) as number[];
  chart.setOption({
    graphic: [
      { id: "m1-handle", position: p1 },
      { id: "m2-handle", position: p2 }
    ]
  });
}

function applySingleFieldNormalization(source: "m1" | "m2", value: number) {
  const normalized = clampMultiplier(value);
  if (source === "m1") {
    state.longRentHourRateMultiplier = normalized;
    return;
  }
  state.extraLongRentHourRateMultiplier = normalized;
}

function onHandleDrag(kind: "m1" | "m2", target: { x: number; y: number }) {
  const chart = (chartRef.value as any)?.chart;
  if (!chart) return;
  const fixedX = kind === "m1" ? secondAnchorX() : thirdAnchorX();
  const fixedPixelX = chart.convertToPixel({ xAxisIndex: 0, yAxisIndex: 0 }, [fixedX, 0]) as number[];
  target.x = fixedPixelX[0];
  const dataCoord = chart.convertFromPixel({ xAxisIndex: 0, yAxisIndex: 0 }, [fixedPixelX[0], target.y]) as number[];
  lastEditedField.value = kind;
  applySingleFieldNormalization(kind, dataCoord[1]);
  syncChartFromInputs();
}

function syncChartFromInputs() {
  state.longRentHourRateMultiplier = clampMultiplier(state.longRentHourRateMultiplier);
  state.extraLongRentHourRateMultiplier = clampMultiplier(state.extraLongRentHourRateMultiplier);
  nextTick(() => refreshHandlePositions());
}

function onInputChange(kind: "m1" | "m2") {
  lastEditedField.value = kind;
  if (kind === "m1") {
    applySingleFieldNormalization("m1", state.longRentHourRateMultiplier);
  } else {
    applySingleFieldNormalization("m2", state.extraLongRentHourRateMultiplier);
  }
  syncChartFromInputs();
}

function hasPendingChanges() {
  return (
    Math.abs(state.longRentHourRateMultiplier - originalState.longRentHourRateMultiplier) > 0.00001 ||
    Math.abs(state.extraLongRentHourRateMultiplier - originalState.extraLongRentHourRateMultiplier) > 0.00001
  );
}

function openPreview() {
  const blockingError = m1RangeError.value || m2RangeError.value || relationError.value;
  if (blockingError) {
    ElMessage.error(blockingError);
    return;
  }
  if (!hasPendingChanges()) {
    ElMessage.info("No changes to save");
    return;
  }
  previewVisible.value = true;
}

function confirmSave() {
  const blockingError = m1RangeError.value || m2RangeError.value || relationError.value;
  if (blockingError) {
    ElMessage.error(blockingError);
    return;
  }
  emit("save", {
    longRentHourRateMultiplier: state.longRentHourRateMultiplier,
    extraLongRentHourRateMultiplier: state.extraLongRentHourRateMultiplier
  });
}

function syncStateFromProps() {
  if (!props.settings) return;
  state.longRentThresholdHours = Number(props.settings.longRentThresholdHours);
  state.extraLongRentThresholdHours = Number(props.settings.extraLongRentThresholdHours);
  state.longRentHourRateMultiplier = readNumberOrFallback(props.settings.longRentHourRateMultiplier, DEFAULT_M1);
  state.extraLongRentHourRateMultiplier = readNumberOrFallback(props.settings.extraLongRentHourRateMultiplier, DEFAULT_M2);
  state.updatedAt = props.settings.updatedAt ? String(props.settings.updatedAt).replace("T", " ").substring(0, 19) : "";
  originalState.longRentHourRateMultiplier = readNumberOrFallback(props.settings.longRentHourRateMultiplier, DEFAULT_M1);
  originalState.extraLongRentHourRateMultiplier = readNumberOrFallback(props.settings.extraLongRentHourRateMultiplier, DEFAULT_M2);
  previewVisible.value = false;
  lastEditedField.value = "m1";
  syncChartFromInputs();
}

function handleClose() {
  previewVisible.value = false;
  emit("update:visible", false);
}

function formatDateTime(v: string) {
  if (!v) return "-";
  return String(v).replace("T", " ").substring(0, 19);
}

watch(
  () => props.visible,
  (visible) => {
    if (visible) {
      syncStateFromProps();
      emit("refresh-logs");
    } else {
      previewVisible.value = false;
    }
  }
);

watch(
  () => props.settings,
  () => {
    if (props.visible) {
      syncStateFromProps();
    }
  },
  { deep: true }
);

onMounted(() => {
  window.addEventListener("resize", refreshHandlePositions);
});

onUnmounted(() => {
  window.removeEventListener("resize", refreshHandlePositions);
});
</script>

<style scoped>
.form-panel {
  padding: 14px;
}

.full-width {
  width: 100%;
}

.chart {
  width: 100%;
  height: 460px;
  border-radius: var(--ui-radius-md);
  border: 1px solid var(--ui-border-soft);
  background: var(--ui-bg-surface);
}

.updated-at {
  margin-top: 12px;
  display: inline-block;
  color: var(--ui-text-muted);
}

.status-alert {
  margin-top: 4px;
  border-radius: 10px;
}

.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.log-title {
  font-weight: 700;
  color: var(--ui-text-strong);
}

.log-table {
  margin-top: 8px;
}

.preview-alert {
  margin-bottom: 12px;
  border-radius: 10px;
}

.preview-table {
  margin-top: 14px;
}

.delta-up {
  color: var(--ui-color-warning-600);
  font-weight: 600;
}

.delta-down {
  color: var(--ui-color-success-600);
  font-weight: 600;
}

:deep(.el-divider--horizontal) {
  margin: 20px 0 16px;
}
</style>
