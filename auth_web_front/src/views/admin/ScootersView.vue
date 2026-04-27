<template>
  <el-card shadow="never" class="scooters-container">
    <template #header>
      <div class="header">
        <span class="page-title">Scooter Management</span>
        <div class="toolbar">
          <el-space>
            <el-select
              v-model="filters.status"
              clearable
              placeholder="Filter by status"
              style="width: 180px"
            >
              <el-option label="AVAILABLE" value="AVAILABLE" />
              <el-option label="RESERVED" value="RESERVED" />
              <el-option label="RENTED" value="RENTED" />
              <el-option label="MAINTENANCE" value="MAINTENANCE" />
            </el-select>
            <el-button type="primary" :icon="Search" :loading="loading" @click="load">Refresh</el-button>
            <el-button type="success" :icon="Plus" @click="openAdd">Add scooter</el-button>
          </el-space>
        </div>
      </div>
    </template>

    <el-alert
      type="info"
      show-icon
      :closable="false"
      class="hint-banner"
      title="Bulk controls apply to all scooters in the selected vehicle type."
    />

    <div v-loading="loading" class="type-card-list">
      <el-card
        v-for="section in sections"
        :key="section.type"
        shadow="hover"
        class="type-card"
      >
        <template #header>
          <div class="type-header">
            <div class="type-title-wrap">
              <el-tag :type="getTypeTagColor(section.type)" effect="plain" round>
                {{ section.type }}
              </el-tag>
              <span class="type-subtitle">{{ getTypeSubtitle(section.type) }}</span>
            </div>
            <el-space>
              <el-tag size="small">Total {{ section.total }}</el-tag>
              <el-tag size="small" type="success">Available {{ section.availableCount }}</el-tag>
              <el-tag size="small" type="info">Hidden {{ section.hiddenCount }}</el-tag>
              <el-button
                text
                :icon="expandedTypes[section.type] ? ArrowDown : ArrowRight"
                @click="toggleSection(section.type)"
              >
                {{ expandedTypes[section.type] ? "Collapse" : "Expand" }}
              </el-button>
            </el-space>
          </div>
        </template>

        <div v-show="expandedTypes[section.type]">
          <div class="bulk-panel">
          <div class="bulk-title">Unified Controller</div>
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
              <el-space>
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
              </el-space>
            </el-col>
          </el-row>

            <div v-if="bulkPreviews[section.type]" class="bulk-preview">
              <el-tag size="small">Matched {{ bulkPreviews[section.type]!.matchedCount }}</el-tag>
              <el-tag size="small">Hidden {{ bulkPreviews[section.type]!.hiddenCount }}</el-tag>
              <el-tag v-if="bulkPreviews[section.type]!.risky" size="small" type="warning">Risky change</el-tag>
              <span v-if="bulkPreviews[section.type]!.riskWarnings.length > 0" class="warning-text">
                {{ bulkPreviews[section.type]!.riskWarnings.join(" ") }}
              </span>
            </div>
          </div>

          <el-table :data="section.scooters" stripe class="data-table compact">
            <el-table-column prop="id" label="ID" width="90" align="center" />
            <el-table-column prop="status" label="Status" width="150" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" effect="light" round>
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
            <el-table-column label="Actions" width="220" align="center" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link :icon="Edit" @click="openEdit(row)">Edit</el-button>
                <el-button
                  v-if="isListed(row)"
                  type="warning"
                  link
                  @click="setFleetVisible(row, false)"
                >
                  Hide
                </el-button>
                <el-button
                  v-else
                  type="success"
                  link
                  @click="setFleetVisible(row, true)"
                >
                  Show
                </el-button>
                <el-button type="danger" link :icon="Delete" @click="confirmDelete(row)">Delete</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>
    </div>
  </el-card>

  <el-dialog v-model="dialogVisible" title="Edit Scooter" width="500px" destroy-on-close>
    <el-form label-position="top" :model="editForm" class="edit-form">
      <el-form-item label="Vehicle Type">
        <el-select v-model="editForm.type" placeholder="Select type" class="full-width">
          <el-option label="GEN1 (Fz3)" value="GEN1" />
          <el-option label="GEN2 (V70)" value="GEN2" />
          <el-option label="GEN3 (M95C)" value="GEN3" />
          <el-option label="GEN3PRO (E300P MK2)" value="GEN3PRO" />
        </el-select>
      </el-form-item>
      <el-form-item label="Visible on client map">
        <el-switch v-model="editForm.visible" active-text="Shown" inactive-text="Hidden" />
      </el-form-item>
      <el-form-item label="Status">
        <el-select v-model="editForm.status" placeholder="Select status" class="full-width">
          <el-option label="AVAILABLE" value="AVAILABLE" />
          <el-option label="RESERVED" value="RESERVED" />
          <el-option label="RENTED" value="RENTED" />
          <el-option label="MAINTENANCE" value="MAINTENANCE" />
        </el-select>
      </el-form-item>
      <el-form-item label="Hourly Rate (£)">
        <el-input-number v-model="editForm.hour_rate" :min="0" :precision="2" :step="0.5" class="full-width" />
      </el-form-item>
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

  <el-dialog v-model="addDialogVisible" title="Add Scooter" width="500px" destroy-on-close @closed="resetAddForm">
    <el-form label-position="top" :model="addForm" class="edit-form">
      <el-form-item label="Vehicle Type">
        <el-select v-model="addForm.type" placeholder="Select type" class="full-width">
          <el-option label="GEN1 (Fz3)" value="GEN1" />
          <el-option label="GEN2 (V70)" value="GEN2" />
          <el-option label="GEN3 (M95C)" value="GEN3" />
          <el-option label="GEN3PRO (E300P MK2)" value="GEN3PRO" />
        </el-select>
      </el-form-item>
      <el-form-item label="Status">
        <el-select v-model="addForm.status" placeholder="Select status" class="full-width">
          <el-option label="AVAILABLE" value="AVAILABLE" />
          <el-option label="MAINTENANCE" value="MAINTENANCE" />
        </el-select>
      </el-form-item>
      <el-form-item label="Hourly Rate (£)" required>
        <el-input-number v-model="addForm.hour_rate" :min="0" :precision="2" :step="0.5" class="full-width" />
      </el-form-item>
      <el-form-item label="Location name (optional)">
        <el-input v-model="addForm.location_name" placeholder="e.g. Library Plaza" clearable />
      </el-form-item>
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
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, onMounted, reactive, ref } from "vue";
import { Search, Edit, Check, Plus, Delete, ArrowDown, ArrowRight } from "@element-plus/icons-vue";

import {
  applyBulkUpdateByType,
  createScooter,
  deleteScooter,
  getAdminScooters,
  previewBulkUpdateByType,
  updateScooter
} from "@/api/admin";
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
const currentId = ref<number | null>(null);
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

const sections = computed(() =>
  TYPE_ORDER.map((type) => {
    const scooters = rows.value.filter((row) => normalizeType(row.type) === type);
    const availableCount = scooters.filter((s) => s.status === "AVAILABLE").length;
    const hiddenCount = scooters.filter((s) => s.visible === false).length;
    return {
      type,
      scooters,
      total: scooters.length,
      availableCount,
      hiddenCount,
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

function getStatusType(status: string) {
  switch (status) {
    case "AVAILABLE": return "success";
    case "RESERVED": return "warning";
    case "RENTED": return "warning";
    case "MAINTENANCE": return "danger";
    default: return "info";
  }
}

function getTypeTagColor(type: string) {
  switch (type) {
    case "GEN1": return "";
    case "GEN2": return "success";
    case "GEN3": return "warning";
    case "GEN3PRO": return "danger";
    default: return "info";
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

onMounted(load);
</script>

<style scoped>
.scooters-container {
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

.hint-banner {
  margin-bottom: 16px;
}

.type-card-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.type-card {
  border-radius: 10px;
}

.type-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.type-title-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
}

.type-subtitle {
  color: #909399;
  font-size: 13px;
}

.bulk-panel {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  background: #fafafa;
}

.bulk-title {
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 10px;
}

.bulk-actions {
  display: flex;
  align-items: center;
}

.bulk-preview {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
}

.warning-text {
  color: #e6a23c;
  font-size: 12px;
}

.data-table {
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.data-table.compact {
  margin-top: 8px;
}

.edit-form {
  margin-top: 8px;
}

.full-width {
  width: 100%;
}

@media (max-width: 1280px) {
  .type-card-list {
    grid-template-columns: 1fr;
  }
}
</style>
