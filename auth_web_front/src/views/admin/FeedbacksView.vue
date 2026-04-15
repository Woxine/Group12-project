<template>
  <el-card shadow="never" class="feedbacks-container">
    <template #header>
      <div class="header">
        <span class="page-title">Feedback Management</span>
        <div class="toolbar">
          <el-space>
            <el-select v-model="filters.priority" clearable placeholder="Priority" style="width: 140px">
              <el-option label="LOW" value="LOW" />
              <el-option label="HIGH" value="HIGH" />
            </el-select>
            <el-select
              v-model="resolvedView"
              clearable
              placeholder="Resolution"
              style="width: 160px"
              @change="onResolvedChange"
            >
              <el-option label="Unresolved" :value="false" />
              <el-option label="Resolved" :value="true" />
            </el-select>
            <el-button type="primary" :icon="Search" :loading="loading" @click="load">Search</el-button>
          </el-space>
        </div>
      </div>
    </template>

    <el-table :data="rows" stripe v-loading="loading" class="data-table">
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="userId" label="User ID" width="100" align="center" />
      <el-table-column prop="scooterId" label="Scooter ID" width="120" align="center" />
      
      <el-table-column prop="priority" label="Priority" width="120" align="center">
        <template #default="{ row }">
          <el-tag
            :type="row.priority === 'HIGH' ? 'danger' : 'info'"
            effect="light"
            round
          >
            {{ row.priority }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="Status" width="120" align="center">
        <template #default="{ row }">
          <el-tag 
            :type="row.resolved ? 'success' : 'warning'"
            effect="dark"
            round
            size="small"
          >
            <el-icon v-if="row.resolved" style="margin-right: 4px"><Select /></el-icon>
            <el-icon v-else style="margin-right: 4px"><Warning /></el-icon>
            {{ row.resolved ? "Resolved" : "Unresolved" }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column prop="content" label="Feedback Content" min-width="320" show-overflow-tooltip />
      
      <el-table-column label="Actions" width="160" align="center" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="!row.resolved"
            type="success"
            link
            :icon="CircleCheck"
            @click="resolve(row.id)"
          >
            Mark Resolved
          </el-button>
          <span v-else class="resolved-text">
            <el-icon><Check /></el-icon> Completed
          </span>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager-container">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-sizes="[10, 20, 50]"
        v-model:page-size="pager.size"
        v-model:current-page="pager.page"
        @current-change="onPageChange"
        @size-change="onSizeChange"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { onMounted, reactive, ref } from "vue";
import { Search, CircleCheck, Check, Warning, Select } from '@element-plus/icons-vue';

import { getFeedbacks, updateFeedback } from "@/api/admin";
import type { FeedbackItem } from "@/types/api";

const loading = ref(false);
const rows = ref<FeedbackItem[]>([]);
const total = ref(0);
const pager = reactive({ page: 1, size: 10 });
const filters = reactive<{ resolved?: boolean; priority?: string }>({});
const resolvedView = ref<boolean | undefined>(undefined);

function onResolvedChange(value?: boolean) {
  filters.resolved = value;
}

async function load() {
  loading.value = true;
  try {
    const result = await getFeedbacks({
      resolved: filters.resolved,
      priority: filters.priority || undefined,
      page: pager.page,
      size: pager.size
    });
    rows.value = result.data;
    total.value = result.total ?? 0;
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to load feedback");
  } finally {
    loading.value = false;
  }
}

function onPageChange(page: number) {
  pager.page = page;
  load();
}

function onSizeChange(size: number) {
  pager.size = size;
  pager.page = 1;
  load();
}

async function resolve(feedbackId: number) {
  try {
    await updateFeedback(feedbackId, "resolved");
    ElMessage.success("Feedback successfully marked as resolved");
    await load();
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to update feedback status");
  }
}

onMounted(load);
</script>

<style scoped>
.feedbacks-container {
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

.data-table {
  margin-top: 16px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.pager-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding: 8px 0;
}

.resolved-text {
  color: #67C23A;
  font-size: 13px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
</style>