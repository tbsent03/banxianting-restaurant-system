<template>
  <div class="not-found-container">
    <div class="content-box">
      <!-- 左边：空盘子视觉 -->
      <div class="plate-visual">
        <div class="plate-inner">
          <span class="chopsticks">🥢</span>
        </div>
      </div>

      <!-- 右边：文字与操作 -->
      <div class="text-content">
        <h1 class="error-code">404</h1>
        <h2 class="error-title">客官，这道菜（页面）还没研发出来</h2>
        <p class="error-desc">您访问的页面可能已被撤下，或者链接输入有误。</p>

        <el-button type="primary" class="back-btn" @click="goHome">
          返回半闲庭首页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  // 如果是管理员进来的，退回后台；否则退回前台
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}')
  if (currentUser.type === 'ADMIN') {
    router.push('/admin')
  } else {
    router.push('/')
  }
}
</script>

<style scoped>
.not-found-container {
  height: 100vh;
  width: 100vw;
  background: #121212; /* 极深墨色 */
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  color: #fff;
}

.content-box {
  display: flex;
  align-items: center;
  gap: 60px;
  max-width: 900px;
  padding: 40px;
}

/* 模拟一个金色的空盘子 */
.plate-visual {
  width: 240px;
  height: 240px;
  border-radius: 50%;
  border: 2px solid rgba(212, 175, 55, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  animation: float 6s ease-in-out infinite;
}

.plate-inner {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  border: 4px solid #d4af37; /* 金色边缘 */
  background: radial-gradient(circle, #2a2a2a 0%, #121212 100%);
  box-shadow: inset 0 0 20px rgba(0, 0, 0, 0.8), 0 0 30px rgba(212, 175, 55, 0.1);
  display: flex;
  justify-content: center;
  align-items: center;
}

.chopsticks {
  font-size: 50px;
  filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.5));
  transform: rotate(45deg);
}

/* 文字区域 */
.text-content {
  flex: 1;
}

.error-code {
  font-size: 100px;
  font-weight: 800;
  color: #d4af37; /* 金色 */
  margin: 0;
  line-height: 1;
  font-family: "Georgia", serif;
  font-style: italic;
  text-shadow: 0 4px 15px rgba(212, 175, 55, 0.2);
}

.error-title {
  font-size: 24px;
  font-weight: 600;
  margin: 15px 0;
  letter-spacing: 2px;
}

.error-desc {
  font-size: 14px;
  color: #999;
  line-height: 1.8;
  margin-bottom: 30px;
}

/* 重写按钮样式，匹配墨金风 */
.back-btn {
  height: 46px;
  padding: 0 30px;
  font-size: 16px;
  letter-spacing: 1px;
  border-radius: 8px;
  border: 1px solid #d4af37;
  background: transparent;
  color: #d4af37;
  transition: all 0.3s;
}

.back-btn:hover {
  background: #d4af37;
  color: #121212;
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(212, 175, 55, 0.3);
}

/* 盘子悬浮动画 */
@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-15px); }
  100% { transform: translateY(0px); }
}

/* 响应式 */
@media (max-width: 768px) {
  .content-box {
    flex-direction: column;
    text-align: center;
    gap: 30px;
  }
  .error-code { font-size: 80px; }
}
</style>