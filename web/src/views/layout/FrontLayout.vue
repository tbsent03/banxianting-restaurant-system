<template>
  <div class="app-wrapper">
    <!-- 顶部导航 -->
    <header class="navbar">
      <div class="nav-container">
        <!-- 左侧 Logo -->
        <div class="logo" @click="router.push('/index')">
          <h1 class="logo-cn">半闲庭</h1>
          <span class="logo-en">BANXIAN TING</span>
        </div>

        <!-- 中间菜单 -->
        <nav class="nav-links">
          <router-link to="/index" class="nav-item" active-class="active">首页</router-link>
          <router-link to="/menu" class="nav-item" active-class="active">菜单</router-link>
          <router-link to="/reservation" class="nav-item" active-class="active">在线订位</router-link>
          <router-link to="/about" class="nav-item" active-class="active">关于我们</router-link>
        </nav>

        <!-- 右侧用户 -->
        <div class="user-actions">
          <div class="table-badge" v-if="diningStore.currentTable">
            📍 {{ diningStore.currentTable.tableNo }} 桌 ({{ diningStore.currentTable.area }})
          </div>

          <span class="cart-text" @click="router.push('/cart')">购物车 ({{  cartStore.totalCount  }})</span>

          <el-dropdown v-if="isUserLogin" popper-class="custom-user-dropdown" trigger="hover">
            <!-- 注意：这里加了 class="user-info" 和 tabindex="-1" 去掉焦点框 -->
            <div class="user-info" tabindex="-1" style="outline: none;">
              <span class="username">{{ currentUser.username|| currentUser.nickname }}</span>
              <img :src="formatImg(currentUser.avatarUrl)" class="avatar-circle" style="object-fit: cover;" />
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/myOrders')">我的订单</el-dropdown-item>
                <el-dropdown-item @click="router.push('/myReservations')">我的预订</el-dropdown-item>
                <el-dropdown-item @click="editCurrentUser">个人信息</el-dropdown-item>
                <el-dropdown-item @click="router.push('/editPassword')">修改密码</el-dropdown-item>
                <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 页面主体 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 底部页脚 -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-col">
          <h3 class="footer-logo">半闲庭</h3>
          <p>现代中华料理 · 当季八道</p>
          <p>上海市静安区和平路 88 号 2F</p>
        </div>
        <div class="footer-col">
          <h4>营业时间</h4>
          <p>午市 11:30 - 14:00</p>
          <p>晚市 18:30 - 22:00</p>
        </div>
        <div class="footer-col">
          <h4>联系方式</h4>
          <p>021-6288 0088</p>
          <p>hello@banxianting.com</p>
        </div>
        <div class="footer-col">
          <h4>关注我们</h4>
          <p>微信公众号 · 大众点评</p>
          <p>© 2026 半闲庭餐饮</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p class="culture-slogan">品味中华美食 · 传承节俭美德 · 倡导绿色餐饮</p>
        <p class="copyright-text">© 2026 半闲庭 BANXIANTING. All Rights Reserved.</p>
      </div>
    </footer>
  </div>
  <AIAssistant />
</template>

<script setup>
import AIAssistant from '@/components/AIAssistant.vue'
import { useRoute } from "vue-router"
import router from '@/router/index.js'
import tools from "../../utils/tools";
import { ref } from "vue";
import { ElMessage } from "element-plus";
import {useCartStore} from "../../store/cart";
import { useDiningStore } from '@/store/dining'
const diningStore = useDiningStore()
const isUserLogin = ref(tools.isLogin())
const currentUser = ref(tools.getCurrentUser())
const cartStore = useCartStore()
if(currentUser.value === null){
  router.push({path:"/login"})
}
if(currentUser.value && currentUser.value.type !== "USER"){
  // 方便你测试，admin账号登录前台自动踢回后台
  router.push({path:"/admin"})
}

function editCurrentUser(){ router.push({path:"/editCurrentUser"}) }
function logout(){
  localStorage.clear()
  ElMessage.success("退出成功")
  setTimeout(() => { router.push({path:"/login"}) }, 1000)
}

function formatImg(url) {
  if (!url) return '';
  return url.replace('localhost', window.location.hostname);
}
</script>

<style scoped>
.app-wrapper { min-height: 100vh; display: flex; flex-direction: column; background: #12100E; color: #F7F3EC; }

/* 导航栏样式 */
.navbar { position: sticky; top: 0; z-index: 100; background: rgba(18, 16, 14, 0.9); backdrop-filter: blur(10px); border-bottom: 1px solid rgba(255,255,255,0.05); }
.nav-container { max-width: 1300px; margin: 0 auto; height: 80px; display: flex; justify-content: space-between; align-items: center; padding: 0 40px; }
.logo { cursor: pointer; display: flex; flex-direction: column; align-items: flex-start; }
.logo-cn { font-family: 'STSong', serif; font-size: 30px; font-weight: 500; margin: 0; letter-spacing: 4px; color: #F7F3EC; }
.logo-en { font-size: 10px; letter-spacing: 8px; color: #8B857C; margin-top: 2px; }

.nav-links { display: flex; gap: 120px; }
.nav-item { color: #8B857C; text-decoration: none; font-size: 18px; letter-spacing: 2px; transition: color 0.3s; }
.nav-item:hover, .nav-item.active { color: #D5A958; }

/* 修改间距：购物车和用户之间的间隔 */
.user-actions {
  display: flex;
  align-items: center;
  gap: 60px; /* 👈 把这里的数值调大（比如 50px, 60px），它们就隔开了 */
}

/* 购物车字体放大 */
.cart-text {
  font-size: 16px; /* 👈 放大到 16px */
  color: #8B857C;
  cursor: pointer;
  transition: color 0.3s;
  letter-spacing: 1px;
}
.cart-text:hover { color: #F7F3EC; }

/* 用户信息区域放大 */
.user-info {
  display: flex;
  align-items: center;
  gap: 15px; /* 头像和名字的间距 */
  cursor: pointer;
  outline: none; /* 去掉点击时的虚线框 */
}

/* 用户名放大 */
.username {
  font-size: 16px; /* 👈 放大到 16px */
  color: #F7F3EC;
  letter-spacing: 1px;
}

/* 头像放大 */
.avatar-circle {
  width: 40px; /* 👈 从 30px 改为 40px */
  height: 40px;
  border-radius: 50%;
  background: #D5A958;
  border: 2px solid #12100E; /* 给头像加个小暗边，更有质感 */
  box-shadow: 0 0 0 1px #D5A958; /* 用阴影做一个金圈 */
}

/* 主体 */
.main-content { flex: 1; }

/* 页脚样式 */
.footer { background: #0A0908; padding: 80px 0 60px; border-top: 1px solid rgba(255,255,255,0.05); }
.footer-container { max-width: 1300px; margin: 0 auto; display: grid; grid-template-columns: repeat(4, 1fr); gap: 40px; padding: 0 40px; }
.footer-logo { font-family: 'STSong', serif; font-size: 20px; letter-spacing: 2px; margin-bottom: 20px; color: #D5A958; }
.footer-col h4 { font-size: 14px; color: #F7F3EC; margin-bottom: 20px; font-weight: normal; }
.footer-col p { font-size: 12px; color: #666; margin: 10px 0; line-height: 1.6; }
</style>
<!-- 在原本的 <style scoped> 下面，再新建一个 -->
<style>
/* 针对刚刚在 el-dropdown 上加的 popper-class 进行样式穿透 */
.custom-user-dropdown .el-dropdown-menu__item {
  font-size: 16px !important;
  padding: 12px 24px !important;
  /*color: #F7F3EC !important;*/
  color: black !important;
}
.custom-user-dropdown .el-dropdown-menu__item:hover {
  background-color: #2A2826 !important; /* 鼠标悬浮时的背景色 */
  color: #D5A958 !important; /* 鼠标悬浮时的金色文字 */
}
/* 修改下拉菜单整体的背景色 */
.custom-user-dropdown.el-popper {
  background: #1A1816 !important;
  border: 1px solid rgba(213, 169, 88, 0.2) !important;
}
.custom-user-dropdown .el-popper__arrow::before {
  background: #1A1816 !important;
  border: 1px solid rgba(213, 169, 88, 0.2) !important;
}
.table-badge {
  background: rgba(198, 161, 91, 0.15);
  border: 1px solid #C6A15B;
  color: #C6A15B;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 1px;
  margin-right: 20px;
}
.footer-bottom {
  text-align: center;
  padding: 25px 0 20px 0;
  margin-top: 40px;
  /* 根据页脚原本的底色，加一条淡淡的金色/灰色分割线 */
  border-top: 1px solid rgba(212, 175, 55, 0.2);
}

.culture-slogan {
  color: #D4AF37; /* 半闲庭主题金 */
  font-size: 14px;
  letter-spacing: 3px; /* 拉开字间距 */
  margin-bottom: 10px;
  font-weight: 500;
}

.copyright-text {
  color: #888;
  font-size: 12px;
  letter-spacing: 1px;
}
@media (max-width: 768px) {
  .footer-container {
    grid-template-columns: 1fr 1fr; /* 变为 2x2 网格 */
    gap: 20px;
    text-align: center;
  }
}
</style>