<template>
  <el-card shadow="never" class="verify-container admin-page-card">
    <template #header>
      <div class="header admin-page-header">
        <div>
          <span class="page-title admin-page-title">Discount Verification Review</span>
          <div class="admin-page-subtitle">Review student and senior submissions with approval traceability.</div>
        </div>
        <el-space class="admin-page-toolbar">
          <el-select v-model="filters.type" clearable placeholder="Type" class="admin-filter-select">
            <el-option label="Student" value="STUDENT" />
            <el-option label="Senior" value="SENIOR" />
          </el-select>
          <el-select v-model="filters.status" clearable placeholder="Status" class="admin-filter-select">
            <el-option label="Pending" value="PENDING" />
            <el-option label="Approved" value="APPROVED" />
            <el-option label="Rejected" value="REJECTED" />
          </el-select>
          <el-button type="primary" :icon="Search" :loading="loading" @click="onSearch">Search</el-button>
        </el-space>
      </div>
    </template>

    <el-table
      :data="rows"
      stripe
      v-loading="loading"
      class="admin-data-table"
      :row-class-name="() => 'clickable-row'"
      @row-click="openDetailDialog"
    >
      <el-table-column prop="id" label="ID" width="90" />
      <el-table-column prop="userId" label="User ID" width="100" />
      <el-table-column prop="type" label="Type" width="120">
        <template #default="{ row }">
          <el-tag type="info" effect="plain" round>
            {{ row.type }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="Status" width="120">
        <template #default="{ row }">
          <el-tag
            :type="getAdminStatusTagType(row.status)"
            effect="light"
            round
          >
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="File Name" min-width="200">
        <template #default="{ row }">
          {{ summarizeFilename(row.originalFilename) }}
        </template>
      </el-table-column>
      <el-table-column prop="submittedAt" label="Submitted At" width="180" />
      <el-table-column prop="reviewedAt" label="Reviewed At" width="180" />
      <el-table-column label="Actions" width="260" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 'PENDING'"
            type="success"
            link
            :icon="Check"
            @click.stop="approve(row.id)"
          >
            Approve
          </el-button>
          <el-button
            v-if="row.status === 'PENDING'"
            type="danger"
            link
            :icon="Close"
            @click.stop="openRejectDialog(row.id)"
          >
            Reject
          </el-button>
          <span v-if="row.status !== 'PENDING'" class="admin-status-text admin-status-text--muted">Reviewed</span>
        </template>
      </el-table-column>
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

  <el-dialog v-model="detailDialogVisible" title="Submission Details" width="620px" append-to-body>
    <div v-if="detailRow" class="admin-detail-grid">
      <div class="admin-detail-field"><span class="admin-detail-label">ID</span><span>#{{ detailRow.id }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">User ID</span><span>{{ detailRow.userId }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Type</span><span>{{ detailRow.type }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Status</span><span>{{ detailRow.status }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">File</span><span>{{ detailRow.originalFilename }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">MIME</span><span>{{ detailRow.mimeType }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Size</span><span>{{ formatBytes(detailRow.sizeBytes) }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Submitted</span><span>{{ detailRow.submittedAt }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Reviewed</span><span>{{ detailRow.reviewedAt || "-" }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Reviewer</span><span>{{ detailRow.reviewerUserId ?? "-" }}</span></div>
      <div class="admin-detail-field admin-detail-field--full"><span class="admin-detail-label">Storage Path</span><span>{{ detailRow.storagePath || "-" }}</span></div>
      <div class="admin-detail-field admin-detail-field--full"><span class="admin-detail-label">Reject Reason</span><span>{{ detailRow.rejectReason || "-" }}</span></div>
    </div>
    <template #footer>
      <el-button @click="detailDialogVisible = false">Close</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="rejectDialogVisible" title="Reject Submission" width="480px" append-to-body>
    <el-input
      v-model="rejectReason"
      type="textarea"
      :rows="4"
      maxlength="500"
      show-word-limit
      placeholder="Please enter a reject reason"
    />
    <template #footer>
      <el-button @click="rejectDialogVisible = false">Cancel</el-button>
      <el-button type="danger" :loading="rejecting" @click="confirmReject">Confirm Reject</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { onMounted, reactive, ref } from "vue";
import { Check, Close, Search } from "@element-plus/icons-vue";

import {
  approveDiscountVerification,
  getDiscountVerifications,
  rejectDiscountVerification
} from "@/api/admin";
import { getAdminStatusTagType } from "@/adminStatus";
import type { DiscountVerificationSubmission } from "@/types/api";

const loading = ref(false);
const rows = ref<DiscountVerificationSubmission[]>([]);
const total = ref(0);
const pager = reactive({ page: 1, size: 10 });
const filters = reactive<{
  type?: "STUDENT" | "SENIOR";
  status?: "PENDING" | "APPROVED" | "REJECTED";
}>({});

const detailDialogVisible = ref(false);
const detailRow = ref<DiscountVerificationSubmission | null>(null);
const rejectDialogVisible = ref(false);
const rejecting = ref(false);
const rejectTargetId = ref<number | null>(null);
const rejectReason = ref("");

async function load() {
  loading.value = true;
  try {
    const result = await getDiscountVerifications({
      page: pager.page,
      size: pager.size,
      type: filters.type,
      status: filters.status
    });
    rows.value = result.data;
    total.value = result.total ?? 0;
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to load submissions");
  } finally {
    loading.value = false;
  }
}

function onSearch() {
  pager.page = 1;
  load();
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

function summarizeFilename(name: string) {
  if (!name) return "-";
  if (name.length <= 36) return name;
  return `${name.slice(0, 36)}...`;
}

function formatBytes(size: number) {
  if (!size) return "0 B";
  if (size < 1024) return `${size} B`;
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
  return `${(size / (1024 * 1024)).toFixed(1)} MB`;
}

function openDetailDialog(row: DiscountVerificationSubmission) {
  detailRow.value = row;
  detailDialogVisible.value = true;
}

async function approve(id: number) {
  try {
    await approveDiscountVerification(id);
    ElMessage.success("Submission approved");
    await load();
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Approve failed");
  }
}

function openRejectDialog(id: number) {
  rejectTargetId.value = id;
  rejectReason.value = "";
  rejectDialogVisible.value = true;
}

async function confirmReject() {
  if (!rejectTargetId.value) return;
  if (!rejectReason.value.trim()) {
    ElMessage.warning("Reject reason is required");
    return;
  }
  rejecting.value = true;
  try {
    await rejectDiscountVerification(rejectTargetId.value, rejectReason.value.trim());
    ElMessage.success("Submission rejected");
    rejectDialogVisible.value = false;
    await load();
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Reject failed");
  } finally {
    rejecting.value = false;
  }
}

onMounted(load);
</script>

<style scoped>
.verify-container {
  border-radius: var(--ui-radius-lg);
}

:deep(.clickable-row) {
  cursor: pointer;
}
</style>
