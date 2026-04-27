<template>
  <el-card shadow="never" class="billing-container admin-page-card">
    <template #header>
      <div class="admin-page-header">
        <div>
          <span class="admin-page-title">Billing Discount Hub</span>
          <div class="admin-page-subtitle">Manage pricing multipliers and discount rates in one place.</div>
        </div>
        <div class="admin-page-toolbar">
          <el-button :icon="Refresh" :loading="loading" @click="load">Reload</el-button>
        </div>
      </div>
    </template>

    <el-row :gutter="16" v-loading="loading" class="billing-grid">
      <el-col :xs="24" :sm="12" :lg="12" :xl="12" class="billing-grid-item">
        <DiscountOverviewCard
          title="Long-Rent Discount"
          tag="LONG_RENT"
          :summary="longRentSummary"
          @click="longRentDialogVisible = true"
        />
      </el-col>
      <el-col :xs="24" :sm="12" :lg="12" :xl="12" class="billing-grid-item">
        <DiscountOverviewCard
          title="Student Discount"
          tag="STUDENT"
          :summary="studentSummary"
          @click="studentDialogVisible = true"
        />
      </el-col>
      <el-col :xs="24" :sm="12" :lg="12" :xl="12" class="billing-grid-item">
        <DiscountOverviewCard
          title="Senior Discount"
          tag="SENIOR"
          :summary="seniorSummary"
          @click="seniorDialogVisible = true"
        />
      </el-col>
      <el-col :xs="24" :sm="12" :lg="12" :xl="12" class="billing-grid-item">
        <DiscountOverviewCard
          title="Frequent User Discount"
          tag="FREQUENT"
          :summary="frequentSummary"
          @click="frequentDialogVisible = true"
        />
      </el-col>
    </el-row>
  </el-card>

  <LongRentDiscountDialog
    :visible="longRentDialogVisible"
    :settings="settings"
    :logs="logs"
    :logs-loading="logsLoading"
    :saving="savingKind === 'longRent'"
    @update:visible="longRentDialogVisible = $event"
    @refresh-logs="loadLogs"
    @save="saveLongRent"
  />

  <SimpleDiscountRateDialog
    :visible="studentDialogVisible"
    title="Student Discount Adjustment"
    description="Adjust the effective discount rate applied to users who pass student eligibility verification."
    label="Student discount rate"
    :rate="settings?.studentDiscountRate ?? 0.8"
    :saving="savingKind === 'student'"
    @update:visible="studentDialogVisible = $event"
    @save="(rate) => saveSingleRate('studentDiscountRate', rate, 'student', 'Student discount has been updated.')"
  />

  <SimpleDiscountRateDialog
    :visible="seniorDialogVisible"
    title="Senior Discount Adjustment"
    description="Adjust the effective discount rate applied when users qualify as seniors."
    label="Senior discount rate"
    :rate="settings?.seniorDiscountRate ?? 0.8"
    :saving="savingKind === 'senior'"
    @update:visible="seniorDialogVisible = $event"
    @save="(rate) => saveSingleRate('seniorDiscountRate', rate, 'senior', 'Senior discount has been updated.')"
  />

  <SimpleDiscountRateDialog
    :visible="frequentDialogVisible"
    title="Frequent User Discount Adjustment"
    description="Adjust the effective discount rate applied to frequent users."
    label="Frequent user discount rate"
    :rate="settings?.frequentDiscountRate ?? 0.8"
    :saving="savingKind === 'frequent'"
    @update:visible="frequentDialogVisible = $event"
    @save="(rate) => saveSingleRate('frequentDiscountRate', rate, 'frequent', 'Frequent user discount has been updated.')"
  />
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { Refresh } from "@element-plus/icons-vue";
import { fetchBillingSettings, fetchBillingSettingsLogs, updateBillingSettings } from "@/api/admin";
import type { BillingSettings, BillingSettingsLog } from "@/types/api";
import DiscountOverviewCard from "@/components/admin/DiscountOverviewCard.vue";
import LongRentDiscountDialog from "@/components/admin/LongRentDiscountDialog.vue";
import SimpleDiscountRateDialog from "@/components/admin/SimpleDiscountRateDialog.vue";

const loading = ref(false);
const logsLoading = ref(false);
const settings = ref<BillingSettings | null>(null);
const logs = ref<BillingSettingsLog[]>([]);
const savingKind = ref<"" | "longRent" | "student" | "senior" | "frequent">("");

const longRentDialogVisible = ref(false);
const studentDialogVisible = ref(false);
const seniorDialogVisible = ref(false);
const frequentDialogVisible = ref(false);

const longRentSummary = computed(() => {
  if (!settings.value) return "Loading...";
  return [
    `T1: ${settings.value.longRentThresholdHours}h, T2: ${settings.value.extraLongRentThresholdHours}h`,
    `m1: ${settings.value.longRentHourRateMultiplier.toFixed(4)}, m2: ${settings.value.extraLongRentHourRateMultiplier.toFixed(4)}`
  ].join("\n");
});

const studentSummary = computed(() => {
  if (!settings.value) return "Loading...";
  return `Current rate: ${settings.value.studentDiscountRate.toFixed(4)}`;
});

const seniorSummary = computed(() => {
  if (!settings.value) return "Loading...";
  return `Current rate: ${settings.value.seniorDiscountRate.toFixed(4)}`;
});

const frequentSummary = computed(() => {
  if (!settings.value) return "Loading...";
  return `Current rate: ${settings.value.frequentDiscountRate.toFixed(4)}`;
});

function normalizeBillingErrorMessage(error: any, fallback: string) {
  const rawMessage = error?.response?.data?.message;
  if (typeof rawMessage !== "string" || !rawMessage.trim()) {
    return fallback;
  }
  const lowered = rawMessage.toLowerCase();
  const looksTechnical =
    lowered.includes("java.") ||
    lowered.includes("com.fasterxml") ||
    lowered.includes("cannot deserialize") ||
    lowered.includes("json parse") ||
    lowered.includes("failed to convert");
  if (looksTechnical) {
    return "Request format is invalid. Please check the value and try again.";
  }
  return rawMessage;
}

async function load() {
  loading.value = true;
  try {
    settings.value = await fetchBillingSettings();
  } catch (error: any) {
    ElMessage.error(normalizeBillingErrorMessage(error, "Could not load billing settings."));
  } finally {
    loading.value = false;
  }
}

async function loadLogs() {
  logsLoading.value = true;
  try {
    const result = await fetchBillingSettingsLogs(20);
    logs.value = result.data;
  } catch (error: any) {
    ElMessage.error(normalizeBillingErrorMessage(error, "Could not load billing logs."));
  } finally {
    logsLoading.value = false;
  }
}

async function saveLongRent(payload: { longRentHourRateMultiplier: number; extraLongRentHourRateMultiplier: number }) {
  savingKind.value = "longRent";
  try {
    settings.value = await updateBillingSettings(payload);
    ElMessage.success("Long-rent discount has been updated.");
    longRentDialogVisible.value = false;
    await loadLogs();
  } catch (error: any) {
    ElMessage.error(normalizeBillingErrorMessage(error, "Could not update the long-rent discount."));
  } finally {
    savingKind.value = "";
  }
}

async function saveSingleRate(
  key: "studentDiscountRate" | "seniorDiscountRate" | "frequentDiscountRate",
  value: number,
  kind: "student" | "senior" | "frequent",
  successMessage: string
) {
  savingKind.value = kind;
  try {
    settings.value = await updateBillingSettings({ [key]: value });
    ElMessage.success(successMessage);
    if (kind === "student") studentDialogVisible.value = false;
    if (kind === "senior") seniorDialogVisible.value = false;
    if (kind === "frequent") frequentDialogVisible.value = false;
  } catch (error: any) {
    ElMessage.error(normalizeBillingErrorMessage(error, "Could not update this discount setting."));
  } finally {
    savingKind.value = "";
  }
}

onMounted(async () => {
  await load();
  await loadLogs();
});
</script>

<style scoped>
.billing-container {
  border-radius: var(--ui-radius-lg);
  overflow: hidden;
}

.billing-grid {
  row-gap: 12px;
}

.billing-grid-item {
  display: flex;
}

.billing-grid-item :deep(.discount-card) {
  width: 100%;
}
</style>
