<template>
  <el-card>
    <template #header>
      <div class="header">
        <span>Feedback Management</span>
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
          <el-button type="primary" :loading="loading" @click="load">Search</el-button>
        </el-space>
      </div>
    </template>

    <el-table :data="rows" stripe v-loading="loading">
      <el-table-column prop="id" label="Feedback ID" width="100" />
      <el-table-column prop="userId" label="User ID" width="100" />
      <el-table-column prop="scooterId" label="Scooter ID" width="100" />
      <el-table-column prop="priority" label="Priority" width="120" />
      <el-table-column label="Status" width="120">
        <template #default="{ row }">
          <el-tag :type="row.resolved ? 'success' : 'warning'">
            {{ row.resolved ? "Resolved" : "Unresolved" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="Feedback Content" min-width="320" />
      <el-table-column label="Actions" width="160">
        <template #default="{ row }">
          <el-button
            type="primary"
            link
            :disabled="row.resolved"
            @click="resolve(row.id)"
          >
            Mark as Resolved
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="pager.size"
        :current-page="pager.page"
        @current-change="onPageChange"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { onMounted, reactive, ref } from "vue";

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

async function resolve(feedbackId: number) {
  try {
    await updateFeedback(feedbackId, "resolved");
    ElMessage.success("Feedback marked as resolved");
    await load();
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to update feedback");
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

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
