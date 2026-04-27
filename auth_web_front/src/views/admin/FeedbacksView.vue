<template>
  <el-card shadow="never" class="feedbacks-container admin-page-card" role="region" aria-labelledby="feedback-management-heading">
    <template #header>
      <div class="admin-page-header">
        <div>
          <h1 id="feedback-management-heading" class="admin-page-title">Feedback Management</h1>
          <div class="admin-page-subtitle">Track issue priority, escalation status, and close the resolution loop.</div>
        </div>
        <div class="toolbar admin-page-toolbar">
          <el-space>
            <label for="feedback-priority-filter" class="sr-only">Filter feedback by priority</label>
            <el-select
              id="feedback-priority-filter"
              v-model="filters.priority"
              clearable
              placeholder="Priority"
              class="admin-filter-select"
              aria-label="Filter feedback by priority"
            >
              <el-option label="LOW" value="LOW" />
              <el-option label="HIGH" value="HIGH" />
            </el-select>
            <label for="feedback-resolution-filter" class="sr-only">Filter feedback by resolution status</label>
            <el-select
              id="feedback-resolution-filter"
              v-model="resolvedView"
              clearable
              placeholder="Resolution"
              class="admin-filter-select"
              aria-label="Filter feedback by resolution status"
              @change="onResolvedChange"
            >
              <el-option label="Unresolved" :value="false" />
              <el-option label="Resolved" :value="true" />
            </el-select>
            <el-button
              type="primary"
              :icon="Search"
              :loading="loading"
              aria-label="Search feedback list"
              @click="load"
            >
              Search
            </el-button>
          </el-space>
        </div>
      </div>
    </template>

    <p id="feedback-table-help" class="sr-only">
      Feedback table with priority, resolution status, escalation status, and actions for direct handling or escalation.
    </p>
    <el-table
      :data="rows"
      stripe
      v-loading="loading"
      class="data-table admin-data-table admin-loading-section"
      :aria-busy="loading"
      aria-label="Feedback records"
      aria-describedby="feedback-table-help"
      :row-class-name="() => 'clickable-row'"
      @row-click="openDetailDialog"
    >
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="userId" label="User ID" width="100" align="center" />
      <el-table-column prop="scooterId" label="Scooter ID" width="120" align="center" />

      <el-table-column prop="priority" label="Priority" width="120" align="center">
        <template #default="{ row }">
          <el-tag
            :type="getAdminPriorityTagType(row.priority)"
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
            :type="getAdminStatusTagType(row.resolved ? 'RESOLVED' : 'UNRESOLVED')"
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

      <el-table-column label="Escalated" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="getAdminStatusTagType(row.escalated ? 'ESCALATED' : 'NO')" effect="light" round>
            {{ row.escalated ? "Yes" : "No" }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="escalatedTo" label="Escalated To" width="160" align="center">
        <template #default="{ row }">
          <span>{{ row.escalatedTo || "-" }}</span>
        </template>
      </el-table-column>

      <el-table-column prop="escalationStatus" label="Process Status" width="170" align="center">
        <template #default="{ row }">
          <el-tag :type="getAdminStatusTagType(row.escalationStatus || 'PENDING')" effect="plain" round>
            {{ row.escalationStatus || "PENDING" }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="Feedback Content" min-width="280" show-overflow-tooltip>
        <template #default="{ row }">
          {{ summarizeContent(row.content) }}
        </template>
      </el-table-column>

      <el-table-column label="Actions" width="320" align="center" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.priority === 'LOW' && !row.resolved"
            type="success"
            link
            :icon="CircleCheck"
            :aria-label="`Directly handle feedback ${row.id}`"
            @click.stop="directHandle(row.id)"
          >
            Direct Handle
          </el-button>
          <el-button
            v-else-if="row.priority === 'HIGH' && !row.escalated"
            type="warning"
            link
            :icon="Warning"
            :aria-label="`Escalate high priority feedback ${row.id}`"
            @click.stop="openEscalateDialog(row)"
          >
            Escalate
          </el-button>
          <span v-else-if="row.priority === 'HIGH' && row.escalated" class="admin-status-text admin-status-text--success">
            <el-icon><Select /></el-icon> Escalated
          </span>
          <span v-else class="admin-status-text admin-status-text--success">
            <el-icon><Check /></el-icon> Completed
          </span>
        </template>
      </el-table-column>
      <template #empty>
        <div class="admin-table-empty-state">
          <p class="admin-table-empty-title">No feedback records found</p>
          <p class="admin-table-empty-text">Try adjusting the priority or resolution filters, then search again.</p>
        </div>
      </template>
    </el-table>

    <div class="admin-pagination-footer">
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

  <el-dialog
    v-model="detailDialogVisible"
    title="Feedback Details"
    width="560px"
    append-to-body
  >
    <div v-if="detailRow" class="admin-detail-grid">
      <div class="admin-detail-field"><span class="admin-detail-label">ID</span><span>#{{ detailRow.id }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">User ID</span><span>{{ detailRow.userId ?? "-" }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Scooter ID</span><span>{{ detailRow.scooterId ?? "-" }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Priority</span><span>{{ detailRow.priority }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Resolved</span><span>{{ detailRow.resolved ? "Yes" : "No" }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Escalated</span><span>{{ detailRow.escalated ? "Yes" : "No" }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Escalated To</span><span>{{ detailRow.escalatedTo || "-" }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Process Status</span><span>{{ detailRow.escalationStatus || "PENDING" }}</span></div>
    </div>
    <el-divider />
    <div class="detail-content">
      <div class="admin-detail-label">Feedback Content</div>
      <p class="full-content">{{ detailRow?.content || "-" }}</p>
    </div>
    <template #footer>
      <el-button @click="detailDialogVisible = false">Close</el-button>
    </template>
  </el-dialog>

  <el-dialog
    v-model="escalateDialog.visible"
    title="Escalate High Priority Feedback"
    width="460px"
    destroy-on-close
    append-to-body
  >
    <el-form label-position="top" class="admin-dialog-form">
      <el-form-item label="Escalate To" required>
        <el-input
          id="escalate-target"
          v-model="escalateDialog.escalateTo"
          placeholder="e.g. TECH_TEAM / MANAGEMENT"
          maxlength="100"
          clearable
          aria-label="Escalate target team"
        />
      </el-form-item>
      <el-form-item label="Note (optional)">
        <el-input
          id="escalate-note"
          v-model="escalateDialog.note"
          type="textarea"
          :rows="3"
          maxlength="300"
          show-word-limit
          placeholder="Add context for escalation"
          aria-label="Escalation note"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeEscalateDialog">Cancel</el-button>
      <el-button type="primary" :loading="escalateDialog.submitting" @click="submitEscalate">Confirm Escalation</el-button>
    </template>
  </el-dialog>
  <div class="sr-only" role="status" aria-live="polite" aria-atomic="true">{{ liveMessage }}</div>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { onMounted, reactive, ref } from "vue";
import { Search, CircleCheck, Check, Warning, Select } from '@element-plus/icons-vue';

import { getFeedbacks, processFeedbackByPriority } from "@/api/admin";
import { getAdminPriorityTagType, getAdminStatusTagType } from "@/adminStatus";
import type { FeedbackItem } from "@/types/api";

const loading = ref(false);
const rows = ref<FeedbackItem[]>([]);
const total = ref(0);
const pager = reactive({ page: 1, size: 10 });
const filters = reactive<{ resolved?: boolean; priority?: string }>({});
const resolvedView = ref<boolean | undefined>(undefined);
const liveMessage = ref("");
const detailDialogVisible = ref(false);
const detailRow = ref<FeedbackItem | null>(null);
const escalateDialog = reactive({
  visible: false,
  feedbackId: 0,
  escalateTo: "",
  note: "",
  submitting: false
});

function onResolvedChange(value?: boolean) {
  filters.resolved = value;
}

function announce(message: string) {
  liveMessage.value = "";
  window.setTimeout(() => {
    liveMessage.value = message;
  }, 0);
}

function summarizeContent(content: string) {
  if (!content) return "-";
  const plain = content.replace(/\s+/g, " ").trim();
  if (plain.length <= 80) return plain;
  return `${plain.slice(0, 80)}...`;
}

function openDetailDialog(row: FeedbackItem) {
  detailRow.value = row;
  detailDialogVisible.value = true;
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
    announce(`Loaded ${rows.value.length} feedback items.`);
  } catch (error: any) {
    const message = error?.response?.data?.message ?? "Failed to load feedback";
    ElMessage.error(message);
    announce(message);
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

async function directHandle(feedbackId: number) {
  try {
    await processFeedbackByPriority(feedbackId, { action: "DIRECT_HANDLE" });
    ElMessage.success("Feedback handled directly");
    announce(`Feedback ${feedbackId} handled directly.`);
    await load();
  } catch (error: any) {
    const message = error?.response?.data?.message ?? "Failed to process feedback";
    ElMessage.error(message);
    announce(message);
  }
}

function openEscalateDialog(row: FeedbackItem) {
  escalateDialog.visible = true;
  escalateDialog.feedbackId = row.id;
  escalateDialog.escalateTo = row.escalatedTo ?? "";
  escalateDialog.note = "";
  announce(`Escalation dialog opened for feedback ${row.id}.`);
}

function closeEscalateDialog() {
  escalateDialog.visible = false;
  escalateDialog.feedbackId = 0;
  escalateDialog.escalateTo = "";
  escalateDialog.note = "";
}

async function submitEscalate() {
  const escalateTo = escalateDialog.escalateTo.trim();
  if (!escalateTo) {
    ElMessage.warning("Escalate target is required");
    announce("Escalate target is required.");
    return;
  }

  escalateDialog.submitting = true;
  try {
    await processFeedbackByPriority(escalateDialog.feedbackId, {
      action: "ESCALATE",
      escalateTo,
      note: escalateDialog.note.trim() || undefined
    });
    ElMessage.success("Feedback escalated");
    announce(`Feedback ${escalateDialog.feedbackId} escalated to ${escalateTo}.`);
    closeEscalateDialog();
    await load();
  } catch (error: any) {
    const message = error?.response?.data?.message ?? "Failed to escalate feedback";
    ElMessage.error(message);
    announce(message);
  } finally {
    escalateDialog.submitting = false;
  }
}

onMounted(load);
</script>

<style scoped>
.feedbacks-container {
  border-radius: var(--ui-radius-lg);
}

.detail-content {
  padding: 10px 12px;
  background: var(--ui-bg-surface-soft);
  border-radius: var(--ui-radius-sm);
}

.full-content {
  margin: 8px 0 0;
  white-space: pre-wrap;
  line-height: 1.6;
  color: var(--ui-text-default);
}

:deep(.clickable-row) {
  cursor: pointer;
}
</style>