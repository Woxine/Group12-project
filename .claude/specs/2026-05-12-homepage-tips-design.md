# Homepage Tips System — Design Spec

## Overview

Replace the current 2 hardcoded safety tips on the home page with a 4-category, context-aware tip system. Tips are displayed in the existing top notice pill area via vertical swiper, distinguished by colored left borders and custom SVG icons.

## Tip Categories

| Category | Border Color | Icon | Icon BG | Icon Name |
|----------|-------------|------|---------|-----------|
| Safety | `#4CAF50` (green) | shield + checkmark | `#E8F5E9` | `tip-safety` |
| Context-aware | `#2196F3` (blue) | radar/crosshair | `#E3F2FD` | `tip-context` |
| Promotion | `#FF9800` (orange) | star burst | `#FFF3E0` | `tip-promo` |
| Greeting | `#9C27B0` (purple) | lock/personal | `#F3E5F5` | `tip-greeting` |

Icons are SVG files stored in `/static/icons/tip-*.svg`. The same icon set can be reused across the app in future iterations.

## Context-Aware Trigger Conditions

Evaluated on home page `onShow`. Only one tip per condition; if the condition is false, the tip is excluded.

| # | Condition | Tip Text |
|---|-----------|----------|
| 1 | User not logged in (`token` absent) | "Log in to unlock all features" |
| 2 | Logged in, no payment card bound | "Bind a payment card to start riding" |
| 3 | Has eligible discount but no verification submitted | "You may qualify for a discount — verify now" |
| 4 | Discount verified and active | "Your {type} discount is active — {rate}% off" |
| 5 | Nearby available scooters > 0 | "{count} scooters available near you" |
| 6 | Nearby available scooters = 0 | "No scooters nearby — try moving to a new area" |
| 7 | Active CONFIRMED booking | "{minutes} min remaining in your ride" |

### Data Sources

- Conditions 1-2: `uni.getStorageSync` (token, paymentCardId)
- Conditions 3-4: `GET /api/v1/users/{id}/discount-verifications` or booking preview
- Conditions 5-6: existing `fetchScooterMarkers` result count (already fetched on map load)
- Condition 7: active booking end time from `GET /api/v1/users/{id}/bookings`

### Condition 7 — Remaining Ride Time

Currently the order overview pill shows elapsed time. To show remaining time:

1. Extend `ActiveBookingInfo` to include `endTime` (ISO string)
2. Compute `remaining = endTime - now` in the existing `elapsedTimer` interval
3. Display remaining time below the order pill or integrate into the pill's detail text
4. If `endTime` is null (no planned end), fall back to elapsed time display

## Safety Tips

Expand from 2 to ~8 static tips. Hardcoded array in frontend, no backend needed.

```uts
const SAFETY_TIPS = [
  'Before cycling, check the brakes, seat, and pedals.',
  'Remember to return the e-Scooter when the ride is over.',
  'Always wear a helmet when riding.',
  'Ride on designated bike lanes when available.',
  'Reduce speed in crowded areas and at intersections.',
  'Do not ride under the influence of alcohol or drugs.',
  'Use lights and reflectors when riding at night.',
  'Keep both hands on the handlebars while riding.',
]
```

## Personalized Greeting

Based on time of day + username (if logged in).

| Time Range | Greeting |
|------------|----------|
| 05:00–11:59 | Good morning |
| 12:00–17:59 | Good afternoon |
| 18:00–21:59 | Good evening |
| 22:00–04:59 | Good night |

- Logged in: "Good morning, {username}! Ready to ride?"
- Not logged in: "Good morning! Log in to start your journey."

## Promotion Announcements

### Backend

New entity `Announcement`:

| Field | Type | Description |
|-------|------|-------------|
| id | Long | PK |
| title | String | Short title (not displayed in tip, used for admin list) |
| content | String | Tip display text |
| type | String | Category hint (PROMO, MAINTENANCE, NEW_FEATURE) |
| startTime | LocalDateTime | Visibility start |
| endTime | LocalDateTime | Visibility end |
| enabled | Boolean | Manual on/off toggle |
| createdAt | LocalDateTime | Auto-set |

New endpoints:
- `GET /api/v1/announcements/active` — returns list of currently valid announcements (now between start/end, enabled=true)
- `POST /api/v1/admin/announcements` — create
- `PUT /api/v1/admin/announcements/{id}` — update
- `DELETE /api/v1/admin/announcements/{id}` — delete
- `GET /api/v1/admin/announcements` — list all (including inactive, for admin management)

### Admin Frontend

New page `AnnouncementsView.vue` in `auth_web_front/src/views/admin/`:
- Table listing all announcements with status badge (active/expired/disabled)
- Create/edit dialog with title, content, type dropdown, start/end datetime pickers, enabled toggle
- Delete with confirmation

Add route and sidebar entry in admin layout.

### Mobile Frontend

- Fetch `GET /api/v1/announcements/active` on home page `onShow`
- Cache result in `ref`, refresh on each `onShow`
- Each announcement becomes a tip with `tip-promo` category

## Priority Ordering

When multiple tips are eligible, sort by this priority chain:

```
未登录 > 未绑卡 > 折扣资格 > 折扣生效 > 车辆可用 > 促销公告 > 安全提示 > 个性化问候
```

Rules:
- Max 5 tips in the swiper; truncate lower-priority tips
- If user is not logged in, show ONLY the login tip (no other tips make sense)
- Tips are re-evaluated on every `onShow`

## Visual Implementation

### Template Structure

```vue
<view v-else class="top-notice-pill">
  <scroll-view scroll-x="false" class="notice-scroll">
    <swiper class="notice-swiper" vertical="true" autoplay="true" circular
            :interval="3000" :duration="1000">
      <swiper-item v-for="tip in activeTips" :key="tip.id">
        <view :class="['notice-item', 'notice-' + tip.category]">
          <view :class="['notice-icon-box', 'icon-' + tip.category]">
            <image :src="tip.icon" mode="aspectFit" class="notice-icon-svg" />
          </view>
          <text class="notice-text">{{ tip.text }}</text>
        </view>
      </swiper-item>
    </swiper>
  </scroll-view>
</view>
```

### CSS (uvue-compatible)

```scss
.notice-item {
  flex-direction: row;
  align-items: center;
  padding: 0 12px;
  height: 44px;
  border-radius: 22px;
  background-color: rgba(255, 255, 255, 0.95);
}

.notice-safety { border-left: 3px solid #4CAF50; }
.notice-context { border-left: 3px solid #2196F3; }
.notice-promo { border-left: 3px solid #FF9800; }
.notice-greeting { border-left: 3px solid #9C27B0; }

.notice-icon-box {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
}

.icon-safety { background-color: #E8F5E9; }
.icon-context { background-color: #E3F2FD; }
.icon-promo { background-color: #FFF3E0; }
.icon-greeting { background-color: #F3E5F5; }

.notice-icon-svg {
  width: 16px;
  height: 16px;
}

.notice-text {
  flex: 1;
  font-size: 13px;
  color: #333333;
  lines: 1;
  text-overflow: ellipsis;
}
```

## File Changes Summary

### Mobile (uni-app x)
- `pages/index/index.uvue` — tip logic, template, styles
- `common/tips.uts` — new file: tip evaluation engine, safety tip array, greeting logic
- `static/icons/tip-safety.svg` — new
- `static/icons/tip-context.svg` — new
- `static/icons/tip-promo.svg` — new
- `static/icons/tip-greeting.svg` — new

### Backend (Spring Boot)
- `entity/Announcement.java` — new
- `repository/AnnouncementRepository.java` — new
- `controller/AnnouncementController.java` — new (public GET)
- `controller/AdminAnnouncementController.java` — new (admin CRUD)
- `service/AnnouncementService.java` — new
- `service/impl/AnnouncementServiceImpl.java` — new

### Admin Web (Vue + Element Plus)
- `views/admin/AnnouncementsView.vue` — new
- `router/index.ts` — add route
- `layouts/AdminLayout.vue` — add sidebar entry
- `api/admin.ts` — add announcement API calls

## Scope Decisions

- No weather API integration (out of scope for course project)
- No push notifications (polling on onShow is sufficient)
- No user-specific targeting for announcements (all users see all active announcements)
- Icons designed as SVG for scalability; can be extended to other app screens later
