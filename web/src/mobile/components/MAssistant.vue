<template>
  <div class="ai-wrapper">
    <!-- 悬浮球 -->
    <div class="ai-btn" @click="showChat = true">
      AI 管家
    </div>

    <!-- 全屏聊天面板 -->
    <van-popup
        v-model:show="showChat"
        position="bottom"
        :style="{ height: '80%' }"
        round
    >
      <div class="chat-container">
        <div class="chat-header">
          <span>半闲庭 · AI 点餐管家</span>
          <van-icon name="cross" @click="showChat = false" />
        </div>
        <div class="chat-content">
          <!-- 这里后续可以接原有的聊天记录和渲染逻辑 -->
          <p style="color:#888; text-align:center; margin-top:20px;">你好，我是您的点餐管家，请问想吃点什么？</p>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const showChat = ref(false)

// 后续实现对话逻辑：调用 POST /ai/chat
// 传参示例：{ message: '推荐个菜', role: 'USER', userId: 当前用户ID }
</script>

<style scoped>
.ai-btn {
  position: fixed;
  right: 16px;
  bottom: calc(50px + env(safe-area-inset-bottom) + 20px); /* 避开底部 Tabbar */
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: var(--m-gold);
  color: #12100E;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: bold;
  box-shadow: 0 4px 12px rgba(213, 169, 88, 0.4);
  z-index: 99;
}
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--m-bg);
  color: var(--m-text);
}
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  color: var(--m-gold);
  font-size: 16px;
  font-weight: bold;
}
.chat-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}
</style>