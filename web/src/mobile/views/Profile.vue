<template>
  <div class="m-profile">
    <van-nav-bar title="个人信息" left-arrow @click-left="$router.back()" :border="false" style="background: transparent;" title-color="#D5A958" />

    <div class="form-wrap">
      <van-form @submit="onSubmit">
        <van-cell-group inset class="form-group">
          <van-field v-model="formData.nickname" label="昵称" placeholder="请输入昵称" />
          <van-field v-model="formData.tel" label="手机号" placeholder="请输入手机号" type="tel" />
          <van-field v-model="formData.email" label="邮箱" placeholder="请输入邮箱" type="email" />
        </van-cell-group>

        <div class="submit-wrap">
          <van-button round block color="#D5A958" style="color: #12100E; font-weight: bold; height: 44px;" native-type="submit" :loading="loading">
            保存修改
          </van-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showSuccessToast, showFailToast } from 'vant'
import http from '@/utils/http.js'

const loading = ref(false)
const formData = ref({
  id: null,
  nickname: '',
  tel: '',
  email: ''
})

onMounted(() => {
  const user = JSON.parse(localStorage.getItem('currentUser') || '{}')
  formData.value = { ...user }
})

const onSubmit = () => {
  loading.value = true
  // 这里假设后端修改个人信息的接口是 /common/updateUser 或 /user/update
  // 请去 PC 端的 EditCurrentUser.vue 看一眼具体的 URL，这里以 /user/update 为例
  http.post('/common/updateCurrentUser', formData.value).then(res => {
    if (res.code === 200) {
      showSuccessToast('保存成功')
      localStorage.setItem('currentUser', JSON.stringify(formData.value))
      setTimeout(() => window.location.reload(), 800) // 刷新页面更新显示
    } else {
      showFailToast(res.msg || '保存失败')
    }
  }).catch(() => {}).finally(() => {
    loading.value = false
  })
}
</script>

<style scoped>
.m-profile { min-height: 100dvh; background: var(--m-bg); }
.form-wrap { padding-top: 20px; }
.form-group { --van-cell-background: var(--m-surface); --van-cell-text-color: var(--m-text); --van-field-label-color: var(--m-muted); }
:deep(.van-field__control) { color: var(--m-text); }
.submit-wrap { margin: 30px 16px; }
</style>