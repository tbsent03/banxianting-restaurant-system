<template>
  <div class="m-reservation">
    <!-- 顶部视觉头图 -->
    <div class="hero-banner">
      <img src="@/assets/reservation-bg.jpeg" class="hero-img" alt="reservation" />
      <div class="hero-mask"></div>
      <div class="hero-content">
        <p class="en-title">RESERVATION</p>
        <h1 class="cn-title">寻一处静谧，<br>品一席真味。</h1>
        <p class="desc">为确保极致体验，包厢需提前一日预订。</p>
      </div>
    </div>

    <!-- 表单区，使用负边距向上层叠 -->
    <div class="form-container">
      <van-form @submit="onSubmit">
        <van-cell-group inset class="form-group">
          <van-field
              v-model="formData.reserveDate"
              is-link readonly name="date" label="就餐日期"
              placeholder="选择日期" @click="showCalendar = true"
              :rules="[{ required: true, message: '请选择日期' }]"
          />
          <van-field
              v-model="timeDisplay"
              is-link readonly name="time" label="就餐时间"
              placeholder="选择时间" @click="showTimePicker = true"
              :rules="[{ required: true, message: '请选择时间' }]"
          />

          <!-- 用餐人数 + 包厢开关同行 -->
          <van-field
              v-model="formData.peopleCount" type="digit" name="peopleCount" label="就餐人数"
              placeholder="请输入人数" :rules="[{ required: true, message: '请输入人数' }]"
          />
          <van-field name="needRoom" label="需要私密包厢?">
            <template #input>
              <van-switch v-model="formData.needRoom" size="20" active-color="#D5A958" :active-value="1" :inactive-value="0" />
              <span style="margin-left: 10px; font-size: 13px; color: var(--m-muted);">{{ formData.needRoom === 1 ? '是' : '否' }}</span>
            </template>
          </van-field>
        </van-cell-group>

        <van-cell-group inset class="form-group" style="margin-top: 12px;">
          <van-field v-model="formData.contactName" label="联系人姓名" placeholder="请输入您的称呼" :rules="[{ required: true, message: '请填写姓名' }]" />
          <van-field v-model="formData.contactTel" type="tel" label="联系电话" placeholder="请输入手机号" :rules="[{ required: true, message: '请填写手机号' }]" />
          <van-field v-model="formData.remark" label="特殊备注" type="textarea" rows="2" autosize placeholder="如：过生日需要布置、忌口、需要宝宝椅等..." />
        </van-cell-group>

        <div class="submit-wrap">
          <van-button round block color="#D5A958" style="color: #12100E; font-weight: bold; height: 44px;" native-type="submit" :loading="loading">
            提交预订申请
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 日历弹窗 -->
    <van-calendar v-model:show="showCalendar" @confirm="onConfirmDate" color="#D5A958" class="dark-calendar" :min-date="new Date()" />

    <!-- 时间选择器 -->
    <van-popup v-model:show="showTimePicker" position="bottom" round>
      <van-picker
          v-model="pickerValue"
          title="选择用餐时间"
          :columns="timeColumns"
          @confirm="onConfirmTime"
          @cancel="showTimePicker = false"
          class="dark-picker"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showFailToast } from 'vant'
import http from '@/utils/http.js'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const showCalendar = ref(false)
const showTimePicker = ref(false)

const pickerValue = ref(['lunch', '11:30'])

const formData = ref({
  reserveDate: '',
  reserveTime: '',
  endTime: '',
  peopleCount: '',
  needRoom: 0, // 0: 否, 1: 是
  contactName: '',
  contactTel: '',
  remark: ''
})

const timeDisplay = computed(() => {
  if (formData.value.reserveTime && formData.value.endTime) {
    return `${formData.value.reserveTime} - ${formData.value.endTime}`
  }
  return ''
})

const timeColumns = [
  {
    text: '午市 (11:30 - 14:00)',
    value: 'lunch',
    children: generateTimes(11, 30, 14, 0)
  },
  {
    text: '晚市 (17:30 - 21:30)',
    value: 'dinner',
    children: generateTimes(17, 30, 21, 30)
  }
]

function generateTimes(startH, startM, endH, endM) {
  let times = []
  let current = dayjs().hour(startH).minute(startM).second(0)
  let end = dayjs().hour(endH).minute(endM).second(0)
  while (current.isBefore(end) || current.isSame(end)) {
    times.push({ text: current.format('HH:mm'), value: current.format('HH:mm') })
    current = current.add(30, 'minute')
  }
  return times
}

const onConfirmDate = (date) => {
  formData.value.reserveDate = dayjs(date).format('YYYY-MM-DD')
  showCalendar.value = false
}

const onConfirmTime = ({ selectedOptions }) => {
  const time = selectedOptions[1].value
  formData.value.reserveTime = time
  formData.value.endTime = dayjs(`2020-01-01 ${time}`).add(2, 'hour').format('HH:mm')
  showTimePicker.value = false
}

const onSubmit = () => {
  loading.value = true

  http.post('/reservation/add', formData.value).then(res => {
    if (res.code === 200) {
      showSuccessToast('预订成功')
      setTimeout(() => router.replace('/myReservations'), 800)
    } else {
      showFailToast(res.msg || '预订失败')
    }
  }).catch(() => {}).finally(() => {
    loading.value = false
  })
}
</script>

<style scoped>
.m-reservation {
  min-height: 100dvh;
  background: var(--m-bg);
  padding-bottom: 30px;
}

/* 🌟 顶部大图样式 */
.hero-banner {
  position: relative;
  height: 280px;
  width: 100%;
  overflow: hidden;
}
.hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(1.35) contrast(1.05);
}
.hero-mask {
  position: absolute;
  inset: 0;
  /* 渐变遮罩：底部纯黑，顶部微透明，保证文字清晰 */
  background: linear-gradient(to bottom, rgba(18,16,14,0) 0%,
  rgba(18,16,14,0.5) 50%,
  rgba(18,16,14,1) 90%
  );
}
.hero-content {
  position: absolute;
  bottom: 30px;
  left: 20px;
  right: 20px;
  z-index: 2;
}
.en-title {
  font-size: 12px;
  color: var(--m-gold);
  letter-spacing: 4px;
  margin-bottom: 8px;
}
.cn-title {
  font-family: 'STSong', serif;
  font-size: 28px;
  color: #fff;
  line-height: 1.3;
  margin: 0 0 10px 0;
  letter-spacing: 2px;
}
.desc {
  font-size: 12px;
  color: var(--m-text-2);
  margin: 0;
}

/* 🌟 表单层叠容器 */
.form-container {
  position: relative;
  z-index: 10;
  /* 负边距，让表单向上盖住图片底部一点 */
  margin-top: -20px;
  border-radius: 24px 24px 0 0;
  background: var(--m-bg);
  padding-top: 20px;
}

.form-group {
  --van-cell-background: var(--m-surface);
  --van-cell-text-color: var(--m-text);
  --van-cell-border-color: rgba(255,255,255,0.05);
  --van-field-label-color: var(--m-text-2);
}
:deep(.van-field__control) { color: var(--m-text); }
:deep(.van-cell) { background: var(--m-surface); }

.submit-wrap { margin: 30px 16px; }

/* 日历暗黑样式 */
:deep(.dark-calendar) {
  --van-calendar-background: #1A1816;
  --van-calendar-title-color: #F7F3EC;
  --van-calendar-month-title-color: #F7F3EC;
  --van-calendar-weekdays-color: #8B857C;
  --van-calendar-day-color: #F7F3EC;
  --van-calendar-selected-day-background: #D5A958;
  --van-calendar-selected-day-color: #12100E;
  --van-calendar-popup-height: 80%;
}

/* 选择器暗黑样式 */
:deep(.dark-picker) { background-color: #1A1816; }
:deep(.dark-picker .van-picker__toolbar) { background-color: #1A1816; border-bottom: 1px solid rgba(255,255,255,0.05); }
:deep(.dark-picker .van-picker__cancel), :deep(.dark-picker .van-picker__confirm) { color: #D5A958; }
:deep(.dark-picker .van-picker__title) { color: #F7F3EC; }
:deep(.dark-picker .van-picker-column__item) { color: #F7F3EC; }
:deep(.dark-picker .van-picker-column__item--selected) { color: #D5A958; font-weight: bold; }
</style>