<template>
  <div class="my-res-page">
    <div class="container">
      <div class="header-nav">
        <h2 class="page-title">我的订位记录</h2>
        <el-button plain @click="$router.back()" style="border-color: #555; color: #aaa;">返回上一页</el-button>
      </div>

      <div v-if="listData.length === 0" class="empty-box">
        <el-empty description="您还没有预订记录哦"></el-empty>
        <el-button type="primary" class="go-btn" @click="$router.push('/reservation')">去预订</el-button>
      </div>

      <div v-else class="res-list">
        <div class="res-card" v-for="res in listData" :key="res.id">
          <!-- 卡片头部：日期与状态 -->
          <div class="card-header">
            <div class="date-time">
              <span class="date">{{ res.reserveDate }}</span>
              <span class="time">{{ res.reserveTime }}</span>
            </div>
            <el-tag :type="getStatusType(res.status)" effect="dark" class="status-tag">{{ res.status }}</el-tag>
          </div>

          <!-- 卡片主体：信息详情 -->
          <div class="card-body">
            <div class="info-grid">
              <div class="info-item"><span>联系人：</span>{{ res.contactName }}</div>
              <div class="info-item"><span>手机号：</span>{{ res.contactTel }}</div>
              <div class="info-item"><span>就餐人数：</span>{{ res.peopleCount }} 人</div>
              <div class="info-item"><span>座位类型：</span>{{ res.needRoom === 1 ? '私密包厢' : '大厅' }}</div>
              <!--  如果老板分配了桌位，高亮显示  -->
              <div class="info-item" v-if="res.tableNo">
                <span>已分配桌号：</span><strong class="highlight">{{ res.tableNo }}</strong>
              </div>
            </div>

            <div class="remark-box" v-if="res.remark">
              <span>特殊备注：</span>{{ res.remark }}
            </div>
          </div>

          <!-- 卡片底部：申请时间 -->
          <div class="card-footer">
            申请时间：{{ res.createTime }}
            <!-- 只有待确认和已确认状态可以取消  -->
            <el-button
                v-if="res.status === '待确认'"
                type="danger"
                link
                style="font-size: 14px;"
                @click="openCancelDialog(res)"
            >
              取消预订
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from "@/utils/http.js"
import { ElMessage, ElMessageBox } from 'element-plus'

const listData = ref([])

onMounted(() => {
  getMyReservations()
})

function getMyReservations() {
  request.get('/reservation/my').then(res => {
    if (res.code === 200) {
      // 获取数据后，在前端按照就餐时间正序排列
      const sortedData = res.data.sort((a, b) => {
        // 拼接日期和时间，比如 "2026-11-20 18:30"
        const timeA = new Date(`${a.reserveDate} ${a.reserveTime}`).getTime()
        const timeB = new Date(`${b.reserveDate} ${b.reserveTime}`).getTime()
        return timeA - timeB // 升序：时间越近的排在越前面
      })
      listData.value = res.data
    }
  })
}

function getStatusType(status) {
  if (status === '待确认') return 'warning'
  if (status === '已确认') return 'success'
  if (status === '已到店') return 'info'
  if (status === '已取消') return 'danger'
  return 'info'
}
// 取消预订逻辑
function openCancelDialog(res) {
  ElMessageBox.confirm(
      '取消后，为您预留的席位将被释放，确定要取消本次预订吗？',
      '半闲庭 · 取消预订',
      {
        confirmButtonText: '确认取消',
        cancelButtonText: '再想想',
        type: '',
        center: true,
        showClose: false,
        customClass: 'luxury-res-dialog' //
      }
  ).then(() => {
    // 发送取消请求
    request.post(`/reservation/cancel/${res.id}`).then(response => {
      if (response && response.code === 200) {
        ElMessage.success('预订已成功取消')
        getMyReservations() // 刷新列表
      } else {
        ElMessage.error(response.msg || '取消失败')
      }
    })
  }).catch(() => {
    // 用户点击了“再想想”
  })
}
</script>

<style scoped>
.my-res-page { background: #12100E; min-height: calc(100vh - 80px); color: #F7F3EC; padding: 60px 0; }
.container { max-width: 900px; margin: 0 auto; padding: 0 40px; }

.header-nav { display: flex; justify-content: space-between; align-items: center; margin-bottom: 40px; border-bottom: 1px solid rgba(255,255,255,0.1); padding-bottom: 20px; }
.page-title { font-family: 'STSong', serif; font-size: 32px; letter-spacing: 2px; margin: 0; }

.empty-box { text-align: center; padding: 100px 0; }
.go-btn { background: #D5A958; color: #12100E; border: none; margin-top: 20px; padding: 12px 30px; font-size: 16px; font-weight: bold; cursor: pointer;}

.res-card { background: #1A1816; border: 1px solid rgba(255,255,255,0.05); border-radius: 8px; margin-bottom: 25px; overflow: hidden; }

.card-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 30px; background: #22201E; border-bottom: 1px solid rgba(255,255,255,0.05); }
.date-time { display: flex; align-items: baseline; gap: 15px; }
.date { font-family: Georgia, serif; font-size: 20px; color: #D5A958; font-weight: bold; }
.time { font-size: 16px; color: #A39F9A; }
.status-tag { font-size: 14px; padding: 0 15px; height: 28px; line-height: 26px; }

.card-body { padding: 30px; }
.info-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; margin-bottom: 20px; }
.info-item { font-size: 15px; color: #F7F3EC; }
.info-item span { color: #888; }
.highlight { color: #D5A958; font-size: 18px; margin-left: 5px; }

.remark-box { background: rgba(213, 169, 88, 0.1); border-left: 3px solid #D5A958; padding: 15px; font-size: 14px; color: #A39F9A; border-radius: 4px; }
.remark-box span { color: #D5A958; }

.card-footer { padding: 15px 30px; background: #12100E; font-size: 12px; color: #666; text-align: right; }

@media (max-width: 768px) {
  .container { padding: 0 20px; }
  .info-grid { grid-template-columns: 1fr; gap: 15px; }
  .card-header { flex-direction: column; align-items: flex-start; gap: 10px; padding: 15px 20px; }
  .card-body { padding: 20px; }
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