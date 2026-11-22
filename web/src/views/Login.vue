<template>
  <div class="login-container">
    <!-- 左侧品牌视觉展示区（绝对定位，悬浮在背景上） -->
    <div class="brand-content">
      <h1 class="brand-title">半闲庭</h1>
      <div class="brand-line"></div>
      <h2 class="brand-subtitle">品味非凡，尽在半闲</h2>
      <p class="brand-desc">
        大隐于市的私享厨房。<br />
        每一道珍馐，都为您精心雕琢。
      </p>
    </div>

    <!-- 右侧表单操作区（毛玻璃卡片） -->
    <div class="login-form-wrapper">
      <!-- Logo 与 标题 -->
      <div class="form-header">
        <img src="../assets/logo4.0.png" class="logo-img" alt="logo" />
        <h3>半闲庭 · 尊享后台</h3>
        <p>service@banxianting.com</p>
      </div>

      <el-form
          ref="formRef"
          :model="formData"
          label-width="0px"
          class="login-form"
      >
        <el-form-item
            prop="username"
            :rules="[{ required: true, message: '请输入用户名', trigger: ['blur', 'change'] }]"
        >
          <el-input
              v-model.trim="formData.username"
              placeholder="请输入账号"
              :prefix-icon="User"
              size="large"
              clearable
          />
        </el-form-item>

        <el-form-item
            prop="password"
            :rules="[{ required: true, message: '请输入密码', trigger: ['blur', 'change'] }]"
        >
          <el-input
              v-model.trim="formData.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
              show-password
              clearable
              @keyup.enter="submitForm"
          />
        </el-form-item>

        <!-- 用户类型选择 -->
        <el-form-item prop="type">
          <el-select
              v-model="formData.type"
              placeholder="请选择身份类型"
              size="large"
              style="width: 100%"
              popper-class="dark-select-popper"
          >
            <el-option label="管理员" value="ADMIN"></el-option>
            <el-option label="普通用户" value="USER"></el-option>
          </el-select>
        </el-form-item>

        <div class="form-options">
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          <router-link tag="span" :to="{ path: 'retrievePassword' }" class="link-text">
            忘记密码？
          </router-link>
        </div>

        <el-button
            type="primary"
            class="login-btn"
            size="large"
            :loading="loading"
            @click="submitForm"
        >
          登 录
        </el-button>

        <div class="register-link">
          <span>没有账号？</span>
          <router-link tag="span" :to="{ path: 'register' }" class="link-text">
            去注册
          </router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
// 逻辑部分完全不变，保持你原有的代码
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import http from "@/utils/http.js";
import { User, Lock } from "@element-plus/icons-vue";
import router from "@/router/index.js";
import { useRoute } from 'vue-router';

const formData = ref({
  username: '',
  password: '',
  type: 'ADMIN' // 默认管理员
});
const rememberMe = ref(false);
const loading = ref(false);
const route = useRoute();
const formRef = ref(null);

const submitForm = () => {
  if (!formRef.value) return;

  formRef.value.validate((valid) => {
    if (!valid) return;

    loading.value = true;
    http.post("/common/login", formData.value).then(res => {
      if (!res) {
        loading.value = false;
        return;
      }

      ElMessage({
        message: "登录成功，正在跳转",
        type: "success"
      });
      localStorage.setItem("token", res.data);

      http.get("/common/currentUser").then(res1 => {
        let currentUser = res1.data;
        localStorage.setItem("currentUser", JSON.stringify(currentUser));

        const redirectUrl = route.query.redirect;
        if (redirectUrl) {
          router.push(redirectUrl);
        } else {
          if (currentUser.type === "USER") {
            router.push({ path: "/" });
          } else {
            router.push({ path: "/admin" });
          }
        }
      }).catch(() => {
        loading.value = false;
      }).finally(() => {
        loading.value = false;
      });
    }).catch(() => {
      loading.value = false;
    });
  });
};
</script>

<style scoped>
/* ================= 整体背景 ================= */
.login-container {
  position: relative;
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  /* 将图3作为全屏背景 */
  background-image: url("../assets/login_bg.png");
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
}

/* 给背景加一层极暗的遮罩，防止部分区域太亮影响文字 */
.login-container::before {
  content: '';
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  pointer-events: none;
}

/* ================= 左侧品牌视觉区 ================= */
.brand-content {
  position: absolute;
  left: 8%;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1;
  max-width: 500px;
  color: #fff;
  /* 增加一点左侧阴影，让文字在背景图上更清晰 */
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.6);
}

.brand-title {
  font-size: 56px;
  font-weight: 600;
  letter-spacing: 8px;
  margin-bottom: 20px;
  color: #d4af37; /* 金色 */
  font-family: "Songti SC", "SimSun", serif;
}

.brand-line {
  width: 60px;
  height: 2px;
  background-color: #d4af37;
  margin-bottom: 24px;
}

.brand-subtitle {
  font-size: 24px;
  font-weight: 300;
  letter-spacing: 4px;
  margin-bottom: 16px;
  color: #eeeeee;
}

.brand-desc {
  font-size: 14px;
  line-height: 1.8;
  color: #cccccc;
  letter-spacing: 1px;
}

/* ================= 右侧表单区（毛玻璃卡片） ================= */
.login-form-wrapper {
  position: absolute;
  right: 10%;
  top: 50%;
  transform: translateY(-50%);
  width: 100%;
  max-width: 400px;
  padding: 40px;
  z-index: 2;

  /* 毛玻璃效果核心代码 */
  background: rgba(20, 20, 20, 0.75); /* 半透明黑底 */
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(212, 175, 55, 0.2); /* 淡淡的金边 */
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
}

.form-header {
  margin-bottom: 30px;
  text-align: center;
}

.logo-img {
  width: 120px;
  margin-bottom: 10px;
  /* 如果logo是深色或透明底，可以加一点发光效果使其融入暗背景 */
  filter: drop-shadow(0 0 8px rgba(212, 175, 55, 0.3));
}

.form-header h3 {
  font-size: 24px;
  font-weight: 600;
  color: #d4af37; /* 金色 */
  margin-bottom: 6px;
  font-style: oblique;
  letter-spacing: 2px;
}

.form-header p {
  font-size: 13px;
  color: #999;
  font-style: oblique;
}

.login-form {
  margin-bottom: 24px;
}

/* ================= Element Plus 输入框“水光玻璃”适配 ================= */
/* 覆盖输入框底层背景、边框和光影 */
:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  /* 使用微弱的线性渐变模拟玻璃厚度和受光面 */
  background: linear-gradient(
      135deg,
      rgba(255, 255, 255, 0.08) 0%,
      rgba(255, 255, 255, 0.02) 100%
  ) !important;
  /* 极细的半透明边框 */
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  border-radius: 10px !important; /* 圆角稍微大一点点，更圆润 */
  padding: 6px 15px !important;
  transition: all 0.3s ease;

  /* 核心：使用内阴影模拟水光高光，外阴影制造悬浮感 */
  box-shadow:
      inset 0 1px 1px rgba(255, 255, 255, 0.2),   /* 顶部高光：模拟玻璃边缘的反光 */
      inset 0 0 12px rgba(255, 255, 255, 0.05),  /* 内部微微发光 */
      0 4px 12px rgba(0, 0, 0, 0.15) !important; /* 底部外阴影，让框浮起来 */

  /* 如果框下面有复杂的背景，可以加上背景模糊 */
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

/* 聚焦（激活）状态：金光边缘 */
:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focused) {
  border: 1px solid rgba(212, 175, 55, 0.8) !important; /* 金色亮边 */
  background: linear-gradient(
      135deg,
      rgba(255, 255, 255, 0.12) 0%,
      rgba(212, 175, 55, 0.05) 100%
  ) !important;
  box-shadow:
      inset 0 1px 2px rgba(255, 255, 255, 0.3),   /* 聚焦时高光更亮 */
      inset 0 0 10px rgba(212, 175, 55, 0.2),     /* 内部泛起金光 */
      0 0 15px rgba(212, 175, 55, 0.2) !important; /* 外部金色光晕 */
}

/* 鼠标悬浮状态：轻微提亮 */
:deep(.el-input__wrapper:hover),
:deep(.el-select__wrapper:hover) {
  background: linear-gradient(
      135deg,
      rgba(255, 255, 255, 0.1) 0%,
      rgba(255, 255, 255, 0.03) 100%
  ) !important;
  border: 1px solid rgba(255, 255, 255, 0.25) !important;
}

/* 输入框文字和占位符颜色 */
:deep(.el-input__inner) {
  color: #fff !important; /* 输入文字变白 */
}
:deep(.el-input__inner::placeholder) {
  color: #888 !important;
}
:deep(.el-input__prefix-inner),
:deep(.el-input__suffix-inner) {
  color: #aaa !important;
}

/* 下拉框组件文字颜色 */
:deep(.el-select__selected-item) {
  color: #fff !important;
}

/* Checkbox 文字颜色 */
:deep(.el-checkbox__label) {
  color: #ccc !important;
}
:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #d4af37 !important;
  border-color: #d4af37 !important;
}

/* 选项行布局 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  font-size: 14px;
}

.link-text {
  color: #aaa;
  cursor: pointer;
  transition: color 0.3s;
}

.link-text:hover {
  color: #d4af37;
}

/* 墨金登录按钮 */
.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  letter-spacing: 4px;
  border-radius: 8px;
  border: 1px solid #d4af37;
  background: transparent;
  color: #d4af37;
  transition: all 0.3s;
  margin-bottom: 20px;
}

.login-btn:hover {
  background: #d4af37;
  color: #1a1a1a;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(212, 175, 55, 0.3);
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: #999;
}

.register-link .link-text {
  margin-left: 5px;
  font-weight: bold;
}

/* ================= 下拉框弹出层适配（挂载在body上，用全局样式） ================= */
/* 如果你用了：popper-class="dark-select-popper"，需要去掉 scoped 才能生效（或者写在全局样式里） */
</style>

<style>
/* 单独处理下拉框菜单的深色样式（因为它是挂载在body下的） */
.dark-select-popper.el-popper {
  background-color: #2b2b2b !important;
  border: 1px solid rgba(212, 175, 55, 0.3) !important;
}
.dark-select-popper .el-select-dropdown__item {
  color: #ccc !important;
}
.dark-select-popper .el-select-dropdown__item.is-hovering {
  background-color: rgba(255, 255, 255, 0.1) !important;
}
.dark-select-popper .el-select-dropdown__item.is-selected {
  color: #d4af37 !important;
  font-weight: bold;
}
.dark-select-popper .el-popper__arrow::before {
  background-color: #2b2b2b !important;
  border: 1px solid rgba(212, 175, 55, 0.3) !important;
}

/* ================= 响应式适配（手机端） ================= */
@media (max-width: 1024px) {
  .login-container {
    justify-content: center;
    align-items: center;
  }
  .brand-content {
    display: none; /* 手机端隐藏左侧大字，突出表单 */
  }
  .login-form-wrapper {
    position: static;
    transform: none;
    width: 90%;
    max-width: 400px;
  }
}
</style>