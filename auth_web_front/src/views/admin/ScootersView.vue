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
              <el-option label="RENTED" value="RENTED" />
              <el-option label="MAINTENANCE" value="MAINTENANCE" />
            </el-select>
            <el-button type="primary" :icon="Search" :loading="loading" @click="load">Search</el-button>
            <el-button type="success" :icon="Plus" @click="openAdd">Add scooter</el-button>
          </el-space>
        </div>
      </div>
    </template>

    <el-table :data="rows" stripe v-loading="loading" class="data-table">
      <el-table-column prop="id" label="ID" width="100" align="center" />
      <el-table-column prop="type" label="Type" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="getTypeTagColor(row.type)" effect="plain" round>
            {{ row.type }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="Status" width="160" align="center">
        <template #default="{ row }">
          <el-tag
            :type="getStatusType(row.status)"
            effect="light"
            round
          >
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="On client map" width="140" align="center">
        <template #default="{ row }">
          <el-tag v-if="isListed(row)" type="success" effect="plain" size="small">Visible</el-tag>
          <el-tag v-else type="info" effect="plain" size="small">Hidden</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="hourRate" label="Hourly Rate" width="140" align="center">
        <template #default="{ row }">
          £{{ Number(row.hourRate).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="locationName" label="Location Name" min-width="200" />
      <el-table-column prop="locationLat" label="Latitude" width="140" align="right" />
      <el-table-column prop="locationLng" label="Longitude" width="140" align="right" />
      <el-table-column label="Actions" width="260" align="center" fixed="right">
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
import { onMounted, reactive, ref } from "vue";
import { Search, Edit, Check, Plus, Delete } from '@element-plus/icons-vue';

import { createScooter, deleteScooter, getAdminScooters, updateScooter } from "@/api/admin";
import type { Scooter } from "@/types/api";

const loading = ref(false);
const saving = ref(false);
const creating = ref(false);
const rows = ref<Scooter[]>([]);
const total = ref(0);
const pager = reactive({ page: 1, size: 10 });
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

function isListed(row: Scooter) {
  return row.visible !== false;
}

function getStatusType(status: string) {
  switch (status) {
    case 'AVAILABLE': return 'success';
    case 'RENTED': return 'warning';
    case 'MAINTENANCE': return 'danger';
    default: return 'info';
  }
}

function getTypeTagColor(type: string) {
  switch (type) {
    case 'GEN1': return '';
    case 'GEN2': return 'success';
    case 'GEN3': return 'warning';
    case 'GEN3PRO': return 'danger';
    default: return 'info';
  }
}

async function load() {
  loading.value = true;
  try {
    const result = await getAdminScooters({
      status: filters.status || undefined,
      page: pager.page,
      size: pager.size
    });
    rows.value = result.data;
    total.value = result.total ?? 0;
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to load scooters");
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

.edit-form {
  margin-top: 8px;
}

.full-width {
  width: 100%;
}
</style>
