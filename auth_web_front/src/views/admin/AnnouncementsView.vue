<template>
  <el-card shadow="never" class="admin-page-card">
    <template #header>
      <div class="admin-page-header">
        <div>
          <h1 class="admin-page-title">Announcements</h1>
          <p class="admin-page-subtitle">Manage promotional announcements shown on the mobile home page</p>
        </div>
        <el-button type="primary" @click="openCreate">
          <el-icon><Plus /></el-icon>
          New Announcement
        </el-button>
      </div>
    </template>

    <el-table :data="rows" v-loading="loading" stripe class="admin-data-table"
              aria-label="Announcements list" aria-busy="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="Title" min-width="200" show-overflow-tooltip />
      <el-table-column prop="type" label="Type" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.type" size="small">{{ row.type }}</el-tag>
          <span v-else class="text-muted">—</span>
        </template>
      </el-table-column>
      <el-table-column label="Status" width="120">
        <template #default="{ row }">
          <el-tag v-if="!row.enabled" type="info" size="small">Disabled</el-tag>
          <el-tag v-else-if="isExpired(row)" type="warning" size="small">Expired</el-tag>
          <el-tag v-else type="success" size="small">Active</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Start" width="170">
        <template #default="{ row }">{{ row.startTime || '—' }}</template>
      </el-table-column>
      <el-table-column label="End" width="170">
        <template #default="{ row }">{{ row.endTime || '—' }}</template>
      </el-table-column>
      <el-table-column label="Actions" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="openEdit(row)">Edit</el-button>
          <el-popconfirm title="Delete this announcement?" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button link type="danger" size="small">Delete</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <!-- Create / Edit Dialog -->
  <el-dialog v-model="dialogVisible" :title="editingId ? 'Edit Announcement' : 'New Announcement'"
             width="560px" destroy-on-close>
    <el-form :model="form" label-position="top">
      <el-form-item label="Title" required>
        <el-input v-model="form.title" maxlength="200" show-word-limit />
      </el-form-item>
      <el-form-item label="Content (displayed in tip)" required>
        <el-input v-model="form.content" type="textarea" :rows="3" maxlength="200" show-word-limit />
      </el-form-item>
      <el-form-item label="Type">
        <el-select v-model="form.type" placeholder="Select type" clearable>
          <el-option label="Promo" value="PROMO" />
          <el-option label="Maintenance" value="MAINTENANCE" />
          <el-option label="New Feature" value="NEW_FEATURE" />
        </el-select>
      </el-form-item>
      <el-form-item label="Start Time">
        <el-date-picker v-model="form.startTime" type="datetime" placeholder="Optional"
                        format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DDTHH:mm:ss" />
      </el-form-item>
      <el-form-item label="End Time">
        <el-date-picker v-model="form.endTime" type="datetime" placeholder="Optional"
                        format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DDTHH:mm:ss" />
      </el-form-item>
      <el-form-item label="Enabled">
        <el-switch v-model="form.enabled" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">Cancel</el-button>
      <el-button type="primary" :loading="saving" @click="handleSave">
        {{ editingId ? 'Update' : 'Create' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getAnnouncements,
  createAnnouncement,
  updateAnnouncement,
  deleteAnnouncement,
} from '@/api/admin'

const rows = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const editingId = ref<number | null>(null)

const form = reactive({
  title: '',
  content: '',
  type: '',
  startTime: '',
  endTime: '',
  enabled: true,
})

const load = async () => {
  loading.value = true
  try {
    const res = await getAnnouncements()
    rows.value = res.data || []
  } catch (e: any) {
    ElMessage.error(e?.message || 'Failed to load announcements')
  } finally {
    loading.value = false
  }
}

const isExpired = (row: any) => {
  if (!row.endTime) return false
  return new Date(row.endTime) < new Date()
}

const resetForm = () => {
  form.title = ''
  form.content = ''
  form.type = ''
  form.startTime = ''
  form.endTime = ''
  form.enabled = true
}

const openCreate = () => {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row: any) => {
  editingId.value = row.id
  form.title = row.title || ''
  form.content = row.content || ''
  form.type = row.type || ''
  form.startTime = row.startTime || ''
  form.endTime = row.endTime || ''
  form.enabled = row.enabled !== false
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.title.trim() || !form.content.trim()) {
    ElMessage.warning('Title and content are required')
    return
  }
  saving.value = true
  try {
    const payload = {
      title: form.title,
      content: form.content,
      type: form.type || undefined,
      startTime: form.startTime || undefined,
      endTime: form.endTime || undefined,
      enabled: form.enabled,
    }
    if (editingId.value) {
      await updateAnnouncement(editingId.value, payload)
      ElMessage.success('Announcement updated')
    } else {
      await createAnnouncement(payload)
      ElMessage.success('Announcement created')
    }
    dialogVisible.value = false
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || 'Failed to save')
  } finally {
    saving.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await deleteAnnouncement(id)
    ElMessage.success('Announcement deleted')
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || 'Failed to delete')
  }
}

onMounted(load)
</script>

<style scoped>
.admin-page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.admin-page-title {
  margin: 0 0 4px;
  font-size: 20px;
  font-weight: 600;
}
.admin-page-subtitle {
  margin: 0;
  color: #909399;
  font-size: 13px;
}
.text-muted {
  color: #c0c4cc;
}
</style>
