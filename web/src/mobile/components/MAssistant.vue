<template>
  <div class="ai-wrapper">
    <!-- 悬浮球：换成图片头像，保留拖拽 -->
    <div
        class="ai-btn"
        :style="{
        left: `${position.left}px`,
        top: `${position.top}px`,
        transition: isDragging ? 'none' : 'all 0.3s'
      }"
        @click="handleClick"
        @touchstart="onTouchStart"
        @touchmove.prevent="onTouchMove"
    >
      <img src="@/assets/agent-avatar.jpeg" alt="AI管家" class="avatar-img" />
    </div>

    <!-- 聊天面板 -->
    <van-popup
        v-model:show="showChat"
        position="bottom"
        :style="{ height: '80%' }"
        round
        teleport="body"
        class="ai-popup"
    >
      <div class="chat-container">
        <!-- 头部 -->
        <div class="chat-header">
          <div class="header-info">
            <img src="@/assets/agent-avatar.jpeg" class="header-avatar" />
            <span>半闲庭 · AI 点餐管家</span>
          </div>
          <van-icon name="cross" @click="showChat = false" />
        </div>

        <!-- 聊天内容区 -->
        <div class="chat-content" ref="chatContentRef">
          <div v-for="(msg, index) in messages" :key="index" :class="['msg', msg.role]">
            <span v-html="formatMessage(msg.content)"></span>
          </div>
          <div v-if="loading" class="msg assistant">思考中...</div>
        </div>

        <!-- 快捷指令区（横向滚动） -->
        <div class="quick-actions">
          <span class="action-btn" @click="handleQuickAsk('帮我查一下我的最新订单状态')">查我的订单</span>
          <span class="action-btn" @click="handleQuickAsk('帮我查一下我最近的订位信息')">查我的订座</span>
          <span class="action-btn" @click="handleQuickAsk('给我推荐几个招牌菜')">推荐招牌菜</span>
        </div>

        <!-- 输入区 -->
        <div class="chat-input">
          <van-field
              v-model="inputText"
              placeholder="想吃点什么？"
              @keyup.enter="sendMessage"
              class="input-field"
          />
          <van-button
              color="#D5A958"
              style="color: #12100E; font-weight: bold;"
              round
              size="small"
              @click="sendMessage"
              :loading="loading"
          >
            发送
          </van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import http from '@/utils/http.js'

const showChat = ref(false)

// ================= 拖拽逻辑 =================
const position = ref({ left: 0, top: 0 })
const isDragging = ref(false)
let startX = 0
let startY = 0
let startLeft = 0
let startTop = 0
let hasMoved = false

const initPosition = () => {
  const btnSize = 56
  position.value = {
    left: window.innerWidth - btnSize - 20,
    top: window.innerHeight - btnSize - 100
  }
}

onMounted(() => {
  initPosition()
})

const handleClick = () => {
  if (!hasMoved) {
    showChat.value = true
    scrollToBottom()
  }
}

const onTouchStart = (e) => {
  isDragging.value = true
  hasMoved = false
  const clientX = e.touches[0].clientX
  const clientY = e.touches[0].clientY
  startX = clientX
  startY = clientY
  startLeft = position.value.left
  startTop = position.value.top
}

const onTouchMove = (e) => {
  const clientX = e.touches[0].clientX
  const clientY = e.touches[0].clientY
  const dx = clientX - startX
  const dy = clientY - startY

  if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
    hasMoved = true
  }

  let newLeft = startLeft + dx
  let newTop = startTop + dy

  const btnSize = 56
  const minLeft = 10
  const maxLeft = window.innerWidth - btnSize - 10
  const minTop = 10
  const maxTop = window.innerHeight - btnSize - 70

  newLeft = Math.max(minLeft, Math.min(newLeft, maxLeft))
  newTop = Math.max(minTop, Math.min(newTop, maxTop))

  position.value = { left: newLeft, top: newTop }
}

// ================= AI 聊天逻辑 =================
const inputText = ref('')
const loading = ref(false)
const messages = ref([
  { role: 'assistant', content: '您好，我是半闲庭的AI管家，今天想吃点什么？' }
])
const chatContentRef = ref(null)

const scrollToBottom = async () => {
  await nextTick()
  if (chatContentRef.value) {
    chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight
  }
}

const formatMessage = (text) => {
  if (!text) return ''
  return text
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\n/g, '<br/>')
}

const sendMessage = () => {
  if (!inputText.value.trim() || loading.value) return
  const userText = inputText.value

  messages.value.push({ role: 'user', content: userText })
  inputText.value = ''
  loading.value = true
  scrollToBottom()

  const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')

  http.post('/ai/chat', {
    message: userText,
    role: 'USER',
    userId: currentUser.id
  }).then(res => {
    if (res.code === 200) {
      messages.value.push({ role: 'assistant', content: res.data })
    } else {
      messages.value.push({ role: 'assistant', content: '哎呀，网络开小差了，请稍后再试。' })
    }
  }).catch(() => {
    messages.value.push({ role: 'assistant', content: '哎呀，网络开小差了，请稍后再试。' })
  }).finally(() => {
    loading.value = false
    scrollToBottom()
  })
}

const handleQuickAsk = (text) => {
  inputText.value = text
  sendMessage()
}
</script>

<style scoped>
/* ================= 悬浮球 ================= */
.ai-btn {
  position: fixed;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #fff;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid var(--m-gold);
  box-shadow: 0 4px 12px rgba(213, 169, 88, 0.4);
  z-index: 99;
  user-select: none;
  touch-action: none;
}
.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* ================= 聊天面板 ================= */
/* 穿透 Vant 弹窗背景改成深色 */
:deep(.ai-popup) {
  background-color: var(--m-bg) !important;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--m-bg);
  color: var(--m-text);
}

/* 头部 */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #1A1816;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  color: var(--m-gold);
  font-size: 16px;
  font-weight: bold;
}
.header-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
.header-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  border: 1px solid var(--m-gold);
  object-fit: cover;
}

/* 聊天区 */
.chat-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: var(--m-bg);
}
.msg {
  max-width: 85%;
  padding: 12px 16px;
  border-radius: 12px;
  margin-bottom: 16px;
  font-size: 14px;
  line-height: 1.6;
}
.msg.user {
  background: var(--m-gold);
  color: #12100E;
  margin-left: auto;
  border-bottom-right-radius: 2px;
  word-break: break-all;
}
.msg.assistant {
  background: var(--m-surface);
  color: var(--m-text);
  border: 1px solid rgba(255,255,255,0.05);
  margin-right: auto;
  border-bottom-left-radius: 2px;
}
/* 高亮 AI 回复里的加粗字体 */
.msg.assistant :deep(strong) {
  color: var(--m-gold);
}

/* 快捷指令 */
.quick-actions {
  display: flex;
  gap: 8px;
  padding: 10px 16px;
  background: #1A1816;
  border-top: 1px solid rgba(255,255,255,0.05);
  overflow-x: auto;
  white-space: nowrap;
}
.quick-actions::-webkit-scrollbar { display: none; }
.action-btn {
  font-size: 12px;
  padding: 6px 14px;
  border-radius: 16px;
  background: rgba(213, 169, 88, 0.1);
  color: var(--m-gold);
  border: 1px solid rgba(213, 169, 88, 0.3);
  cursor: pointer;
  flex-shrink: 0;
  transition: all 0.2s;
}
.action-btn:active {
  background: rgba(213, 169, 88, 0.2);
}

/* 输入区 */
.chat-input {
  padding: 10px 16px calc(10px + env(safe-area-inset-bottom));
  background: #1A1816;
  display: flex;
  align-items: center;
  gap: 12px;
  border-top: 1px solid rgba(255,255,255,0.05);
}
.input-field {
  flex: 1;
  background: var(--m-surface);
  border-radius: 20px;
  padding: 4px 12px !important;
}
:deep(.input-field .van-field__control) {
  color: var(--m-text);
}
</style>