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

    <!-- Drafts Section -->
    <el-collapse v-model="draftsExpanded" class="drafts-collapse">
      <el-collapse-item name="drafts">
        <template #title>
          <div class="drafts-header">
            <el-icon><Document /></el-icon>
            <span>Drafts</span>
            <el-badge v-if="drafts.length" :value="drafts.length" class="drafts-badge" />
          </div>
        </template>
        <div v-if="drafts.length === 0" class="drafts-empty">No drafts</div>
        <div v-else class="drafts-list">
          <div v-for="draft in drafts" :key="draft.localId" class="draft-item">
            <div class="draft-item-info">
              <span class="draft-item-title">{{ draft.title || '(Untitled)' }}</span>
              <el-tag v-if="draft.type" size="small" class="draft-item-type">{{ draft.type }}</el-tag>
              <span class="draft-item-time">Updated {{ timeAgo(draft.updatedAt) }}</span>
            </div>
            <div class="draft-item-actions">
              <el-button link type="primary" size="small" @click="openDraft(draft)">Edit</el-button>
              <el-button link type="success" size="small" @click="openDraft(draft)">Publish</el-button>
              <el-popconfirm title="Delete this draft?" @confirm="handleDeleteDraft(draft.localId)">
                <template #reference>
                  <el-button link type="danger" size="small">Delete</el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </div>
      </el-collapse-item>
    </el-collapse>

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
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close>
    <el-form :model="form" label-position="top">
      <el-form-item label="Title" required>
        <el-input v-model="form.title" maxlength="200" show-word-limit />
      </el-form-item>
      <el-form-item label="Content (displayed in tip)" :required="!canSaveDraft">
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
      <el-button v-if="canSaveDraft" :loading="saving" @click="handleSaveDraft">
        Save as Draft
      </el-button>
      <el-button type="primary" :loading="saving" @click="handlePublish">
        {{ editingId ? 'Update' : 'Publish' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Document } from '@element-plus/icons-vue'
import {
  getAnnouncements,
  createAnnouncement,
  updateAnnouncement,
  deleteAnnouncement,
} from '@/api/admin'

// --- Draft types & storage ---

interface AnnouncementDraft {
  localId: string
  title: string
  content: string
  type: string
  startTime: string
  endTime: string
  enabled: boolean
  createdAt: number
  updatedAt: number
}

type DraftFormData = Pick<AnnouncementDraft, 'title' | 'content' | 'type' | 'startTime' | 'endTime' | 'enabled'>

const DRAFTS_KEY = 'announcement_drafts'

function loadDraftsFromStorage(): AnnouncementDraft[] {
  try {
    const raw = localStorage.getItem(DRAFTS_KEY)
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
}

function saveDraftsToStorage(drafts: AnnouncementDraft[]) {
  try {
    localStorage.setItem(DRAFTS_KEY, JSON.stringify(drafts))
  } catch {
    // localStorage unavailable or full
  }
}

function addDraftToStorage(form: DraftFormData): AnnouncementDraft {
  const now = Date.now()
  const draft: AnnouncementDraft = {
    localId: crypto.randomUUID(),
    title: form.title,
    content: form.content,
    type: form.type,
    startTime: form.startTime,
    endTime: form.endTime,
    enabled: form.enabled,
    createdAt: now,
    updatedAt: now,
  }
  const drafts = loadDraftsFromStorage()
  drafts.unshift(draft)
  saveDraftsToStorage(drafts)
  return draft
}

function updateDraftInStorage(localId: string, form: DraftFormData) {
  const drafts = loadDraftsFromStorage()
  const idx = drafts.findIndex(d => d.localId === localId)
  if (idx === -1) return
  drafts[idx] = { ...drafts[idx], ...form, updatedAt: Date.now() }
  saveDraftsToStorage(drafts)
}

function removeDraftFromStorage(localId: string) {
  saveDraftsToStorage(loadDraftsFromStorage().filter(d => d.localId !== localId))
}

// --- State ---

const rows = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const editingId = ref<number | null>(null)
const editingDraftLocalId = ref<string | null>(null)
const drafts = ref<AnnouncementDraft[]>([])
const draftsExpanded = ref('')

const createFormDefaults = () => ({
  title: '', content: '', type: '', startTime: '', endTime: '', enabled: true,
})
const form = reactive(createFormDefaults())

// --- Computed ---

const canSaveDraft = computed(() => editingId.value === null)

const dialogTitle = computed(() => {
  if (editingId.value) return 'Edit Announcement'
  if (editingDraftLocalId.value) return 'Edit Draft'
  return 'New Announcement'
})

// --- Helpers ---

function timeAgo(ts: number): string {
  const seconds = Math.floor((Date.now() - ts) / 1000)
  if (seconds < 60) return 'just now'
  const minutes = Math.floor(seconds / 60)
  if (minutes < 60) return `${minutes}m ago`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}h ago`
  const days = Math.floor(hours / 24)
  return `${days}d ago`
}

const isExpired = (row: any) => {
  if (!row.endTime) return false
  return new Date(row.endTime) < new Date()
}

// --- Load ---

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

const loadDrafts = () => {
  drafts.value = loadDraftsFromStorage()
}

// --- Form helpers ---

const fillForm = (data: Partial<DraftFormData>) => {
  form.title = data.title || ''
  form.content = data.content || ''
  form.type = data.type || ''
  form.startTime = data.startTime || ''
  form.endTime = data.endTime || ''
  form.enabled = data.enabled !== false
}

// --- Open actions ---

const openCreate = () => {
  editingId.value = null
  editingDraftLocalId.value = null
  fillForm({})
  dialogVisible.value = true
}

const openEdit = (row: any) => {
  editingId.value = row.id
  editingDraftLocalId.value = null
  fillForm(row)
  dialogVisible.value = true
}

const openDraft = (draft: AnnouncementDraft) => {
  editingId.value = null
  editingDraftLocalId.value = draft.localId
  fillForm(draft)
  dialogVisible.value = true
}

// --- Save actions ---

const handleSaveDraft = () => {
  if (!form.title.trim()) {
    ElMessage.warning('Title is required')
    return
  }
  if (editingDraftLocalId.value) {
    updateDraftInStorage(editingDraftLocalId.value, form)
    ElMessage.success('Draft updated')
  } else {
    addDraftToStorage(form)
    ElMessage.success('Draft saved')
  }
  dialogVisible.value = false
  loadDrafts()
}

const handlePublish = async () => {
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
      ElMessage.success('Announcement published')
    }
    // Remove draft if publishing from draft
    if (editingDraftLocalId.value) {
      removeDraftFromStorage(editingDraftLocalId.value)
      editingDraftLocalId.value = null
      loadDrafts()
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

const handleDeleteDraft = (localId: string) => {
  removeDraftFromStorage(localId)
  loadDrafts()
  ElMessage.success('Draft deleted')
}

onMounted(() => {
  load()
  loadDrafts()
})
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
.drafts-collapse {
  margin-bottom: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}
.drafts-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}
.drafts-badge {
  margin-left: 4px;
}
.drafts-empty {
  color: #909399;
  font-size: 13px;
  padding: 8px 0;
}
.drafts-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.draft-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #fafafa;
  border-radius: 4px;
}
.draft-item-info {
  display: flex;
  align-items: center;
  gap: 8px;
}
.draft-item-title {
  font-weight: 500;
}
.draft-item-type {
  margin-left: 4px;
}
.draft-item-time {
  color: #909399;
  font-size: 12px;
}
.draft-item-actions {
  display: flex;
  gap: 4px;
}
</style>
