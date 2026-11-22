<template>
  <div style="width: 100%; max-width: 600px; margin: 40px auto; padding: 0 20px; box-sizing: border-box;">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人信息修改</span>
          <el-icon class="close-icon" @click="handleClose"><Close /></el-icon>
        </div>
      </template>
      <el-form :model="formData" label-width="100px">
        <el-form-item prop="img" label="头像">
          <MyUpLoad type="imageCard" :limit="1" :files="formatImg(formData.avatarUrl)"
                    @setFiles="formData.avatarUrl =$event"></MyUpLoad>
        </el-form-item>
        <el-form-item prop="username" label="用户名">
          <el-input type="text"
                    v-model="formData.username"
                    auto-complete="off"
                    placeholder="用户名"
                    :disabled="true"
          ></el-input>
        </el-form-item>
        <el-form-item prop="nickname" label="昵称">
          <el-input type="text"
                    v-model="formData.nickname"
                    auto-complete="off"
                    placeholder="用户名"
                    :disabled="true"
          ></el-input>
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <el-input
              v-model="formData.email"
              auto-complete="off"
              placeholder="邮箱"
          ></el-input>
        </el-form-item>
        <el-form-item prop="tel" label="电话">
          <el-input type="text"
                    v-model="formData.tel"
                    auto-complete="off"
                    placeholder="电话"
          ></el-input>
        </el-form-item>
        <el-form-item style="width:100%;">
          <el-button type="primary" style="width:100px;" @click="handleSubmit">修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
<script setup>
import {ref} from 'vue';
import utils from "@/utils/tools.js";
import MyUpLoad from "@/components/MyUpload.vue";
import {ElMessage} from 'element-plus';
import http from "@/utils/http.js";
import { useRouter } from 'vue-router'; //  引入路由
import { Close } from '@element-plus/icons-vue'; // 引入图标
const router = useRouter();
const formData = ref({});

load();

function load() {
  formData.value = utils.getCurrentUser();
}
//关闭弹窗逻辑
function handleClose(){
  //如果有历史记录就返回上一页，若没有则跳回首页
  if(window.history.length > 1){
    router.back();
  }
  else{
    router.push('/index');
  }
}

function handleSubmit() {
  http.post('/common/updateCurrentUser', formData.value).then(res => {
    http.get("/common/currentUser").then(res1 => {
      let currentUser = res1.data;
      localStorage.setItem("currentUser", JSON.stringify(currentUser));
      ElMessage({
        message: '修改成功',
        type: 'success'
      });
    })
  });
};
function formatImg(url) {
  if (!url) return '';
  return url.replace('localhost', window.location.hostname);
}
</script>
<style scoped>
.card-header {
  display: flex;
  justify-content: space-between; /* 两端对齐 */
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
  color: #C6A15B; /* 悬浮时变成你的品牌金色 */
}
</style>
