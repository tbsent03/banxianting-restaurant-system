<template>
  <div class="my-orders-page">
    <div class="container">
      <h2 class="page-title">我的订单</h2>

      <div v-if="listData.length === 0" class="empty-box">
        <el-empty description="您还没有下过单哦"></el-empty>
        <el-button type="primary" class="go-btn" @click="$router.push('/menu')">去点菜</el-button>
      </div>

      <div v-else class="order-list">
        <div class="order-card" v-for="order in listData" :key="order.id">
<!--          卡片头部：单号与状态-->
          <div class="card-header">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <el-tag :type="getStatusType(order.status)" effect="dark">
              {{ order.status }}
            </el-tag>
          </div>

<!--          卡片内容区：进度条展示-->
          <div class="card-body">
            <div class="info-row">
              <span>下单时间：{{ order.createTime }}</span>
              <span class="amount">合计：¥{{ order.totalAmount }}</span>
            </div>

            <!-- 状态时间轴（如果是已取消，就不显示进度条） -->
            <div class="timeline-box" v-if="order.status !== '已取消'">
              <el-steps :active="getStepActive(order.status)" finish-status="success" align-center>
                <el-step title="已下单" description="等待餐厅接单"></el-step>
                <el-step title="制作中" description="后厨正在准备"></el-step>
                <el-step title="已上菜" description="请慢用"></el-step>
                <el-step title="已完成" description="欢迎再次光临"></el-step>
              </el-steps>
            </div>

            <div class="cancel-box" v-else>
              该订单已取消
            </div>
          </div>

          <!-- 卡片底部：操作 -->
          <div class="card-footer">
            <!-- 已完成状态才显示操作  -->
            <template v-if="order.status === '已完成'">
              <!-- 如果已有评价ID，显示“查看评价” -->
              <el-button
                  v-if="order.reviewId"
                  plain
                  style="margin-right: 15px; border-color: #888; color: #ccc;"
                  @click="openViewReview(order)"
              >
                查看评价
              </el-button>
              <!-- 如果没有评价ID，显示“去评价” -->
              <el-button
                  v-else
                  type="warning"
                  plain
                  class="review-btn"
                  @click="openReviewDialog(order)"
                  style="margin-right: 15px; border-color: #D5A958; color: #D5A958;"
              >
                去评价
              </el-button>
            </template>

            <el-button plain class="detail-btn" @click="viewDetail(order.orderNo)">查看详情</el-button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-box" v-if="pageInfo.total > 0">
        <el-pagination background layout="prev, pager, next" :total="pageInfo.total"
                       :page-size="pageInfo.pageSize" :current-page="pageInfo.pageNum" @current-change="currentChange" />
      </div>
    </div>
    <!-- 前台订单详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="订单详情" size="400px" direction="rtl">
      <div v-if="currentOrder" class="front-order-detail">
        <div class="detail-header">
          <span class="order-no">{{ currentOrder.orderNo }}</span>
          <el-tag :type="getStatusType(currentOrder.status)" effect="dark">{{ currentOrder.status }}</el-tag>
        </div>

        <div class="detail-info">
          <p><span>订单类型：</span>{{ currentOrder.orderType }}</p>
          <p><span>联系人：</span>{{ currentOrder.contactName }} ({{ currentOrder.contactTel }})</p>
          <p><span>下单时间：</span>{{ currentOrder.createTime }}</p>
        </div>

        <el-divider style="border-color: rgba(255,255,255,0.1);">菜品明细</el-divider>

        <div class="item-list">
          <div class="item-row" v-for="item in currentOrder.items" :key="item.id">
            <span>{{ item.dishName }} <span class="qty">×{{ item.quantity }}</span></span>
            <span class="price">¥{{ item.subtotal }}</span>
          </div>
        </div>

        <div class="detail-total">
          <span>合计</span>
          <span class="price">¥{{ currentOrder.totalAmount }}</span>
        </div>

        <div class="detail-remark" v-if="currentOrder.remark">
          <span>备注：</span>{{ currentOrder.remark }}
        </div>
      </div>
    </el-drawer>

    <!-- 提交评价的弹窗  -->
    <el-dialog v-model="reviewDialogVisible" title="评价本次用餐" width="400px" align-center>
      <div style="text-align: center; margin-bottom: 20px;">
        <p style="color: #888; font-size: 14px; margin-bottom: 10px;">请为本次服务打分</p>
        <el-rate v-model="reviewForm.score" show-score text-color="#D5A958" score-template="{value} 星"></el-rate>
      </div>
      <el-input
          v-model="reviewForm.content"
          type="textarea"
          :rows="4"
          placeholder="菜品口味、服务体验如何？写下您的感受吧（选填）"
      ></el-input>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" class="submit-btn" :loading="reviewSubmitting" @click="submitReview" style="background: #D5A958; border: none; color: #12100E;">提交评价</el-button>
      </template>
    </el-dialog>
    <!-- 查看评价弹窗  -->
    <el-dialog v-model="viewReviewVisible" title="我的评价" width="400px" align-center>
      <div v-if="currentReviewData" class="view-review-box">
        <div style="margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
          <span style="color: #888;">我的评分：</span>
          <el-rate v-model="currentReviewData.reviewScore" disabled text-color="#D5A958"></el-rate>
        </div>
        <div style="background: #12100E; padding: 15px; border-radius: 6px; margin-bottom: 20px;">
          <p style="color: #F7F3EC; font-size: 14px; line-height: 1.6;">
            {{ currentReviewData.reviewContent || '（未填写文字内容）' }}
          </p>
        </div>

        <!-- 商家回复区 -->
        <div v-if="currentReviewData.reviewReply" class="reply-box">
          <p style="color: #D5A958; font-size: 13px; margin-bottom: 5px;">半闲庭官方回复：</p>
          <p style="color: #A39F9A; font-size: 14px; line-height: 1.6;">{{ currentReviewData.reviewReply }}</p>
        </div>
        <div v-else style="color: #666; font-size: 13px; text-align: center; padding: 10px 0;">
          商家还未回复，请耐心等待。
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from "@/utils/http.js"
import { ElMessage } from 'element-plus'

const router = useRouter()
const listData = ref([])
const pageInfo = ref({ pageNum: 1, pageSize: 5, total: 0 })
const drawerVisible = ref(false)
const currentOrder = ref(null)
// 评价相关的状态
const reviewDialogVisible = ref(false)
const reviewSubmitting = ref(false)
const reviewForm = ref({
  orderId: null,
  score: 5,
  content: ''
})
// 查看评价的状态和逻辑
const viewReviewVisible = ref(false)
const currentReviewData = ref(null)


onMounted(() => {
  getMyOrders()
})

function getMyOrders() {
  request.get('/order/myPage', {
    params: { pageNum: pageInfo.value.pageNum, pageSize: pageInfo.value.pageSize }
  }).then(res => {
    if(res.code === 200) {
      listData.value = res.data.list
      pageInfo.value.total = res.data.total
    }
  })
}

function openViewReview(order) {
  currentReviewData.value = order
  viewReviewVisible.value = true
}
function currentChange(num) {
  pageInfo.value.pageNum = num
  getMyOrders()
}

// 映射状态颜色
function getStatusType(status) {
  if (status === '待接单') return 'warning'
  if (status === '制作中') return 'primary'
  if (status === '已上菜') return 'success'
  if (status === '已完成') return 'info'
  return 'info'
}

// 映射时间轴进度 (0开始, 3结束)
function getStepActive(status) {
  if (status === '待接单') return 0
  if (status === '制作中') return 1
  if (status === '已上菜') return 2
  if (status === '已完成') return 3
  return 1
}

function viewDetail(orderNo) {
  request.get(`/order/detail/${orderNo}`).then(res => {
    if (res.code === 200) {
      currentOrder.value = res.data
      drawerVisible.value = true // 打开抽屉
    }
  })
}

// 打开评价弹窗
function openReviewDialog(order) {
  reviewForm.value.orderId = order.id
  reviewForm.value.score = 5
  reviewForm.value.content = ''
  reviewDialogVisible.value = true
}

// 提交评价
function submitReview() {
  if (!reviewForm.value.score) {
    return ElMessage.warning('请至少给出 1 星评分')
  }

  reviewSubmitting.value = true
  request.post('/review/add', reviewForm.value).then(res => {
    reviewSubmitting.value = false
    if (res && res.code === 200) {
      ElMessage.success('感谢您的评价！')
      reviewDialogVisible.value = false
    } else {
      ElMessage.error(res.msg || '评价失败')
    }
  }).catch(() => {
    reviewSubmitting.value = false
  })
}
</script>

<style scoped>
.my-orders-page { background: #12100E; min-height: 100vh; padding: 60px 0; color: #F7F3EC; }
.container { max-width: 900px; margin: 0 auto; padding: 0 40px; }
.page-title { font-family: 'STSong', serif; font-size: 32px; margin-bottom: 40px; border-bottom: 1px solid rgba(255,255,255,0.1); padding-bottom: 20px; letter-spacing: 2px; }

.empty-box { text-align: center; padding: 100px 0; }
.go-btn { background: #D5A958; color: #12100E; border: none; margin-top: 20px; padding: 12px 30px; font-size: 16px; font-weight: bold; cursor: pointer;}

.order-card { background: #1A1816; border: 1px solid rgba(255,255,255,0.05); border-radius: 8px; margin-bottom: 30px; overflow: hidden; }
.card-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 30px; background: #22201E; border-bottom: 1px solid rgba(255,255,255,0.05); }
.order-no { font-family: Georgia, serif; font-size: 16px; color: #A39F9A; letter-spacing: 1px; }
.card-header :deep(.el-tag) { font-size: 15px; height: 32px; line-height: 30px; padding: 0 15px; }

.card-body { padding: 30px; }
.info-row { display: flex; justify-content: space-between; color: #888; font-size: 14px; margin-bottom: 30px; }
.amount { color: #D5A958; font-size: 18px; font-weight: bold; font-family: Georgia, serif; }

.timeline-box { padding: 10px 0; }
:deep(.el-step__title) { font-size: 15px; color: #A39F9A; font-weight: normal; }
:deep(.el-step__title.is-success), :deep(.el-step__title.is-process) { color: #D5A958; font-weight: bold; }
:deep(.el-step__description) { font-size: 13px; color: #666; }
:deep(.el-step__head.is-success), :deep(.el-step__head.is-process) { color: #D5A958; border-color: #D5A958; }

.cancel-box { color: #C0705C; font-size: 16px; font-weight: bold; text-align: center; padding: 20px 0; border: 1px dashed rgba(192, 112, 92, 0.3); border-radius: 6px; }

.card-footer { padding: 20px 30px; border-top: 1px solid rgba(255,255,255,0.05); text-align: right; }
.detail-btn { background: transparent; color: #D5A958; border-color: #D5A958; padding: 8px 20px; }
.detail-btn:hover { background: rgba(213, 169, 88, 0.1); }

.pagination-box { display: flex; justify-content: center; margin-top: 40px; }
:deep(.el-pagination.is-background .el-pager li) { background: #1A1816; color: #888; }
:deep(.el-pagination.is-background .el-pager li.is-active) { background: #D5A958; color: #12100E; }

/*  前台详情抽屉的暗黑样式  */
:deep(.el-drawer) {
  background-color: #1A1816; /* 抽屉暗色背景 */
  color: #F7F3EC;
}
:deep(.el-drawer__header) {
  color: #F7F3EC;
  margin-bottom: 0;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  padding-bottom: 20px;
}
:deep(.el-drawer__close-btn) { color: #F7F3EC; }

.front-order-detail { padding: 0 10px; }
.detail-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; }
.detail-header .order-no { font-size: 18px; font-family: Georgia, serif; color: #D5A958; }
.detail-info p { margin: 12px 0; font-size: 14px; color: #A39F9A; }
.detail-info span { color: #666; }

.item-list { background: rgba(255,255,255,0.02); padding: 20px; border-radius: 6px; margin-bottom: 20px; }
.item-row { display: flex; justify-content: space-between; margin-bottom: 15px; font-size: 15px; font-family: 'STSong', 'STZhongsong', 'Songti SC', 'Noto Serif SC', serif;
  letter-spacing: 1px;}
.item-row:last-child { margin-bottom: 0; }
.item-row .qty { color: #888; font-size: 13px; margin-left: 10px; }
.item-row .price { color: #D5A958; font-family: Georgia, 'Times New Roman', serif;font-size: 16px; }

.detail-total { display: flex; justify-content: space-between; font-size: 20px; font-weight: bold; border-top: 1px dashed rgba(255,255,255,0.1); padding-top: 20px; }
.detail-total .price { color: #D5A958; font-size: 24px; }

.detail-remark { margin-top: 20px; font-size: 14px; color: #D5A958; background: rgba(213, 169, 88, 0.1); padding: 15px; border-radius: 6px; }
@media (max-width: 768px) {
  .container { padding: 0 20px; }
  .card-header { padding: 15px; flex-direction: column; align-items: flex-start; gap: 10px; }
  .card-body { padding: 15px; }
  .info-row { flex-direction: column; gap: 10px; align-items: flex-start; }

  /* 手机端把横向时间轴变成紧凑版 */
  .timeline-box :deep(.el-step__title) { font-size: 11px; }
  .timeline-box :deep(.el-step__description) { font-size: 10px; display: none; /* 手机屏幕窄，隐藏描述文字 */ }
  .timeline-box :deep(.el-step__icon) { width: 20px; height: 20px; font-size: 12px; }
}
/* 商家回复样式 */
.reply-box {
  background: rgba(213, 169, 88, 0.1);
  border-left: 3px solid #D5A958;
  padding: 15px;
  border-radius: 4px;
}
</style>