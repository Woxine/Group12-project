<template>
  <el-dialog :model-value="visible" title="Long-Rent Discount Adjustment" width="1080px" @close="handleClose">
    <!-- 倍率输入与折线预览 / Multiplier inputs paired with the interactive line preview. -->
    <el-row :gutter="24">
      <el-col :xs="24" :sm="10">
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
          <el-alert v-if="inlineValidationMessage" :title="inlineValidationMessage" type="warning" show-icon :closable="false" class="status-alert admin-hint" />
          <el-alert
            v-else
            title="You can review and save after confirming both multipliers."
            type="success"
            show-icon
            :closable="false"
            class="status-alert admin-hint"
          />
          <el-text v-if="state.updatedAt" class="updated-at">
            Last updated: {{ state.updatedAt }}
          </el-text>
        </el-form>
      </el-col>

      <el-col :xs="24" :sm="14">
        <v-chart ref="chartRef" class="chart" :option="chartOption" autoresize />
      </el-col>
    </el-row>

    <el-divider />
    <!-- 计费设置审计日志 / Billing settings audit log. -->
    <div class="log-header">
      <span class="log-title admin-section-title">Multiplier Adjustment Logs</span>
      <el-button size="small" @click="emit('refresh-logs')">Refresh logs</el-button>
    </div>
    <el-table :data="logs" stripe class="log-table admin-data-table admin-loading-section" v-loading="logsLoading" :aria-busy="logsLoading">
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

  <!-- 保存前影响预览 / Impact preview shown before persisting multiplier changes. -->
  <el-dialog v-model="previewVisible" title="Confirm Multiplier Update" width="720px" append-to-body class="preview-dialog">
    <el-alert
      title="Please review the pricing impact before applying the new multipliers."
      type="info"
      show-icon
      :closable="false"
      class="preview-alert admin-hint"
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

/** 折扣曲线和保存预览使用的固定业务参数 / Fixed business parameters for the discount curve and save preview. */
const H_MAX = 168;
const PREVIEW_BASE_RATE = 3.5;
const PREVIEW_HOURS = [24, 48, 72, 100, 168];
const MIN_MULTIPLIER = 0.0001;
const MAX_MULTIPLIER = 1;
const DEFAULT_M1 = 0.85;
const DEFAULT_M2 = 0.75;

/** 父页面提供当前计费设置、日志和加载状态 / Parent page provides billing settings, logs, and loading states. */
const props = defineProps<{
  visible: boolean;
  settings: BillingSettings | null;
  logs: BillingSettingsLog[];
  logsLoading: boolean;
  saving: boolean;
}>();

/** 向父页面同步弹窗状态、保存请求和日志刷新请求 / Sync dialog state, save requests, and log refresh requests to the parent page. */
const emit = defineEmits<{
  (e: "update:visible", value: boolean): void;
  (e: "save", payload: { longRentHourRateMultiplier: number; extraLongRentHourRateMultiplier: number }): void;
  (e: "refresh-logs"): void;
}>();

/** 本地编辑状态，保存前与原始状态对比 / Local edit state compared with the original state before saving. */
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

/** 单个倍率范围校验 / Per-multiplier range validation. */
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

/** 根据设计令牌读取图表颜色 / Read chart colors from design tokens. */
function uiColor(tokenName: string, fallback: string) {
  if (typeof window === "undefined") {
    return fallback;
  }
  return getComputedStyle(document.documentElement).getPropertyValue(tokenName).trim() || fallback;
}

/** 跨区间关系校验，确保更长租期不会更贵 / Cross-segment validation so longer rentals do not become more expensive. */
const relationError = computed(() => {
  if (state.extraLongRentHourRateMultiplier > state.longRentHourRateMultiplier) {
    return "The >72h multiplier should not be higher than the 24h-72h multiplier.";
  }
  return "";
});

/** 仅展示最近编辑字段的即时提示 / Show inline feedback for the most recently edited field. */
const inlineValidationMessage = computed(() => {
  if (lastEditedField.value === "m1") {
    return m1RangeError.value;
  }
  if (lastEditedField.value === "m2") {
    return m2RangeError.value;
  }
  return "";
});

/** 将用户输入压到后端接受的倍率范围 / Clamp user input into the backend-accepted multiplier range. */
function clampMultiplier(v: number) {
  if (Number.isNaN(v)) return MAX_MULTIPLIER;
  if (v < MIN_MULTIPLIER) return MIN_MULTIPLIER;
  if (v > MAX_MULTIPLIER) return MAX_MULTIPLIER;
  return Math.round(v * 10000) / 10000;
}

/** 读取后端值，异常时回落到安全默认值 / Read backend values and fall back safely when malformed. */
function readNumberOrFallback(value: unknown, fallback: number) {
  const num = typeof value === "number" ? value : Number(value);
  if (!Number.isFinite(num)) return fallback;
  if (num <= 0) return fallback;
  return clampMultiplier(num);
}

/** 按 T1/T2 分段计算长租费用 / Calculate long-rent cost by T1/T2 pricing segments. */
function computeSegmentedTotal(rate: number, hours: number, m1: number, m2: number) {
  const t1 = state.longRentThresholdHours;
  const t2 = state.extraLongRentThresholdHours;
  if (hours <= t1) return rate * hours;
  if (hours <= t2) return rate * t1 + rate * m1 * (hours - t1);
  return rate * t1 + rate * m1 * (t2 - t1) + rate * m2 * (hours - t2);
}

/** 保存预览表，比较修改前后的典型租期费用 / Save preview rows comparing before and after totals for typical durations. */
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

/** 图表拖拽手柄的横向锚点 / Horizontal anchors for the draggable chart handles. */
function secondAnchorX() {
  return state.longRentThresholdHours + (state.extraLongRentThresholdHours - state.longRentThresholdHours) / 2;
}

function thirdAnchorX() {
  return state.extraLongRentThresholdHours + (H_MAX - state.extraLongRentThresholdHours) / 2;
}

/** ECharts 阶梯线数据，表达不同租期区间的有效倍率 / ECharts step-line data for effective multipliers across duration segments. */
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

/** 折扣曲线配置和可拖拽控制点 / Discount curve option with draggable control handles. */
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

/** 根据当前坐标系刷新拖拽手柄位置 / Refresh handle positions from the current chart coordinate system. */
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

/** 单字段输入规整，不隐式改动另一个倍率 / Normalize one field without implicitly changing the other multiplier. */
function applySingleFieldNormalization(source: "m1" | "m2", value: number) {
  const normalized = clampMultiplier(value);
  if (source === "m1") {
    state.longRentHourRateMultiplier = normalized;
    return;
  }
  state.extraLongRentHourRateMultiplier = normalized;
}

/** 处理图表手柄拖拽，将像素坐标转换回倍率 / Handle chart dragging by converting pixel position back to a multiplier. */
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

/** 输入框和图表之间保持双向同步 / Keep form inputs and the chart in sync. */
function syncChartFromInputs() {
  state.longRentHourRateMultiplier = clampMultiplier(state.longRentHourRateMultiplier);
  state.extraLongRentHourRateMultiplier = clampMultiplier(state.extraLongRentHourRateMultiplier);
  nextTick(() => refreshHandlePositions());
}

/** 记录最后编辑字段并刷新曲线 / Track the last edited field and refresh the curve. */
function onInputChange(kind: "m1" | "m2") {
  lastEditedField.value = kind;
  if (kind === "m1") {
    applySingleFieldNormalization("m1", state.longRentHourRateMultiplier);
  } else {
    applySingleFieldNormalization("m2", state.extraLongRentHourRateMultiplier);
  }
  syncChartFromInputs();
}

/** 判断当前输入是否真的改变了设置 / Detect whether current inputs changed the original settings. */
function hasPendingChanges() {
  return (
    Math.abs(state.longRentHourRateMultiplier - originalState.longRentHourRateMultiplier) > 0.00001 ||
    Math.abs(state.extraLongRentHourRateMultiplier - originalState.extraLongRentHourRateMultiplier) > 0.00001
  );
}

/** 打开保存预览前执行所有阻塞校验 / Run blocking validation before opening the save preview. */
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

/** 确认保存时只提交两个可编辑倍率 / Submit only the two editable multipliers after confirmation. */
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

/** 将父组件传入的后端设置同步到本地编辑状态 / Sync backend settings from props into local edit state. */
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

/** 关闭主弹窗并清理预览层 / Close the main dialog and reset the preview layer. */
function handleClose() {
  previewVisible.value = false;
  emit("update:visible", false);
}

/** 日志时间展示格式化 / Format timestamps for the audit log. */
function formatDateTime(v: string) {
  if (!v) return "-";
  return String(v).replace("T", " ").substring(0, 19);
}

/** 打开弹窗时刷新表单和日志 / Refresh form state and logs when the dialog opens. */
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

/** 后端设置异步更新时刷新打开中的弹窗 / Refresh an open dialog when backend settings update asynchronously. */
watch(
  () => props.settings,
  () => {
    if (props.visible) {
      syncStateFromProps();
    }
  },
  { deep: true }
);

/** 窗口尺寸变化后重新定位图表手柄 / Reposition chart handles after window resizing. */
onMounted(() => {
  window.addEventListener("resize", refreshHandlePositions);
});

onUnmounted(() => {
  window.removeEventListener("resize", refreshHandlePositions);
});
</script>

<style scoped>
/* 表单与图表布局 / Form and chart layout. */
.form-panel {
  padding: var(--ui-space-4);
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

/* 状态提示与日志区域 / Status hints and audit log area. */
.updated-at {
  margin-top: var(--ui-space-3);
  display: inline-block;
  color: var(--ui-text-muted);
}

.status-alert {
  margin-top: var(--ui-space-2);
}

.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--ui-space-3);
  margin-bottom: var(--ui-space-3);
  padding: var(--ui-space-3);
  border: 1px solid var(--ui-border-soft);
  border-radius: var(--ui-radius-sm);
  background: var(--ui-bg-surface-soft);
}

.log-title {
  margin-bottom: 0;
}

.log-table {
  margin-top: var(--ui-space-2);
}

/* 保存预览与价格差异标记 / Save preview and price-delta markers. */
.preview-alert {
  margin-bottom: var(--ui-space-3);
}

.preview-table {
  margin-top: var(--ui-space-4);
}

.delta-up {
  color: var(--ui-color-warning-600);
  font-weight: 600;
}

.delta-down {
  color: var(--ui-color-success-600);
  font-weight: 600;
}

/* Element Plus 间距和响应式调整 / Element Plus spacing and responsive adjustments. */
:deep(.el-divider--horizontal) {
  margin: var(--ui-space-5) 0 var(--ui-space-4);
}

@media (max-width: 768px) {
  .chart {
    height: 300px;
    margin-top: var(--ui-space-3);
  }

  .form-panel {
    padding: var(--ui-space-2);
  }

  .log-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
