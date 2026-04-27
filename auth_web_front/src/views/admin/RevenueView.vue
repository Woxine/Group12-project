<template>
  <el-card shadow="never" class="revenue-container" role="region" aria-labelledby="revenue-overview-heading">
    <template #header>
      <div class="header">
        <h1 id="revenue-overview-heading" class="page-title">Revenue Overview</h1>
        <el-space>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="to"
            start-placeholder="Start date"
            end-placeholder="End date"
            value-format="YYYY-MM-DD"
            aria-label="Select revenue date range"
          />
          <el-button type="primary" :icon="Search" :loading="loading" aria-label="Search revenue data" @click="load">Search</el-button>
        </el-space>
      </div>
    </template>

    <el-row :gutter="24" class="stats-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card green">
          <div class="stat-icon"><el-icon><Money /></el-icon></div>
          <div class="stat-info">
            <div class="stat-title">Total Revenue</div>
            <div class="stat-value">£{{ (stats.totalRevenue || 0).toFixed(2) }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card blue">
          <div class="stat-icon"><el-icon><Tickets /></el-icon></div>
          <div class="stat-info">
            <div class="stat-title">Total Orders</div>
            <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card purple">
          <div class="stat-icon"><el-icon><DataAnalysis /></el-icon></div>
          <div class="stat-info">
            <div class="stat-title">Average Order Value</div>
            <div class="stat-value">£{{ (stats.averageOrderValue || 0).toFixed(2) }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="tables">
      <el-card shadow="hover" class="table-card">
        <template #header>
          <div class="table-title">
            <el-icon><Calendar /></el-icon> Duration Breakdown (Selected Range)
          </div>
        </template>
        <p id="duration-selected-range-desc" class="sr-only">Revenue by rental duration in the selected date range.</p>
        <el-table :data="durationData" stripe v-loading="loading" aria-label="Duration breakdown for selected range" aria-describedby="duration-selected-range-desc">
          <el-table-column prop="durationType" label="Duration Type" />
          <el-table-column prop="totalOrders" label="Orders" align="right" />
          <el-table-column label="Revenue" align="right">
            <template #default="{ row }">
              £{{ Number(row.totalRevenue).toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card shadow="hover" class="table-card">
        <template #header>
          <div class="table-title">
            <el-icon><Calendar /></el-icon> Duration Breakdown (This Week)
          </div>
        </template>
        <p id="duration-weekly-desc" class="sr-only">Revenue by rental duration for the current week.</p>
        <el-table :data="weeklyDurationData" stripe v-loading="loading" aria-label="Duration breakdown this week" aria-describedby="duration-weekly-desc">
          <el-table-column prop="durationType" label="Duration Type" />
          <el-table-column prop="totalOrders" label="Orders" align="right" />
          <el-table-column label="Revenue" align="right">
            <template #default="{ row }">
              £{{ Number(row.totalRevenue).toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card shadow="hover" class="table-card">
        <template #header>
          <div class="table-title">
            <el-icon><Calendar /></el-icon> Popular Rental Dates (This Week)
          </div>
        </template>
        <p id="popular-dates-desc" class="sr-only">Ranked list of popular rental dates this week.</p>
        <el-table :data="popularDatesData" stripe v-loading="loading" aria-label="Popular rental dates this week" aria-describedby="popular-dates-desc">
          <el-table-column prop="rank" label="Rank" width="80" align="right" />
          <el-table-column prop="date" label="Date" />
          <el-table-column prop="orderCount" label="Orders" align="right" />
          <el-table-column label="Revenue" align="right">
            <template #default="{ row }">
              £{{ Number(row.revenue).toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
    <div class="sr-only" role="status" aria-live="polite" aria-atomic="true">{{ liveMessage }}</div>
  </el-card>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { onMounted, reactive, ref } from "vue";
import { Search, Money, Tickets, DataAnalysis, Calendar } from '@element-plus/icons-vue';

import { getPopularRentalDates, getRevenueByDuration, getRevenueStats, getWeeklyRevenueByDuration } from "@/api/admin";
import type { DurationRevenue, PopularRentalDate, RevenueStats } from "@/types/api";

const loading = ref(false);
const dateRange = ref<[string, string] | null>(null);
const stats = reactive<RevenueStats>({
  totalRevenue: 0,
  totalOrders: 0,
  averageOrderValue: 0
});
const durationData = ref<DurationRevenue[]>([]);
const weeklyDurationData = ref<DurationRevenue[]>([]);
const popularDatesData = ref<PopularRentalDate[]>([]);
const liveMessage = ref("");

function announce(message: string) {
  liveMessage.value = "";
  window.setTimeout(() => {
    liveMessage.value = message;
  }, 0);
}

async function load() {
  loading.value = true;
  try {
    const params = {
      start_date: dateRange.value?.[0],
      end_date: dateRange.value?.[1]
    };
    const [statsRes, durationRes, weeklyRes, popularDatesRes] = await Promise.all([
      getRevenueStats(params),
      getRevenueByDuration(params),
      getWeeklyRevenueByDuration(),
      getPopularRentalDates(getCurrentWeekRange())
    ]);
    stats.totalRevenue = statsRes.totalRevenue ?? 0;
    stats.totalOrders = statsRes.totalOrders ?? 0;
    stats.averageOrderValue = statsRes.averageOrderValue ?? 0;
    durationData.value = durationRes;
    weeklyDurationData.value = weeklyRes;
    popularDatesData.value = popularDatesRes;
    announce("Revenue data loaded.");
  } catch (error: any) {
    const message = error?.response?.data?.message ?? "Failed to load revenue data";
    ElMessage.error(message);
    announce(message);
  } finally {
    loading.value = false;
  }
}

onMounted(load);

function getCurrentWeekRange() {
  const now = new Date();
  const day = now.getDay();
  const diffToMonday = day === 0 ? -6 : 1 - day;
  const start = new Date(now);
  start.setDate(now.getDate() + diffToMonday);
  const end = new Date(start);
  end.setDate(start.getDate() + 6);
  return {
    start_date: formatDate(start),
    end_date: formatDate(end)
  };
}

function formatDate(date: Date) {
  const year = date.getFullYear();
  const month = `${date.getMonth() + 1}`.padStart(2, "0");
  const day = `${date.getDate()}`.padStart(2, "0");
  return `${year}-${month}-${day}`;
}
</script>

<style scoped>
.revenue-container {
  border-radius: 8px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.stats-row {
  margin-top: 8px;
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 12px;
  border: none;
  background-color: #ffffff;
  height: 100%;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  padding: 24px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 32px;
  margin-right: 20px;
}

.blue .stat-icon { background: #ecf5ff; color: #409EFF; }
.green .stat-icon { background: #f0f9eb; color: #67C23A; }
.purple .stat-icon { background: #f4f4f5; color: #909399; }

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #4b5563;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.tables {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 24px;
}

.table-card {
  border-radius: 8px;
}

.table-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}
</style>