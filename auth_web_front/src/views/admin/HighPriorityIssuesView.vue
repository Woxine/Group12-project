<template>
  <el-card shadow="never" class="high-priority-container">
    <template #header>
      <div class="header">
        <span class="page-title">High Priority Issues</span>
        <el-space>
          <el-select v-model="filters.escalated" clearable placeholder="Escalation" style="width: 160px">
            <el-option label="Escalated" :value="true" />
            <el-option label="Not Escalated" :value="false" />
          </el-select>
          <el-button type="primary" :loading="loading" @click="load">Search</el-button>
        </el-space>
      </div>
    </template>

    <el-alert
      title="TODO: This page is a development skeleton for ID15."
      type="info"
      :closable="false"
      show-icon
      class="todo-alert"
    />

    <el-table :data="rows" stripe v-loading="loading">
      <el-table-column prop="feedbackId" label="Feedback ID" width="120" />
      <el-table-column prop="priority" label="Priority" width="120" />
      <el-table-column prop="escalated" label="Escalated" width="120" />
      <el-table-column prop="escalatedTo" label="Escalated To" width="180" />
      <el-table-column prop="content" label="Content" min-width="280" show-overflow-tooltip />
      <el-table-column prop="resolved" label="Resolved" width="120" />
      <el-table-column label="Actions" width="260">
        <template #default="{ row }">
          <!-- TODO(ID14): wire direct-handle / escalate actions -->
          <el-space>
            <el-button size="small" @click="directHandle(row.feedbackId)">Direct Handle</el-button>
            <el-button size="small" type="warning" @click="escalate(row.feedbackId)">Escalate</el-button>
          </el-space>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { onMounted, reactive, ref } from "vue";
import { getHighPriorityIssues } from "@/api/admin";
import type { HighPriorityIssue } from "@/types/api";

const loading = ref(false);
const rows = ref<HighPriorityIssue[]>([]);
const filters = reactive<{ escalated?: boolean }>({});

async function load() {
  loading.value = true;
  try {
    const result = await getHighPriorityIssues({
      escalated: filters.escalated,
      page: 1,
      size: 20
    });
    rows.value = result.data;
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to load high-priority issues");
  } finally {
    loading.value = false;
  }
}

function directHandle(feedbackId: number) {
  // TODO(ID14): call processFeedbackByPriority with action DIRECT_HANDLE.
  ElMessage.info(`TODO: Direct handle feedback #${feedbackId}`);
}

function escalate(feedbackId: number) {
  // TODO(ID14): call processFeedbackByPriority with action ESCALATE.
  ElMessage.info(`TODO: Escalate feedback #${feedbackId}`);
}

onMounted(load);
</script>

<style scoped>
.high-priority-container {
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

.todo-alert {
  margin-bottom: 16px;
}
</style>
