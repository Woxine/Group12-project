<template>
  <!-- 全局跳转链接和路由出口 / Global skip link and router outlet. -->
  <a class="skip-link" href="#main-content">Skip to main content</a>
  <router-view />
  <!-- 全局无障碍播报区域 / Global live region for accessibility announcements. -->
  <div id="global-live-region" class="sr-only" aria-live="polite" aria-atomic="true"></div>
</template>
