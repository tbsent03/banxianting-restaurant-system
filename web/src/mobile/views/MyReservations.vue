<template>
  <div class="m-reservations">
    <van-nav-bar title="我的预订" left-arrow @click-left="$router.back()" :border="false" style="background: var(--m-surface);" title-class="nav-text" />

    <div class="list-wrap">
      <div class="res-card" v-for="res in list" :key="res.id">
        <!-- 头部：日期时间和状态 -->
        <div class="card-head">
          <div class="date-time">
            <span class="date">{{ res.reserveDate }}</span>
            <span class="time">{{ res.reserveTime }}</span>
          </div>
          <span class="status" :class="getStatusClass(res.status)">{{ res.status }}</span>
        </div>

        <!-- 内容：预订详情 -->
        <div class="card-body">
          <p><span>联系人：</span>{{ res.contactName }} ({{ res.contactTel }})</p>
          <p><span>就餐人数：</span>{{ res.peopleCount }} 人</p>
          <p><span>座位类型：</span>{{ res.needRoom === 1 ? '私密包厢' : '大厅' }}</p>
          <p v-if="res.tableNo"><span>已分配桌号：</span><strong class="highlight">{{ res.tableNo }}</strong></p>
          <p v-if="res.remark"><span>特殊备注：</span>{{ res.remark }}</p>
        </div>

        <!-- 底部：操作与时间 -->
        <div class="card-footer">
          <span class="create-time">申请时间：{{ res.createTime }}</span>
          <van-button
              v-if="res.status === '待确认'"
              size="small" plain type="danger" round
              @click="cancel(res.id)"
          >取消预订</van-button>
        </div>
      </div>
      <van-empty v-if="list.length === 0" description="暂无预订记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showConfirmDialog, showSuccessToast, showFailToast } from 'vant'
import http from '@/utils/http.js'

const list = ref([])

onMounted(() => {
  fetchList()
})

function fetchList() {
  http.get('/reservation/my').then(res => {
    if (res.code === 200) {
      const sortedData = res.data.sort((a, b) => {
        const timeA = new Date(`${a.reserveDate} ${a.reserveTime}`).getTime()
        const timeB = new Date(`${b.reserveDate} ${b.reserveTime}`).getTime()
        return timeA - timeB
      })
      list.value = sortedData
    }
  })
}

function cancel(id) {
  showConfirmDialog({
    title: '取消预订',
    message: '取消后，为您预留的席位将被释放，确定要取消本次预订吗？',
    confirmButtonText: '确认取消',
    cancelButtonText: '再想想',
  }).then(() => {
    http.post(`/reservation/cancel/${id}`).then(res => {
      if (res.code === 200) {
        showSuccessToast('已取消')
        fetchList()
      } else {
        showFailToast(res.msg || '取消失败')
      }
    })
  }).catch(() => {})
}

// 状态颜色映射，与 PC 端的 el-tag 颜色对齐
const getStatusClass = (status) => {
  if (status === '待确认') return 'wait'
  if (status === '已确认') return 'success'
  if (status === '已到店') return 'info'
  if (status === '已取消') return 'cancel'
  return 'info'
}
</script>

<style scoped>
.m-reservations {
  min-height: 100dvh;
  background: var(--m-bg);
}
.nav-text {
  color: var(--m-text) !important;
}
.list-wrap {
  padding: 16px;
}
.res-card {
  background: var(--m-surface);
  border-radius: var(--m-radius);
  margin-bottom: 16px;
  border: 1px solid rgba(255,255,255,0.05);
  overflow: hidden;
}
.card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #22201E;
  border-bottom: 1px solid rgba(255,255,255,0.05);
}
.date-time {
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.date {
  color: var(--m-gold);
  font-size: 18px;
  font-family: 'Georgia', serif;
  font-weight: bold;
}
.time {
  color: var(--m-text-2);
  font-size: 14px;
}
.status {
  font-size: 13px;
  font-weight: bold;
}
.status.wait { color: #E6A23C; }
.status.success { color: #67C23A; }
.status.info { color: #909399; }
.status.cancel { color: #C0705C; }

.card-body {
  padding: 16px;
}
.card-body p {
  font-size: 14px;
  color: var(--m-text);
  margin: 0 0 10px 0;
}
.card-body p:last-child {
  margin-bottom: 0;
}
.card-body span {
  color: var(--m-muted);
}
.highlight {
  color: var(--m-gold);
  font-size: 16px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #12100E;
  border-top: 1px solid rgba(255,255,255,0.02);
}
.create-time {
  font-size: 12px;
  color: var(--m-muted);
}
</style>