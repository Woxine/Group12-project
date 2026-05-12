<template>
  <!-- 折扣资格审核列表页 / Discount eligibility verification review page. -->
  <el-card shadow="never" class="verify-container admin-page-card">
    <template #header>
      <!-- 认证类型和审核状态筛选 / Verification type and review status filters. -->
      <div class="admin-page-header">
        <div>
          <h1 class="admin-page-title">Discount Verification Review</h1>
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

    <!-- 申请列表，点击行可查看提交材料 / Submission list; clicking a row opens submitted material details. -->
    <el-table
      :data="rows"
      stripe
      v-loading="loading"
      class="admin-data-table admin-loading-section"
      :aria-busy="loading"
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
      <template #empty>
        <div class="admin-table-empty-state">
          <p class="admin-table-empty-title">No submissions found</p>
          <p class="admin-table-empty-text">Try another type or status filter to review more verification records.</p>
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

  <!-- 申请详情和附件预览弹窗 / Submission detail and attachment preview dialog. -->
  <el-dialog v-model="detailDialogVisible" title="Submission Details" width="620px" append-to-body @closed="onDetailDialogClosed">
    <div v-if="detailRow" class="admin-detail-grid">
      <div class="admin-detail-field"><span class="admin-detail-label">ID</span><span>#{{ detailRow.id }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">User ID</span><span>{{ detailRow.userId }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Type</span><span>{{ detailRow.type }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Status</span><span>{{ detailRow.status }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">File</span><span>{{ detailRow.originalFilename }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">MIME</span><span>{{ detailRow.mimeType }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Size</span><span>{{ formatBytes(detailRow.sizeBytes) }}</span></div>
      <div v-if="fileLoading" class="admin-detail-field admin-detail-field--full">
        <span class="admin-detail-label">Preview</span><span>Loading...</span>
      </div>
      <div v-else-if="filePreviewUrl && detailRow.mimeType?.startsWith('image/')" class="admin-detail-field admin-detail-field--full">
        <span class="admin-detail-label">Preview</span>
        <img :src="filePreviewUrl" alt="Verification document" class="file-preview-image" />
      </div>
      <div v-else-if="filePreviewUrl && detailRow.mimeType === 'application/pdf'" class="admin-detail-field admin-detail-field--full">
        <span class="admin-detail-label">Document</span>
        <a :href="filePreviewUrl" target="_blank" rel="noopener" class="file-download-link">Open PDF</a>
      </div>
      <div class="admin-detail-field"><span class="admin-detail-label">Submitted</span><span>{{ detailRow.submittedAt }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Reviewed</span><span>{{ detailRow.reviewedAt || "-" }}</span></div>
      <div class="admin-detail-field"><span class="admin-detail-label">Reviewer</span><span>{{ detailRow.reviewerUserId ?? "-" }}</span></div>
      <div class="admin-detail-field admin-detail-field--full"><span class="admin-detail-label">Storage Path</span><span>{{ detailRow.storagePath || "-" }}</span></div>
      <div class="admin-detail-field admin-detail-field--full"><span class="admin-detail-label">Reject Reason</span><span>{{ detailRow.rejectReason || "-" }}</span></div>
    </div>
    <template #footer>
      <el-button @click="closeDetailDialog">Close</el-button>
    </template>
  </el-dialog>

  <!-- 驳回原因输入弹窗 / Reject reason input dialog. -->
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
  getDiscountVerificationFileUrl,
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
const filePreviewUrl = ref<string | null>(null);
const fileLoading = ref(false);

/** 加载折扣认证申请列表 / Load discount verification submissions. */
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

/** 搜索时回到第一页 / Reset to the first page before searching. */
function onSearch() {
  pager.page = 1;
  load();
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

/** 表格中压缩过长文件名 / Truncate long filenames in the table. */
function summarizeFilename(name: string) {
  if (!name) return "-";
  if (name.length <= 36) return name;
  return `${name.slice(0, 36)}...`;
}

/** 将附件大小转成人类可读格式 / Convert attachment size into a human-readable format. */
function formatBytes(size: number) {
  if (!size) return "0 B";
  if (size < 1024) return `${size} B`;
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
  return `${(size / (1024 * 1024)).toFixed(1)} MB`;
}

/** 打开详情弹窗，并加载图片或 PDF 预览地址 / Open details and load preview URLs for images or PDFs. */
async function openDetailDialog(row: DiscountVerificationSubmission) {
  detailRow.value = row;
  detailDialogVisible.value = true;
  filePreviewUrl.value = null;
  if (row.mimeType && (row.mimeType.startsWith("image/") || row.mimeType === "application/pdf")) {
    fileLoading.value = true;
    try {
      filePreviewUrl.value = await getDiscountVerificationFileUrl(row.id);
    } catch {
      filePreviewUrl.value = null;
    } finally {
      fileLoading.value = false;
    }
  }
}

/** 关闭详情弹窗 / Close the detail dialog. */
function closeDetailDialog() {
  detailDialogVisible.value = false;
}

/** 弹窗关闭后释放临时附件 URL / Revoke temporary attachment URLs after the dialog closes. */
function onDetailDialogClosed() {
  if (filePreviewUrl.value) {
    URL.revokeObjectURL(filePreviewUrl.value);
    filePreviewUrl.value = null;
  }
}

/** 通过认证申请并刷新列表 / Approve a submission and refresh the list. */
async function approve(id: number) {
  try {
    await approveDiscountVerification(id);
    ElMessage.success("Submission approved");
    await load();
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Approve failed");
  }
}

/** 打开驳回弹窗并记录目标申请 / Open the reject dialog and remember the target submission. */
function openRejectDialog(id: number) {
  rejectTargetId.value = id;
  rejectReason.value = "";
  rejectDialogVisible.value = true;
}

/** 校验驳回原因并提交驳回请求 / Validate reject reason and submit the rejection. */
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
/* 页面容器与行交互 / Page container and row interaction. */
.verify-container {
  border-radius: var(--ui-radius-lg);
}

:deep(.clickable-row) {
  cursor: pointer;
}

/* 附件预览和弹窗滚动 / Attachment preview and dialog scrolling. */
.file-preview-image {
  width: 100%;
  max-height: 70vh;
  object-fit: contain;
  border-radius: 6px;
  border: 1px solid var(--el-border-color-lighter);
}

.file-download-link {
  color: var(--el-color-primary);
  text-decoration: underline;
}

:deep(.el-dialog) {
  max-height: 85vh;
  display: flex;
  flex-direction: column;
}

:deep(.el-dialog__body) {
  overflow-y: auto;
  flex: 1;
}
</style>
