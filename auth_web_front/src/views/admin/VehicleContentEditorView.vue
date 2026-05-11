<template>
  <el-card shadow="never" class="editor-container admin-page-card">
    <template #header>
      <div class="admin-page-header">
        <div>
          <h1 class="admin-page-title">Vehicle Content Editor</h1>
          <div class="admin-page-subtitle">Edit model descriptions and preview content in real time.</div>
        </div>
        <el-space class="admin-page-toolbar">
          <el-button @click="resetDefaults">Reset Defaults</el-button>
          <el-button type="primary" :icon="Check" :loading="saving" @click="save">Save Content</el-button>
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
            <div class="admin-section-title">Editable Content</div>
          </template>

          <el-tabs v-model="activeKey">
            <el-tab-pane v-for="item in models" :key="item.key" :name="item.key" :label="item.key">
              <el-form label-position="top" class="form">
                <el-form-item label="Display Name">
                  <el-input v-model="item.name" />
                </el-form-item>
                <el-form-item label="Subtitle">
                  <el-input v-model="item.subtitle" />
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
            <div class="admin-section-title">Live Preview</div>
          </template>
          <el-space direction="vertical" fill :size="12">
            <div v-for="item in models" :key="item.key" class="preview-item">
              <div class="preview-title">{{ item.name }}</div>
              <div class="preview-line" v-if="item.subtitle">{{ item.subtitle }}</div>
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
import { ref, onMounted } from "vue";
import { getVehicleDescriptions, updateVehicleDescription } from "@/api/admin";
import type { VehicleDescription } from "@/types/api";

type VehicleContent = {
  key: string;
  name: string;
  subtitle: string;
  description: string;
  range: string;
  speed: string;
  motor: string;
  advice: string;
};

const defaults: VehicleContent[] = [
  { key: "GEN1", name: "GEN1", subtitle: "Ninebot Fz3", description: "Best for beginners and short daily trips.", range: "115km/92km", speed: "25km/h", motor: "400W", advice: "Best for beginners and short daily trips." },
  { key: "GEN2", name: "GEN2", subtitle: "Ninebot V70", description: "Balanced choice for mid-range commuting.", range: "70km", speed: "47km/h", motor: "800W", advice: "Balanced choice for mid-range commuting." },
  { key: "GEN3", name: "GEN3", subtitle: "Ninebot M95C", description: "Long-range flagship for heavy usage scenarios.", range: "145km max", speed: "55km/h", motor: "1500W/2600W", advice: "Long-range flagship for heavy usage scenarios." },
  { key: "GEN3PRO", name: "GEN3 PRO", subtitle: "Ninebot E300P MK2", description: "Performance-first choice for advanced riders.", range: "125km", speed: "135km/h", motor: "29kW peak", advice: "Performance-first choice for advanced riders." }
];

const models = ref<VehicleContent[]>(JSON.parse(JSON.stringify(defaults)));
const activeKey = ref("GEN1");
const loading = ref(false);
const saving = ref(false);

const API_TYPE_MAP: Record<string, string> = { GEN1: "GEN1", GEN2: "GEN2", GEN3: "GEN3", GEN3PRO: "GEN3PRO" };

function toContent(d: VehicleDescription): VehicleContent {
  return {
    key: d.vehicleType,
    name: d.displayName,
    subtitle: d.subtitle,
    description: d.description,
    range: d.rangeText,
    speed: d.speedText,
    motor: d.motorText,
    advice: d.advice
  };
}

async function load() {
  loading.value = true;
  try {
    const data = await getVehicleDescriptions();
    if (data && data.length > 0) {
      models.value = data.map(toContent);
    }
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to load vehicle descriptions, using defaults");
  } finally {
    loading.value = false;
  }
}

async function save() {
  saving.value = true;
  try {
    const results = await Promise.allSettled(
      models.value.map(async (m) => {
        const type = API_TYPE_MAP[m.key] ?? m.key;
        await updateVehicleDescription(type, {
          display_name: m.name,
          subtitle: m.subtitle,
          description: m.description,
          range_text: m.range,
          speed_text: m.speed,
          motor_text: m.motor,
          advice: m.advice
        });
        return m.key;
      })
    );
    const succeeded = results.filter((r) => r.status === "fulfilled").length;
    const failed = results.filter((r) => r.status === "rejected");
    if (failed.length === 0) {
      ElMessage.success("Vehicle content saved");
    } else {
      const failedTypes = results
        .map((r, i) => (r.status === "rejected" ? models.value[i].key : null))
        .filter(Boolean)
        .join(", ");
      ElMessage.warning(`Saved ${succeeded}/${results.length} types. Failed: ${failedTypes}`);
    }
  } catch (error: any) {
    ElMessage.error(error?.response?.data?.message ?? "Failed to save vehicle descriptions");
  } finally {
    saving.value = false;
  }
}

function resetDefaults() {
  models.value = JSON.parse(JSON.stringify(defaults));
  ElMessage.success("Default content restored (click Save to persist)");
}

onMounted(load);
</script>

<style scoped>
.editor-container {
  border-radius: var(--ui-radius-lg);
}

.hint {
  margin-bottom: var(--ui-space-4);
}

.content-row {
  margin-top: var(--ui-space-2);
  row-gap: var(--ui-space-4);
}

.form {
  margin-top: var(--ui-space-2);
}

.preview-item {
  border: 1px solid var(--ui-border-soft);
  border-radius: var(--ui-radius-sm);
  padding: var(--ui-space-3);
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
