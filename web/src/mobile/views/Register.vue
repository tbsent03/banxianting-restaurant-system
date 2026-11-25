<template>
  <div class="m-register">
    <van-nav-bar title="注册账号" left-arrow @click-left="$router.back()" :border="false" style="background: transparent;" title-color="#D5A958" />

    <div class="header">
      <h1 class="title">欢迎加入</h1>
      <p class="subtitle">半闲庭 · 尊享美味</p>
    </div>

    <van-form @submit="onSubmit">
      <van-cell-group inset class="form-group">
        <van-field
            v-model="formData.username"
            name="username"
            label="账号"
            placeholder="请输入账号"
            :rules="[{ required: true, message: '请填写账号' }]"
        />
        <van-field
            v-model="formData.nickname"
            name="nickname"
            label="昵称"
            placeholder="请输入昵称（选填）"
        />
        <van-field
            v-model="formData.tel"
            name="tel"
            type="tel"
            label="手机号"
            placeholder="请输入手机号"
            :rules="[{ required: true, message: '请填写手机号' }]"
        />
        <van-field
            v-model="formData.password"
            :type="showPassword ? 'text' : 'password'"
            name="password"
            label="密码"
            placeholder="请设置密码"
            :right-icon="showPassword ? 'eye-o' : 'closed-eye'"
            @click-right-icon="showPassword = !showPassword"
            :rules="[{ required: true, message: '请填写密码' }]"
        />
        <van-field
            v-model="confirmPassword"
            :type="showConfirmPassword ? 'text' : 'password'"
            name="confirmPassword"
            label="确认密码"
            placeholder="请再次输入密码"
            :right-icon="showConfirmPassword ? 'eye-o' : 'closed-eye'"
            @click-right-icon="showConfirmPassword = !showConfirmPassword"
            :rules="[
            { required: true, message: '请再次输入密码' },
            { validator: validateConfirmPassword, message: '两次输入的密码不一致' }
          ]"
        />
      </van-cell-group>

      <div class="submit-wrap">
        <van-button round block color="#D5A958" style="color: #12100E; font-weight: bold; height: 44px;" native-type="submit" :loading="loading">
          注 册
        </van-button>
        <div class="links" @click="$router.replace('/login')">
          已有账号？返回登录
        </div>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showFailToast } from 'vant'
import http from '@/utils/http.js'

const router = useRouter()
const loading = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const confirmPassword = ref('')

const formData = ref({
  username: '',
  password: '',
  nickname: '',
  tel: '',
  type: 'USER' // 移动端注册默认是普通用户
})
const validateConfirmPassword = (val) => {
  return val === formData.value.password
}

const onSubmit = () => {
  loading.value = true
  localStorage.removeItem('token')
  localStorage.removeItem('currentUser')

  http.put('/common/register', formData.value).then(res => {
    if (res.code === 200) {
      showSuccessToast('注册成功，请登录')
      setTimeout(() => {
        router.replace('/login')
      }, 800)
    } else {
      showFailToast(res.msg || '注册失败')
    }
  }).catch((err) => {
    // 捕获 401 等异常，防止页面无响应
    console.error('注册异常:', err)
    showFailToast('注册服务异常，请稍后再试')
    loading.value = false
  }).finally(() => {
    loading.value = false
  })
}
</script>

<style scoped>
.m-register { min-height: 100dvh; background: var(--m-bg); }
.header { text-align: center; padding: 40px 0 30px; }
.title { font-size: 28px; color: var(--m-gold); font-family: 'Songti SC', serif; letter-spacing: 2px; margin: 0 0 10px 0; }
.subtitle { font-size: 13px; color: var(--m-muted); letter-spacing: 2px; margin: 0; }
.form-group {
  --van-cell-background: var(--m-surface);
  --van-cell-text-color: var(--m-text);
  --van-field-label-color: var(--m-muted);
}
:deep(.van-field__control) { color: var(--m-text); }
:deep(.van-field__right-icon) { color: var(--m-muted); }
.submit-wrap { margin: 30px 16px; }
.links { text-align: center; margin-top: 20px; font-size: 14px; color: var(--m-muted); }
</style>