<template>
  <el-container class="admin-wrapper" style="height: 100%;">
    <el-aside width="200px" class="my-aside">
      <h3 class="title">半闲庭 · 尊享后台</h3>
      <el-menu
          style="width: 100%; overflow: hidden;"
          active-text-color="#409EFF"
          background-color="#000000"
          text-color="white"
          :default-active="useRoute().path"
          @select="handleMenuSelect"
          router>
        <!-- 经营分组 -->
        <div class="menu-group-title">经营</div>
        <el-menu-item index="/admin/home">
          <el-icon><Odometer /></el-icon>
          <span>经营看板</span>
        </el-menu-item>
        <el-menu-item index="/admin/order">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/reservation">
          <el-icon><Calendar /></el-icon>
          <span>在线订位</span>
        </el-menu-item>

        <!-- 菜品分组 -->
        <div class="menu-group-title">菜品</div>
        <el-menu-item index="/admin/dish">
          <el-icon><Food /></el-icon>
          <span>菜品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/category">
          <el-icon><Grid /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/table">
          <el-icon><Monitor /></el-icon>
          <span>桌位与桌码</span>
        </el-menu-item>

        <!-- 内容分组 -->
        <div class="menu-group-title">内容</div>
        <el-menu-item index="/admin/review">
          <el-icon><ChatDotRound /></el-icon>
          <span>评价管理</span>
        </el-menu-item>

        <!-- 系统分组 -->
        <div class="menu-group-title">系统</div>
        <el-menu-item index="/admin/user">
          <el-icon><User /></el-icon>
          <span>普通用户</span>
        </el-menu-item>
        <el-menu-item index="/admin/admin">
          <el-icon><UserFilled /></el-icon>
          <span>管理员管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>

      <el-header height="60px">
        <el-row :gutter="5">
          <el-col :span="6" style="margin-top: 20px;">
            <el-space>
<!--              <router-link to="/admin">-->
<!--                <HomeOutlined/>-->
<!--              </router-link>-->
            </el-space>
          </el-col>
          <el-col :span="6">
          </el-col>
          <el-col :span="9">
          </el-col>
          <el-col :span="3">
            <div style="text-align: right;">
              <el-space style="margin-top: 15px;">
                <el-dropdown v-if="isUserLogin">
                  <div>
                    <el-space>
                      <el-avatar style="width: 30px;height: 30px;border-radius: 50%"
                                 shape="square" :size="30" :src="currentUser.avatarUrl"></el-avatar>
                      <span style="font-size: 16px">  {{ currentUser.username }}</span></el-space>
                  </div>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item><span @click="editCurrentUser">个人信息</span></el-dropdown-item>
                      <el-dropdown-item><span @click="editPassword">修改密码</span></el-dropdown-item>
                      <el-dropdown-item divided><span @click="logout">退出登录</span></el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>

              </el-space>
            </div>
          </el-col>
        </el-row>
      </el-header>
      <el-main style="background-color: RGB(245,245,245)" class="my-main">
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
  <AIAssistant />
</template>

<script setup>
import tools from "@/utils/tools.js";
import {ref} from "vue";
import router from "@/router/index.js";
import {ElMessage} from "element-plus";
import {useRoute} from 'vue-router';
import {HomeOutlined} from "@ant-design/icons-vue";
import {
  HomeFilled, List, User, UserFilled, Grid, Food,
  Odometer, Calendar, Monitor, ChatDotRound
} from '@element-plus/icons-vue'
import "@/styles/admin.css";
import { Tickets } from '@element-plus/icons-vue'
import AIAssistant from '@/components/AIAssistant.vue'
const isUserLogin = ref(tools.isLogin())
const currentUser = ref(tools.getCurrentUser())
const activeIndex = ref(useRoute().path)

if (currentUser.value === null) {
  window.location.href = "/login"
}
if (currentUser.value && currentUser.value.type === 'USER') {
  router.push({path: "/"})
}

function handleMenuSelect(key, keyPath) {
  activeIndex.value = key
}


function logout() {
  ElMessage({
    message: '退出登录成功，正在跳转',
    type: 'success'
  });
  localStorage.clear()
  router.push({path: "/login"})
}

function editCurrentUser() {
  router.push({path: "/admin/editCurrentUser"})
}

function editPassword() {
  router.push({path: "/admin/editPassword"})
}


const isCollapse = ref(true)

</script>

<style scoped>
.title {
  color: white;
  width: 100%;
  text-align: center;
  margin: 15px 5px;
}
.my-main::-webkit-scrollbar {
  display: none; /* 隐藏滚动条 */
}
.my-aside {
  background-color: #000000;
  overflow-x: hidden;
}

.el-menu {
  border-right: 0px;
}
/*分组小标题样式*/
.menu-group-title{
  padding: 20px 0 10px 24px;
  font-size: 12px;
  color: #D5A958;
  letter-spacing: 2px;
}
/*菜单项悬浮与选中样式*/
:deep(.el-menu-item){
  max-resolution: 4px 10px;
  border-radius: 6px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1) !important;
}
:deep(.el-menu-item:hover){
  color: #409EFF !important;
  transform: translateY(-3px);
  background-color: rgba(64, 158, 255, 0.05) !important;
}
</style>

