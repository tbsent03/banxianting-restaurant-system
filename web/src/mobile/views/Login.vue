<template>
  <div class="m-login">
    <div class="header">
      <h1 class="title">半闲庭</h1>
      <p class="subtitle">品味非凡，尽在半闲</p>
    </div>

    <!-- 移除了 van-form，改为普通 div -->
    <div class="form-wrap">
      <van-cell-group inset>
        <van-field
            v-model="formData.username"
            name="username"
            label="账号"
            placeholder="请输入账号"
        />
        <van-field
            v-model="formData.password"
            :type="showPassword ? 'text' : 'password'"
            name="password"
            label="密码"
            placeholder="请输入密码"
            :right-icon="showPassword ? 'eye-o' : 'closed-eye'"
            @click-right-icon="showPassword = !showPassword"
        />
      </van-cell-group>

      <div class="submit-wrap">
        <!-- 改为 @click 直接触发 -->
        <van-button round block type="primary" :loading="loading" class="login-btn" @click="handleLogin">
          登 录
        </van-button>

        <div class="links">
          <span @click="$router.push('/register')">没有账号？去注册</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showSuccessToast, showFailToast } from 'vant'
import http from '@/utils/http.js'

const route = useRoute()
const router = useRouter()

const formData = ref({
  username: '',
  password: '',
  type: ''
})
const loading = ref(false)
const showPassword = ref(false)
const handleLogin = () => {
  if (!formData.value.username) {
    showFailToast('请输入账号')
    return
  }
  if (!formData.value.password) {
    showFailToast('请输入密码')
    return
  }
//  新增：根据用户名自动猜测类型（仅限方便测试）
  formData.value.type = formData.value.username === 'admin' ? 'ADMIN' : 'USER'

  loading.value = true
  http.post('/common/login', formData.value).then(res => {
    if (!res) {
      loading.value = false
      return
    }

    // 1. 存储 token
    localStorage.setItem('token', res.data)
    showSuccessToast('登录成功')

    // 2. 获取当前用户信息
    http.get('/common/currentUser').then(res1 => {
      const currentUser = res1.data
      localStorage.setItem('currentUser', JSON.stringify(currentUser))

      // 3. 回跳逻辑
      const redirectUrl = route.query.redirect
      if (redirectUrl) {
        router.replace(redirectUrl)
      } else {
        router.replace('/index')
      }
    }).catch((err) => {
      console.error('获取用户信息失败', err)
      loading.value = false
    }).finally(() => {
      loading.value = false
    })
  }).catch((err) => {
    // 重点：如果 http.js 报了 ElMessage 的错，这里会捕获到
    console.error('登录失败', err)
    showFailToast('登录失败，请检查账号密码')
  }).finally(() => {
    loading.value = false
  })
}
</script>

<style scoped>
.m-login {
  padding-top: 80px;
  min-height: 100dvh;
  background: var(--m-bg);
}
.header {
  text-align: center;
  margin-bottom: 40px;
}
.title {
  font-size: 32px;
  color: var(--m-gold);
  font-family: 'Songti SC', 'SimSun', serif;
  letter-spacing: 4px;
  margin-bottom: 10px;
}
.subtitle {
  font-size: 14px;
  color: var(--m-muted);
  letter-spacing: 2px;
}
.form-wrap {
  margin: 0 16px;
}
.submit-wrap {
  margin-top: 30px;
}
.login-btn {
  height: 44px;
  background: var(--m-gold);
  border-color: var(--m-gold);
  color: #12100E;
  font-weight: bold;
}
.links {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: var(--m-muted);
}
</style>