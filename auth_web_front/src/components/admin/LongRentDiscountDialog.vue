<template>
  <el-dialog :model-value="visible" title="Long-Rent Discount Adjustment" width="1080px" @close="handleClose">
    <el-row :gutter="24">
      <el-col :span="10">
        <el-form label-position="top" class="form-panel">
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
          <el-alert v-if="!auditResult.ok" :title="auditResult.message" type="warning" show-icon :closable="false" />
          <el-alert v-else title="Linked review passed: m1 >= m2." type="success" show-icon :closable="false" />
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
    <el-table :data="logs" stripe class="log-table" v-loading="logsLoading">
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

  <el-dialog v-model="previewVisible" title="Confirm Multiplier Update" width="720px" append-to-body>
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

    <el-table :data="previewRows" size="small" class="preview-table">
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
const state = reactive({
  longRentThresholdHours: 24,
  extraLongRentThresholdHours: 72,
  longRentHourRateMultiplier: 0.85,
  extraLongRentHourRateMultiplier: 0.75,
  updatedAt: ""
});
const originalState = reactive({
  longRentHourRateMultiplier: 0.85,
  extraLongRentHourRateMultiplier: 0.75
});

const auditResult = computed(() => {
  const m1 = state.longRentHourRateMultiplier;
  const m2 = state.extraLongRentHourRateMultiplier;
  if (m1 < 0.0001 || m1 > 1 || m2 < 0.0001 || m2 > 1) {
    return { ok: false, message: "Multipliers must be within [0.0001, 1.0]." };
  }
  if (m1 < m2) {
    return { ok: false, message: "Linked review failed: m1 cannot be lower than m2." };
  }
  return { ok: true, message: "OK" };
});

function clampMultiplier(v: number) {
  if (Number.isNaN(v)) return 1;
  if (v < 0.0001) return 0.0001;
  if (v > 1) return 1;
  return Math.round(v * 10000) / 10000;
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
        lineStyle: { width: 3, color: "#409EFF" },
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
        style: { fill: "#67C23A", stroke: "#fff", lineWidth: 2 },
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
        style: { fill: "#E6A23C", stroke: "#fff", lineWidth: 2 },
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

function applyLinkedReview(source: "m1" | "m2", value: number) {
  const normalized = clampMultiplier(value);
  if (source === "m1") {
    state.longRentHourRateMultiplier = normalized;
    if (state.extraLongRentHourRateMultiplier > state.longRentHourRateMultiplier) {
      state.extraLongRentHourRateMultiplier = state.longRentHourRateMultiplier;
    }
  } else {
    state.extraLongRentHourRateMultiplier = Math.min(normalized, clampMultiplier(state.longRentHourRateMultiplier));
  }
}

function onHandleDrag(kind: "m1" | "m2", target: { x: number; y: number }) {
  const chart = (chartRef.value as any)?.chart;
  if (!chart) return;
  const fixedX = kind === "m1" ? secondAnchorX() : thirdAnchorX();
  const fixedPixelX = chart.convertToPixel({ xAxisIndex: 0, yAxisIndex: 0 }, [fixedX, 0]) as number[];
  target.x = fixedPixelX[0];
  const dataCoord = chart.convertFromPixel({ xAxisIndex: 0, yAxisIndex: 0 }, [fixedPixelX[0], target.y]) as number[];
  applyLinkedReview(kind, dataCoord[1]);
  syncChartFromInputs();
}

function syncChartFromInputs() {
  applyLinkedReview("m1", state.longRentHourRateMultiplier);
  applyLinkedReview("m2", state.extraLongRentHourRateMultiplier);
  nextTick(() => refreshHandlePositions());
}

function onInputChange(kind: "m1" | "m2") {
  if (kind === "m1") {
    applyLinkedReview("m1", state.longRentHourRateMultiplier);
  } else {
    applyLinkedReview("m2", state.extraLongRentHourRateMultiplier);
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
  if (!auditResult.value.ok) {
    ElMessage.error(auditResult.value.message);
    return;
  }
  if (!hasPendingChanges()) {
    ElMessage.info("No changes to save");
    return;
  }
  previewVisible.value = true;
}

function confirmSave() {
  if (!auditResult.value.ok) {
    ElMessage.error(auditResult.value.message);
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
  state.longRentHourRateMultiplier = Number(props.settings.longRentHourRateMultiplier);
  state.extraLongRentHourRateMultiplier = Number(props.settings.extraLongRentHourRateMultiplier);
  state.updatedAt = props.settings.updatedAt ? String(props.settings.updatedAt).replace("T", " ").substring(0, 19) : "";
  originalState.longRentHourRateMultiplier = Number(props.settings.longRentHourRateMultiplier);
  originalState.extraLongRentHourRateMultiplier = Number(props.settings.extraLongRentHourRateMultiplier);
  previewVisible.value = false;
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
  background: #fff;
  border-radius: 8px;
  padding: 12px;
}

.full-width {
  width: 100%;
}

.chart {
  width: 100%;
  height: 460px;
}

.updated-at {
  margin-top: 10px;
  display: inline-block;
  color: #909399;
}

.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.log-title {
  font-weight: 600;
  color: #303133;
}

.log-table {
  margin-top: 8px;
}

.preview-alert {
  margin-bottom: 12px;
}

.preview-table {
  margin-top: 14px;
}

.delta-up {
  color: #e6a23c;
  font-weight: 600;
}

.delta-down {
  color: #67c23a;
  font-weight: 600;
}
</style>
