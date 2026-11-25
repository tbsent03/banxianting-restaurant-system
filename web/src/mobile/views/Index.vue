<template>
  <div class="m-index">
    <!-- 顶部 Banner -->
    <div class="hero">
      <h1 class="brand">半闲庭</h1>
      <p class="slogan">品味非凡，尽在半闲</p>
    </div>

    <!-- 桌号卡片 -->
    <div class="table-card">
      <template v-if="diningStore.currentTable">
        <div class="card-head">
          <van-icon name="checked" color="#67C23A" size="20" />
          <span>桌位已绑定</span>
        </div>
        <h2>{{ diningStore.currentTable.tableNo }}</h2>
        <p>{{ diningStore.currentTable.area }} · {{ diningStore.currentTable.capacity }}人桌</p>
      </template>
      <template v-else>
        <div class="card-head">
          <van-icon name="info-o" color="#D5A958" size="20" />
          <span>您还未绑定桌位</span>
        </div>
        <p style="margin-top: 10px;">请扫描桌面上的二维码，或直接以自取模式点餐。</p>
      </template>
    </div>

    <!-- 招牌菜推荐 -->
    <div class="section-title">招牌推荐</div>
    <div class="dish-grid">
      <div class="dish-card" v-for="dish in signatureDishes" :key="dish.id" @click="goToDetail(dish.id)">
        <img :src="formatImg(dish.coverUrl)" alt="dish">
        <div class="info">
          <h3>{{ dish.name }}</h3>
          <p class="price">¥{{ dish.price }}</p>
        </div>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="bottom-btn-wrap">
      <van-button block round color="#D5A958" style="color: #12100E; font-weight: bold; height: 44px;" @click="$router.push('/menu')">
        开始点餐
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showSuccessToast, showFailToast } from 'vant'
import http from '@/utils/http.js'
import { formatImg } from '@/utils/img.js'
import { useDiningStore } from '@/store/dining'

const route = useRoute()
const router = useRouter()
const diningStore = useDiningStore()
const signatureDishes = ref([])

onMounted(() => {
  checkTable()
  fetchSignature()
})

// 1. 扫码桌号拦截逻辑
function checkTable() {
  const token = route.query.table
  if (token) {
    http.get(`/table/token/${token}`).then(res => {
      if (res.code === 200 && res.data) {
        if (res.data.status === '占用') {
          // 使用 Vant 的 Dialog 拦截
          import('vant').then(({ showDialog }) => {
            showDialog({
              title: '桌位已被占用',
              message: `当前 ${res.data.tableNo} 桌正在用餐中，为避免错单，请扫码其他空闲桌位。`,
              confirmButtonText: '我知道了',
              theme: 'round-button',
            }).then(() => {
              diningStore.clearTable()
            })
          })
          return
        }
        diningStore.setTable(res.data)
        showSuccessToast(`欢迎光临，当前桌号：${res.data.tableNo}`)
      } else {
        showFailToast('无效的桌码，请重新扫码')
      }
    })
  }
}

// 2. 获取招牌菜
function fetchSignature() {
  http.get('/dish/page', {
    params: { pageNum: 1, pageSize: 3, isSignature: 1, status: '上架' }
  }).then(res => {
    if (res.code === 200) {
      signatureDishes.value = res.data.list
    }
  })
}

function goToDetail(id) {
  router.push(`/dish/${id}`)
}
</script>

<style scoped>
.m-index {
  padding: 16px;
  background: var(--m-bg);
}
.hero {
  text-align: center;
  padding: 30px 0;
}
.brand {
  font-size: 36px;
  color: var(--m-gold);
  font-family: 'Songti SC', serif;
  letter-spacing: 4px;
  margin: 0;
}
.slogan {
  font-size: 13px;
  color: var(--m-muted);
  margin-top: 10px;
  letter-spacing: 2px;
}
.table-card {
  background: var(--m-surface);
  border-radius: var(--m-radius);
  padding: 20px;
  border: 1px solid rgba(213, 169, 88, 0.2);
  margin-bottom: 24px;
}
.card-head {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  margin-bottom: 10px;
}
.table-card h2 {
  font-size: 28px;
  color: var(--m-gold);
  margin: 0 0 6px 0;
}
.table-card p {
  font-size: 13px;
  color: var(--m-muted);
  margin: 0;
}
.section-title {
  font-size: 18px;
  font-family: 'Songti SC', serif;
  margin-bottom: 16px;
  border-left: 3px solid var(--m-gold);
  padding-left: 10px;
}
.dish-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.dish-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background: var(--m-surface);
  padding: 10px;
  border-radius: var(--m-radius);
}
.dish-card img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}
.dish-card .info {
  flex: 1;
}
.dish-card h3 {
  font-size: 15px;
  margin: 0 0 8px 0;
}
.price {
  color: var(--m-gold);
  font-size: 16px;
  margin: 0;
}
.bottom-btn-wrap {
  margin-top: 30px;
  padding-bottom: 20px;
}
</style>