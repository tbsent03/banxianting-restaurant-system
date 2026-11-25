<!--suppress CssInvalidPropertyValue -->
<template>
  <div class="m-layout">
    <!-- 顶部导航 (仅在需要返回的页面显示，这里简单处理为统一隐藏或根据需求开启) -->
    <!-- <van-nav-bar title="半闲庭" left-arrow @click-left="$router.back()" /> -->

    <main class="m-main">
      <router-view />
    </main>

    <MTabbar />
    <MAssistant />
  </div>
</template>

<script setup>
import MTabbar from '@/mobile/components/MTabbar.vue'
import MAssistant from '@/mobile/components/MAssistant.vue'
</script>

<style scoped>
.m-layout {
  min-height: 100dvh;
  display: flex;
  flex-direction: column;
  background: var(--m-bg);
}
.m-main {
  flex: 1;
  /* 底部留出 Tabbar 高度 50px + 安全区 + 额外留白 */
  padding-bottom: calc(50px + env(safe-area-inset-bottom) + 16px);
}
</style>