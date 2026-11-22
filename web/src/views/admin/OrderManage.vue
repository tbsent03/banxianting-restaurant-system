<template>
  <div class="order-manage">
    <div class="breadcrumb-bar">
      经营 / 订单管理
    </div>
    <h2 class="page-title">订单管理</h2>
    <!-- 顶部搜索区与 Tab 区 -->
    <el-card shadow="never" class="filter-card">
      <!-- 1. 搜索栏在上 -->
      <el-form :inline="true" class="search-form">
        <el-form-item>
          <el-input v-model="searchForm.orderNo" placeholder="订单号/手机号" clearable style="width: 220px;" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px;" @change="handleStatusSelect">
            <el-option label="全部状态" value="" />
            <el-option label="待接单" value="待接单" />
            <el-option label="制作中" value="制作中" />
            <el-option label="已上菜" value="已上菜" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>

        <el-form-item label="">
          <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="~"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 260px;"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item>
          <el-button class="search-btn" @click="search">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="status-tabs">
        <div
          :class="['status-tab',{active: activeStatus === '全部' }]"
          @click="handleTabClick('全部')"
          >
          全部 <span class="count">{{ countAll }}</span>
        </div>
        <div
            :class="['status-tab', { active: activeStatus === '待接单' }]"
            @click="handleTabClick('待接单')"
        >
          待接单 <span class="count">{{ countPending }}</span>
        </div>
        <div
            :class="['status-tab', { active: activeStatus === '制作中' }]"
            @click="handleTabClick('制作中')"
        >
          制作中 <span class="count">{{ countMaking }}</span>
        </div>
        <div
            :class="['status-tab', { active: activeStatus === '已上菜' }]"
            @click="handleTabClick('已上菜')"
        >
          已上菜 <span class="count">{{ countServed }}</span>
        </div>
        <div
            :class="['status-tab', { active: activeStatus === '已完成' }]"
            @click="handleTabClick('已完成')"
        >
          已完成 <span class="count">{{ countFinished }}</span>
        </div>
      </div>

    </el-card>

    <!-- 表格区 -->
    <el-card shadow="never" style="margin-top: 15px;">
      <el-table :data="listData" style="width: 100%" stripe row-key="id">
        <el-table-column prop="orderNo" label="订单号" width="200"></el-table-column>
        <el-table-column prop="orderType" label="类型" width="100"></el-table-column>

        <el-table-column prop="tableNo" label="桌号" width="100">
          <template #default="scope">
            <span v-if="scope.row.tableNo" style="color: #C6A15B; font-weight: bold;">{{ scope.row.tableNo }}</span>
            <span v-else>自取/外卖</span>
          </template>
        </el-table-column>

        <el-table-column prop="contactName" label="联系人" width="120"></el-table-column>
        <el-table-column prop="totalAmount" label="金额">
          <template #default="scope"> ¥ {{ scope.row.totalAmount }} </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button link type="primary" @click="openDrawer(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 20px; text-align: right;">
        <el-pagination background layout="total, prev, pager, next" :total="pageInfo.total"
                       :page-size="pageInfo.pageSize" :current-page="pageInfo.pageNum" @current-change="currentChange" />
      </div>
    </el-card>

    <!-- 3. 右侧抽屉：加宽至 600px -->
    <el-drawer v-model="drawerVisible" title="订单详情" size="600px"
               style="font-weight: 600">
      <div v-if="currentOrder" class="order-detail">
        <div class="detail-header">
          <span class="order-no">{{ currentOrder.orderNo }}</span>
          <el-tag :type="getStatusType(currentOrder.status)">
            {{ currentOrder.status }}
          </el-tag>
        </div>

        <div class="detail-info">
          <p><span>订单类型：</span>{{ currentOrder.orderType }} · 2 位</p>
          <p v-if="currentOrder.tableNo"><span>就餐桌号：</span>{{ currentOrder.tableNo }}</p>
          <p><span>联系人：</span>{{ currentOrder.contactName }} · {{ currentOrder.contactTel }}</p>
          <p><span>下单时间：</span>{{ currentOrder.createTime }}</p>
        </div>

        <el-divider>菜品明细</el-divider>

        <!-- 4. 修复明细显示（强制赋予背景色和黑色字体） -->
        <div class="item-list">
          <div class="item-row" v-for="item in currentOrder.items" :key="item.id">
            <span class="item-name">{{ item.dishName }} <span class="item-qty">× {{ item.quantity }}</span></span>
            <span class="item-price">¥ {{ item.subtotal }}</span>
          </div>
          <!-- 如果明细为空，给个提示 -->
          <div v-if="!currentOrder.items || currentOrder.items.length === 0" style="text-align: center; color: #999; padding: 20px;">
            暂无菜品明细数据
          </div>
        </div>

        <div class="detail-total">
          <span class="total-label">合计</span>
          <span class="price">¥ {{ currentOrder.totalAmount }}</span>
        </div>

        <div class="detail-remark" v-if="currentOrder.remark">
          <span>备注：</span>{{ currentOrder.remark }}
        </div>

        <!-- 底部操作按钮 -->
        <div class="drawer-footer" v-if="currentOrder.status !== '已完成' && currentOrder.status !== '已取消'">
          <el-button class="btn-cancel" @click="updateStatus('已取消')" v-if="currentOrder.status === '待接单'">取消订单</el-button>

          <el-button class="btn-primary" @click="updateStatus(getNextStatus(currentOrder.status))" v-if="currentOrder.status === '待接单'">
            接单并通知后厨
          </el-button>

          <el-button class="btn-primary" @click="updateStatus(getNextStatus(currentOrder.status))" v-if="currentOrder.status === '制作中'">
            确认已上菜
          </el-button>

          <el-button class="btn-primary" @click="updateStatus(getNextStatus(currentOrder.status))" v-if="currentOrder.status === '已上菜'">
            完成结账
          </el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from "@/utils/http.js"
import { ElMessage } from 'element-plus'

const activeStatus = ref('全部')
const listData = ref([])
const pageInfo = ref({ pageNum: 1, pageSize: 10, total: 0 })

// 👇 修改：searchForm 增加 status 和 dateRange
const searchForm = ref({
  orderNo: '',
  status: '',
  dateRange: []
})

const drawerVisible = ref(false)
const currentOrder = ref(null)
const allOrders = ref([])

onMounted(() => {
  getPageList()
  getStatCounts()
})

function getStatCounts() {
  request.get('/order/page', { params: { pageNum: 1, pageSize: 9999 } }).then(res => {
    if (res && res.data && res.data.list) allOrders.value = res.data.list
  })
}

const countAll = computed(() => allOrders.value.length)
const countPending = computed(() => allOrders.value.filter(o => o.status === '待接单').length)
const countMaking = computed(() => allOrders.value.filter(o => o.status === '制作中').length)
const countServed = computed(() => allOrders.value.filter(o => o.status === '已上菜').length)
const countFinished = computed(() => allOrders.value.filter(o => o.status === '已完成').length)

function getPageList() {
  const params = {
    orderNo: searchForm.value.orderNo,
    status: activeStatus.value === '全部' ? searchForm.value.status : activeStatus.value,
    pageNum: pageInfo.value.pageNum,
    pageSize: pageInfo.value.pageSize
  }

  // 👇 处理日期区间参数
  if (searchForm.value.dateRange && searchForm.value.dateRange.length === 2) {
    params.startDate = searchForm.value.dateRange[0]
    params.endDate = searchForm.value.dateRange[1]
  }

  request.get('/order/page', { params }).then(res => {
    if (res && res.data) {
      listData.value = res.data.list || []
      pageInfo.value.total = res.data.total || 0
    }
  })
}

// 👇 新增：下拉框和 Tab 的联动
function handleStatusSelect(val) {
  activeStatus.value = val ? val : '全部'
  pageInfo.value.pageNum = 1
  getPageList()
}

function handleTabClick(status) {
  activeStatus.value = status
  searchForm.value.status = status === '全部' ? '' : status
  pageInfo.value.pageNum = 1
  getPageList()
}

function search() {
  pageInfo.value.pageNum = 1
  getPageList()
}

// 👇 新增：重置搜索
function resetSearch() {
  searchForm.value = { orderNo: '', status: '', dateRange: [] }
  activeStatus.value = '全部'
  pageInfo.value.pageNum = 1
  getPageList()
}

function currentChange(num) {
  pageInfo.value.pageNum = num
  getPageList()
}

function openDrawer(row) {
  currentOrder.value = null
  request.get(`/order/detail/${row.orderNo}`).then(res => {
    if (res.code === 200) {
      currentOrder.value = res.data
      drawerVisible.value = true
    }
  })
}

function getNextStatus(currentStatus) {
  if (currentStatus === '待接单') return '制作中'
  if (currentStatus === '制作中') return '已上菜'
  if (currentStatus === '已上菜') return '已完成'
  return currentStatus
}

function updateStatus(nextStatus) {
  request.post('/order/updateStatus', { id: currentOrder.value.id, status: nextStatus }).then(res => {
    if (res.code === 200) {
      ElMessage.success('状态更新成功')
      drawerVisible.value = false
      getPageList()
      getStatCounts()
    }
  })
}

function getStatusType(status) {
  if (status === '待接单') return 'warning'
  if (status === '制作中') return 'primary'
  if (status === '已上菜') return 'success'
  if (status === '已完成') return 'info'
  return 'info'
}
</script>

<style scoped>
.breadcrumb-bar{
  width: 100%;
  background-color: #fff;
  padding: 15px 30px;
  font-size: 14px;
  color: #666;
  border-bottom: 1px solid #eee;
  box-sizing: border-box;
  margin-bottom: 20px;
}
/*主体内容区加内边距，防止贴着屏幕边缘*/
.main-content{
  padding: 0 30px;
}
/* 页面大标题基础样式 */
.page-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
  font-weight: 600;
}

.filter-card { background: #fff; }
.search-form { margin-bottom: 20px; }

/*状态按钮组样式（外方框效果）*/
.status-tabs{
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}
.status-tab{
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  border:1px solid #dcdfe6;
  border-radius: 14px;
  font-size: 14px;
  color: #606266;
  background-color: #fff;
  transition: all 0.3s;
}
.status-tab .count{
  font-weight: bold;
}
/*鼠标悬浮效果（黑底金字）*/
.status-tab:hover {
  background-color: #101010;
  border-color: #101010;
  color: #C6A15B;
  /*color: #fff;*/
}
/* 悬浮时里面的数字也变成金色 */
.status-tab:hover .count {
  color: #C6A15B;
}
.status-tab .active{
  background-color: #101010;
  border-color: #101010;
  color: #fff;
}
.status-tab.active .count {
  color: #C6A15B;
}

.search-btn{
  background-color: #C6A15B !important;
  color: #101010 !important;
  border-color: #C6A15B !important;
  font-weight: 400;
}

/* 抽屉整体布局 */
.order-detail { display: flex; flex-direction: column; height: 100%; }
.detail-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.order-no { font-size: 18px; font-weight: 300; color: #333; }

/* 订单基础信息 */
.detail-info p { margin: 10px 0; font-size: 14px; color: #555; }
.detail-info span { color: #999; width: 80px; display: inline-block; }

/* 菜品明细列表 */
.item-list { background: #f9f9f9; padding: 15px; border-radius: 4px; margin-bottom: 20px; }
.item-row { display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 15px; color: #333; }
.item-row:last-child { margin-bottom: 0; }
.item-name { font-weight: 700;font-family: 'STSong', 'STZhongsong', 'Songti SC', 'Noto Serif SC', serif;
  font-size: 16px; }
.item-qty { color: #999; font-size: 13px; margin-left: 10px; }
.item-price { color: #C6A15B; font-family: Georgia, serif; }

/* 合计 */
.detail-total { display: flex; justify-content: space-between; font-size: 20px; font-weight: bold; border-top: 1px solid #eee; padding-top: 20px; }
.detail-total .price { color: #C6A15B; font-size: 24px; }

/* 备注 */
.detail-remark { margin-top: 20px; font-size: 14px; color: #666; background: #fdf8e8; padding: 15px; border-radius: 4px; }

/* 底部按钮区 */
.drawer-footer {
  margin-top: auto;
  padding-top: 20px;
  display: flex;
  justify-content: space-between;
  gap: 15px;
  border-top: 1px solid #eee;
}
.drawer-footer .el-button{
  margin:0 !important;
  height: 46px;
  font-size: 16px;
}

/*订单详情页显示状态大标签*/
.detail-header :deep(.el-tag) {
  height: 34px !important;       /* 调高 */
  line-height: 32px !important;  /* 文字垂直居中 */
  font-size: 15px !important;    /* 字体变大 */
  padding: 0 16px !important;    /* 左右加宽 */
  font-weight: 600 !important;   /* 字重加粗，更有质感 */
  border-radius: 6px !important; /* 稍微大一点的圆角 */
}
/*合计文字为金色*/
.total-label {
  color: #101010 !important; /* 纯黑 */
}
/*取消订单按钮：白底灰字*/
.btn-cancel{
  flex: 1;
  background-color: #fff !important;
  border-color: #dcdfe6 !important;
  color: #606266;
}
.btn-cancel :hover{
  border-color: #C6A15B !important;
  color: #C6A15B !important;
}
/* 核心操作按钮：金底黑字 */
.btn-primary {
  flex: 2; /* 占 2 份宽度，比取消按钮宽 */
  background-color: #C6A15B !important;
  border-color: #C6A15B !important;
  color: #101010 !important;
  font-weight: 600;
}
.btn-primary:hover {
  background-color: #E5C07B !important;
  border-color: #E5C07B !important;
}
</style>