<template>
  <el-card>
    <template #header>
      <div class="header">
        <span>Management Analytics</span>
        <el-space>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="to"
            start-placeholder="Start date"
            end-placeholder="End date"
            value-format="YYYY-MM-DD"
          />
          <el-button type="primary" :loading="loading" @click="load">Refresh</el-button>
        </el-space>
      </div>
    </template>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-statistic title="Total Orders" :value="overview.orderStats.totalOrders" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="Valid Orders" :value="overview.orderStats.validOrders" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="Total Revenue" :value="overview.revenueStats.totalRevenue" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="Average Order Value" :value="overview.revenueStats.averageOrderValue" />
      </el-col>
    </el-row>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-statistic title="Scooter Usage Rate" :value="usageRatePercent" suffix="%" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="Rented Scooters" :value="overview.vehicleStats.rentedScooters" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="Fault Reports" :value="overview.faultStats.totalFeedbacks" />
      </el-col>
      <el-col :span="6">
        <el-statistic title="Resolved Faults" :value="overview.faultStats.resolvedFeedbacks" />
      </el-col>
    </el-row>

    <div class="charts">
      <el-card shadow="never">
        <template #header>Orders & Revenue Trend (Table)</template>
        <el-table :data="paginatedDailyTrend" stripe>
          <el-table-column prop="date" label="Date" />
          <el-table-column prop="orderCount" label="Orders" />
          <el-table-column prop="revenue" label="Revenue" />
        </el-table>
        <div class="pagination-wrap">
          <el-pagination
            v-model:current-page="trendCurrentPage"
            v-model:page-size="trendPageSize"
            :page-sizes="[10, 20, 50]"
            :total="trendTotal"
            layout="total, sizes, prev, pager, next"
          />
        </div>
      </el-card>

      <el-card shadow="never">
        <template #header>Vehicle Status Distribution</template>
        <div class="progress-group">
          <div class="progress-item">
            <span>Available</span>
            <el-progress :percentage="availablePercent" />
          </div>
          <div class="progress-item">
            <span>Rented</span>
            <el-progress :percentage="rentedPercent" status="success" />
          </div>
          <div class="progress-item">
            <span>Maintenance</span>
            <el-progress :percentage="maintenancePercent" status="warning" />
          </div>
        </div>
      </el-card>

      <el-card shadow="never">
        <template #header>Fault Priority Distribution</template>
        <el-table :data="faultPriorityRows" stripe>
          <el-table-column prop="priority" label="Priority" />
          <el-table-column prop="count" label="Count" />
        </el-table>
      </el-card>

      <el-card shadow="never">
        <template #header>Fault Resolution Status</template>
        <div class="progress-group">
          <div class="progress-item">
            <span>Resolved</span>
            <el-progress :percentage="resolvedPercent" status="success" />
          </div>
          <div class="progress-item">
            <span>Unresolved</span>
            <el-progress :percentage="unresolvedPercent" status="exception" />
          </div>
        </div>
      </el-card>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { computed, onMounted, reactive, ref } from "vue";

import { getDashboardOverview } from "@/api/admin";
import type { DashboardOverview } from "@/types/api";

const loading = ref(false);
const dateRange = ref<[string, string] | null>(getDefaultRange());
const trendCurrentPage = ref(1);
const trendPageSize = ref(10);
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
const trendTotal = computed(() => overview.dailyTrend.length);
const paginatedDailyTrend = computed(() => {
  const start = (trendCurrentPage.value - 1) * trendPageSize.value;
  const end = start + trendPageSize.value;
  return overview.dailyTrend.slice(start, end);
});
const faultPriorityRows = computed(() =>
  Object.entries(overview.faultStats.priorityDistribution ?? {}).map(([priority, count]) => ({ priority, count }))
);
const availablePercent = computed(() =>
  toPercent(overview.vehicleStats.availableScooters, overview.vehicleStats.totalScooters)
);
const rentedPercent = computed(() => toPercent(overview.vehicleStats.rentedScooters, overview.vehicleStats.totalScooters));
const maintenancePercent = computed(() =>
  toPercent(overview.vehicleStats.maintenanceScooters, overview.vehicleStats.totalScooters)
);
const resolvedPercent = computed(() =>
  toPercent(overview.faultStats.resolvedFeedbacks, overview.faultStats.totalFeedbacks)
);
const unresolvedPercent = computed(() =>
  toPercent(overview.faultStats.unresolvedFeedbacks, overview.faultStats.totalFeedbacks)
);

async function load() {
  loading.value = true;
  try {
    const payload = await getDashboardOverview({
      start_date: dateRange.value?.[0],
      end_date: dateRange.value?.[1]
    });
    Object.assign(overview, payload);
    trendCurrentPage.value = 1;
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to load dashboard data");
  } finally {
    loading.value = false;
  }
}

onMounted(load);

function getDefaultRange(): [string, string] {
  const end = new Date();
  const start = new Date();
  start.setDate(end.getDate() - 6);
  return [formatDate(start), formatDate(end)];
}

function formatDate(date: Date) {
  const year = date.getFullYear();
  const month = `${date.getMonth() + 1}`.padStart(2, "0");
  const day = `${date.getDate()}`.padStart(2, "0");
  return `${year}-${month}-${day}`;
}

function toPercent(value = 0, total = 0) {
  if (!total) return 0;
  return Number(((value / total) * 100).toFixed(2));
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-row {
  margin-top: 12px;
}

.charts {
  margin-top: 16px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.progress-group {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.progress-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.pagination-wrap {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>
