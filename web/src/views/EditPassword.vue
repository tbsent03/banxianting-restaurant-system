<template>
  <div style="width: 100%; max-width: 600px; margin: 40px auto; padding: 0 20px; box-sizing: border-box;">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
          <!-- 👇 顺手加上的关闭按钮 👇 -->
          <el-icon class="close-icon" @click="handleClose"><Close /></el-icon>
        </div>
      </template>
      <el-form :model="formData" label-width="100px">
        <el-form-item prop="oldPassword" label="旧密码" >
          <el-input type="password" v-model="formData.oldPassword" auto-complete="off" placeholder="请输入旧密码" show-password></el-input>
        </el-form-item>
        <el-form-item prop="newPassword" label="新密码">
          <el-input type="password" v-model="formData.newPassword" auto-complete="off" placeholder="请输入新密码" show-password></el-input>
        </el-form-item>
        <el-form-item prop="confirmPassword" label="确认新密码">
          <el-input type="password" v-model="formData.confirmPassword" auto-complete="off" placeholder="请再次输入新密码" show-password></el-input>
        </el-form-item>
        <el-form-item style="width:100%;">
          <el-button type="primary" style="width:100px;" @click="handleSubmit">确认修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router"; // 👈 推荐用 useRouter 替代直接 import
import { ElMessage } from "element-plus";
import { Close } from '@element-plus/icons-vue';
import http from "@/utils/http.js";

const router = useRouter(); // 👈 实例化路由

const formData = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

function handleClose() {
  if (window.history.length > 1) {
    router.back();
  } else {
    router.push('/index');
  }
}

function handleSubmit() {
  // 1. 基础非空校验
  if(!formData.value.oldPassword || !formData.value.newPassword || !formData.value.confirmPassword) {
    return ElMessage.warning("请填写完整密码")
  }

  // 👇 2. 核心校验：两次新密码是否一致 👇
  if (formData.value.newPassword !== formData.value.confirmPassword) {
    return ElMessage.error("两次输入的新密码不一致，请重新输入！")
  }

  // 3. 构造发给后端的参数（不把 confirmPassword 发给后端）
  const payload = {
    oldPassword: formData.value.oldPassword,
    newPassword: formData.value.newPassword
  }

  http.post('/common/updatePassword', payload).then(res => {
    if (!res) return;

    localStorage.clear()
    ElMessage.success('修改成功，请重新登录');
    router.push({path: "/login"})
  })
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
.close-icon {
  font-size: 20px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s;
}
.close-icon:hover {
  color: #C6A15B;
}
</style>