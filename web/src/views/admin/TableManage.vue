<template>
  <div class="table-manage">
    <div class="breadcrumb-bar">经营 / 桌位与桌码</div>
    <h2 class="page-title">桌位管理</h2>
    <!-- 视图切换 -->
    <div style="margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; background: #fff; padding: 15px; border-radius: 8px;">
      <!-- 左侧：日期与时段选择器 -->
      <div style="display: flex; gap: 15px; align-items: center;">
        <span style="font-size: 14px; color: #606266;">排期查看：</span>
        <el-date-picker
            v-model="floorDate"
            type="date"
            placeholder="选择查看日期"
            value-format="YYYY-MM-DD"
            @change="handleFilterChange"
            style="width: 150px;"
        />
        <el-radio-group v-model="floorPeriod" @change="handleFilterChange">
          <el-radio-button label="午市">午市</el-radio-button>
          <el-radio-button label="晚市">晚市</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 右侧：视图切换 -->
      <el-radio-group v-model="viewMode" size="large">
        <el-radio-button label="table">表格视图</el-radio-button>
        <el-radio-button label="floor">平面图视图</el-radio-button>
      </el-radio-group>
    </div>

    <!--  表格视图 (包起来，通过 v-show 控制显示)  -->
    <div v-show="viewMode === 'table'">
      <el-card shadow="never" class="filter-card">
        <el-form :inline="true">
          <el-form-item>
            <el-input v-model="searchForm.tableNo" placeholder="搜索桌号" clearable style="width: 180px;" />
          </el-form-item>
          <el-form-item>
            <el-select v-model="searchForm.area" placeholder="全部区域" clearable style="width: 140px;">
              <el-option label="大厅" value="大厅" />
              <el-option label="包厢" value="包厢" />
              <el-option label="露台" value="露台" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button class="search-btn" @click="search">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" :icon="Plus" @click="add">新增桌位</el-button>
      </el-card>

      <el-card shadow="never" style="margin-top: 15px;">
        <el-table :data="listData" style="width: 100%" stripe row-key="id">
          <el-table-column prop="tableNo" label="桌号" width="150">
            <template #default="scope">
              <span style="font-family: 'STSong', serif; font-size: 18px; font-weight: bold; color: #C6A15B;">{{ scope.row.tableNo }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="area" label="区域" width="120"></el-table-column>
          <el-table-column prop="capacity" label="容纳人数"></el-table-column>
          <el-table-column prop="shape" label="形状">
            <template #default="scope">{{ scope.row.shape === 'circle' ? '圆形' : '方形' }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === '空闲' ? 'success' : scope.row.status === '占用' ? 'danger' : 'warning'">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="260" fixed="right">
            <template #default="scope">
              <el-button link type="primary" @click="showQRCode(scope.row)">查看桌码</el-button>
              <el-button link type="primary" @click="edit(scope.row)">编辑</el-button>
              <el-button link type="danger" @click="deleteOne(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div style="margin-top: 20px; text-align: right;">
          <el-pagination background layout="total, prev, pager, next" :total="pageInfo.total"
                         :page-size="pageInfo.pageSize" :current-page="pageInfo.pageNum" @current-change="currentChange" />
        </div>
      </el-card>
    </div>

    <!--  新增：平面图视图  -->
    <div v-show="viewMode === 'floor'">
      <el-card shadow="never" style="border: none; background-color: #12100E; overflow: hidden;">
        <!-- 平面图画布 -->
        <div class="floor-plan">
          <!-- 循环渲染桌子 -->
          <div
              v-for="table in allTables"
              :key="table.id"
              :class="['table-item', table.shape === 'circle' ? 'is-circle' : 'is-rect', 'status-' + (table.status === '空闲' ? 'free' : table.status === '占用' ? 'busy' : 'reserved')]"
              :style="{ left: table.posX + '%', top: table.posY + '%', ...getTableSize(table) }"
              @mousedown="startDrag($event, table)"
          >
            <div class="table-no">{{ table.tableNo }}</div>
            <div class="table-cap">{{ table.capacity }}人</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="formData.id ? '编辑桌位' : '新增桌位'" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="桌号" required>
          <el-input v-model="formData.tableNo" placeholder="如 A01"></el-input>
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="formData.area">
            <el-option label="大厅" value="大厅" />
            <el-option label="包厢" value="包厢" />
            <el-option label="露台" value="露台" />
          </el-select>
        </el-form-item>
        <el-form-item label="容纳人数">
          <el-input-number v-model="formData.capacity" :min="1" :max="30"></el-input-number>
        </el-form-item>
        <el-form-item label="形状">
          <el-radio-group v-model="formData.shape">
            <el-radio label="rect">方形</el-radio>
            <el-radio label="circle">圆形</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="X轴坐标(%)">
          <el-input-number v-model="formData.posX" :min="0" :max="100"></el-input-number>
        </el-form-item>
        <el-form-item label="Y轴坐标(%)">
          <el-input-number v-model="formData.posY" :min="0" :max="100"></el-input-number>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio label="空闲">空闲</el-radio>
            <el-radio label="占用">占用</el-radio>
            <el-radio label="预留">预留</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">提交</el-button>
      </template>
    </el-dialog>

    <!-- 二维码展示弹窗 -->
    <el-dialog v-model="qrDialogVisible" title="专属桌码" width="350px" align-center>
      <div class="qr-card">
        <h3 class="qr-brand">半闲庭</h3>
        <p class="qr-en">BANXIAN TING</p>
        <img :src="qrCodeUrl" class="qr-img" alt="桌码" />
        <h2 class="qr-table-no">{{ currentTable?.tableNo }} 桌</h2>
        <p class="qr-tip">微信扫码 · 开启味觉之旅</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from "@/utils/http.js"
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import QRCode from 'qrcode' // 👈 引入二维码库

const listData = ref([])
const pageInfo = ref({ pageNum: 1, pageSize: 20, total: 0 })
const searchForm = ref({ tableNo: '', area: '' })

const dialogVisible = ref(false)
const formData = ref({})

const qrDialogVisible = ref(false)
const qrCodeUrl = ref('')
const currentTable = ref(null)

const viewMode = ref('table') // 默认显示表格视图
const allTables = ref([])     // 用于渲染平面图的所有桌子

const floorDate = ref(new Date().toISOString().split('T')[0]) // 默认今天
const floorPeriod = ref('晚市') // 默认晚市

onMounted(() => {
  getPageList()
  getAllTablesForFloorPlan() //  页面加载时拉取全量数据
})

// ================= 拖拽逻辑 =================
let isDragging = false
let draggedTable = null
let startX = 0
let startY = 0
let startPosX = 0
let startPosY = 0
let hasMoved = false

function getPageList() {
  const params = {
    tableNo: searchForm.value.tableNo,
    area: searchForm.value.area,
    pageNum: pageInfo.value.pageNum,
    pageSize: pageInfo.value.pageSize,
    date: floorDate.value,      // 传入日期
    period: floorPeriod.value   // 传入时段
  }
  console.log("请求参数", params)
  request.get('/table/page', { params: params }).then(res => {
    if (res && res.code === 200) {
      listData.value = res.data.list || []
      pageInfo.value.total = res.data.total || 0
    }
  })
}
// 获取全量桌位数据
function getAllTablesForFloorPlan() {
  request.get('/table/list').then(res => {
    if (res.code === 200) {
      allTables.value = res.data
    }
  })
}

function search() { pageInfo.value.pageNum = 1; getPageList() }
function resetSearch() { searchForm.value = { tableNo: '', area: '' }; search() }
function currentChange(num) { pageInfo.value.pageNum = num; getPageList() }

function add() {
  formData.value = { shape: 'rect', status: '空闲', capacity: 2, area: '大厅', posX: 50, posY: 50 }
  dialogVisible.value = true
}
function edit(row) {
  formData.value = { ...row }
  dialogVisible.value = true
}

function submit() {
  const method = formData.value.id ? request.put : request.post
  const url = formData.value.id ? '/table/update' : '/table/add'
  method(url, formData.value).then(res => {
    if (res.code === 200) {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      getPageList()
      getAllTablesForFloorPlan()
    }
  })
}

function deleteOne(row) {
  ElMessageBox.confirm(`确认删除桌位 ${row.tableNo} 吗？`, '警告', { type: 'warning' }).then(() => {
    request.delete('/table/delBatch', { data: [row.id] }).then(res => {
      if (res.code === 200) {
        ElMessage.success('删除成功')
        getPageList()
        getAllTablesForFloorPlan()
      }
    })
  }).catch(() => {})
}

//  生成二维码核心逻辑
function showQRCode(row) {
  currentTable.value = row

  // 获取当前域名
  let origin = window.location.origin;
  //  如果当前是 localhost，为了手机能扫码，可以替换成局域网 IP，或者给个提示
  if (origin.includes('localhost')) {
    // 这里可以写死 IP，或者用弹窗提示用局域网 IP 访问后台
    // 假设 IP 是 192.168.53.42
    // origin = 'http://192.168.53.42:5173';

    // 更好的做法：提示用户
    ElMessage.warning('提示：当前使用的是 localhost，手机扫码可能无法打开，请使用局域网 IP 访问后台');
  }

  // 生成的内容是前台的扫码链接，带上 token 参数
  const scanUrl = `${window.location.origin}/index?table=${row.qrToken}`

  QRCode.toDataURL(scanUrl, {
    width: 260,
    margin: 1,
    color: { dark: '#101010', light: '#FFFFFF' }
  }).then(url => {
    qrCodeUrl.value = url
    qrDialogVisible.value = true
  })
}

// 根据人数和形状动态计算桌子尺寸
function getTableSize(table) {
  const cap = table.capacity || 2;
  // 基础尺寸：2人桌方桌宽 60px，圆桌宽 70px
  let baseSize = table.shape === 'circle' ? 70 : 60;

  // 面积和人数成正比，所以边长和(人数/2)的平方根成正比
  let scale = Math.sqrt(cap / 2);

  // 算出最终宽高
  let size = Math.round(baseSize * scale);

  // 给个上限和下限，防止数字太大或太小破坏页面
  if (size > 160) size = 160;
  if (size < 50) size = 50;

  return {
    width: size + 'px',
    height: size + 'px'
  }
}

function startDrag(e, table) {
  // 只允许左键拖拽
  if (e.button !== 0) return

  isDragging = true
  draggedTable = table
  hasMoved = false

  // 记录初始位置
  startX = e.clientX
  startY = e.clientY
  startPosX = table.posX
  startPosY = table.posY

  // 添加全局事件监听
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)

  // 改变鼠标样式
  e.target.style.cursor = 'grabbing'
  e.preventDefault() // 防止选中文本
}

function onDrag(e) {
  if (!isDragging || !draggedTable) return

  const floorPlan = document.querySelector('.floor-plan')
  const rect = floorPlan.getBoundingClientRect()

  // 计算鼠标移动的像素距离
  const deltaX = e.clientX - startX
  const deltaY = e.clientY - startY

  // 如果移动距离超过 3px，认定为拖拽（防止误触）
  if (Math.abs(deltaX) > 3 || Math.abs(deltaY) > 3) {
    hasMoved = true
  }

  // 将像素距离转换为百分比
  const percentX = (deltaX / rect.width) * 100
  const percentY = (deltaY / rect.height) * 100

  // 更新坐标，限制在 5% 到 95% 之间（防止桌子飞出画布）
  draggedTable.posX = Math.max(5, Math.min(95, Math.round(startPosX + percentX)))
  draggedTable.posY = Math.max(5, Math.min(95, Math.round(startPosY + percentY)))
}

function stopDrag(e) {
  if (!isDragging) return

  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)

  // 恢复鼠标样式
  if (e && e.target) {
    e.target.style.cursor = 'pointer'
  }

  // 如果仅仅是点击，没有拖拽，则弹出二维码
  if (!hasMoved && draggedTable) {
    showQRCode(draggedTable)
  }
  // 如果发生了拖拽，则保存新坐标到后端
  else if (hasMoved && draggedTable) {
    request.put('/table/update', draggedTable).then(res => {
      if (res.code === 200) {
        ElMessage.success(`${draggedTable.tableNo} 位置已更新`)
      }
    })
  }

  isDragging = false
  draggedTable = null
}
// 2. 当日期或时段改变时，同时刷新表格和平面图
function handleFilterChange() {
  if (viewMode.value === 'table') {
    getPageList()
  } else {
    fetchFloorPlanStatus()
  }
}
// 4. 修改平面图的请求方法
function fetchFloorPlanStatus() {
  request.get('/table/list', {
    params: { date: floorDate.value, period: floorPeriod.value }
  }).then(res => {
    if (res.code === 200) {
      allTables.value = res.data
    }
  })
}
</script>

<style scoped>
.breadcrumb-bar { width: 100%; background-color: #fff; padding: 15px 30px; font-size: 14px; color: #666; border-bottom: 1px solid #eee; margin-bottom: 20px; box-sizing: border-box; }
.page-title { font-size: 24px; color: #333; margin-bottom: 20px; font-weight: 600; }
.filter-card { background: #fff; }
.search-btn { background-color: #C6A15B !important; color: #101010 !important; border-color: #C6A15B !important; }

/* 二维码弹窗专属卡片样式 */
.qr-card {
  text-align: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
}
.qr-brand {
  font-family: 'STSong', serif;
  font-size: 24px;
  margin: 0;
  color: #101010;
  letter-spacing: 4px;
}
.qr-en {
  font-size: 10px;
  color: #999;
  letter-spacing: 2px;
  margin-top: 5px;
  margin-bottom: 20px;
}
.qr-img {
  width: 200px;
  height: 200px;
  border: 1px solid #eee;
  padding: 10px;
  border-radius: 8px;
}
.qr-table-no {
  font-family: Georgia, serif;
  font-size: 22px;
  color: #C6A15B;
  margin: 15px 0 5px 0;
}
.qr-tip {
  font-size: 12px;
  color: #888;
}

/* 平面图容器与暗金网格背景  */
.floor-plan {
  position: relative;
  width: 100%;
  height: 600px; /* 画布高度 */
  background-color: #12100E;
  /* 核心：使用 CSS 绘制极细的暗金网格 */
  background-image:
      linear-gradient(rgba(198, 161, 91, 0.08) 1px, transparent 1px),
      linear-gradient(90deg, rgba(198, 161, 91, 0.08) 1px, transparent 1px);
  background-size: 40px 40px; /* 网格大小 */
  background-position: center;
  border-radius: 8px;
  border: 1px solid rgba(198, 161, 91, 0.2);
  overflow: hidden;
}

/* 墙角的十字定位标（装饰用） */
.floor-plan::before, .floor-plan::after {
  content: '+';
  position: absolute;
  color: rgba(198, 161, 91, 0.4);
  font-size: 20px;
}
.floor-plan::before { top: 10px; left: 10px; }
.floor-plan::after { bottom: 10px; right: 10px; }

/*  桌子基础样式  */
.table-item {
  position: absolute;
  /* 居中对齐坐标点 */
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: grab;
  transition: all 0.2s ease-out;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.5);
  z-index: 10;
  user-select: none;/*防止拖拽时选中文字*/
}

.table-item:hover {
  transform: translate(-50%, -50%) scale(1.05);
  z-index: 20;
  box-shadow: 0 0 20px rgba(198, 161, 91, 0.4);
}

/* 方形桌 */
.table-item.is-rect {
  border-radius: 8px;
}

/* 圆形桌 */
.table-item.is-circle {
  border-radius: 50%;
}

/*  状态颜色 (暗黑金/红/黄)  */
.table-item .table-no {
  font-family: 'STSong', serif;
  font-size: 18px;
  font-weight: bold;
  letter-spacing: 1px;
}
.table-item .table-cap {
  font-size: 11px;
  margin-top: 4px;
  opacity: 0.8;
}

/* 空闲 (绿色) */
.status-free {
  background: rgba(103, 194, 58, 0.15); /* 👈 淡绿色背景 */
  border: 2px solid #67C23A;            /* 👈 绿色边框 */
  color: #67C23A;                       /* 👈 绿色文字 */
}
/* 占用 (红色) */
.status-busy {
  background: rgba(192, 112, 92, 0.15);
  border: 2px solid #C0705C;
  color: #C0705C;
}

/* 预留 (黄色) */
.status-reserved {
  background: rgba(217, 162, 74, 0.15);
  border: 2px solid #D9A24A;
  color: #D9A24A;
}
</style>