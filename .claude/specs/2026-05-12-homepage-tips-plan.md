# Homepage Tips System — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the 2 hardcoded safety tips on the home page with a 4-category, context-aware tip system featuring colored borders, SVG icons, smart priority ordering, and an admin-manageable announcement backend.

**Architecture:** A new `common/tips.uts` module evaluates user state and returns a sorted tip list. The home page `index.uvue` consumes this list and renders tips with category-specific styling. A new `Announcement` entity + CRUD API supports admin-managed promo tips. All tip logic is client-side except announcements which are fetched from the backend.

**Tech Stack:** UniApp X (uvue + UTS), Spring Boot (Java), Vue 3 + Element Plus (admin), SCSS

**Design Spec:** `.claude/specs/2026-05-12-homepage-tips-design.md`

---

## File Map

| File | Action | Responsibility |
|------|--------|----------------|
| `static/icons/tip-safety.svg` | Create | Safety icon (shield + checkmark) |
| `static/icons/tip-context.svg` | Create | Context icon (radar) |
| `static/icons/tip-promo.svg` | Create | Promo icon (star) |
| `static/icons/tip-greeting.svg` | Create | Greeting icon (lock) |
| `common/tips.uts` | Create | Tip evaluation engine: conditions, sorting, safety array, greeting logic |
| `common/api.uts` | Modify | Add `ANNOUNCEMENTS_ACTIVE_URL` constant |
| `pages/index/index.uvue` | Modify | Replace notice swiper with tip system, extend ActiveBookingInfo, add fetchAnnouncements |
| `backend/.../entity/Announcement.java` | Create | JPA entity |
| `backend/.../repository/AnnouncementRepository.java` | Create | Spring Data repository |
| `backend/.../dto/AnnouncementRequest.java` | Create | Admin create/update request DTO |
| `backend/.../service/AnnouncementService.java` | Create | Service interface |
| `backend/.../service/impl/AnnouncementServiceImpl.java` | Create | Service implementation |
| `backend/.../controller/AnnouncementController.java` | Create | Public GET endpoint |
| `backend/.../controller/AdminAnnouncementController.java` | Create | Admin CRUD endpoints |
| `auth_web_front/src/api/admin.ts` | Modify | Add announcement API functions |
| `auth_web_front/src/views/admin/AnnouncementsView.vue` | Create | Admin announcement management page |
| `auth_web_front/src/router/index.ts` | Modify | Add announcements route |
| `auth_web_front/src/layouts/AdminLayout.vue` | Modify | Add sidebar entry |

---

## Task 1: Create SVG Icon Files

**Files:**
- Create: `static/icons/tip-safety.svg`
- Create: `static/icons/tip-context.svg`
- Create: `static/icons/tip-promo.svg`
- Create: `static/icons/tip-greeting.svg`

- [ ] **Step 1: Create tip-safety.svg**

```xml
<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#4CAF50" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
  <polyline points="9 12 11 14 15 10"/>
</svg>
```

- [ ] **Step 2: Create tip-context.svg**

```xml
<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#2196F3" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <circle cx="12" cy="12" r="10"/>
  <circle cx="12" cy="12" r="3"/>
  <line x1="12" y1="2" x2="12" y2="6"/>
  <line x1="12" y1="18" x2="12" y2="22"/>
  <line x1="2" y1="12" x2="6" y2="12"/>
  <line x1="18" y1="12" x2="22" y2="12"/>
</svg>
```

- [ ] **Step 3: Create tip-promo.svg**

```xml
<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#FF9800" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
</svg>
```

- [ ] **Step 4: Create tip-greeting.svg**

```xml
<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#9C27B0" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
  <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
  <circle cx="12" cy="16" r="1"/>
</svg>
```

- [ ] **Step 5: Commit**

```bash
git add static/icons/tip-*.svg
git commit -m "feat: add SVG icons for homepage tip categories"
```

---

## Task 2: Create Tip Evaluation Engine (common/tips.uts)

**Files:**
- Create: `common/tips.uts`

This module exports a single function `evaluateTips(context)` that returns a sorted array of tip objects. All UTS syntax constraints are followed (no `typeof`, no `reactive()`, no object literal types).

- [ ] **Step 1: Create common/tips.uts**

```uts
/**
 * Homepage tip evaluation engine.
 * Evaluates user context and returns sorted tips by priority.
 */

export type TipItem = {
    id: string
    category: string   // 'safety' | 'context' | 'promo' | 'greeting'
    text: string
    icon: string       // icon path
    priority: number   // lower = higher priority
}

export type TipContext = {
    isLoggedIn: boolean
    username: string
    hasPaymentCard: boolean
    hasDiscountVerified: boolean
    discountType: string   // 'STUDENT' | 'SENIOR' | 'FREQUENT' or ''
    discountRate: number   // e.g. 80 means 20% off
    nearbyScooters: number
    hasActiveBooking: boolean
    remainingMinutes: number
    announcements: AnnouncementTip[]
}

export type AnnouncementTip = {
    id: string
    content: string
}

// Priority constants (lower number = shown first)
const PRIORITY_LOGIN = 10
const PRIORITY_CARD = 20
const PRIORITY_DISCOUNT_VERIFY = 30
const PRIORITY_DISCOUNT_ACTIVE = 40
const PRIORITY_SCOOTERS = 50
const PRIORITY_PROMO = 60
const PRIORITY_SAFETY = 70
const PRIORITY_GREETING = 80

const SAFETY_TIPS: string[] = [
    'Before cycling, check the brakes, seat, and pedals.',
    'Remember to return the e-Scooter when the ride is over.',
    'Always wear a helmet when riding.',
    'Ride on designated bike lanes when available.',
    'Reduce speed in crowded areas and at intersections.',
    'Do not ride under the influence of alcohol or drugs.',
    'Use lights and reflectors when riding at night.',
    'Keep both hands on the handlebars while riding.',
]

const MAX_TIPS = 5

const readStorageString = (key: string): string => {
    const raw = uni.getStorageSync(key)
    return raw == null ? '' : ('' + raw).trim()
}

const readStorageStringOrNull = (key: string): string | null => {
    const value = readStorageString(key)
    return value.length < 1 ? null : value
}

const getTimeGreeting = (): string => {
    const hour = new Date().getHours()
    if (hour >= 5 && hour < 12) return 'Good morning'
    if (hour >= 12 && hour < 18) return 'Good afternoon'
    if (hour >= 18 && hour < 22) return 'Good evening'
    return 'Good night'
}

const buildContext = (scooterCount: number, announcements: AnnouncementTip[]): TipContext => {
    const token = readStorageStringOrNull('token')
    const isLoggedIn = token != null
    const username = readStorageString('username')
    const paymentCardId = readStorageStringOrNull('paymentCardId')

    return {
        isLoggedIn: isLoggedIn,
        username: username,
        hasPaymentCard: paymentCardId != null,
        hasDiscountVerified: readStorageString('discountVerified') == 'true',
        discountType: readStorageString('discountType'),
        discountRate: 80,
        nearbyScooters: scooterCount,
        hasActiveBooking: false,
        remainingMinutes: 0,
        announcements: announcements,
    }
}

const evaluateTips = (ctx: TipContext): TipItem[] => {
    const tips: TipItem[] = []

    // Not logged in → only show login tip
    if (!ctx.isLoggedIn) {
        tips.push({
            id: 'login',
            category: 'context',
            text: 'Log in to unlock all features',
            icon: '/static/icons/tip-context.svg',
            priority: PRIORITY_LOGIN,
        })
        return tips
    }

    // No payment card
    if (!ctx.hasPaymentCard) {
        tips.push({
            id: 'no-card',
            category: 'context',
            text: 'Bind a payment card to start riding',
            icon: '/static/icons/tip-context.svg',
            priority: PRIORITY_CARD,
        })
    }

    // Discount eligible but not verified
    if (!ctx.hasDiscountVerified && ctx.discountType.length > 0) {
        tips.push({
            id: 'discount-verify',
            category: 'context',
            text: 'You may qualify for a discount — verify now',
            icon: '/static/icons/tip-context.svg',
            priority: PRIORITY_DISCOUNT_VERIFY,
        })
    }

    // Discount active
    if (ctx.hasDiscountVerified && ctx.discountType.length > 0) {
        const discountPercent = 100 - ctx.discountRate
        const typeLabel = ctx.discountType.charAt(0).toUpperCase() + ctx.discountType.slice(1).toLowerCase()
        tips.push({
            id: 'discount-active',
            category: 'context',
            text: 'Your ' + typeLabel + ' discount is active — ' + discountPercent + '% off',
            icon: '/static/icons/tip-context.svg',
            priority: PRIORITY_DISCOUNT_ACTIVE,
        })
    }

    // Nearby scooters
    if (ctx.nearbyScooters > 0) {
        tips.push({
            id: 'scooters-nearby',
            category: 'context',
            text: '' + ctx.nearbyScooters + ' scooters available near you',
            icon: '/static/icons/tip-context.svg',
            priority: PRIORITY_SCOOTERS,
        })
    } else {
        tips.push({
            id: 'no-scooters',
            category: 'context',
            text: 'No scooters nearby — try moving to a new area',
            icon: '/static/icons/tip-context.svg',
            priority: PRIORITY_SCOOTERS,
        })
    }

    // Active booking remaining time
    if (ctx.hasActiveBooking && ctx.remainingMinutes > 0) {
        tips.push({
            id: 'ride-remaining',
            category: 'context',
            text: '' + ctx.remainingMinutes + ' min remaining in your ride',
            icon: '/static/icons/tip-context.svg',
            priority: PRIORITY_SCOOTERS + 1,
        })
    }

    // Announcements (promo)
    for (let i = 0; i < ctx.announcements.length; i++) {
        const ann = ctx.announcements[i]
        tips.push({
            id: 'promo-' + ann.id,
            category: 'promo',
            text: ann.content,
            icon: '/static/icons/tip-promo.svg',
            priority: PRIORITY_PROMO + i,
        })
    }

    // Safety tip (rotate daily based on day of year)
    const dayOfYear = Math.floor(Date.now() / 86400000)
    const safetyIndex = dayOfYear % SAFETY_TIPS.length
    tips.push({
        id: 'safety-' + safetyIndex,
        category: 'safety',
        text: SAFETY_TIPS[safetyIndex],
        icon: '/static/icons/tip-safety.svg',
        priority: PRIORITY_SAFETY,
    })

    // Greeting (only for logged-in users with username)
    if (ctx.isLoggedIn && ctx.username.length > 0) {
        const greeting = getTimeGreeting()
        tips.push({
            id: 'greeting',
            category: 'greeting',
            text: greeting + ', ' + ctx.username + '! Ready to ride?',
            icon: '/static/icons/tip-greeting.svg',
            priority: PRIORITY_GREETING,
        })
    }

    // Sort by priority and truncate
    tips.sort((a: TipItem, b: TipItem): number => {
        return a.priority - b.priority
    })

    if (tips.length > MAX_TIPS) {
        return tips.slice(0, MAX_TIPS)
    }
    return tips
}

export { evaluateTips, buildContext, readStorageStringOrNull, getTimeGreeting, PRIORITY_LOGIN, PRIORITY_CARD, PRIORITY_DISCOUNT_VERIFY, PRIORITY_DISCOUNT_ACTIVE, PRIORITY_SCOOTERS, PRIORITY_PROMO, PRIORITY_SAFETY, PRIORITY_GREETING, MAX_TIPS, SAFETY_TIPS }
```

- [ ] **Step 2: Commit**

```bash
git add common/tips.uts
git commit -m "feat: add tip evaluation engine with 4 categories and smart priority sorting"
```

---

## Task 3: Add Announcement API Constant

**Files:**
- Modify: `common/api.uts`

- [ ] **Step 1: Add the endpoint constant**

Add at the end of `common/api.uts`:

```uts
export const ANNOUNCEMENTS_ACTIVE_URL = '/api/v1/announcements/active'
```

- [ ] **Step 2: Commit**

```bash
git add common/api.uts
git commit -m "feat: add announcements active endpoint constant"
```

---

## Task 4: Refactor Home Page Notice Area

**Files:**
- Modify: `pages/index/index.uvue`

This is the largest task. It replaces the static notice swiper with the dynamic tip system, extends booking info to include endTime, and fetches announcements.

- [ ] **Step 1: Update imports in script setup**

Add after the existing imports (line 62):

```uts
import { evaluateTips, buildContext, TipItem, AnnouncementTip } from '../../common/tips.uts'
import { ANNOUNCEMENTS_ACTIVE_URL } from '../../common/api.uts'
```

- [ ] **Step 2: Add tip-related reactive state**

Add after `const elapsedText = ref('')` (around line 84):

```uts
const activeTips = ref([] as TipItem[])
const announcements = ref([] as AnnouncementTip[])
```

- [ ] **Step 3: Extend ActiveBookingInfo to include endTime**

Change the type definition (around line 75):

```uts
type ActiveBookingInfo = {
    id: string
    status: string
    scooterId: string
    startTime: string
    endTime: string
}
```

- [ ] **Step 4: Update checkActiveBooking to parse endTime**

In the `checkActiveBooking` function, update the `found` object construction (around line 172) to also extract endTime:

```uts
const rawEnd = item['end_time'] != null ? item['end_time'] : item['endTime']
const endTime = rawEnd != null ? ('' + rawEnd) : ''
found = { id: id, status: status, scooterId: scooterId, startTime: startTime, endTime: endTime }
```

- [ ] **Step 5: Add remaining minutes computation**

Add a new function after `formatElapsed`:

```uts
const computeRemainingMinutes = (endTimeStr: string): number => {
    const endMs = Date.parse(endTimeStr)
    if (isNaN(endMs)) return 0
    const nowMs = Date.now()
    const diffMin = Math.floor((endMs - nowMs) / 60000)
    return diffMin > 0 ? diffMin : 0
}
```

- [ ] **Step 6: Add fetchAnnouncements function**

Add after `checkActiveBooking`:

```uts
const fetchAnnouncements = () => {
    uni.request({
        url: BASE_URL + ANNOUNCEMENTS_ACTIVE_URL + '?_t=' + Date.now(),
        method: 'GET',
        success: (res) => {
            if (res.statusCode != 200 || res.data == null) {
                announcements.value = []
                return
            }
            const raw = res.data as UTSJSONObject
            let arr: UTSJSONObject[] = []
            if (raw['data'] != null && Array.isArray(raw['data'])) {
                arr = raw['data'] as UTSJSONObject[]
            }
            const result: AnnouncementTip[] = []
            for (let i = 0; i < arr.length; i++) {
                const item = arr[i] as UTSJSONObject
                const rawId = item['id']
                const rawContent = item['content']
                if (rawId != null && rawContent != null) {
                    result.push({
                        id: '' + rawId,
                        content: '' + rawContent,
                    })
                }
            }
            announcements.value = result
        },
        fail: () => {
            announcements.value = []
        }
    })
}
```

- [ ] **Step 7: Add refreshTips function**

Add after `fetchAnnouncements`:

```uts
const refreshTips = () => {
    const scooterCount = markers.value.length
    const booking = activeBooking.value
    let remainingMin = 0
    let hasBooking = false
    if (booking != null && booking.status == 'CONFIRMED' && booking.endTime.length > 0) {
        hasBooking = true
        remainingMin = computeRemainingMinutes(booking.endTime)
    }
    const ctx = buildContext(scooterCount, announcements.value)
    ctx.hasActiveBooking = hasBooking
    ctx.remainingMinutes = remainingMin
    activeTips.value = evaluateTips(ctx)
}
```

- [ ] **Step 8: Update onShow to fetch announcements and refresh tips**

In the `onShow` callback, add `fetchAnnouncements()` call and a delayed `refreshTips()`:

```uts
onShow(() => {
    checkActiveBooking()
    fetchAnnouncements()
    applyMarkers(latitude.value, longitude.value)
    nextTick(() => {
        // ... existing map context setup ...
        setTimeout(() => {
            getCurrentLocation()
            refreshTips()
        }, 200)
    })
})
```

- [ ] **Step 9: Update onMapRegionChange to refresh tips after markers update**

In `onMapRegionChange`, after the debounce calls `refreshMarkersByViewportCenter()`, add a tip refresh:

```uts
regionDebounceTimer = setTimeout(() => {
    refreshMarkersByViewportCenter()
    refreshTips()
}, 500) as number
```

- [ ] **Step 10: Replace the notice swiper template**

Replace the entire `<view v-else class="top-notice-pill">` block (lines 17-23) with:

```vue
<view v-else class="top-notice-pill">
    <swiper class="notice-swiper" vertical="true" autoplay="true" circular
            :interval="3000" :duration="1000">
        <swiper-item v-for="tip in activeTips" :key="tip.id">
            <view :class="['notice-item', 'notice-' + tip.category]">
                <view :class="['notice-icon-box', 'icon-' + tip.category]">
                    <image :src="tip.icon" mode="aspectFit" class="notice-icon-svg"></image>
                </view>
                <text class="notice-text">{{ tip.text }}</text>
            </view>
        </swiper-item>
    </swiper>
</view>
```

- [ ] **Step 11: Replace the notice CSS styles**

Replace the existing `.top-notice-pill`, `.notice-icon`, `.notice-swiper`, `.notice-text` styles with:

```scss
.top-notice-pill {
    position: absolute;
    top: 16px;
    left: 5%;
    width: 90%;
    height: 44px;
    background-color: transparent;
    border-radius: 22px;
    z-index: 10;
}

.notice-swiper {
    width: 100%;
    height: 44px;
}

.notice-item {
    flex-direction: row;
    align-items: center;
    padding: 0 14px;
    height: 44px;
    border-radius: 22px;
    background-color: rgba(255, 255, 255, 0.95);
    box-shadow: $shadow-card;
}

.notice-safety {
    border-left: 3px solid #4CAF50;
}

.notice-context {
    border-left: 3px solid #2196F3;
}

.notice-promo {
    border-left: 3px solid #FF9800;
}

.notice-greeting {
    border-left: 3px solid #9C27B0;
}

.notice-icon-box {
    width: 28px;
    height: 28px;
    border-radius: 8px;
    align-items: center;
    justify-content: center;
    margin-right: 10px;
}

.icon-safety {
    background-color: #E8F5E9;
}

.icon-context {
    background-color: #E3F2FD;
}

.icon-promo {
    background-color: #FFF3E0;
}

.icon-greeting {
    background-color: #F3E5F5;
}

.notice-icon-svg {
    width: 16px;
    height: 16px;
}

.notice-text {
    flex: 1;
    font-size: $font-hint;
    color: $text-primary;
    lines: 1;
    text-overflow: ellipsis;
    overflow: hidden;
}
```

- [ ] **Step 12: Commit**

```bash
git add pages/index/index.uvue common/tips.uts common/api.uts
git commit -m "feat: replace static tips with dynamic context-aware tip system on homepage"
```

---

## Task 5: Create Announcement Entity + Repository (Backend)

**Files:**
- Create: `backend/src/main/java/com/group12/backend/entity/Announcement.java`
- Create: `backend/src/main/java/com/group12/backend/repository/AnnouncementRepository.java`

- [ ] **Step 1: Create Announcement.java**

```java
package com.group12.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
public class Announcement implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 50)
    private String type;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Announcement() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

- [ ] **Step 2: Create AnnouncementRepository.java**

```java
package com.group12.backend.repository;

import com.group12.backend.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    @Query("SELECT a FROM Announcement a WHERE a.enabled = true " +
           "AND (a.startTime IS NULL OR a.startTime <= :now) " +
           "AND (a.endTime IS NULL OR a.endTime >= :now) " +
           "ORDER BY a.createdAt DESC")
    List<Announcement> findActiveAnnouncements(@Param("now") LocalDateTime now);
}
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/group12/backend/entity/Announcement.java backend/src/main/java/com/group12/backend/repository/AnnouncementRepository.java
git commit -m "feat: add Announcement entity and repository"
```

---

## Task 6: Create Announcement DTO + Service (Backend)

**Files:**
- Create: `backend/src/main/java/com/group12/backend/dto/AnnouncementRequest.java`
- Create: `backend/src/main/java/com/group12/backend/service/AnnouncementService.java`
- Create: `backend/src/main/java/com/group12/backend/service/impl/AnnouncementServiceImpl.java`

- [ ] **Step 1: Create AnnouncementRequest.java**

```java
package com.group12.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AnnouncementRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String type;
    private String startTime;
    private String endTime;
    private Boolean enabled;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
```

- [ ] **Step 2: Create AnnouncementService.java**

```java
package com.group12.backend.service;

import com.group12.backend.dto.AnnouncementRequest;
import java.util.List;
import java.util.Map;

public interface AnnouncementService {
    List<Map<String, Object>> getActiveAnnouncements();
    List<Map<String, Object>> getAllAnnouncements();
    Map<String, Object> createAnnouncement(AnnouncementRequest request);
    Map<String, Object> updateAnnouncement(Long id, AnnouncementRequest request);
    void deleteAnnouncement(Long id);
}
```

- [ ] **Step 3: Create AnnouncementServiceImpl.java**

```java
package com.group12.backend.service.impl;

import com.group12.backend.dto.AnnouncementRequest;
import com.group12.backend.entity.Announcement;
import com.group12.backend.exception.BusinessException;
import com.group12.backend.repository.AnnouncementRepository;
import com.group12.backend.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Override
    public List<Map<String, Object>> getActiveAnnouncements() {
        List<Announcement> list = announcementRepository.findActiveAnnouncements(LocalDateTime.now());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Announcement a : list) {
            result.add(toResponse(a));
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getAllAnnouncements() {
        List<Announcement> list = announcementRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Announcement a : list) {
            result.add(toResponse(a));
        }
        return result;
    }

    @Override
    public Map<String, Object> createAnnouncement(AnnouncementRequest request) {
        Announcement entity = new Announcement();
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());
        entity.setType(request.getType());
        entity.setStartTime(parseDateTime(request.getStartTime()));
        entity.setEndTime(parseDateTime(request.getEndTime()));
        entity.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);
        Announcement saved = announcementRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public Map<String, Object> updateAnnouncement(Long id, AnnouncementRequest request) {
        Announcement entity = announcementRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Announcement not found", HttpStatus.NOT_FOUND));
        if (request.getTitle() != null) entity.setTitle(request.getTitle());
        if (request.getContent() != null) entity.setContent(request.getContent());
        if (request.getType() != null) entity.setType(request.getType());
        if (request.getStartTime() != null) entity.setStartTime(parseDateTime(request.getStartTime()));
        if (request.getEndTime() != null) entity.setEndTime(parseDateTime(request.getEndTime()));
        if (request.getEnabled() != null) entity.setEnabled(request.getEnabled());
        Announcement saved = announcementRepository.save(entity);
        return toResponse(saved);
    }

    @Override
    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new BusinessException("Announcement not found", HttpStatus.NOT_FOUND);
        }
        announcementRepository.deleteById(id);
    }

    private Map<String, Object> toResponse(Announcement a) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", a.getId());
        map.put("title", a.getTitle());
        map.put("content", a.getContent());
        map.put("type", a.getType());
        map.put("startTime", a.getStartTime() != null ? a.getStartTime().toString() : null);
        map.put("endTime", a.getEndTime() != null ? a.getEndTime().toString() : null);
        map.put("enabled", a.getEnabled());
        map.put("createdAt", a.getCreatedAt() != null ? a.getCreatedAt().toString() : null);
        return map;
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return null;
        }
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/group12/backend/dto/AnnouncementRequest.java backend/src/main/java/com/group12/backend/service/AnnouncementService.java backend/src/main/java/com/group12/backend/service/impl/AnnouncementServiceImpl.java
git commit -m "feat: add Announcement DTO, service interface and implementation"
```

---

## Task 7: Create Announcement Controllers (Backend)

**Files:**
- Create: `backend/src/main/java/com/group12/backend/controller/AnnouncementController.java`
- Create: `backend/src/main/java/com/group12/backend/controller/AdminAnnouncementController.java`

- [ ] **Step 1: Create AnnouncementController.java (public)**

```java
package com.group12.backend.controller;

import com.group12.backend.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/active")
    public ResponseEntity<Object> getActive() {
        List<Map<String, Object>> list = announcementService.getActiveAnnouncements();
        return ResponseEntity.ok(Map.of("data", list));
    }
}
```

- [ ] **Step 2: Create AdminAnnouncementController.java**

```java
package com.group12.backend.controller;

import com.group12.backend.dto.AnnouncementRequest;
import com.group12.backend.security.AdminAccessGuard;
import com.group12.backend.service.AnnouncementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/announcements")
public class AdminAnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AdminAccessGuard adminAccessGuard;

    @GetMapping
    public ResponseEntity<Object> listAll(HttpServletRequest request) {
        adminAccessGuard.requireAdmin(request);
        List<Map<String, Object>> list = announcementService.getAllAnnouncements();
        return ResponseEntity.ok(Map.of("data", list));
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody AnnouncementRequest request,
                                         HttpServletRequest httpRequest) {
        adminAccessGuard.requireAdmin(httpRequest);
        Map<String, Object> created = announcementService.createAnnouncement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("data", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @Valid @RequestBody AnnouncementRequest request,
                                         HttpServletRequest httpRequest) {
        adminAccessGuard.requireAdmin(httpRequest);
        Map<String, Object> updated = announcementService.updateAnnouncement(id, request);
        return ResponseEntity.ok(Map.of("data", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id,
                                         HttpServletRequest httpRequest) {
        adminAccessGuard.requireAdmin(httpRequest);
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok(Map.of("message", "Announcement deleted"));
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add backend/src/main/java/com/group12/backend/controller/AnnouncementController.java backend/src/main/java/com/group12/backend/controller/AdminAnnouncementController.java
git commit -m "feat: add public and admin Announcement controllers"
```

---

## Task 8: Create Admin Announcements Page (Frontend)

**Files:**
- Modify: `auth_web_front/src/api/admin.ts`
- Create: `auth_web_front/src/views/admin/AnnouncementsView.vue`

- [ ] **Step 1: Add announcement API functions to admin.ts**

Append to `auth_web_front/src/api/admin.ts`:

```ts
// --- Announcements ---

export async function getAnnouncements() {
  const response = await http.get<{ data: any[] }>("/api/v1/admin/announcements");
  return response.data;
}

export async function createAnnouncement(data: {
  title: string;
  content: string;
  type?: string;
  startTime?: string;
  endTime?: string;
  enabled?: boolean;
}) {
  const response = await http.post<{ data: any }>("/api/v1/admin/announcements", data);
  return response.data;
}

export async function updateAnnouncement(id: number, data: {
  title?: string;
  content?: string;
  type?: string;
  startTime?: string;
  endTime?: string;
  enabled?: boolean;
}) {
  const response = await http.put<{ data: any }>(`/api/v1/admin/announcements/${id}`, data);
  return response.data;
}

export async function deleteAnnouncement(id: number) {
  const response = await http.delete<{ message: string }>(`/api/v1/admin/announcements/${id}`);
  return response.data;
}
```

- [ ] **Step 2: Create AnnouncementsView.vue**

Create `auth_web_front/src/views/admin/AnnouncementsView.vue` following the project's admin page pattern:

```vue
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
        <el-date-picker v-model="form.startTime" type="datetime" placeholder="Optional" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DDTHH:mm:ss" />
      </el-form-item>
      <el-form-item label="End Time">
        <el-date-picker v-model="form.endTime" type="datetime" placeholder="Optional" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DDTHH:mm:ss" />
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
```

- [ ] **Step 3: Commit**

```bash
git add auth_web_front/src/api/admin.ts auth_web_front/src/views/admin/AnnouncementsView.vue
git commit -m "feat: add admin announcements management page"
```

---

## Task 9: Wire Admin Routes + Sidebar

**Files:**
- Modify: `auth_web_front/src/router/index.ts`
- Modify: `auth_web_front/src/layouts/AdminLayout.vue`

- [ ] **Step 1: Add route in router/index.ts**

Add lazy import at the top of the file:

```ts
const AnnouncementsView = () => import("@/views/admin/AnnouncementsView.vue");
```

Add child entry in the `/admin` children array:

```ts
{ path: "announcements", component: AnnouncementsView }
```

- [ ] **Step 2: Add sidebar entry in AdminLayout.vue**

Import `Bell` icon from `@element-plus/icons-vue` (add to the existing destructured import).

Add menu item in the sidebar `<el-menu>` (after Vehicle Content):

```vue
<el-menu-item index="/admin/announcements">
  <el-icon><Bell /></el-icon>
  <span>Announcements</span>
</el-menu-item>
```

- [ ] **Step 3: Commit**

```bash
git add auth_web_front/src/router/index.ts auth_web_front/src/layouts/AdminLayout.vue
git commit -m "feat: add announcements route and sidebar entry to admin panel"
```

---

## Task 10: Manual Verification

- [ ] **Step 1: Verify backend compiles and starts**

```bash
cd backend && ./mvnw spring-boot:run
# Or: mvn spring-boot:run
```

Check that the `announcements` table is created (DDL_AUTO=update or manual SQL).

- [ ] **Step 2: Test backend API with curl**

```bash
# Create an announcement
curl -X POST http://localhost:8080/api/v1/admin/announcements \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin_token>" \
  -d '{"title":"Weekend Promo","content":"20% off all rides this weekend!","type":"PROMO","enabled":true}'

# List active announcements (no auth needed)
curl http://localhost:8080/api/v1/announcements/active
```

- [ ] **Step 3: Verify admin frontend builds**

```bash
cd auth_web_front && npm run build
```

- [ ] **Step 4: Verify mobile app compiles in HBuilderX**

Open project in HBuilderX, run to device/emulator, verify:
- Home page shows tips with colored borders and icons
- Tips rotate in the swiper
- Login as user with no card → "Bind a payment card" tip appears
- Logout → only "Log in" tip shows
- Create announcement via admin → appears as promo tip on mobile

- [ ] **Step 5: Push all changes**

```bash
git push origin master_sprint3
```
