<template>
  <div class="m-orders">
    <div class="header-title">
      <h2>我的订单</h2>
    </div>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
      >
        <div class="order-card" v-for="order in orderList" :key="order.id">
          <!-- 卡片头部 -->
          <div class="card-header">
            <span class="order-no">单号：{{ order.orderNo }}</span>
            <span class="status" :class="getStatusClass(order.status)">{{ order.status }}</span>
          </div>

          <!-- 卡片内容：时间与金额 -->
          <div class="card-info">
            <span class="time">{{ order.createTime }}</span>
            <div class="total">合计：<span>¥{{ order.totalAmount?.toFixed(2) || '0.00' }}</span></div>
          </div>

          <!-- 🌟 高精度手写时间轴 -->
          <div class="timeline-box" v-if="order.status !== '已取消'">
            <div class="custom-steps">
              <div
                  v-for="(step, index) in stepsConfig"
                  :key="index"
                  class="step-item"
                  :class="{
                  'is-finish': getStepActive(order.status) > index,
                  'is-process': getStepActive(order.status) === index
                }"
              >
                <!-- 圆圈部分 -->
                <div class="step-circle">
                  <!-- 已经完成的步骤显示对勾 -->
                  <van-icon v-if="getStepActive(order.status) > index" name="success" />
                  <!-- 当前进度和未到达的步骤显示数字 -->
                  <span v-else class="step-num">{{ index + 1 }}</span>
                </div>

                <!-- 文字部分 -->
                <div class="step-text-group">
                  <div class="step-title">{{ step.title }}</div>
                  <div class="step-desc">{{ step.desc }}</div>
                </div>

                <!-- 连接线（最后一个不显示） -->
                <div class="step-line" v-if="index < stepsConfig.length - 1"></div>
              </div>
            </div>
          </div>

          <!-- 已取消状态 -->
          <div class="cancel-box" v-else>
            该订单已取消
          </div>

          <!-- 卡片底部：操作按钮 -->
          <div class="card-footer">
            <template v-if="order.status === '已完成'">
              <van-button
                  v-if="!order.reviewId"
                  size="small"  type="warning" round
                  class="btn-eval" @click="openReviewDialog(order)"
              >去评价</van-button>
              <van-button
                  v-else
                  size="small"  round
                  class="btn-eval" @click="openViewReview(order)"
              >查看评价</van-button>
            </template>

            <van-button size="small" plain round class="btn-custom" @click="viewDetail(order.orderNo)">
              查看详情
            </van-button>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>

    <!-- 订单详情弹窗 -->
    <van-popup v-model:show="drawerVisible" position="bottom" round :style="{ height: '75%' }" class="dark-popup">
      <div class="popup-header">
        <h3>订单详情</h3>
        <van-icon name="cross" @click="drawerVisible = false" />
      </div>
      <div class="popup-content" v-if="currentOrder">
        <div class="detail-row">
          <span>订单状态</span>
          <span class="status" :class="getStatusClass(currentOrder.status)">{{ currentOrder.status }}</span>
        </div>
        <div class="detail-row"><span>订单类型</span><span>{{ currentOrder.orderType }}</span></div>
        <div class="detail-row"><span>联系人</span><span>{{ currentOrder.contactName }} ({{ currentOrder.contactTel }})</span></div>
        <div class="detail-row"><span>下单时间</span><span>{{ currentOrder.createTime }}</span></div>

        <div class="divider">菜品明细</div>
        <div class="dish-scroll-area">
          <div class="detail-dish" v-for="item in currentOrder.items" :key="item.id">
            <span>{{ item.dishName }} <span class="multiply">x{{ item.quantity }}</span></span>
            <span>¥{{ item.subtotal?.toFixed(2) || (item.price * item.quantity).toFixed(2) }}</span>
          </div>
        </div>

        <div class="total-price">合计：<span>¥{{ currentOrder.totalAmount?.toFixed(2) }}</span></div>
        <div class="detail-remark" v-if="currentOrder.remark">备注：{{ currentOrder.remark }}</div>
      </div>
    </van-popup>

    <!-- 提交评价弹窗 -->
    <van-popup v-model:show="reviewDialogVisible" position="bottom" round class="dark-popup">
      <div class="popup-header">
        <h3>评价本次用餐</h3>
        <van-icon name="cross" @click="reviewDialogVisible = false" />
      </div>
      <div class="popup-content review-content">
        <p class="rate-tip">请为本次服务打分</p>
        <van-rate v-model="reviewForm.score" color="#D5A958" void-color="#444" size="32" />

        <van-field
            v-model="reviewForm.content"
            type="textarea"
            rows="3"
            placeholder="菜品口味、服务体验如何？写下您的感受吧（选填）"
            class="review-input"
        />

        <van-button round block color="#D5A958" style="color: #12100E; font-weight: bold; margin-top: 20px;" :loading="reviewSubmitting" @click="submitReview">
          提交评价
        </van-button>
      </div>
    </van-popup>

    <!-- 查看评价弹窗 -->
    <van-popup v-model:show="viewReviewVisible" position="bottom" round class="dark-popup">
      <div class="popup-header">
        <h3>我的评价</h3>
        <van-icon name="cross" @click="viewReviewVisible = false" />
      </div>
      <div class="popup-content" v-if="currentReviewData">
        <div class="view-rate-row">
          <span>我的评分：</span>
          <van-rate v-model="currentReviewData.reviewScore" disabled color="#D5A958" size="20" />
        </div>
        <div class="review-text-box">
          {{ currentReviewData.reviewContent || '（未填写文字内容）' }}
        </div>

        <div v-if="currentReviewData.reviewReply" class="reply-box">
          <p class="reply-title">半闲庭官方回复：</p>
          <p class="reply-text">{{ currentReviewData.reviewReply }}</p>
        </div>
        <div v-else class="no-reply">
          商家还未回复，请耐心等待。
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showSuccessToast, showFailToast } from 'vant'
import http from '@/utils/http.js'

const orderList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const pageNum = ref(1)
const pageSize = 10

const drawerVisible = ref(false)
const currentOrder = ref(null)

const reviewDialogVisible = ref(false)
const reviewSubmitting = ref(false)
const reviewForm = ref({ orderId: null, score: 5, content: '' })

const viewReviewVisible = ref(false)
const currentReviewData = ref(null)

// 🌟 补全副标题描述
const stepsConfig = [
  { title: '已下单', desc: '等待餐厅接单' },
  { title: '制作中', desc: '后厨正在准备' },
  { title: '已上菜', desc: '请慢用' },
  { title: '已完成', desc: '欢迎再次光临' }
]

onMounted(() => {
  onLoad()
})

const onLoad = () => {
  http.get('/order/myPage', {
    params: { pageNum: pageNum.value, pageSize }
  }).then(res => {
    if (res.code === 200) {
      if (refreshing.value) {
        orderList.value = []
        refreshing.value = false
      }
      const newList = res.data.list || []
      orderList.value = [...orderList.value, ...newList]
      loading.value = false
      if (orderList.value.length >= res.data.total) {
        finished.value = true
      } else {
        pageNum.value++
      }
    }
  }).catch(() => {
    loading.value = false
    finished.value = true
  })
}

const onRefresh = () => {
  finished.value = false
  loading.value = true
  pageNum.value = 1
  onLoad()
}

const getStatusClass = (status) => {
  switch(status) {
    case '待接单': return 'wait'
    case '制作中': return 'cooking'
    case '已上菜': return 'served'
    case '已完成': return 'done'
    case '已取消': return 'cancel'
    default: return 'wait'
  }
}

// 状态对应的时间轴索引
const getStepActive = (status) => {
  if (status === '待接单') return 0
  if (status === '制作中') return 1
  if (status === '已上菜') return 2
  if (status === '已完成') return 3
  return 0
}

const viewDetail = (orderNo) => {
  http.get(`/order/detail/${orderNo}`).then(res => {
    if (res.code === 200) {
      currentOrder.value = res.data
      drawerVisible.value = true
    }
  })
}

const openReviewDialog = (order) => {
  reviewForm.value = { orderId: order.id, score: 5, content: '' }
  reviewDialogVisible.value = true
}

const submitReview = () => {
  if (!reviewForm.value.score) return showFailToast('请至少给出 1 星评分')
  reviewSubmitting.value = true
  http.post('/review/add', reviewForm.value).then(res => {
    reviewSubmitting.value = false
    if (res && res.code === 200) {
      showSuccessToast('感谢您的评价！')
      reviewDialogVisible.value = false
      onRefresh()
    } else {
      showFailToast(res.msg || '评价失败')
    }
  }).catch(() => {
    reviewSubmitting.value = false
  })
}

const openViewReview = (order) => {
  currentReviewData.value = order
  viewReviewVisible.value = true
}
</script>

<style scoped>
.m-orders { padding: 16px; padding-bottom: 120px; min-height: calc(100dvh - 50px); background: var(--m-bg); }
.header-title h2 { font-family: 'Songti SC', serif; font-size: 24px; color: var(--m-text); margin-bottom: 20px; }
.order-card { background: var(--m-surface); border-radius: var(--m-radius); margin-bottom: 16px; border: 1px solid rgba(255,255,255,0.05); overflow: hidden; }

.card-header { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; background: #22201E; border-bottom: 1px solid rgba(255,255,255,0.05); }
.order-no { font-size: 13px; color: var(--m-text-2); font-family: Georgia, serif; }
.status { font-size: 13px; font-weight: bold; padding: 2px 8px; border-radius: 4px; }
.status.wait { color: #E6A23C; background: rgba(230, 162, 60, 0.1); }
.status.cooking { color: #409EFF; background: rgba(64, 158, 255, 0.1); }
.status.served { color: #67C23A; background: rgba(103, 194, 58, 0.1); }
.status.done { color: #909399; background: rgba(144, 147, 153, 0.1); }
.status.cancel { color: #C0705C; background: rgba(192, 112, 92, 0.1); }

.card-info { display: flex; justify-content: space-between; align-items: center; padding: 16px 16px 8px; }
.time { font-size: 12px; color: var(--m-muted); }
.total { font-size: 13px; color: var(--m-text-2); }
.total span { font-size: 18px; color: var(--m-gold); font-family: Georgia, serif; font-weight: bold; }

/* 🌟 高精度时间轴 */
.timeline-box { padding: 20px 10px 10px; }
.custom-steps { display: flex; align-items: flex-start; position: relative; }
.step-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 1;
}

/* 圆圈基础（未到达状态） */
.step-circle {
  width: 24px; height: 24px; border-radius: 50%;
  border: 2px solid #777; background: #fff;
  display: flex; align-items: center; justify-content: center;
  z-index: 2; font-size: 12px; color: #777;
  transition: all 0.3s;
}

/* 文字组 */
.step-text-group { text-align: center; margin-top: 8px; }
.step-title { font-size: 13px; color: #777; font-weight: 500; }
.step-desc { font-size: 11px; color: #555; margin-top: 4px; }

/* 连接线基础（未到达状态） */
.step-line {
  position: absolute; top: 12px; left: 50%; width: 100%; height: 2px;
  background: #777; z-index: 0; transition: all 0.3s;
}

/* 当前进行中 (is-process) */
.is-process .step-circle { border-color: var(--m-gold); background: #fff; color: var(--m-gold); }
.is-process .step-title { color: var(--m-gold); }
.is-process .step-desc { color: var(--m-text-2); }
/* 当前进行中，前方的线是金色的 */
.is-process .step-line { background: var(--m-gold); }

/* 已经完成 (is-finish) */
.is-finish .step-circle { border-color: var(--m-gold); background: #fff; color: #12100E; }
.is-finish .step-circle .van-icon {
  color: var(--m-gold) !important;
  font-weight: bold;
}
.is-finish .step-title { color: var(--m-gold); }
.is-finish .step-desc { color: var(--m-text-2); }
/* 已经完成的线是金色的 */
.is-finish .step-line { background: var(--m-gold); }

.cancel-box { padding: 20px; margin: 0 16px 16px; color: #C0705C; font-size: 14px; text-align: center; border: 1px dashed rgba(192, 112, 92, 0.3); border-radius: 6px; }

.card-footer { display: flex; justify-content: flex-end; gap: 12px; padding: 12px 16px; border-top: 1px solid rgba(255,255,255,0.05); }

.btn-custom {
  background: transparent !important;
  color: var(--m-gold) !important;
  border: 1px solid var(--m-gold) !important;
  padding: 0 16px;
}
.btn-custom:active { background: rgba(213, 169, 88, 0.1) !important; }

/* 弹窗通用样式 */
.dark-popup { background-color: #1A1816 !important; }
.popup-header { display: flex; justify-content: space-between; align-items: center; padding: 16px 20px; border-bottom: 1px solid rgba(255,255,255,0.05); color: var(--m-text); }
.popup-header h3 { margin: 0; font-size: 18px; }
.popup-content { padding: 16px 20px; color: var(--m-text); height: calc(100% - 60px); overflow-y: auto; }

.detail-row { display: flex; justify-content: space-between; font-size: 14px; margin-bottom: 12px; color: var(--m-muted); }
.detail-row span:last-child { color: var(--m-text); }
.divider { text-align: center; margin: 24px 0 16px; font-size: 13px; color: var(--m-muted); position: relative; }
.divider::before, .divider::after { content: ''; position: absolute; top: 50%; width: 30%; height: 1px; background: rgba(255,255,255,0.1); }
.divider::before { left: 0; } .divider::after { right: 0; }
.dish-scroll-area { max-height: 240px; overflow-y: auto; margin-bottom: 16px; }
.detail-dish { display: flex; justify-content: space-between; font-size: 14px; margin-bottom: 12px; }
.multiply { color: var(--m-muted); font-size: 12px; }
.total-price { text-align: right; font-size: 14px; padding-top: 16px; border-top: 1px dashed rgba(255,255,255,0.1); color: var(--m-muted); }
.total-price span { color: var(--m-gold); font-size: 22px; font-weight: bold; font-family: Georgia, serif; }
.detail-remark { margin-top: 16px; font-size: 13px; color: var(--m-gold); background: rgba(213, 169, 88, 0.1); padding: 12px; border-radius: 6px; }

.review-content { text-align: center; }
.rate-tip { color: var(--m-muted); font-size: 14px; margin-bottom: 16px; }
.review-input { background: #12100E; border-radius: 8px; margin-top: 20px; }
:deep(.review-input .van-field__control) { color: var(--m-text); }

.view-rate-row { display: flex; align-items: center; gap: 10px; margin-bottom: 16px; color: var(--m-muted); font-size: 14px; }
.review-text-box { background: #12100E; padding: 16px; border-radius: 8px; color: var(--m-text); font-size: 14px; line-height: 1.6; margin-bottom: 20px; }
.reply-box { background: rgba(213, 169, 88, 0.1); border-left: 3px solid var(--m-gold); padding: 16px; border-radius: 4px; }
.reply-title { color: var(--m-gold); font-size: 13px; margin: 0 0 8px 0; font-weight: bold; }
.reply-text { color: var(--m-text-2); font-size: 14px; line-height: 1.6; margin: 0; }
.no-reply { color: #666; font-size: 13px; text-align: center; padding: 10px 0; }

.btn-eval {
  background: #409EFF !important; /* 清爽的蓝色 */
  color: #ffffff !important;      /* 白色文字 */
  border: none !important;
  padding: 0 16px;
  font-weight: 500;
}

/* 点击时稍微变浅一点的蓝色反馈 */
.btn-eval:active {
  background: #66b1ff !important;
}

</style>