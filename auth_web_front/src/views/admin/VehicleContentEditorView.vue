<template>
  <el-card shadow="never" class="editor-container admin-page-card">
    <template #header>
      <div class="admin-page-header">
        <div>
          <span class="admin-page-title">Vehicle Content Editor</span>
          <div class="admin-page-subtitle">Edit model descriptions and preview content in real time.</div>
        </div>
        <el-space class="admin-page-toolbar">
          <el-button @click="resetDefaults">Reset Defaults</el-button>
          <el-button type="primary" :icon="Check" @click="save">Save Content</el-button>
        </el-space>
      </div>
    </template>

    <el-alert
      title="This page edits the vehicle intro content shown in the admin preview."
      type="info"
      show-icon
      :closable="false"
      class="hint admin-hint"
    />

    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="admin-panel">
          <template #header>
            <strong>Editable Content</strong>
          </template>

          <el-tabs v-model="activeKey">
            <el-tab-pane v-for="item in models" :key="item.key" :name="item.key" :label="item.key">
              <el-form label-position="top" class="form">
                <el-form-item label="Display Name">
                  <el-input v-model="item.name" />
                </el-form-item>
                <el-form-item label="Description">
                  <el-input v-model="item.description" />
                </el-form-item>
                <el-form-item label="Range">
                  <el-input v-model="item.range" />
                </el-form-item>
                <el-form-item label="Top Speed">
                  <el-input v-model="item.speed" />
                </el-form-item>
                <el-form-item label="Motor">
                  <el-input v-model="item.motor" />
                </el-form-item>
                <el-form-item label="Selection Advice">
                  <el-input v-model="item.advice" type="textarea" :rows="3" />
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="admin-panel">
          <template #header>
            <strong>Live Preview</strong>
          </template>
          <el-space direction="vertical" fill :size="12">
            <div v-for="item in models" :key="item.key" class="preview-item">
              <div class="preview-title">{{ item.name }}</div>
              <div class="preview-line">{{ item.description }}</div>
              <div class="preview-line">Range: {{ item.range }}</div>
              <div class="preview-line">Top Speed: {{ item.speed }}</div>
              <div class="preview-line">Motor: {{ item.motor }}</div>
              <div class="preview-line">Advice: {{ item.advice }}</div>
            </div>
          </el-space>
        </el-card>
      </el-col>
    </el-row>
  </el-card>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { Check } from "@element-plus/icons-vue";
import { ref } from "vue";

type VehicleContent = {
  key: "GEN1" | "GEN2" | "GEN3" | "GEN3PRO";
  name: string;
  description: string;
  range: string;
  speed: string;
  motor: string;
  advice: string;
};

const STORAGE_KEY = "admin.vehicle.content.v1";

const defaults: VehicleContent[] = [
  {
    key: "GEN1",
    name: "GEN1: Ninebot Fz3",
    description: "Entry level for daily commute.",
    range: "115km (Max) / 92km (Real)",
    speed: "25km/h",
    motor: "400W",
    advice: "Good for beginners, no license required."
  },
  {
    key: "GEN2",
    name: "GEN2: Ninebot V70",
    description: "Mid-range commute with better speed.",
    range: "70km",
    speed: "47km/h",
    motor: "800W",
    advice: "Suitable for medium distance travel."
  },
  {
    key: "GEN3",
    name: "GEN3: Ninebot M95C",
    description: "Long-range flagship for heavy usage.",
    range: "up to 145km",
    speed: "55km/h",
    motor: "1500W (Peak 2600W)",
    advice: "Large storage, best for delivery or long trips."
  },
  {
    key: "GEN3PRO",
    name: "GEN3PRO: Ninebot E300P MK2",
    description: "Top-performance electric motorcycle.",
    range: "125km (Mixed)",
    speed: "135km/h (0-100 in 5.9s)",
    motor: "Peak 29kW",
    advice: "For ultimate speed and performance enthusiasts."
  }
];

const parseStored = (): VehicleContent[] | null => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return null;
    const parsed = JSON.parse(raw) as VehicleContent[];
    if (!Array.isArray(parsed) || parsed.length !== 4) return null;
    return parsed;
  } catch {
    return null;
  }
};

const models = ref<VehicleContent[]>(parseStored() ?? JSON.parse(JSON.stringify(defaults)));
const activeKey = ref<VehicleContent["key"]>("GEN1");

function save() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(models.value));
  ElMessage.success("Vehicle content saved");
}

function resetDefaults() {
  models.value = JSON.parse(JSON.stringify(defaults));
  ElMessage.success("Default content restored");
}
</script>

<style scoped>
.editor-container {
  border-radius: var(--ui-radius-lg);
}

.hint {
  margin-bottom: var(--ui-space-4);
}

.content-row {
  margin-top: 4px;
  row-gap: var(--ui-space-4);
}

.form {
  margin-top: 8px;
}

.preview-item {
  border: 1px solid var(--ui-border-soft);
  border-radius: var(--ui-radius-sm);
  padding: 10px 12px;
  background: var(--ui-bg-surface-soft);
}

.preview-title {
  font-weight: 600;
  margin-bottom: 6px;
  color: var(--ui-text-strong);
}

.preview-line {
  font-size: 13px;
  color: var(--ui-text-muted);
  line-height: 1.5;
}
</style>
