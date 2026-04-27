# UI Modernization Guide

This document defines the reusable UI baseline for `auth_web_front`.

## 1. Design Tokens

Use global tokens from `src/styles.css` as the first option.

- Color: `--ui-color-primary-*`, `--ui-color-success-*`, `--ui-color-warning-*`, `--ui-color-danger-*`
- Text: `--ui-text-strong`, `--ui-text-default`, `--ui-text-muted`, `--ui-text-subtle`
- Surface: `--ui-bg-app`, `--ui-bg-surface`, `--ui-bg-surface-soft`
- Border and shape: `--ui-border-default`, `--ui-border-soft`, `--ui-radius-*`
- Spacing and shadow: `--ui-space-*`, `--ui-shadow-*`

Do not add hard-coded colors or shadows in new pages unless there is a clear exception.

## 2. Reusable Page Skeleton

For admin pages, reuse shared classes from `styles.css`.

- Root card: `admin-page-card`
- Header row: `admin-page-header`
- Title: `admin-page-title`
- Subtitle: `admin-page-subtitle`
- Toolbar: `admin-page-toolbar`
- Filter select width: `admin-filter-select`
- Section title/subtitle: `admin-section-title` and `admin-section-subtitle` for page-local subsection headings below card titles.

For table/list pages:

- Use `admin-data-table` on `el-table`
- Keep pagination in a dedicated footer container

For dialogs/forms:

- Use `admin-dialog-form` for major forms in dialog body
- Keep footer buttons in `Cancel` then `Primary action` order

For hints and lightweight panels:

- Use `admin-hint` and `admin-panel`
- Use `admin-inline-notice` with semantic modifiers for inline info, warning, or risk messages.
- Use `admin-table-empty-state`, `admin-table-empty-title`, and `admin-table-empty-text` for custom table empty slots.
- Use `admin-chart-card`, `admin-chart-description`, and `admin-chart-frame` for admin chart sections that need consistent header/body spacing and accessible chart descriptions.
- Use `admin-loading-section` on `v-loading` containers and `admin-skeleton-card` on Element Plus skeletons so loading surfaces share radius, minimum height, and visible loading affordances.

For status mapping:

- Prefer helpers from `src/adminStatus.ts` before adding page-local tag or chart status rules.

## 3. Component Family Rules

### Header + Toolbar

- Every page should expose one primary title and one short subtitle.
- Toolbars should keep core actions on the right and avoid mixed visual weight.
- Use `Refresh` for plain reload actions and `Search` when filters are being applied.

### Table

- Use striped rows and a soft border.
- Use status tags with semantic colors and consistent wording.
- Keep action labels verb-first (`Approve`, `Reject`, `Escalate`, `Save`).

Status tag mapping baseline:

- `success`: Completed, Approved, Resolved, Visible, Healthy.
- `warning`: Pending, Escalated, Needs review, At-risk transitions.
- `danger`: Rejected, Failed, Maintenance-critical, Destructive state.
- `info`: Neutral metadata, Hidden/not enabled, non-critical progress.

Status mapping audit:

- Tables and detail surfaces should use shared status helpers for repeated concepts such as approval, resolution, escalation, fleet status, and priority.
- Status-like charts must use the same semantic meaning as tags. For fault or priority charts, `HIGH`/`CRITICAL` maps to `danger`, `MEDIUM` maps to `warning`, and `LOW`/`UNKNOWN` maps to `info`.
- Vehicle status charts should match fleet tags: available is `success`, maintenance is `danger`, rented is `info`, and reserved is `warning`.

### Dialog + Form

- Body density should be medium (avoid over-compressed rows).
- Avoid custom spacing values unless token spacing is insufficient.
- Keep one clear primary button only.

### Card + KPI

- KPI cards use token color accents and consistent icon container sizing.
- Static KPI cards use `admin-kpi-card` with `admin-kpi-content`, `admin-kpi-title`, `admin-kpi-value`, and optional `admin-kpi-desc`; keep them non-clickable and avoid hover affordances.
- Do not mix multiple shadow strengths in the same section.
- Clickable cards use `admin-clickable-card`, expose button semantics, and support activation with both `Enter` and `Space`.
- Admin routes should prefer route-level dynamic imports for heavier views, especially chart-heavy pages, while preserving route paths, guards, and default redirects.

## 4. Allowed Local Overrides

Local page/component overrides are allowed only when:

1. A visualization needs a custom palette (for example ECharts series colors).
2. A business widget has unique structure not covered by base primitives.
3. Accessibility requires stronger contrast than token default.

When overriding locally:

- Reference tokens first.
- Scope overrides to component class.
- Add a short reason comment if the override is non-obvious.

## 5. New Page Checklist

Before merge, verify:

1. Uses token-based colors, radius, spacing, and shadows.
2. Uses shared page/header/toolbar/table/dialog classes where applicable.
3. No random hex colors introduced without reason.
4. Responsive layout works at desktop and narrow widths.
5. Focus states remain visible and keyboard navigation is usable.
6. Loading, empty, and error states have consistent visual style.
7. KPI cards use `admin-kpi-card` plus semantic `admin-kpi-icon--*` variants instead of page-local color classes.
8. Paginated tables place pagination in `admin-pagination-footer` and avoid page-local pager spacing.
9. Detail dialogs use `admin-detail-grid`, `admin-detail-field`, and `admin-detail-label` before adding custom field layouts.
10. Chart palettes read from global tokens or a token-backed helper; any fallback color must match the token it represents.
11. Status tags and status-like chart encodings follow the shared semantic mapping (`success/warning/danger/info`) across pages.
12. Clickable cards support keyboard activation (`Enter`/`Space`) and use shared `admin-clickable-card` interaction styles.
13. Status mapping audit covers Admin tables and charts that encode state, priority, or risk.
14. Clickable card accessibility audit confirms hover, focus-visible, active, pointer, role, label, and keyboard activation.
15. Loading containers expose `admin-loading-section` and keep `aria-busy` in sync with loading state where practical.
16. Admin section headings use `admin-section-title`/`admin-section-subtitle` before adding page-local title classes.

## 6. Visual Regression Baseline

Pilot pages to snapshot first:

- `views/admin/ScootersView.vue`
- `views/admin/FeedbacksView.vue`
- `views/admin/BillingSettingsView.vue`

Then expand to:

- `views/admin/RevenueView.vue`
- `views/admin/AnalyticsView.vue`
- `views/admin/DiscountVerificationsView.vue`
- `views/admin/HighPriorityIssuesView.vue`
- `views/admin/VehicleContentEditorView.vue`
