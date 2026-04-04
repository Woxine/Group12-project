<template>
  <el-card>
    <template #header>
      <div class="header">
        <span>Revenue Overview</span>
        <el-space>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="to"
            start-placeholder="Start date"
            end-placeholder="End date"
            value-format="YYYY-MM-DD"
          />
          <el-button type="primary" :loading="loading" @click="load">Search</el-button>
        </el-space>
      </div>
    </template>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="8">
        <el-statistic title="Total Revenue" :value="stats.totalRevenue || 0" />
      </el-col>
      <el-col :span="8">
        <el-statistic title="Total Orders" :value="stats.totalOrders || 0" />
      </el-col>
      <el-col :span="8">
        <el-statistic title="Average Order Value" :value="stats.averageOrderValue || 0" />
      </el-col>
    </el-row>

    <el-divider />

    <div class="tables">
      <el-card shadow="never">
        <template #header>Duration Breakdown (Selected Range)</template>
        <el-table :data="durationData" stripe>
          <el-table-column prop="durationType" label="Duration Type" />
          <el-table-column prop="totalOrders" label="Orders" />
          <el-table-column prop="totalRevenue" label="Revenue" />
        </el-table>
      </el-card>

      <el-card shadow="never">
        <template #header>Duration Breakdown (This Week)</template>
        <el-table :data="weeklyDurationData" stripe>
          <el-table-column prop="durationType" label="Duration Type" />
          <el-table-column prop="totalOrders" label="Orders" />
          <el-table-column prop="totalRevenue" label="Revenue" />
        </el-table>
      </el-card>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { onMounted, reactive, ref } from "vue";

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
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-row {
  margin-top: 12px;
}

.tables {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
</style>
