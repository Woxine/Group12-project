<template>
  <section class="analytics-container" role="region" aria-labelledby="analytics-heading">
    <el-card shadow="never" class="control-card admin-page-card">
      <div class="header admin-page-header">
        <div>
          <h1 id="analytics-heading" class="page-title admin-page-title">Management Analytics</h1>
          <div class="admin-page-subtitle">Operational KPIs, trends, and fleet health in one dashboard.</div>
        </div>
        <el-space wrap class="admin-page-toolbar">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="to"
            start-placeholder="Start date"
            end-placeholder="End date"
            value-format="YYYY-MM-DD"
            aria-label="Select analytics date range"
          />
          <el-button type="primary" :icon="Refresh" :loading="loading" aria-label="Refresh analytics data" @click="load">Refresh</el-button>
        </el-space>
      </div>
    </el-card>

    <div class="kpi-grid">
      <el-card shadow="hover" class="kpi-card small-card admin-kpi-card">
        <div class="stat-icon admin-kpi-icon admin-kpi-icon--primary"><el-icon><Tickets /></el-icon></div>
        <div class="stat-info">
          <div class="stat-title">Total Orders</div>
          <div class="stat-value">{{ overview.orderStats.totalOrders }}</div>
          <div class="stat-desc">Valid: {{ overview.orderStats.validOrders }}</div>
        </div>
      </el-card>
      <el-card shadow="hover" class="kpi-card small-card admin-kpi-card">
        <div class="stat-icon admin-kpi-icon admin-kpi-icon--success"><el-icon><Money /></el-icon></div>
        <div class="stat-info">
          <div class="stat-title">Total Revenue</div>
          <div class="stat-value">£{{ overview.revenueStats.totalRevenue.toFixed(2) }}</div>
          <div class="stat-desc">Avg: £{{ overview.revenueStats.averageOrderValue.toFixed(2) }}</div>
        </div>
      </el-card>
      <el-card shadow="hover" class="kpi-card small-card admin-kpi-card">
        <div class="stat-icon admin-kpi-icon admin-kpi-icon--neutral"><el-icon><Bicycle /></el-icon></div>
        <div class="stat-info">
          <div class="stat-title">Scooter Usage</div>
          <div class="stat-value">{{ usageRatePercent }}%</div>
          <div class="stat-desc">Rented: {{ overview.vehicleStats.rentedScooters }}</div>
        </div>
      </el-card>
      <el-card shadow="hover" class="kpi-card small-card admin-kpi-card">
        <div class="stat-icon admin-kpi-icon admin-kpi-icon--warning"><el-icon><Warning /></el-icon></div>
        <div class="stat-info">
          <div class="stat-title">Fault Reports</div>
          <div class="stat-value">{{ overview.faultStats.totalFeedbacks }}</div>
          <div class="stat-desc">Resolved: {{ overview.faultStats.resolvedFeedbacks }}</div>
        </div>
      </el-card>
    </div>

    <div class="analytics-grid">
      <el-card shadow="hover" class="big-card">
        <template #header>
          <div class="admin-card-title"><el-icon><TrendCharts /></el-icon> Orders & Revenue Trend</div>
        </template>
        <p id="trend-chart-desc" class="chart-a11y-desc">Bar and line chart comparing daily order counts and revenue over the selected range.</p>
        <div class="chart-accessible" role="img" aria-labelledby="trend-chart-desc">
          <v-chart class="chart" :option="trendChartOption" autoresize aria-hidden="true" />
        </div>
      </el-card>

      <el-card shadow="hover" class="medium-card hot-dates-card">
        <template #header>
          <div class="hot-header">
            <div class="admin-card-title"><el-icon><Calendar /></el-icon> Popular Rental Dates</div>
            <el-radio-group
              v-model="hotRangeKey"
              size="small"
              @change="onHotRangeChange"
            >
              <el-radio-button label="WEEK">Last 7 Days</el-radio-button>
              <el-radio-button label="HALF_MONTH">Last 15 Days</el-radio-button>
              <el-radio-button label="MONTH">Last 30 Days</el-radio-button>
              <el-radio-button label="QUARTER">Last 90 Days</el-radio-button>
            </el-radio-group>
          </div>
        </template>

        <el-skeleton :loading="hotDatesLoading" animated :rows="5">
          <template #default>
            <div class="hot-summary">
              <el-statistic title="Top Date" :value="topHotDateLabel" />
              <el-statistic title="Top Orders" :value="topHotOrders" />
            </div>
            <el-table :data="hotDates" size="small" max-height="270" class="admin-data-table">
              <el-table-column prop="rank" label="#" width="60" />
              <el-table-column prop="date" label="Date" min-width="120" />
              <el-table-column prop="orderCount" label="Orders" width="90" align="right" />
              <el-table-column label="Revenue" width="100" align="right">
                <template #default="{ row }">
                  £{{ Number(row.revenue).toFixed(2) }}
                </template>
              </el-table-column>
            </el-table>
          </template>
        </el-skeleton>
      </el-card>

      <el-card shadow="hover" class="big-card">
        <template #header>
          <div class="admin-card-title"><el-icon><Calendar /></el-icon> Riding Activity Heatmap</div>
        </template>
        <p id="heatmap-desc" class="chart-a11y-desc">Calendar heatmap showing daily riding activity intensity for the selected date range.</p>
        <div class="chart-accessible" role="img" aria-labelledby="heatmap-desc">
          <v-chart class="chart" :option="heatmapOption" autoresize aria-hidden="true" />
        </div>
        <div class="heatmap-stats">
          <el-statistic title="Avg Daily Rides" :value="averageDailyRides" />
          <el-statistic title="Max Daily Rides" :value="maxDailyRides" />
        </div>
      </el-card>

      <el-card shadow="hover" class="medium-card">
        <template #header>
          <div class="admin-card-title"><el-icon><PieChart /></el-icon> Vehicle Status Distribution</div>
        </template>
        <p id="vehicle-chart-desc" class="chart-a11y-desc">Donut chart showing available, rented, and maintenance scooter counts.</p>
        <div class="chart-accessible" role="img" aria-labelledby="vehicle-chart-desc">
          <v-chart class="chart compact-chart" :option="vehicleStatusOption" autoresize aria-hidden="true" />
        </div>
      </el-card>

      <el-card shadow="hover" class="medium-card">
        <template #header>
          <div class="admin-card-title"><el-icon><SuccessFilled /></el-icon> Fault Resolution Status</div>
        </template>
        <p id="fault-resolution-desc" class="chart-a11y-desc">Pie chart comparing resolved and unresolved fault reports.</p>
        <div class="chart-accessible" role="img" aria-labelledby="fault-resolution-desc">
          <v-chart class="chart compact-chart" :option="faultResolutionOption" autoresize aria-hidden="true" />
        </div>
      </el-card>

      <el-card shadow="hover" class="medium-card">
        <template #header>
          <div class="admin-card-title"><el-icon><DataAnalysis /></el-icon> Fault Priority Distribution</div>
        </template>
        <p id="fault-priority-desc" class="chart-a11y-desc">Horizontal bar chart of fault counts by priority level.</p>
        <div class="chart-accessible" role="img" aria-labelledby="fault-priority-desc">
          <v-chart class="chart compact-chart" :option="faultPriorityOption" autoresize aria-hidden="true" />
        </div>
      </el-card>
    </div>

    <el-card shadow="never" class="chart-summary-card" aria-labelledby="chart-summary-heading">
      <h2 id="chart-summary-heading">Text summary of key analytics</h2>
      <ul>
        <li>{{ chartSummary.totalOrders }}</li>
        <li>{{ chartSummary.totalRevenue }}</li>
        <li>{{ chartSummary.vehicleUsage }}</li>
        <li>{{ chartSummary.faultStatus }}</li>
        <li>{{ chartSummary.activity }}</li>
      </ul>
    </el-card>
    <div class="sr-only" role="status" aria-live="polite" aria-atomic="true">{{ liveMessage }}</div>
  </section>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { computed, onMounted, reactive, ref } from "vue";
import { getDashboardOverview, getPopularRentalDates } from "@/api/admin";
import {
  ADMIN_CHART_COLOR_TOKENS,
  getAdminPriorityTagType,
  getScooterStatusTagType,
  type AdminChartColorKey
} from "@/adminStatus";
import type { DashboardOverview } from "@/types/api";
import type { PopularRentalDate } from "@/types/api";

import { Refresh, Tickets, Money, Bicycle, Warning, TrendCharts, PieChart, SuccessFilled, DataAnalysis, Calendar } from '@element-plus/icons-vue';

import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { BarChart, LineChart, PieChart as EchartsPieChart, HeatmapChart } from 'echarts/charts';
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  VisualMapComponent,
  CalendarComponent
} from 'echarts/components';
import VChart from 'vue-echarts';

use([
  CanvasRenderer,
  BarChart,
  LineChart,
  EchartsPieChart,
  HeatmapChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  VisualMapComponent,
  CalendarComponent
]);

const loading = ref(false);
const dateRange = ref<[string, string] | null>(getDefaultRange());
const hotDatesLoading = ref(false);
const hotRangeKey = ref<"WEEK" | "HALF_MONTH" | "MONTH" | "QUARTER">("WEEK");
const hotDates = ref<PopularRentalDate[]>([]);
const liveMessage = ref("");
const overview = reactive<DashboardOverview>({
  orderStats: {
    totalOrders: 0,
    validOrders: 0,
    cancelledOrders: 0,
    cancellationRate: 0
  },
  revenueStats: {
    totalRevenue: 0,
    totalOrders: 0,
    averageOrderValue: 0
  },
  vehicleStats: {
    totalScooters: 0,
    rentedScooters: 0,
    maintenanceScooters: 0,
    availableScooters: 0,
    usageRate: 0
  },
  faultStats: {
    totalFeedbacks: 0,
    resolvedFeedbacks: 0,
    unresolvedFeedbacks: 0,
    priorityDistribution: {}
  },
  dailyTrend: []
});

const usageRatePercent = computed(() => Number(((overview.vehicleStats.usageRate ?? 0) * 100).toFixed(2)));

function uiColor(tokenName: string, fallback: string) {
  if (typeof window === "undefined") {
    return fallback;
  }
  return getComputedStyle(document.documentElement).getPropertyValue(tokenName).trim() || fallback;
}

function chartColor(key: AdminChartColorKey) {
  const color = ADMIN_CHART_COLOR_TOKENS[key];
  return uiColor(color.token, color.fallback);
}

const averageDailyRides = computed(() => {
  if (overview.dailyTrend.length === 0) return 0;
  const sum = overview.dailyTrend.reduce((acc, curr) => acc + curr.orderCount, 0);
  return Math.round(sum / overview.dailyTrend.length);
});

const maxDailyRides = computed(() => {
  if (overview.dailyTrend.length === 0) return 0;
  return Math.max(...overview.dailyTrend.map(d => d.orderCount));
});

const topHotDate = computed(() => hotDates.value[0]);
const topHotDateLabel = computed(() => topHotDate.value?.date ?? "-");
const topHotOrders = computed(() => topHotDate.value?.orderCount ?? 0);

const chartSummary = computed(() => ({
  totalOrders: `Total orders: ${overview.orderStats.totalOrders}.`,
  totalRevenue: `Total revenue: ${overview.revenueStats.totalRevenue.toFixed(2)} pounds.`,
  vehicleUsage: `Scooter usage rate: ${usageRatePercent.value} percent, with ${overview.vehicleStats.rentedScooters} currently rented scooters.`,
  faultStatus: `Fault reports: ${overview.faultStats.totalFeedbacks} total, ${overview.faultStats.resolvedFeedbacks} resolved, ${overview.faultStats.unresolvedFeedbacks} unresolved.`,
  activity: `Riding activity averages ${averageDailyRides.value} rides per day and peaks at ${maxDailyRides.value} rides in a day.`
}));

function announce(message: string) {
  liveMessage.value = "";
  window.setTimeout(() => {
    liveMessage.value = message;
  }, 0);
}

// 1. Orders & Revenue Trend Option
const trendChartOption = computed(() => {
  const dates = overview.dailyTrend.map(d => d.date);
  const orders = overview.dailyTrend.map(d => d.orderCount);
  const revenues = overview.dailyTrend.map(d => d.revenue);
  const primary = chartColor("primary");
  const success = chartColor("success");

  return {
    tooltip: { trigger: 'axis' },
    legend: {
      data: ['Orders', 'Revenue'],
      top: 8
    },
    grid: { left: '3%', right: '4%', top: 56, bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: dates },
    yAxis: [
      { type: 'value', name: 'Orders', position: 'left' },
      { type: 'value', name: 'Revenue (£)', position: 'right', axisLabel: { formatter: '£{value}' } }
    ],
    series: [
      {
        name: 'Orders',
        type: 'bar',
        data: orders,
        itemStyle: { color: primary, borderRadius: [4, 4, 0, 0] },
        yAxisIndex: 0
      },
      {
        name: 'Revenue',
        type: 'line',
        data: revenues,
        itemStyle: { color: success },
        areaStyle: { opacity: 0.1 },
        yAxisIndex: 1
      }
    ]
  };
});

// 2. Vehicle Status Donut Chart Option
const vehicleStatusOption = computed(() => {
  const availableColor = chartColor(getScooterStatusTagType("AVAILABLE"));
  const rentedColor = chartColor(getScooterStatusTagType("RENTED"));
  const maintenanceColor = chartColor(getScooterStatusTagType("MAINTENANCE"));
  const surface = uiColor("--ui-bg-surface", "#ffffff");

  return {
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [
      {
        name: 'Vehicle Status',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: surface,
          borderWidth: 2
        },
        label: { show: false, position: 'center' },
        emphasis: {
          label: { show: true, fontSize: 20, fontWeight: 'bold' }
        },
        labelLine: { show: false },
        data: [
          { value: overview.vehicleStats.availableScooters, name: 'Available', itemStyle: { color: availableColor } },
          { value: overview.vehicleStats.rentedScooters, name: 'Rented', itemStyle: { color: rentedColor } },
          { value: overview.vehicleStats.maintenanceScooters, name: 'Maintenance', itemStyle: { color: maintenanceColor } }
        ]
      }
    ]
  };
});

// 3. Fault Resolution Pie Chart Option
const faultResolutionOption = computed(() => {
  const success = chartColor("success");
  const warning = chartColor("warning");
  const shadow = uiColor("--ui-text-strong", "#0f172a");

  return {
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [
      {
        name: 'Fault Resolution',
        type: 'pie',
        radius: '50%',
        data: [
          { value: overview.faultStats.resolvedFeedbacks, name: 'Resolved', itemStyle: { color: success } },
          { value: overview.faultStats.unresolvedFeedbacks, name: 'Unresolved', itemStyle: { color: warning } }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: shadow
          }
        }
      }
    ]
  };
});

// 4. Fault Priority Horizontal Bar Chart Option
const faultPriorityOption = computed(() => {
  const distribution = overview.faultStats.priorityDistribution || {};
  let priorities = Object.keys(distribution);
  let counts = Object.values(distribution);
  
  if (priorities.length === 0) {
    // Show default empty categories if no data
    priorities = ['LOW', 'HIGH'];
    counts = [0, 0];
  }

  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value', minInterval: 1 },
    yAxis: { type: 'category', data: priorities },
    series: [
      {
        name: 'Faults',
        type: 'bar',
        data: counts.map((value, index) => ({
          value,
          itemStyle: { color: chartColor(getAdminPriorityTagType(priorities[index])), borderRadius: [0, 4, 4, 0] }
        }))
      }
    ]
  };
});

// 5. Activity Heatmap Option
const heatmapOption = computed(() => {
  const data = overview.dailyTrend.map(d => [d.date, d.orderCount]);
  const surface = uiColor("--ui-bg-surface", "#ffffff");
  const heatmapRange = [
    uiColor("--ui-color-primary-50", "#eef4ff"),
    uiColor("--ui-color-primary-100", "#dbe7ff"),
    uiColor("--ui-color-primary-500", "#3b82f6"),
    uiColor("--ui-color-primary-600", "#2563eb"),
    uiColor("--ui-color-primary-700", "#1d4ed8")
  ];
  
  let start = dateRange.value?.[0] ?? '2026-01-01';
  let end = dateRange.value?.[1] ?? '2026-12-31';

  return {
    tooltip: {
      position: 'top',
      formatter: function (p: any) {
        return p.data[0] + ': ' + p.data[1] + ' orders';
      }
    },
    visualMap: {
      min: 0,
      max: Math.max(1, ...overview.dailyTrend.map(d => d.orderCount)),
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: '5%',
      itemWidth: 15,
      inRange: {
        color: heatmapRange
      }
    },
    calendar: {
      top: 40,
      left: 20,
      right: 20,
      cellSize: ['auto', 20],
      range: [start, end],
      itemStyle: {
        borderWidth: 1,
        borderColor: surface
      },
      splitLine: { show: false },
      yearLabel: { show: false }
    },
    series: {
      type: 'heatmap',
      coordinateSystem: 'calendar',
      data: data
    }
  };
});

async function load() {
  loading.value = true;
  try {
    const payload = await getDashboardOverview({
      start_date: dateRange.value?.[0],
      end_date: dateRange.value?.[1]
    });
    Object.assign(overview, payload);
    announce("Analytics data loaded.");
  } catch (error: any) {
    const message = error?.response?.data?.message ?? "Failed to load dashboard data";
    ElMessage.error(message);
    announce(message);
  } finally {
    loading.value = false;
  }
}

async function loadHotDates() {
  hotDatesLoading.value = true;
  const [start, end] = getHotRangeDates(hotRangeKey.value);
  try {
    hotDates.value = await getPopularRentalDates({ start_date: start, end_date: end });
  } catch (error: any) {
    const message = error?.response?.data?.message ?? "Failed to load popular rental dates";
    ElMessage.error(message);
    announce(message);
  } finally {
    hotDatesLoading.value = false;
  }
}

function onHotRangeChange() {
  loadHotDates();
}

onMounted(load);
onMounted(loadHotDates);

function getDefaultRange(): [string, string] {
  const end = new Date();
  const start = new Date();
  start.setDate(end.getDate() - 30); // Default to last 30 days for better heatmap
  return [formatDate(start), formatDate(end)];
}

function formatDate(date: Date) {
  const year = date.getFullYear();
  const month = `${date.getMonth() + 1}`.padStart(2, "0");
  const day = `${date.getDate()}`.padStart(2, "0");
  return `${year}-${month}-${day}`;
}

function getHotRangeDates(range: "WEEK" | "HALF_MONTH" | "MONTH" | "QUARTER"): [string, string] {
  const end = new Date();
  const start = new Date();
  const daysByRange = {
    WEEK: 7,
    HALF_MONTH: 15,
    MONTH: 30,
    QUARTER: 90
  } as const;
  start.setDate(end.getDate() - (daysByRange[range] - 1));
  return [formatDate(start), formatDate(end)];
}

</script>

<style scoped>
.analytics-container {
  display: flex;
  flex-direction: column;
  gap: var(--ui-space-4);
}

.control-card {
  border-radius: var(--ui-radius-lg);
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: var(--ui-space-4);
}

.kpi-card {
  min-height: 120px;
}

.small-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  padding: var(--ui-space-5);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--ui-radius-md);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28px;
  margin-right: var(--ui-space-4);
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: var(--ui-text-muted);
  margin-bottom: var(--ui-space-2);
}

.stat-value {
  font-size: 26px;
  font-weight: bold;
  color: var(--ui-text-strong);
  margin-bottom: var(--ui-space-2);
  line-height: 1;
}

.stat-desc {
  font-size: 13px;
  color: var(--ui-text-muted);
}

.analytics-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: var(--ui-space-4);
}

.big-card {
  grid-column: span 2;
  border-radius: var(--ui-radius-md);
  border: 1px solid var(--ui-border-soft);
}

.medium-card {
  grid-column: span 1;
  border-radius: var(--ui-radius-md);
  border: 1px solid var(--ui-border-soft);
}

.hot-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: var(--ui-space-3);
}

.hot-summary {
  display: flex;
  justify-content: space-between;
  gap: var(--ui-space-4);
  margin-bottom: var(--ui-space-3);
}

.chart {
  height: 300px;
  width: 100%;
}

.compact-chart {
  height: 250px;
}

.chart-a11y-desc {
  margin: 0 0 var(--ui-space-2);
  color: var(--ui-text-muted);
  font-size: 14px;
}

.chart-accessible {
  width: 100%;
}

.heatmap-stats {
  display: flex;
  gap: var(--ui-space-5);
  margin-top: var(--ui-space-3);
}

.chart-summary-card {
  border-radius: var(--ui-radius-md);
  border: 1px solid var(--ui-border-soft);
}

@media (max-width: 1200px) {
  .analytics-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .big-card {
    grid-column: span 2;
  }
}

@media (max-width: 768px) {
  .analytics-grid {
    grid-template-columns: 1fr;
  }

  .big-card,
  .medium-card {
    grid-column: span 1;
  }

}

.chart-summary-card h2 {
  margin: 0 0 var(--ui-space-2);
  font-size: 16px;
}

.chart-summary-card ul {
  margin: 0;
  padding-left: 20px;
  color: var(--ui-text-default);
}
</style>