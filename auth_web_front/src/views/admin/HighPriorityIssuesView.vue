<template>
  <el-card shadow="never" class="high-priority-container admin-page-card">
    <template #header>
      <div class="header admin-page-header">
        <div>
          <span class="page-title admin-page-title">High Priority Issues</span>
          <div class="admin-page-subtitle">Focus on unresolved urgent feedback and escalation progress.</div>
        </div>
        <el-space class="admin-page-toolbar">
          <el-select v-model="filters.escalated" clearable placeholder="Escalation" class="admin-filter-select">
            <el-option label="Escalated" :value="true" />
            <el-option label="Not Escalated" :value="false" />
          </el-select>
          <el-button type="primary" :loading="loading" @click="load">Search</el-button>
        </el-space>
      </div>
    </template>

    <el-table :data="rows" stripe v-loading="loading" class="admin-data-table">
      <el-table-column prop="feedbackId" label="Feedback ID" width="120" />
      <el-table-column prop="priority" label="Priority" width="120">
        <template #default="{ row }">
          <el-tag :type="getAdminPriorityTagType(row.priority)" effect="light" round>
            {{ row.priority }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="escalated" label="Escalated" width="120">
        <template #default="{ row }">
          <el-tag :type="getAdminStatusTagType(row.escalated ? 'ESCALATED' : 'NO')" effect="plain" round>
            {{ row.escalated ? "Yes" : "No" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="escalatedTo" label="Escalated To" width="180" />
      <el-table-column prop="content" label="Content" min-width="280" show-overflow-tooltip />
      <el-table-column prop="resolved" label="Resolved" width="120">
        <template #default="{ row }">
          <el-tag :type="getAdminStatusTagType(row.resolved ? 'RESOLVED' : 'OPEN')" effect="light" round>
            {{ row.resolved ? "Resolved" : "Open" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" width="300">
        <template #default="{ row }">
          <el-space v-if="!row.resolved && !row.escalated">
            <el-button size="small" type="warning" @click="escalate(row.feedbackId)">Escalate</el-button>
          </el-space>
          <el-space v-else-if="!row.resolved && row.escalated">
            <el-button size="small" type="success" @click="markResolved(row.feedbackId)">Mark Resolved</el-button>
          </el-space>
          <span v-else class="admin-status-text admin-status-text--success">Resolved</span>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from "element-plus";
import { onMounted, reactive, ref } from "vue";
import { getHighPriorityIssues, processFeedbackByPriority, updateFeedback } from "@/api/admin";
import { getAdminPriorityTagType, getAdminStatusTagType } from "@/adminStatus";
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

async function escalate(feedbackId: number) {
  try {
    const { value } = await ElMessageBox.prompt(
      "Input escalate target (e.g. TECH_TEAM / MANAGEMENT)",
      `Escalate #${feedbackId}`,
      {
        confirmButtonText: "Confirm",
        cancelButtonText: "Cancel",
        inputPattern: /\S+/,
        inputErrorMessage: "Escalate target is required"
      }
    );
    await processFeedbackByPriority(feedbackId, {
      action: "ESCALATE",
      escalateTo: value.trim()
    });
    ElMessage.success(`Feedback #${feedbackId} escalated`);
    await load();
  } catch (error: any) {
    if (error === "cancel" || error === "close") {
      return;
    }
    ElMessage.error(error?.response?.data?.message ?? "Failed to escalate feedback");
  }
}

async function markResolved(feedbackId: number) {
  try {
    await ElMessageBox.confirm(`Mark feedback #${feedbackId} as resolved?`, "Confirm Resolve", {
      confirmButtonText: "Confirm",
      cancelButtonText: "Cancel",
      type: "warning"
    });
    await updateFeedback(feedbackId, "resolved");
    ElMessage.success(`Feedback #${feedbackId} marked as resolved`);
    await load();
  } catch (error: any) {
    if (error === "cancel" || error === "close") {
      return;
    }
    ElMessage.error(error?.response?.data?.message ?? "Failed to mark feedback as resolved");
  }
}

onMounted(load);
</script>

<style scoped>
.high-priority-container {
  border-radius: var(--ui-radius-lg);
}
</style>
