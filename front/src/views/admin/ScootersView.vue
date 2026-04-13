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
          </el-space>
        </div>
      </div>
    </template>

    <el-table :data="rows" stripe v-loading="loading" class="data-table">
      <el-table-column prop="id" label="ID" width="100" align="center" />
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
      <el-table-column prop="hourRate" label="Hourly Rate" width="140" align="center">
        <template #default="{ row }">
          £{{ Number(row.hourRate).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="locationName" label="Location Name" min-width="200" />
      <el-table-column prop="locationLat" label="Latitude" width="140" align="right" />
      <el-table-column prop="locationLng" label="Longitude" width="140" align="right" />
      <el-table-column label="Actions" width="120" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link :icon="Edit" @click="openEdit(row)">Edit</el-button>
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
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { onMounted, reactive, ref } from "vue";
import { Search, Edit, Check } from '@element-plus/icons-vue';

import { getScooters, updateScooter } from "@/api/admin";
import type { Scooter } from "@/types/api";

const loading = ref(false);
const saving = ref(false);
const rows = ref<Scooter[]>([]);
const total = ref(0);
const pager = reactive({ page: 1, size: 10 });
const filters = reactive<{ status?: string }>({});

const dialogVisible = ref(false);
const currentId = ref<number | null>(null);
const editForm = reactive<{
  status?: string;
  hour_rate?: number;
  location_lat?: number;
  location_lng?: number;
}>({});

function getStatusType(status: string) {
  switch (status) {
    case 'AVAILABLE': return 'success';
    case 'RENTED': return 'warning';
    case 'MAINTENANCE': return 'danger';
    default: return 'info';
  }
}

async function load() {
  loading.value = true;
  try {
    const result = await getScooters({
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
  editForm.hour_rate = Number(row.hourRate);
  editForm.location_lat = row.locationLat ?? undefined;
  editForm.location_lng = row.locationLng ?? undefined;
  dialogVisible.value = true;
}

async function save() {
  if (!currentId.value) {
    return;
  }
  saving.value = true;
  try {
    await updateScooter(currentId.value, editForm);
    ElMessage.success("Scooter updated successfully");
    dialogVisible.value = false;
    await load();
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to update scooter");
  } finally {
    saving.value = false;
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