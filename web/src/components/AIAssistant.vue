<template>
  <div class="ai-assistant">
    <!-- 悬浮按钮 -->
    <div class="ai-btn" @click="toggleChat">
      <img src="@/assets/agent-avatar.jpeg" alt="AI管家" class="avatar-img" />
    </div>

    <!-- 聊天窗口 -->
    <div class="chat-window" v-if="isOpen">
      <div class="chat-header">
        <div class="header-info">
          <img src="@/assets/agent-avatar.jpeg" class="header-avatar" />
          <span>半闲庭 AI 管家</span>
        </div>
        <el-icon @click="toggleChat" style="cursor: pointer;"><Close /></el-icon>
      </div>
      <div class="chat-content" ref="chatContentRef">
        <div v-for="(msg, index) in messages" :key="index" :class="['msg', msg.role]">
          <span v-html="formatMessage(msg.content)"></span>
        </div>
        <div v-if="loading" class="msg assistant">思考中...</div>
      </div>
      <!-- 快捷指令区域 -->
      <div class="quick-actions" >
        <span v-if="roleType === 'USER'" class="action-btn" @click="handleQuickAsk('帮我查一下我的最新订单状态')">查我的订单</span>
        <span v-if="roleType === 'USER'" class="action-btn" @click="handleQuickAsk('帮我查一下我最近的订位信息')">查我的订座</span>
        <span v-if="roleType === 'USER'" class="action-btn" @click="handleQuickAsk('推荐几个招牌菜')">推荐招牌菜</span>
        <span v-if="roleType === 'ADMIN'" class="action-btn" @click="handleQuickAsk('今天生意怎么样？')">今日经营概况</span>
        <span v-if="roleType === 'ADMIN'" class="action-btn" @click="handleQuickAsk('帮我对比一下今天和昨天的营业额')">昨日营业对比</span>
        <span v-if="roleType === 'ADMIN'" class="action-btn" @click="handleQuickAsk('最近哪些菜品卖得最好？')">热销菜品</span>
      </div>
      <div class="chat-input">
        <el-input
            v-model="inputText"
            @keyup.enter="sendMessage"
            :placeholder="roleType === 'ADMIN' ? '请输入您的经营问题...' : '想吃点什么？'"
        />
        <el-button type="primary" @click="sendMessage" :loading="loading">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import request from '@/utils/http.js'

const isOpen = ref(false)
const inputText = ref('')
const loading = ref(false)
const messages = ref([
  { role: 'assistant', content: '您好，我是半闲庭的AI管家，今天想吃点什么？' }
])
const chatContentRef = ref(null)

// 自动判断是前台还是后台
const roleType = window.location.pathname.startsWith('/admin') ? 'ADMIN' : 'USER'

const toggleChat = () => {
  isOpen.value = !isOpen.value
  scrollToBottom()
}

const scrollToBottom = async () => {
  await nextTick()
  if (chatContentRef.value) {
    chatContentRef.value.scrollTop = chatContentRef.value.scrollHeight
  }
}

// 处理 AI 返回的 Markdown 格式（加粗和换行）
const formatMessage = (text) => {
  if (!text) return ''
  let formatted = text
      // 1. 将 **文本** 替换为加粗标签
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      // 2. 将换行符 \n 替换为 <br>
      .replace(/\n/g, '<br/>')
  return formatted
}

const sendMessage = () => {
  if (!inputText.value.trim() || loading.value) return
  const userText = inputText.value
  messages.value.push({ role: 'user', content: userText })
  inputText.value = ''
  loading.value = true
  scrollToBottom()
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')

  request.post('/ai/chat', { message: userText, role: roleType,userId: currentUser.id }).then(res => {
    if (res.code === 200) {
      messages.value.push({ role: 'assistant', content: res.data })
    } else {
      messages.value.push({ role: 'assistant', content: '哎呀，网络开小差了，请稍后再试。' })
    }
  }).finally(() => {
    loading.value = false
    scrollToBottom()
  })
}
//添加快捷提问方法
const handleQuickAsk = (text) => {
  inputText.value = text
  sendMessage() // 直接触发发送
}
</script>

<style scoped>
.ai-btn {
  position: fixed;
  bottom: 40px;
  right: 40px;
  width: 60px;
  height: 60px;
  background: #fff;
  overflow: hidden;
  color: #d4af37;
  font-size: 30px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(0, 191, 255, 0.4);
  border: 2px solid #d4af37;
  transition: transform 0.3s;
  z-index: 9999;
}
.ai-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(0, 191, 255, 0.6);
}
.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 保证图片不变形 */
}
/* 聊天窗口头部的新布局 */
.header-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
/* 聊天窗头部的小头像 */
.header-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  border: 1px solid #d4af37;
  object-fit: cover;
}
.chat-window {
  position: fixed;
  bottom: 110px;
  right: 40px;
  width: 350px;
  height: 500px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
  display: flex;
  flex-direction: column;
  z-index: 9999;
  overflow: hidden;
}
.chat-header {
  background: #1a1a1a;
  color: #d4af37;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  font-weight: bold;
}
.chat-content {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  background: #f9f9f9;
}
.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 10px 15px;
  background: #fff;
  border-top: 1px solid #eee; /* 跟聊天区隔开 */
  overflow-x: auto;
  white-space: nowrap;
}
.quick-actions::-webkit-scrollbar {
  display: none;
}
.action-btn {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 12px;
  background: #f0f2f5;
  color: #555;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;
}
.action-btn:hover {
  background: #00BFFF;
  color: #fff;
}
.msg {
  max-width: 80%;
  padding: 10px 14px;
  border-radius: 8px;
  margin-bottom: 15px;
  font-size: 14px;
  line-height: 1.5;
}
.msg.user {
  background: #d4af37;
  color: #fff;
  margin-left: auto;
  border-bottom-right-radius: 0;
  white-space: normal;
  word-break: break-all;
}
.msg.assistant {
  background: #fff;
  color: #333;
  border: 1px solid #eee;
  margin-right: auto;
  border-bottom-left-radius: 0;
  line-height: 1.8;
}
.chat-input {
  padding: 10px;
  display: flex;
  gap: 10px;
  border-top: 1px solid #eee;
}
.msg.assistant :deep(strong) {
  color: #d4af37; /* 金色高亮菜名 */
  font-weight: bold;
}
</style>