<template>
  <el-card shadow="never" class="revenue-container">
    <template #header>
      <div class="header">
        <span class="page-title">Revenue Overview</span>
        <el-space>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="to"
            start-placeholder="Start date"
            end-placeholder="End date"
            value-format="YYYY-MM-DD"
          />
          <el-button type="primary" :icon="Search" :loading="loading" @click="load">Search</el-button>
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
        <el-table :data="durationData" stripe v-loading="loading">
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
        <el-table :data="weeklyDurationData" stripe v-loading="loading">
          <el-table-column prop="durationType" label="Duration Type" />
          <el-table-column prop="totalOrders" label="Orders" align="right" />
          <el-table-column label="Revenue" align="right">
            <template #default="{ row }">
              £{{ Number(row.totalRevenue).toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { onMounted, reactive, ref } from "vue";
import { Search, Money, Tickets, DataAnalysis, Calendar } from '@element-plus/icons-vue';

import { getRevenueByDuration, getRevenueStats, getWeeklyRevenueByDuration } from "@/api/admin";
import type { DurationRevenue, RevenueStats } from "@/types/api";

const loading = ref(false);
const dateRange = ref<[string, string] | null>(null);
const stats = reactive<RevenueStats>({
  totalRevenue: 0,
  totalOrders: 0,
  averageOrderValue: 0
});
const durationData = ref<DurationRevenue[]>([]);
const weeklyDurationData = ref<DurationRevenue[]>([]);

async function load() {
  loading.value = true;
  try {
    const params = {
      start_date: dateRange.value?.[0],
      end_date: dateRange.value?.[1]
    };
    const [statsRes, durationRes, weeklyRes] = await Promise.all([
      getRevenueStats(params),
      getRevenueByDuration(params),
      getWeeklyRevenueByDuration()
    ]);
    stats.totalRevenue = statsRes.totalRevenue ?? 0;
    stats.totalOrders = statsRes.totalOrders ?? 0;
    stats.averageOrderValue = statsRes.averageOrderValue ?? 0;
    durationData.value = durationRes;
    weeklyDurationData.value = weeklyRes;
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to load revenue data");
  } finally {
    loading.value = false;
  }
}

onMounted(load);
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
  color: #909399;
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
  grid-template-columns: 1fr 1fr;
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