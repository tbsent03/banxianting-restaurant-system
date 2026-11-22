<template>
  <div class="reservation-manage">
    <div class="breadcrumb-bar">经营 / 在线订位</div>
    <h2 class="page-title">在线订位</h2>

    <!-- 顶部搜索与 Tab 区 -->
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" class="search-form">
        <el-form-item>
          <el-input v-model="searchForm.contactName" placeholder="联系人姓名" clearable style="width: 180px;" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="searchForm.contactTel" placeholder="联系电话" clearable style="width: 180px;" />
        </el-form-item>
        <el-form-item>
          <el-button class="search-btn" @click="search">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 状态 Tabs -->
      <div class="status-tabs">
        <div
            v-for="tab in tabs"
            :key="tab.value"
            :class="['status-tab', { active: activeStatus === tab.value }]"
            @click="handleTabClick(tab.value)"
        >
          {{ tab.label }} <span class="count">{{ getCount(tab.value) }}</span>
        </div>
      </div>
    </el-card>

    <!-- 表格区 -->
    <el-card shadow="never" style="margin-top: 15px;">
      <el-table :data="listData" style="width: 100%" stripe row-key="id">
        <el-table-column prop="reserveDate" label="预订日期" width="120" />
        <el-table-column prop="reserveTime" label="时间" width="100" />
        <el-table-column prop="contactName" label="联系人" width="120" />
        <el-table-column prop="contactTel" label="联系电话" width="140" />
        <el-table-column prop="peopleCount" label="人数" width="80" />
        <el-table-column label="包厢" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.needRoom === 1" type="danger" size="small">是</el-tag>
            <span v-else>否</span>
          </template>
        </el-table-column>
        <el-table-column prop="tableNo" label="分配桌位" width="120">
          <template #default="scope">
            <span v-if="scope.row.tableNo" style="color: #C6A15B; font-weight: bold;">{{ scope.row.tableNo }}</span>
            <span v-else style="color: #999;">未分配</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <!-- 待确认状态：显示确认和拒绝 -->
            <template v-if="scope.row.status === '待确认'">
              <el-button link type="primary" @click="openAssignDialog(scope.row)">确认预订</el-button>
              <el-button link type="danger" @click="updateStatus(scope.row.id, '已取消')">拒绝</el-button>
            </template>
            <!-- 已确认状态：显示到店 -->
            <template v-else-if="scope.row.status === '已确认'">
              <el-button link type="success" @click="updateStatus(scope.row.id, '已到店')">标记到店</el-button>
              <el-button link type="danger" @click="updateStatus(scope.row.id, '已取消')">取消</el-button>
            </template>
            <!-- 其他状态不显示操作 -->
            <span v-else style="color: #ccc; font-size: 13px;">无操作</span>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 20px; text-align: right;">
        <el-pagination background layout="total, prev, pager, next" :total="pageInfo.total"
                       :page-size="pageInfo.pageSize" :current-page="pageInfo.pageNum" @current-change="currentChange" />
      </div>
    </el-card>

    <!-- 分配桌位弹窗 -->
    <el-dialog v-model="assignDialogVisible" title="分配桌位并确认" width="400px">
      <div style="margin-bottom: 20px; color: #606266;">
        请为 <b>{{ currentReservation?.contactName }}</b> ({{ currentReservation?.peopleCount }}人) 分配一个空闲桌位：
      </div>
      <el-select v-model="selectedTableId" placeholder="请选择空闲桌位" style="width: 100%">
        <el-option
            v-for="table in availableTables"
            :key="table.id"
            :label="`${table.tableNo} (${table.area} - ${table.capacity}人)`"
            :value="table.id"
        />
      </el-select>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" class="search-btn" @click="confirmReservation" :disabled="!selectedTableId">确认分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from "@/utils/http.js"
import { ElMessage } from 'element-plus'

const listData = ref([])
const allDataForCount = ref([]) // 用于计算各状态数量
const pageInfo = ref({ pageNum: 1, pageSize: 10, total: 0 })
const searchForm = ref({ contactName: '', contactTel: '' })
const activeStatus = ref('全部')

// Tab 配置
const tabs = [
  { label: '全部', value: '全部' },
  { label: '待确认', value: '待确认' },
  { label: '已确认', value: '已确认' },
  { label: '已到店', value: '已到店' },
  { label: '已取消', value: '已取消' }
]

// 分配桌位相关
const assignDialogVisible = ref(false)
const currentReservation = ref(null)
const availableTables = ref([])
const selectedTableId = ref(null)

onMounted(() => {
  getPageList()
  getCounts()
})

// 获取列表
function getPageList() {
  const params = {
    contactName: searchForm.value.contactName,
    contactTel: searchForm.value.contactTel,
    pageNum: pageInfo.value.pageNum,
    pageSize: pageInfo.value.pageSize
  }
  if (activeStatus.value !== '全部') params.status = activeStatus.value

  request.get('/reservation/page', { params }).then(res => {
    if (res && res.data) {
      listData.value = res.data.list || []
      pageInfo.value.total = res.data.total || 0
    }
  })
}

// 获取所有数据用于统计角标
function getCounts() {
  request.get('/reservation/page', { params: { pageNum: 1, pageSize: 9999 } }).then(res => {
    if (res.data) allDataForCount.value = res.data.list
  })
}

// 计算各状态数量
function getCount(status) {
  if (status === '全部') return allDataForCount.value.length
  return allDataForCount.value.filter(item => item.status === status).length
}

function handleTabClick(status) {
  activeStatus.value = status
  pageInfo.value.pageNum = 1
  getPageList()
}

function search() {
  pageInfo.value.pageNum = 1
  getPageList()
}

function resetSearch() {
  searchForm.value = { contactName: '', contactTel: '' }
  search()
}

function currentChange(num) {
  pageInfo.value.pageNum = num
  getPageList()
}

// 打开分配桌位弹窗
function openAssignDialog(row) {
  currentReservation.value = row
  selectedTableId.value = null

  //请求符合日期的空闲桌位
  request.get('/table/available', {
    params: {
      reserveDate: row.reserveDate,
      reserveTime: row.reserveTime,
      peopleCount: row.peopleCount
    }
  }).then(res => {
    if (res.code === 200) {
      availableTables.value = res.data
      assignDialogVisible.value = true
    }
  })
}

// 确认分配桌位
function confirmReservation() {
  if (!selectedTableId.value) return ElMessage.warning('请选择桌位')

  const payload = {
    id: currentReservation.value.id,
    status: '已确认',
    tableId: selectedTableId.value
  }

  request.put('/reservation/update', payload).then(res => {
    if (res.code === 200) {
      ElMessage.success('预订已确认，桌位已锁定')
      assignDialogVisible.value = false
      getPageList()
      getCounts()
    }
  })
}

// 快捷更新状态（到店、取消）
function updateStatus(id, status) {
  const payload = { id, status }
  request.put('/reservation/update', payload).then(res => {
    if (res.code === 200) {
      ElMessage.success('状态更新成功')
      getPageList()
      getCounts()
    }
  })
}

// 状态颜色映射
function getStatusType(status) {
  if (status === '待确认') return 'warning'
  if (status === '已确认') return 'primary'
  if (status === '已到店') return 'success'
  if (status === '已取消') return 'info'
  return 'info'
}
</script>

<style scoped>
.breadcrumb-bar { width: 100%; background-color: #fff; padding: 15px 30px; font-size: 14px; color: #666; border-bottom: 1px solid #eee; box-sizing: border-box; margin-bottom: 20px; }
.page-title { font-size: 24px; color: #333; margin-bottom: 20px; font-weight: 600; }

.filter-card { background: #fff; border: none; }
.search-form { margin-bottom: 20px; }
.search-btn { background-color: #C6A15B !important; color: #101010 !important; border-color: #C6A15B !important; font-weight: bold;}

/* 状态 Tabs 样式 */
.status-tabs { display: flex; gap: 15px; flex-wrap: wrap; }
.status-tab { display: flex; align-items: center; gap: 8px; padding: 8px 20px; border: 1px solid #dcdfe6; border-radius: 14px; font-size: 14px; color: #606266; background-color: #fff; cursor: pointer; transition: all 0.3s; }
.status-tab .count { font-weight: bold; }
.status-tab:hover, .status-tab.active { background-color: #101010; border-color: #101010; color: #C6A15B; }
.status-tab:hover .count, .status-tab.active .count { color: #C6A15B; }
</style>