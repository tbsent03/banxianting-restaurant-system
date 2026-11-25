<template>
  <div class="m-me">
    <!-- 顶部背景图与头像卡片 -->
    <div class="header-bg"></div>
    <div class="user-card">
      <div class="avatar">
        <img :src="formatImg(userInfo.avatarUrl)" alt="avatar" v-if="userInfo.avatarUrl">
        <van-icon name="user-circle-o" size="60" color="#D5A958" v-else />
      </div>
      <div class="info">
        <h2>{{ userInfo.nickname || userInfo.username || '半闲庭贵宾' }}</h2>
        <p>{{ userInfo.tel || '未绑定手机号' }}</p>
      </div>
    </div>

    <!-- 菜单列表 -->
    <van-cell-group inset class="menu-group">
      <van-cell title="我的订单" is-link icon="orders-o" to="/orders" />
      <van-cell title="我的预订" is-link icon="calendar-o" to="/myReservations" />
      <van-cell title="个人信息" is-link icon="contact" to="/profile" />
    </van-cell-group>

    <!-- 其他功能（预留） -->
    <van-cell-group inset class="menu-group">
      <van-cell title="关于半闲庭" is-link icon="info-o" @click="showAbout" />
    </van-cell-group>

    <div class="logout-wrap">
      <!-- 修改为透明底+红色边框 -->
      <van-button block round plain class="btn-logout" @click="handleLogout">退出登录</van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showConfirmDialog, showToast } from 'vant'
import { formatImg } from '@/utils/img.js'

const router = useRouter()
const userInfo = ref({})

onMounted(() => {
  const userStr = localStorage.getItem('currentUser')
  if (userStr) {
    userInfo.value = JSON.parse(userStr)
  }
})

const showAbout = () => {
  showToast('半闲庭 · 现代中华料理')
}

const handleLogout = () => {
  showConfirmDialog({
    title: '退出登录',
    message: '确定要退出当前账号吗？',
  }).then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('currentUser')
    router.replace('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.m-me {
  padding-bottom: 30px;
  min-height: calc(100dvh - 50px);
  background: var(--m-bg);
  position: relative;
}

/* 顶部装饰背景 */
.header-bg {
  height: 140px;
  background: linear-gradient(135deg, #1A1816 0%, #2A2826 100%);
  border-bottom-left-radius: 24px;
  border-bottom-right-radius: 24px;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 0;
}

/* 用户信息卡片 */
.user-card {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 16px;
  background: rgba(26, 24, 22, 0.9);
  backdrop-filter: blur(10px);
  margin: 40px 16px 24px;
  padding: 24px 20px;
  border-radius: var(--m-radius);
  border: 1px solid rgba(213, 169, 88, 0.2);
  box-shadow: 0 8px 24px rgba(0,0,0,0.3);
}
.avatar img {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--m-gold);
}
.info h2 {
  font-size: 20px;
  color: var(--m-gold);
  margin: 0 0 6px 0;
  font-family: 'Songti SC', serif;
}
.info p {
  font-size: 13px;
  color: var(--m-muted);
  margin: 0;
}

/* 菜单列表 */
.menu-group {
  margin: 0 16px 16px;
  --van-cell-background: var(--m-surface);
  --van-cell-text-color: var(--m-text);
  --van-cell-border-color: rgba(255,255,255,0.05);
  border-radius: var(--m-radius);
  overflow: hidden;
}
:deep(.van-cell__left-icon) {
  color: var(--m-gold);
}
:deep(.van-cell__right-icon) {
  color: var(--m-muted);
}

:deep(.van-cell) {
  padding: 20px 24px;
}

:deep(.van-cell__title) {
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 1px;
}


:deep(.van-cell__left-icon) {
  color: var(--m-gold);
  font-size: 22px;
  margin-right: 12px;
}


:deep(.van-cell__right-icon) {
  color: var(--m-muted);
  font-size: 18px;
}
/* 退出按钮 */
.logout-wrap {
  padding: 20px 16px;
}
.btn-logout {
  color: #C0705C !important;
  border-color: #C0705C !important;
  background: transparent !important;
  font-weight: bold;
}
</style>