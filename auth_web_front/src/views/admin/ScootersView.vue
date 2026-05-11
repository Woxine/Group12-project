<template>
  <el-card shadow="never" class="scooters-container admin-page-card">
    <template #header>
      <div class="admin-page-header">
        <div>
          <h1 class="page-title admin-page-title">Scooter Management</h1>
          <div class="admin-page-subtitle">Manage fleet status, visibility, and bulk updates by vehicle type.</div>
        </div>
        <div class="admin-page-toolbar">
          <div class="toolbar-row">
            <el-select
              v-model="filters.status"
              clearable
              placeholder="Filter by status"
              class="admin-filter-select"
            >
              <el-option label="AVAILABLE" value="AVAILABLE" />
              <el-option label="RESERVED" value="RESERVED" />
              <el-option label="RENTED" value="RENTED" />
              <el-option label="MAINTENANCE" value="MAINTENANCE" />
            </el-select>
            <el-button type="primary" :icon="Search" :loading="loading" aria-label="Search scooters" @click="load">Search</el-button>
            <el-button type="success" :icon="Plus" @click="openAdd">Add scooter</el-button>
            <el-button type="warning" :icon="Upload" @click="batchDialogVisible = true">Batch Import</el-button>
          </div>
        </div>
      </div>
    </template>

    <el-alert
      type="info"
      show-icon
      :closable="false"
      class="hint-banner admin-hint admin-inline-notice admin-inline-notice--info"
      title="Bulk controls apply to all scooters in the selected vehicle type."
    />

    <div v-loading="loading" class="type-card-list admin-loading-section" :aria-busy="loading">
      <el-card
        v-for="section in sections"
        :key="section.type"
        shadow="never"
        class="type-card admin-panel"
      >
        <template #header>
          <div class="type-header">
            <div class="type-title-wrap">
              <el-tag :type="getTypeTagColor(section.type)" effect="plain" round class="type-main-tag">
                {{ section.type }}
              </el-tag>
              <span class="type-subtitle">{{ getTypeSubtitle(section.type) }}</span>
            </div>
            <div class="type-meta-actions">
              <div class="type-metrics" role="group" :aria-label="`${section.type} scooter summary`">
                <el-tag size="small" effect="plain" type="info" class="type-metric-tag">Total {{ section.total }}</el-tag>
                <el-tag size="small" effect="plain" type="success" class="type-metric-tag">Avail {{ section.availableCount }}</el-tag>
                <el-tag size="small" effect="plain" type="primary" class="type-metric-tag">Rented {{ section.rentedCount }}</el-tag>
                <el-tag size="small" effect="plain" type="warning" class="type-metric-tag">Maint {{ section.maintenanceCount }}</el-tag>
                <el-tag size="small" effect="plain" type="info" class="type-metric-tag">Hidden {{ section.hiddenCount }}</el-tag>
                <el-progress
                  :percentage="section.usageRate"
                  :stroke-width="14"
                  :text-inside="true"
                  :status="section.usageRate >= 80 ? 'exception' : section.usageRate >= 50 ? undefined : 'success'"
                  class="type-usage-bar"
                />
              </div>
              <el-button
                text
                class="type-toggle-button"
                :icon="expandedTypes[section.type] ? ArrowDown : ArrowRight"
                :aria-expanded="expandedTypes[section.type]"
                :aria-label="`${expandedTypes[section.type] ? 'Collapse' : 'Expand'} ${section.type} scooter type`"
                @click="toggleSection(section.type)"
              >
                {{ expandedTypes[section.type] ? "Collapse" : "Expand" }}
              </el-button>
            </div>
          </div>
        </template>

        <div v-show="expandedTypes[section.type]">
          <div class="bulk-panel admin-panel">
            <div class="bulk-panel-header" @click="bulkCollapsed[section.type] = !bulkCollapsed[section.type]">
              <span class="admin-section-title">Unified Controller</span>
              <el-icon class="bulk-collapse-icon">
                <ArrowDown v-if="!bulkCollapsed[section.type]" />
                <ArrowRight v-else />
              </el-icon>
            </div>
            <div v-show="!bulkCollapsed[section.type]">
            <el-row :gutter="12" class="bulk-row">
              <el-col :md="6" :sm="12" :xs="24">
                <el-input-number
                  v-model="bulkForms[section.type].hour_rate"
                  :min="0.1"
                  :precision="2"
                  :step="0.5"
                  class="full-width"
                  placeholder="Hourly rate"
                />
              </el-col>
              <el-col :md="6" :sm="12" :xs="24">
                <el-select v-model="bulkForms[section.type].status" clearable placeholder="Status" class="full-width">
                  <el-option label="AVAILABLE" value="AVAILABLE" />
                  <el-option label="RESERVED" value="RESERVED" />
                  <el-option label="RENTED" value="RENTED" />
                  <el-option label="MAINTENANCE" value="MAINTENANCE" />
                </el-select>
              </el-col>
              <el-col :md="6" :sm="12" :xs="24">
                <el-select v-model="bulkForms[section.type].visibleChoice" class="full-width">
                  <el-option label="Visibility: keep unchanged" value="UNCHANGED" />
                  <el-option label="Visibility: show all" value="SHOW" />
                  <el-option label="Visibility: hide all" value="HIDE" />
                </el-select>
              </el-col>
              <el-col :md="6" :sm="12" :xs="24" class="bulk-actions">
                <div class="bulk-actions-inner">
                  <el-button
                    :loading="bulkForms[section.type].previewing"
                    @click="previewBulk(section.type)"
                  >
                    Preview
                  </el-button>
                  <el-button
                    type="primary"
                    :loading="bulkForms[section.type].applying"
                    @click="applyBulk(section.type)"
                  >
                    Apply
                  </el-button>
                </div>
              </el-col>
            </el-row>

            <div
              v-if="bulkPreviews[section.type]"
              :class="[
                'bulk-preview',
                'admin-inline-notice',
                bulkPreviews[section.type]!.risky ? 'admin-inline-notice--warning' : 'admin-inline-notice--info'
              ]"
            >
              <el-tag size="small" type="info">Matched {{ bulkPreviews[section.type]!.matchedCount }}</el-tag>
              <el-tag size="small" type="info">Hidden {{ bulkPreviews[section.type]!.hiddenCount }}</el-tag>
              <el-tag v-if="bulkPreviews[section.type]!.risky" size="small" type="warning">Risky change</el-tag>
              <span v-if="bulkPreviews[section.type]!.riskWarnings.length > 0" class="admin-status-text warning-text">
                {{ bulkPreviews[section.type]!.riskWarnings.join(" ") }}
              </span>
            </div>
            <p v-else class="bulk-preview-hint admin-inline-notice">
              Run preview to estimate matched scooters before applying updates.
            </p>
            </div>
          </div>

          <el-table :data="section.scooters" stripe class="data-table compact admin-data-table">
            <el-table-column prop="id" label="ID" width="90" align="center" />
            <el-table-column prop="status" label="Status" width="150" align="center">
              <template #default="{ row }">
                <el-tag :type="getScooterStatusTagType(row.status)" effect="light" round>
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="On client map" width="130" align="center">
              <template #default="{ row }">
                <el-tag v-if="isListed(row)" type="success" effect="plain" size="small">Visible</el-tag>
                <el-tag v-else type="info" effect="plain" size="small">Hidden</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="hourRate" label="Hourly Rate" width="130" align="center">
              <template #default="{ row }">£{{ Number(row.hourRate).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="locationName" label="Location Name" min-width="180" />
            <el-table-column label="Actions" width="80" align="center" fixed="right">
              <template #default="{ row }">
                <el-dropdown trigger="click" @command="(cmd: string) => handleAction(cmd, row)">
                  <el-button :icon="MoreFilled" link />
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item :icon="Edit" command="edit">Edit</el-dropdown-item>
                      <el-dropdown-item
                        :icon="isListed(row) ? Hide : View"
                        :command="isListed(row) ? 'hide' : 'show'"
                      >
                        {{ isListed(row) ? 'Hide' : 'Show' }}
                      </el-dropdown-item>
                      <el-dropdown-item :icon="Delete" command="delete" divided>Delete</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
            </el-table-column>
            <template #empty>
              <div class="admin-table-empty-state">
                <p class="admin-table-empty-title">No scooters in {{ section.type }}</p>
                <p class="admin-table-empty-text">Try another status filter or add a scooter for this vehicle type.</p>
              </div>
            </template>
          </el-table>
        </div>
      </el-card>
    </div>
  </el-card>

  <el-dialog v-model="dialogVisible" title="Edit Scooter" width="420px" destroy-on-close>
    <el-form label-position="top" :model="editForm" class="edit-form admin-dialog-form">
      <el-row :gutter="16">
        <el-col :span="16">
          <el-form-item label="Vehicle Type">
            <el-select v-model="editForm.type" placeholder="Select type" class="full-width">
              <el-option label="GEN1 (Fz3)" value="GEN1" />
              <el-option label="GEN2 (V70)" value="GEN2" />
              <el-option label="GEN3 (M95C)" value="GEN3" />
              <el-option label="GEN3PRO (E300P MK2)" value="GEN3PRO" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="Visible">
            <el-switch v-model="editForm.visible" active-text="Yes" inactive-text="No" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="Status">
            <el-select v-model="editForm.status" placeholder="Select status" class="full-width">
              <el-option label="AVAILABLE" value="AVAILABLE" />
              <el-option label="RESERVED" value="RESERVED" />
              <el-option label="RENTED" value="RENTED" />
              <el-option label="MAINTENANCE" value="MAINTENANCE" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Hourly Rate (£)">
            <el-input-number v-model="editForm.hour_rate" :min="0" :precision="2" :step="0.5" class="full-width" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="Latitude">
            <el-input-number v-model="editForm.location_lat" :precision="6" :step="0.001" class="full-width" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Longitude">
            <el-input-number v-model="editForm.location_lng" :precision="6" :step="0.001" class="full-width" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" :icon="Check" :loading="saving" @click="save">Save Changes</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="addDialogVisible" title="Add Scooter" width="420px" destroy-on-close @closed="resetAddForm">
    <el-form label-position="top" :model="addForm" class="edit-form admin-dialog-form">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="Vehicle Type">
            <el-select v-model="addForm.type" placeholder="Select type" class="full-width">
              <el-option label="GEN1 (Fz3)" value="GEN1" />
              <el-option label="GEN2 (V70)" value="GEN2" />
              <el-option label="GEN3 (M95C)" value="GEN3" />
              <el-option label="GEN3PRO (E300P MK2)" value="GEN3PRO" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Status">
            <el-select v-model="addForm.status" placeholder="Select status" class="full-width">
              <el-option label="AVAILABLE" value="AVAILABLE" />
              <el-option label="MAINTENANCE" value="MAINTENANCE" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="Hourly Rate (£)" required>
            <el-input-number v-model="addForm.hour_rate" :min="0" :precision="2" :step="0.5" class="full-width" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Location name">
            <el-input v-model="addForm.location_name" placeholder="e.g. Library Plaza" clearable />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="Latitude">
            <el-input-number v-model="addForm.location_lat" :precision="6" :step="0.001" class="full-width" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Longitude">
            <el-input-number v-model="addForm.location_lng" :precision="6" :step="0.001" class="full-width" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="addDialogVisible = false">Cancel</el-button>
        <el-button type="primary" :icon="Check" :loading="creating" @click="submitAdd">Create</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="batchDialogVisible" title="Batch Import Scooters" width="680px" destroy-on-close @closed="resetBatchForm">
    <el-tabs v-model="batchMode" class="batch-tabs">
      <el-tab-pane label="Manual Entry" name="manual">
        <div class="batch-manual">
          <el-table :data="batchRows" stripe class="batch-table" size="small">
            <el-table-column label="Type" width="130">
              <template #default="{ row }">
                <el-select v-model="row.type" size="small" class="full-width">
                  <el-option label="GEN1" value="GEN1" />
                  <el-option label="GEN2" value="GEN2" />
                  <el-option label="GEN3" value="GEN3" />
                  <el-option label="GEN3PRO" value="GEN3PRO" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="Status" width="130">
              <template #default="{ row }">
                <el-select v-model="row.status" size="small" class="full-width">
                  <el-option label="AVAILABLE" value="AVAILABLE" />
                  <el-option label="MAINTENANCE" value="MAINTENANCE" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="Rate (£)" width="110">
              <template #default="{ row }">
                <el-input-number v-model="row.hour_rate" :min="0" :precision="2" :step="0.5" size="small" class="full-width" />
              </template>
            </el-table-column>
            <el-table-column label="Count" width="90">
              <template #default="{ row }">
                <el-input-number v-model="row.count" :min="1" :max="100" size="small" class="full-width" />
              </template>
            </el-table-column>
            <el-table-column label="Location" min-width="140">
              <template #default="{ row }">
                <el-input v-model="row.location_name" size="small" placeholder="Optional" clearable />
              </template>
            </el-table-column>
            <el-table-column width="60" align="center">
              <template #default="{ $index }">
                <el-button type="danger" link :icon="Delete" @click="batchRows.splice($index, 1)" :disabled="batchRows.length <= 1" />
              </template>
            </el-table-column>
          </el-table>
          <el-button text type="primary" :icon="Plus" class="batch-add-row" @click="addBatchRow">Add row</el-button>
        </div>
      </el-tab-pane>
      <el-tab-pane label="CSV Import" name="csv">
        <div class="batch-csv">
          <p class="batch-csv-hint">Paste CSV data. Header: <code>type,status,hour_rate,count,location_name</code></p>
          <el-input
            v-model="csvText"
            type="textarea"
            :rows="8"
            placeholder="GEN1,AVAILABLE,3.50,10,Library Plaza&#10;GEN2,AVAILABLE,4.00,5&#10;GEN3,MAINTENANCE,3.00,3"
            class="batch-csv-input"
          />
          <el-button size="small" @click="parseCsv">Parse CSV</el-button>
        </div>
      </el-tab-pane>
    </el-tabs>

    <div class="batch-summary">
      <el-tag type="info">Total entries: {{ batchRows.length }}</el-tag>
      <el-tag type="primary">Total scooters: {{ batchTotalCount }}</el-tag>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="batchDialogVisible = false">Cancel</el-button>
        <el-button type="primary" :icon="Check" :loading="batchSubmitting" @click="submitBatch">
          Create {{ batchTotalCount }} Scooters
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, onMounted, reactive, ref } from "vue";
import { Search, Edit, Check, Plus, Delete, ArrowDown, ArrowRight, Upload, MoreFilled, Hide, View } from "@element-plus/icons-vue";

import {
  applyBulkUpdateByType,
  batchCreateScooters,
  createScooter,
  deleteScooter,
  getAdminScooters,
  previewBulkUpdateByType,
  updateScooter
} from "@/api/admin";
import { getScooterStatusTagType } from "@/adminStatus";
import type {
  BulkScooterUpdatePayload,
  Scooter,
  ScooterBulkPreview
} from "@/types/api";

const TYPE_ORDER = ["GEN1", "GEN2", "GEN3", "GEN3PRO"] as const;
type VehicleType = (typeof TYPE_ORDER)[number];
type VisibleChoice = "UNCHANGED" | "SHOW" | "HIDE";

type BulkFormState = {
  hour_rate: number | null;
  status: "" | "AVAILABLE" | "RESERVED" | "RENTED" | "MAINTENANCE";
  visibleChoice: VisibleChoice;
  previewing: boolean;
  applying: boolean;
};

const loading = ref(false);
const saving = ref(false);
const creating = ref(false);
const rows = ref<Scooter[]>([]);
const filters = reactive<{ status?: string }>({});

const dialogVisible = ref(false);
const addDialogVisible = ref(false);
const batchDialogVisible = ref(false);
const batchSubmitting = ref(false);
const batchMode = ref<"manual" | "csv">("manual");
const csvText = ref("");
const currentId = ref<number | null>(null);

type BatchRow = {
  type: string;
  status: string;
  hour_rate: number;
  count: number;
  location_name: string;
};

function createBatchRow(): BatchRow {
  return { type: "GEN1", status: "AVAILABLE", hour_rate: 3.5, count: 1, location_name: "" };
}

const batchRows = ref<BatchRow[]>([createBatchRow()]);

const batchTotalCount = computed(() =>
  batchRows.value.reduce((sum, row) => sum + (row.count || 0), 0)
);
const editForm = reactive<{
  status?: string;
  type?: string;
  hour_rate?: number;
  location_lat?: number;
  location_lng?: number;
  visible?: boolean;
}>({});

const addForm = reactive({
  status: "AVAILABLE",
  type: "GEN1",
  hour_rate: 3.5 as number,
  location_lat: undefined as number | undefined,
  location_lng: undefined as number | undefined,
  location_name: "",
});

const bulkForms = reactive<Record<VehicleType, BulkFormState>>({
  GEN1: createBulkForm(),
  GEN2: createBulkForm(),
  GEN3: createBulkForm(),
  GEN3PRO: createBulkForm(),
});

const bulkPreviews = reactive<Record<VehicleType, ScooterBulkPreview | null>>({
  GEN1: null,
  GEN2: null,
  GEN3: null,
  GEN3PRO: null,
});

const expandedTypes = reactive<Record<VehicleType, boolean>>({
  GEN1: true,
  GEN2: true,
  GEN3: true,
  GEN3PRO: true,
});

const bulkCollapsed = reactive<Record<VehicleType, boolean>>({
  GEN1: true,
  GEN2: true,
  GEN3: true,
  GEN3PRO: true,
});

const sections = computed(() =>
  TYPE_ORDER.map((type) => {
    const scooters = rows.value.filter((row) => normalizeType(row.type) === type);
    const total = scooters.length;
    const availableCount = scooters.filter((s) => s.status === "AVAILABLE").length;
    const rentedCount = scooters.filter((s) => s.status === "RENTED").length;
    const maintenanceCount = scooters.filter((s) => s.status === "MAINTENANCE").length;
    const hiddenCount = scooters.filter((s) => s.visible === false).length;
    const usageRate = total > 0 ? Math.round((rentedCount / total) * 100) : 0;
    return {
      type,
      scooters,
      total,
      availableCount,
      rentedCount,
      maintenanceCount,
      hiddenCount,
      usageRate,
    };
  })
);

function createBulkForm(): BulkFormState {
  return {
    hour_rate: null,
    status: "",
    visibleChoice: "UNCHANGED",
    previewing: false,
    applying: false
  };
}

function normalizeType(type: string | undefined): VehicleType {
  const value = (type ?? "").trim().toUpperCase();
  if (value === "GEN3 PRO" || value === "GEN3_PRO" || value === "GEN-3-PRO") {
    return "GEN3PRO";
  }
  return TYPE_ORDER.includes(value as VehicleType) ? (value as VehicleType) : "GEN1";
}

function getTypeSubtitle(type: VehicleType) {
  switch (type) {
    case "GEN1":
      return "Ninebot Fz3";
    case "GEN2":
      return "Ninebot V70";
    case "GEN3":
      return "Ninebot M95C";
    case "GEN3PRO":
      return "Ninebot E300P MK2";
  }
}

function isListed(row: Scooter) {
  return row.visible !== false;
}

function getTypeTagColor(type: string) {
  switch (type) {
    case "GEN1": return undefined;
    case "GEN2": return "success";
    case "GEN3": return "warning";
    case "GEN3PRO": return "danger";
    default: return "info";
  }
}

function handleAction(cmd: string, row: Scooter) {
  switch (cmd) {
    case "edit":
      openEdit(row);
      break;
    case "hide":
      setFleetVisible(row, false);
      break;
    case "show":
      setFleetVisible(row, true);
      break;
    case "delete":
      confirmDelete(row);
      break;
  }
}

function toggleSection(type: VehicleType) {
  expandedTypes[type] = !expandedTypes[type];
}

function buildBulkPayload(type: VehicleType, confirmRisky = false): BulkScooterUpdatePayload | null {
  const state = bulkForms[type];
  const payload: BulkScooterUpdatePayload = { type };
  let hasAnyField = false;

  if (state.hour_rate != null) {
    payload.hour_rate = Number(state.hour_rate);
    hasAnyField = true;
  }
  if (state.status) {
    payload.status = state.status;
    hasAnyField = true;
  }
  if (state.visibleChoice !== "UNCHANGED") {
    payload.visible = state.visibleChoice === "SHOW";
    hasAnyField = true;
  }
  if (confirmRisky) {
    payload.confirm_risky = true;
  }
  return hasAnyField ? payload : null;
}

async function previewBulk(type: VehicleType) {
  const payload = buildBulkPayload(type);
  if (!payload) {
    ElMessage.warning("Select at least one field in unified controller");
    return;
  }
  bulkForms[type].previewing = true;
  try {
    const result = await previewBulkUpdateByType(payload);
    bulkPreviews[type] = result;
    ElMessage.success(`Preview ready for ${type}`);
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to preview bulk update");
  } finally {
    bulkForms[type].previewing = false;
  }
}

async function applyBulk(type: VehicleType) {
  const basePayload = buildBulkPayload(type);
  if (!basePayload) {
    ElMessage.warning("Select at least one field in unified controller");
    return;
  }

  bulkForms[type].applying = true;
  try {
    let preview = bulkPreviews[type];
    if (!preview) {
      preview = await previewBulkUpdateByType(basePayload);
      bulkPreviews[type] = preview;
    }

    if (preview.risky) {
      const warningText = preview.riskWarnings.join(" ");
      await ElMessageBox.confirm(
        `This action is risky for ${type}. ${warningText} Continue?`,
        "Confirm risky bulk update",
        { type: "warning", confirmButtonText: "Apply", cancelButtonText: "Cancel" }
      );
    } else {
      await ElMessageBox.confirm(
        `Apply updates to ${preview.matchedCount} scooters in ${type}?`,
        "Confirm bulk update",
        { type: "warning", confirmButtonText: "Apply", cancelButtonText: "Cancel" }
      );
    }

    const result = await applyBulkUpdateByType(buildBulkPayload(type, preview.risky)!);
    ElMessage.success(`Updated ${result.updatedCount} scooters in ${type}`);
    bulkPreviews[type] = null;
    await load();
  } catch (error: any) {
    if (error === "cancel" || error === "close") {
      return;
    }
    ElMessage.error(error?.response?.data?.message ?? "Failed to apply bulk update");
  } finally {
    bulkForms[type].applying = false;
  }
}

async function load() {
  loading.value = true;
  try {
    const result = await getAdminScooters({
      status: filters.status || undefined,
      page: 1,
      size: 1000
    });
    rows.value = result.data;
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to load scooters");
  } finally {
    loading.value = false;
  }
}

function openEdit(row: Scooter) {
  currentId.value = row.id;
  editForm.status = row.status;
  editForm.type = row.type;
  editForm.hour_rate = Number(row.hourRate);
  editForm.location_lat = row.locationLat ?? undefined;
  editForm.location_lng = row.locationLng ?? undefined;
  editForm.visible = row.visible !== false;
  dialogVisible.value = true;
}

function openAdd() {
  resetAddForm();
  addDialogVisible.value = true;
}

function resetAddForm() {
  addForm.status = "AVAILABLE";
  addForm.type = "GEN1";
  addForm.hour_rate = 3.5;
  addForm.location_lat = undefined;
  addForm.location_lng = undefined;
  addForm.location_name = "";
}

async function save() {
  if (!currentId.value) {
    return;
  }
  saving.value = true;
  try {
    await updateScooter(currentId.value, {
      status: editForm.status,
      type: editForm.type,
      hour_rate: editForm.hour_rate,
      location_lat: editForm.location_lat,
      location_lng: editForm.location_lng,
      visible: editForm.visible
    });
    ElMessage.success("Scooter updated successfully");
    dialogVisible.value = false;
    await load();
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to update scooter");
  } finally {
    saving.value = false;
  }
}

async function setFleetVisible(row: Scooter, visible: boolean) {
  try {
    await updateScooter(row.id, { visible });
    ElMessage.success(visible ? "Scooter is visible on the client map again" : "Scooter hidden from client map");
    await load();
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to update visibility");
  }
}

async function confirmDelete(row: Scooter) {
  try {
    await ElMessageBox.confirm(
      `Delete scooter #${row.id} permanently? This is only allowed when the scooter has no booking history.`,
      "Delete scooter",
      { type: "warning", confirmButtonText: "Delete", cancelButtonText: "Cancel" }
    );
    await deleteScooter(row.id);
    ElMessage.success("Scooter deleted");
    await load();
  } catch (error: any) {
    if (error === "cancel" || error === "close") {
      return;
    }
    ElMessage.error(error?.response?.data?.message ?? "Failed to delete scooter");
  }
}

async function submitAdd() {
  if (addForm.hour_rate == null || Number.isNaN(Number(addForm.hour_rate))) {
    ElMessage.warning("Please set an hourly rate");
    return;
  }
  creating.value = true;
  try {
    const name = addForm.location_name?.trim();
    await createScooter({
      status: addForm.status,
      type: addForm.type,
      hour_rate: Number(addForm.hour_rate),
      location_lat: addForm.location_lat,
      location_lng: addForm.location_lng,
      ...(name ? { location_name: name } : {})
    });
    ElMessage.success("Scooter created");
    addDialogVisible.value = false;
    await load();
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to create scooter");
  } finally {
    creating.value = false;
  }
}

function addBatchRow() {
  batchRows.value.push(createBatchRow());
}

function parseCsv() {
  const lines = csvText.value.trim().split("\n").filter((l) => l.trim());
  if (lines.length === 0) {
    ElMessage.warning("No CSV data to parse");
    return;
  }
  const parsed: BatchRow[] = [];
  for (const line of lines) {
    const parts = line.split(",").map((p) => p.trim());
    if (parts.length < 3) continue;
    parsed.push({
      type: parts[0] || "GEN1",
      status: parts[1] || "AVAILABLE",
      hour_rate: parseFloat(parts[2]) || 3.5,
      count: parseInt(parts[3], 10) || 1,
      location_name: parts[4] || "",
    });
  }
  if (parsed.length === 0) {
    ElMessage.warning("No valid rows found in CSV");
    return;
  }
  batchRows.value = parsed;
  batchMode.value = "manual";
  ElMessage.success(`Parsed ${parsed.length} rows from CSV`);
}

async function submitBatch() {
  if (batchRows.value.length === 0) {
    ElMessage.warning("Add at least one row");
    return;
  }
  for (let i = 0; i < batchRows.value.length; i++) {
    const row = batchRows.value[i];
    if (!row.hour_rate || row.hour_rate <= 0) {
      ElMessage.warning(`Row ${i + 1}: hourly rate must be greater than 0`);
      return;
    }
  }
  batchSubmitting.value = true;
  try {
    const result = await batchCreateScooters({
      scooters: batchRows.value.map((row) => ({
        type: row.type,
        status: row.status,
        hour_rate: row.hour_rate,
        count: row.count,
        location_name: row.location_name || undefined,
      })),
    });
    ElMessage.success(`Created ${result.totalCreated} scooters`);
    batchDialogVisible.value = false;
    await load();
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to batch create scooters");
  } finally {
    batchSubmitting.value = false;
  }
}

function resetBatchForm() {
  batchRows.value = [createBatchRow()];
  csvText.value = "";
  batchMode.value = "manual";
}

onMounted(load);
</script>

<style scoped>
.scooters-container {
  border-radius: var(--ui-radius-lg);
}

.hint-banner {
  margin-bottom: var(--ui-space-4);
}

.toolbar-row {
  display: flex;
  align-items: center;
  gap: var(--ui-space-3);
  flex-wrap: wrap;
}

.type-card-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--ui-space-4);
}

.type-card {
  height: 100%;
}

.type-card :deep(.el-card__header) {
  padding: var(--ui-space-4) var(--ui-space-5);
}

.type-card :deep(.el-card__body) {
  padding: var(--ui-space-4);
}

.type-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--ui-space-4);
}

.type-title-wrap {
  display: flex;
  align-items: center;
  gap: var(--ui-space-3);
  min-width: 0;
  flex-wrap: wrap;
}

.type-main-tag {
  min-height: 28px;
  padding: 0 var(--ui-space-3);
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.3px;
}

.type-subtitle {
  color: var(--ui-text-muted);
  font-size: 13px;
}

.type-meta-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: var(--ui-space-3);
  flex-wrap: wrap;
}

.type-metrics {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: var(--ui-space-2);
  flex-wrap: wrap;
}

.type-metric-tag {
  font-weight: 500;
}

.type-usage-bar {
  width: 100px;
}

.type-toggle-button {
  flex-shrink: 0;
}

.bulk-panel {
  padding: var(--ui-space-4);
  margin-bottom: var(--ui-space-3);
  background: var(--ui-bg-surface-soft);
  box-shadow: none;
}

.bulk-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  user-select: none;
}

.bulk-panel-header:hover {
  opacity: 0.8;
}

.bulk-collapse-icon {
  font-size: 14px;
  color: var(--ui-text-muted);
  transition: transform 0.2s;
}

.bulk-row {
  row-gap: var(--ui-space-3);
  align-items: center;
}

.bulk-actions {
  display: flex;
  align-items: center;
  height: 100%;
}

.bulk-actions-inner {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.bulk-actions-inner .el-button {
  min-width: 80px;
  height: 32px;
}

.bulk-preview {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--ui-space-2);
  margin-top: var(--ui-space-2);
}

.warning-text {
  color: var(--ui-color-warning-600);
  font-size: 12px;
}

.bulk-preview-hint {
  margin: var(--ui-space-2) 0 0;
  font-size: 12px;
}

.data-table {
  margin-top: var(--ui-space-2);
}

.edit-form {
  margin-top: var(--ui-space-2);
}

.full-width {
  width: 100%;
}

@media (max-width: 1280px) {
  .type-card-list {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 992px) {
  .type-header {
    flex-wrap: wrap;
    align-items: flex-start;
  }

  .type-meta-actions,
  .type-metrics {
    justify-content: flex-start;
  }

  .bulk-actions {
    justify-content: flex-start;
    margin-top: var(--ui-space-2);
  }

  .type-meta-actions {
    width: 100%;
  }
}

.batch-manual {
  max-height: 360px;
  overflow-y: auto;
}

.batch-table {
  width: 100%;
}

.batch-add-row {
  margin-top: var(--ui-space-2);
}

.batch-csv-hint {
  font-size: 13px;
  color: var(--ui-text-muted);
  margin-bottom: var(--ui-space-2);
}

.batch-csv-hint code {
  background: var(--ui-bg-subtle);
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.batch-csv-input {
  margin-bottom: var(--ui-space-2);
}

.batch-summary {
  display: flex;
  gap: var(--ui-space-2);
  margin-top: var(--ui-space-3);
  padding-top: var(--ui-space-3);
  border-top: 1px solid var(--ui-border-soft);
}

.batch-tabs {
  margin-top: var(--ui-space-2);
}

@media (max-width: 640px) {
  .type-card :deep(.el-card__header),
  .type-card :deep(.el-card__body),
  .bulk-panel {
    padding: var(--ui-space-3);
  }
}
</style>
