# Custom Notification System Design

**Date:** 2026-05-06
**Status:** Approved

## Context

The app currently uses native `uni.showToast` / `uni.showModal` / `uni.showActionSheet` for all user feedback. These produce Android system-styled dialogs that visually clash with the app's green brand design system. The goal is to replace all three with custom components that match the app's visual identity while maintaining the existing API surface (19 pages import from `appToast.uts`).

## Architecture

**Event-driven singleton pattern.** `appToast.uts` emits events via `uni.$emit`. Global components in `App.uvue` listen and render. Zero changes needed in consuming pages.

```
Page → appToast.uts → uni.$emit("app:toast") → AppToast.uvue (renders)
```

## Components

### 1. AppToast (top notification)

**Visual:**
```
┌───────────────────────────────────┐
│  ┌──┐                            │
│  │✓ │  Booking confirmed!        │
│  └──┘                            │
└───────────────────────────────────┘
```

- White card, `border-radius: 12px`, `$shadow-card`
- Left: 24x24 color block icon (rounded) + white symbol
- Right: message text `$font-body`, color `$text-primary`
- Position: top, below safe area, `16px` horizontal margin
- Slide-in from top + fade out (`transition: all 0.3s ease`)

**Variants:**

| Type | Icon bg | Symbol |
|------|---------|--------|
| success | `$brand-green` #4CAF50 | ✓ |
| error | `$color-danger` #f44336 | ✕ |
| info | `$color-info` #2196F3 | ℹ |

### 2. AppModal (confirmation dialog)

**Visual:**
- Overlay: `rgba(0,0,0,0.5)`, full screen
- Card: centered, `width: 80%`, `border-radius: 16px`, white bg, `$shadow-modal`
- Title: `$font-section` (16px), bold, `$text-primary`
- Content: `$font-body` (14px), `$text-secondary`
- Buttons: row at bottom, confirm (green bg + white text) + cancel (gray bg + dark text)

### 3. AppActionSheet (bottom picker)

**Visual:**
- Overlay: `rgba(0,0,0,0.5)`, full screen
- Card: bottom-anchored, `border-radius: 16px 16px 0 0`, white bg
- Options: list with `$font-body` text, dividers (`$border-light`)
- Cancel: separate section below divider, `$color-danger` text
- Slide-up animation

## API

### Existing (unchanged signatures)

```typescript
showSuccess(title: string, duration?: number = 2500)
showError(title: string, duration?: number = 4000)
showInfo(title: string, duration?: number = 2800)
showThenNavigate(title: string, url: string, isSuccess?: boolean, delayMs?: number, navigateType?: string)
```

### New

```typescript
showConfirm(options: {
  title: string,
  content: string,
  confirmText?: string,   // default "Confirm"
  cancelText?: string     // default "Cancel"
}): Promise<boolean>

showActionSheet(items: string[]): Promise<number>  // returns selected index
```

## Event Protocol

| Event name | Payload | Source |
|---|---|---|
| `app:toast` | `{ type: 'success'\|'error'\|'info', message: string, duration: number }` | appToast.uts |
| `app:modal` | `{ title, content, confirmText, cancelText, resolve }` | appToast.uts |
| `app:actionsheet` | `{ items: string[], resolve }` | appToast.uts |

## Files to Modify

| File | Action |
|---|---|
| `components/AppToast/AppToast.uvue` | **Create** — toast component |
| `components/AppModal/AppModal.uvue` | **Create** — modal component |
| `components/AppActionSheet/AppActionSheet.uvue` | **Create** — action sheet component |
| `common/appToast.uts` | **Modify** — replace `uni.showToast`/`uni.showModal` with `uni.$emit`, add `showConfirm`/`showActionSheet` |
| `App.uvue` | **Modify** — mount three global components |

**No changes to any of the 19 page files.**

## Constraints

- Only class selectors (uvue requirement)
- No `gap` property (use margins)
- `position: fixed` for overlays
- `transition` and `transform` confirmed working in this codebase

## Verification

1. Build the app in HBuilderX
2. Trigger toast from any page (e.g., login success) — verify custom toast appears at top
3. Trigger confirm dialog (e.g., logout) — verify custom modal appears centered
4. Trigger action sheet (e.g., card brand picker) — verify custom sheet slides up from bottom
5. Verify auto-dismiss timing matches existing behavior
6. Verify `showThenNavigate` still navigates after toast
7. Test on Android device for rendering and animation smoothness
