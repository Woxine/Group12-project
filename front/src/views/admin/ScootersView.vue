<template>
  <el-card>
    <template #header>
      <div class="header">
        <span>Scooter Management</span>
        <el-space>
          <el-select v-model="filters.status" clearable placeholder="Filter by status" style="width: 180px">
            <el-option label="AVAILABLE" value="AVAILABLE" />
            <el-option label="RENTED" value="RENTED" />
            <el-option label="MAINTENANCE" value="MAINTENANCE" />
          </el-select>
          <el-button type="primary" :loading="loading" @click="load">Search</el-button>
        </el-space>
      </div>
    </template>

    <el-table :data="rows" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="status" label="Status" />
      <el-table-column prop="hourRate" label="Hourly Rate" />
      <el-table-column prop="locationName" label="Location Name" />
      <el-table-column prop="locationLat" label="Latitude" />
      <el-table-column prop="locationLng" label="Longitude" />
      <el-table-column label="Actions" width="140">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEdit(row)">Edit</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager">
      <el-pagination
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="pager.size"
        :current-page="pager.page"
        @current-change="onPageChange"
      />
    </div>
  </el-card>

  <el-dialog v-model="dialogVisible" title="Edit Scooter">
    <el-form label-position="top" :model="editForm">
      <el-form-item label="Status">
        <el-select v-model="editForm.status" placeholder="Select status">
          <el-option label="AVAILABLE" value="AVAILABLE" />
          <el-option label="RENTED" value="RENTED" />
          <el-option label="MAINTENANCE" value="MAINTENANCE" />
        </el-select>
      </el-form-item>
      <el-form-item label="Hourly Rate">
        <el-input-number v-model="editForm.hour_rate" :min="0" :precision="2" />
      </el-form-item>
      <el-form-item label="Latitude">
        <el-input-number v-model="editForm.location_lat" :precision="6" />
      </el-form-item>
      <el-form-item label="Longitude">
        <el-input-number v-model="editForm.location_lng" :precision="6" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">Cancel</el-button>
      <el-button type="primary" :loading="saving" @click="save">Save</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { onMounted, reactive, ref } from "vue";

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
    ElMessage.success("Scooter updated");
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
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
