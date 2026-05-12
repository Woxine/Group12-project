<template>
  <!-- 高优先级问题处理页 / High-priority issue handling page. -->
  <el-card shadow="never" class="high-priority-container admin-page-card">
    <template #header>
      <!-- 升级状态筛选 / Escalation status filter. -->
      <div class="admin-page-header">
        <div>
          <h1 class="admin-page-title">High Priority Issues</h1>
          <div class="admin-page-subtitle">Focus on unresolved urgent feedback and escalation progress.</div>
        </div>
        <el-space class="admin-page-toolbar">
          <el-select v-model="filters.escalated" clearable placeholder="Escalation" class="admin-filter-select">
            <el-option label="Escalated" :value="true" />
            <el-option label="Not Escalated" :value="false" />
          </el-select>
          <el-button type="primary" :loading="loading" aria-label="Search high priority issues" @click="load">Search</el-button>
        </el-space>
      </div>
    </template>

    <!-- 高优先级反馈表格和处理动作 / High-priority feedback table and workflow actions. -->
    <el-table :data="rows" stripe v-loading="loading" class="admin-data-table admin-loading-section" :aria-busy="loading">
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
      <template #empty>
        <div class="admin-table-empty-state">
          <p class="admin-table-empty-title">No high priority issues found</p>
          <p class="admin-table-empty-text">Try adjusting the escalation filter or check again after new urgent feedback arrives.</p>
        </div>
      </template>
    </el-table>

    <div class="admin-pagination-footer">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-sizes="[10, 20, 50]"
        v-model:current-page="pager.page"
        v-model:page-size="pager.size"
        @current-change="onPageChange"
        @size-change="onSizeChange"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from "element-plus";
import { onMounted, reactive, ref } from "vue";
import { getHighPriorityIssues, processFeedbackByPriority } from "@/api/admin";
import { getAdminPriorityTagType, getAdminStatusTagType } from "@/adminStatus";
import type { HighPriorityIssue } from "@/types/api";

const loading = ref(false);
const rows = ref<HighPriorityIssue[]>([]);
const total = ref(0);
const pager = reactive({ page: 1, size: 10 });
const filters = reactive<{ escalated?: boolean }>({});

/** 加载高优先级问题列表 / Load high-priority issue records. */
async function load() {
  loading.value = true;
  try {
    const result = await getHighPriorityIssues({
      escalated: filters.escalated,
      page: pager.page,
      size: pager.size
    });
    rows.value = result.data;
    total.value = result.total ?? 0;
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to load high-priority issues");
  } finally {
    loading.value = false;
  }
}

/** 切换页码后重新加载 / Reload after the current page changes. */
function onPageChange(page: number) {
  pager.page = page;
  load();
}

/** 切换每页数量后回到第一页 / Return to the first page after page size changes. */
function onSizeChange(size: number) {
  pager.size = size;
  pager.page = 1;
  load();
}

/** 将未升级的紧急反馈升级到指定团队 / Escalate an urgent feedback item to a selected team. */
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

/** 对已升级反馈执行完成确认 / Mark an escalated feedback item as resolved after confirmation. */
async function markResolved(feedbackId: number) {
  try {
    await ElMessageBox.confirm(`Mark feedback #${feedbackId} as resolved?`, "Confirm Resolve", {
      confirmButtonText: "Confirm",
      cancelButtonText: "Cancel",
      type: "warning"
    });
    await processFeedbackByPriority(feedbackId, { action: "RESOLVE" });
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
/* 页面容器 / Page container. */
.high-priority-container {
  border-radius: var(--ui-radius-lg);
}
</style>
