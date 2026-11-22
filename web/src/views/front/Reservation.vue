<template>
  <div class="reservation-page">
    <div class="reservation-container">
      <!-- 左侧：品牌氛围区 -->
      <div class="left-visual">
        <div class="visual-bg"></div>
        <div class="visual-content">
          <p class="subtitle">RESERVATION</p>
          <h1 class="title">寻一处静谧，<br>品一席真味。</h1>
          <p class="desc">为确保极致体验，包厢需提前一日预订。<br>我们将为您预留最舒适的赏味空间。</p>
        </div>
      </div>

      <!-- 右侧：订位表单 -->
      <div class="right-form">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
          <h2 class="form-title" style="margin: 0;">预订今晚的餐桌</h2>
          <!-- 跳转我的订位记录按钮  -->
          <el-button type="primary" link style="color: #D5A958; font-size: 14px;" @click="$router.push('/myReservations')">
            查看我的订位记录 &rarr;
          </el-button>
        </div>
        <p class="form-tip">请填写以下信息，我们的客服将尽快与您电话确认。</p>

        <el-form :model="formData" label-position="top" class="dark-form">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="联系人姓名" required>
                <el-input v-model="formData.contactName" placeholder="请输入您的称呼"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话" required>
                <el-input v-model="formData.contactTel" placeholder="请输入手机号"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="就餐日期" required>
                <el-date-picker
                    v-model="formData.reserveDate"
                    type="date"
                    placeholder="请选择日期"
                    style="width: 100%"
                    value-format="YYYY-MM-DD"
                    :disabled-date="disabledDate"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="就餐时间" required>
                <el-select v-model="formData.reserveTime" placeholder="请选择时间" style="width: 100%">
                  <el-option-group label="午市 (11:30 - 14:00)">
                    <el-option v-for="time in lunchTimes" :key="time" :label="time" :value="time" />
                  </el-option-group>
                  <el-option-group label="晚市 (17:30 - 21:30)">
                    <el-option v-for="time in dinnerTimes" :key="time" :label="time" :value="time" />
                  </el-option-group>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="就餐人数" required>
                <el-input-number v-model="formData.peopleCount" :min="1" :max="20" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="需要私密包厢？">
                <el-switch v-model="formData.needRoom" :active-value="1" :inactive-value="0" active-text="是" inactive-text="否" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="特殊备注">
            <el-input
                v-model="formData.remark"
                type="textarea"
                :rows="3"
                placeholder="如：过生日需要布置、忌口、需要宝宝椅等..."
            ></el-input>
          </el-form-item>

          <el-button class="submit-btn" :loading="submitting" @click="submitReservation">
            提交预订申请
          </el-button>
        </el-form>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from "@/utils/http.js"
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 表单数据
const formData = ref({
  contactName: '',
  contactTel: '',
  reserveDate: '',
  reserveTime: '',
  peopleCount: 2,
  needRoom: 0,
  remark: ''
})

const submitting = ref(false)

// 生成营业时间选项
const lunchTimes = ['11:30', '12:00', '12:30', '13:00', '13:30']
const dinnerTimes = ['17:30', '18:00', '18:30', '19:00', '19:30', '20:00', '20:30', '21:00']



// 禁止选择过去的日期
function disabledDate(time) {
  // 如果时间小于今天（减去 8.64e7 毫秒，即一天），则禁用
  return time.getTime() < Date.now() - 8.64e7;
}



// 提交预订
function submitReservation() {
  // 1. 基础校验
  if (!formData.value.contactName || !formData.value.contactTel) {
    return ElMessage.warning('请填写完整的联系人信息')
  }
  if (!formData.value.reserveDate || !formData.value.reserveTime) {
    return ElMessage.warning('请选择就餐日期和时间')
  }

  // 电话正则简单校验
  if (!/^1[3-9]\d{9}$/.test(formData.value.contactTel)) {
    return ElMessage.warning('请输入正确的手机号码')
  }

  submitting.value = true

  // 2. 发送请求
  request.post('/reservation/add', formData.value).then(res => {
    submitting.value = false
    if (res.code === 200) {
      ElMessageBox.confirm(
          '您的预订申请已成功呈交。客服将在稍后与您电话确认细节。现在查看预订记录？',
          '半闲庭 · 预订回执',
          {
            confirmButtonText: '查看我的预订',
            cancelButtonText: '暂不查看',
            type: '', // 去掉默认的绿色图标，更显克制
            center: true,
            showClose: false,
            customClass: 'luxury-res-dialog' // 注入自定义样式类名
          }
      ).then(() => {
        router.push('/myReservations')
      }).catch(() => {
        router.push('/index')
      })

      // 提交成功后，清空关键信息
      formData.value.reserveDate = ''
      formData.value.reserveTime = ''
      formData.value.remark = ''
    }
  }).catch(() => {
    submitting.value = false
  })
}

// 状态颜色映射
function getStatusType(status) {
  if (status === '待确认') return 'warning'
  if (status === '已确认') return 'success'
  if (status === '已到店') return 'info'
  if (status === '已取消') return 'danger'
  return 'info'
}
</script>

<style scoped>
.reservation-page {
  background: #12100E;
  min-height: calc(100vh - 80px);
  color: #F7F3EC;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px 0;
}

.reservation-container {
  max-width: 1100px;
  width: 100%;
  display: flex;
  background: #1A1816;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.05);
}

/* 左侧视觉区 */
.left-visual {
  flex: 1;
  position: relative;
  display: flex;
  align-items: flex-end;
  padding: 60px 40px;
}

.visual-bg {
  position: absolute;
  inset: 0;
  background: url('@/assets/reservation-bg.jpeg') center/cover;
  opacity: 1.0;
}

.visual-content {
  position: relative;
  z-index: 1;
}

.subtitle {
  font-size: 12px;
  color: #D5A958;
  letter-spacing: 4px;
  margin-bottom: 20px;
}

.title {
  font-family: 'STSong', serif;
  font-size: 42px;
  line-height: 1.3;
  margin-bottom: 20px;
  letter-spacing: 2px;
}

.desc {
  font-size: 14px;
  color: #A39F9A;
  line-height: 1.8;
}

/* 右侧表单区 */
.right-form {
  flex: 1.2;
  padding: 60px 50px;
  background: #1A1816;
}

.form-title {
  font-family: 'STSong', serif;
  font-size: 28px;
  margin-bottom: 10px;
  letter-spacing: 2px;
}

.form-tip {
  font-size: 13px;
  color: #888;
  margin-bottom: 40px;
}

/* 覆盖 Element Plus 的默认样式，适配暗黑风 */
:deep(.el-form-item__label) {
  color: #A39F9A !important;
  font-size: 13px;
}
:deep(.el-input__wrapper), :deep(.el-textarea__inner), :deep(.el-select__wrapper) {
  background-color: #12100E !important;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.1) inset !important;
  border-radius: 6px;
}
:deep(.el-input__inner), :deep(.el-textarea__inner) {
  color: #F7F3EC !important;
}
:deep(.el-input__inner::placeholder) {
  color: #555 !important;
}

.submit-btn {
  width: 100%;
  height: 50px;
  background: #D5A958;
  color: #12100E;
  border: none;
  font-size: 16px;
  font-weight: bold;
  letter-spacing: 2px;
  margin-top: 10px;
  cursor: pointer;
  transition: all 0.3s;
}
.submit-btn:hover {
  background: #E5C07B;
}

/* 手机端适配 */
@media (max-width: 768px) {
  .reservation-container {
    flex-direction: column;
    width: 90%;
  }
  .left-visual {
    padding: 40px 20px;
    min-height: 200px;
  }
  .title { font-size: 32px; }
  .right-form { padding: 30px 20px; }
}

/* 我的预订列表样式  */
.my-reservations {
  margin-top: 40px;
}
.divider {
  font-size: 13px;
  color: #888;
  border-bottom: 1px dashed rgba(255,255,255,0.1);
  padding-bottom: 10px;
  margin-bottom: 20px;
}
.res-card {
  background: #12100E;
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
}
.res-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.res-date {
  font-family: Georgia, serif;
  color: #D5A958;
  font-size: 16px;
}
.res-body {
  font-size: 13px;
  color: #A39F9A;
  display: flex;
  justify-content: space-between;
}
.res-table {
  color: #C6A15B;
  font-weight: bold;
}

@media (max-width: 768px) {
  .reservation-container { flex-direction: column; width: 90%; }
  .left-visual { padding: 40px 20px; min-height: 200px; }
  .title { font-size: 32px; }
  .right-form { padding: 30px 20px; }
}
</style>

<style>
/*  弹窗暗黑金专属样式  */
.luxury-res-dialog {
  background-color: #1A1816 !important; /* 深色背景 */
  border: 1px solid rgba(213, 169, 88, 0.3) !important; /* 暗金边框 */
  border-radius: 12px !important;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.8) !important;
  padding: 30px !important;
  max-width: 450px;
}

/* 标题样式 */
.luxury-res-dialog .el-message-box__title {
  color: #D5A958 !important; /* 金色标题 */
  font-family: 'STSong', serif !important; /* 宋体 */
  font-size: 22px !important;
  letter-spacing: 2px;
  font-weight: bold;
}

/* 内容文字样式 */
.luxury-res-dialog .el-message-box__message {
  color: #A39F9A !important; /* 高级灰文字 */
  font-size: 15px !important;
  line-height: 1.8 !important;
  padding: 10px 0 20px 0 !important;
}

/* 按钮区容器 */
.luxury-res-dialog .el-message-box__btns {
  justify-content: center !important;
  gap: 20px;
  padding-top: 10px !important;
}

/* 自定义按钮通用样式 */
.luxury-res-dialog .el-button {
  border-radius: 4px;
  font-size: 14px;
  letter-spacing: 1px;
  padding: 10px 25px;
  margin: 0 !important;
}

/* “查看我的预订”按钮（主按钮） */
.luxury-res-dialog .el-button--primary {
  background-color: #D5A958 !important; /* 金色背景 */
  border-color: #D5A958 !important;
  color: #12100E !important; /* 黑色文字 */
  font-weight: bold;
}
.luxury-res-dialog .el-button--primary:hover {
  background-color: #E5C07B !important;
}

/* “暂不查看”按钮（副按钮） */
.luxury-res-dialog .el-button--default {
  background-color: transparent !important;
  border-color: rgba(255, 255, 255, 0.2) !important;
  color: #888 !important;
}
.luxury-res-dialog .el-button--default:hover {
  border-color: #D5A958 !important;
  color: #D5A958 !important;
}
</style>