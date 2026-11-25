<template>
  <div class="pc-dish-detail" v-if="dish.id">
    <div class="container">
      <!-- 顶部返回 -->
      <div class="back-nav" @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回菜单</span>
      </div>

      <div class="detail-layout">
        <!-- 左侧图片区 -->
        <div class="image-section">
          <div class="circle-mask">
            <img :src="formatImg(dish.coverUrl)" alt="dish">
          </div>
        </div>

        <!-- 右侧信息区 -->
        <div class="info-section">
          <!-- 动态标签 -->
          <div class="eyebrow">
            <span v-if="dish.isSignature" class="tag-signature">SIGNATURE</span>
            <span v-if="dish.isSignature" class="dot">·</span>
            <span>{{ currentCategoryName }}</span>
          </div>

          <h1 class="title">{{ dish.name }}</h1>
          <div class="en-title">{{ dish.nameEn }}</div>
          <div class="desc">{{ dish.description }}</div>

          <!-- 规格参数列表 -->
          <div class="spec-list">
            <div class="spec-row">
              <span class="k">价格</span>
              <span class="v price"><span class="cur">¥</span>{{ dish.price }}</span>
            </div>
            <div class="spec-row" v-if="dish.ingredients">
              <span class="k">主要食材</span>
              <span class="v">{{ dish.ingredients }}</span>
            </div>
            <div class="spec-row">
              <span class="k">辣度</span>
              <span class="v spicy-dots">
                <template v-if="dish.spicyLevel > 0">
                  <i v-for="n in 3" :key="n" :class="{ on: n <= dish.spicyLevel }"></i>
                </template>
                <span v-else class="no-spicy">不辣</span>
              </span>
            </div>
            <div class="spec-row" v-if="dish.pairing">
              <span class="k">推荐搭配</span>
              <span class="v">{{ dish.pairing }}</span>
            </div>
            <div class="spec-row">
              <span class="k">出餐时间</span>
              <span class="v">约 20 分钟</span>
            </div>
          </div>

          <!-- 底部购买栏 -->
          <div class="buy-bar">
            <div class="stepper">
              <button @click="quantity > 1 && quantity--">−</button>
              <b>{{ quantity }}</b>
              <button @click="quantity++">+</button>
            </div>
            <button class="btn-primary" @click="handleAdd">
              加入点菜单 · ¥ {{ (dish.price * quantity).toFixed(2) }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted,computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import http from '@/utils/http.js'
import { formatImg } from '@/utils/img.js'
import { useCartStore } from '@/store/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const dish = ref({})
const quantity = ref(1)
const categoryList = ref([])
const currentCategoryName = computed(() => {
  if (!dish.value.categoryId) return '主厨精选'
  const cate = categoryList.value.find(c => c.id === dish.value.categoryId)
  return cate ? cate.name : '主厨精选'
})
onMounted(() => {
  fetchDetail()
  fetchCategories()
})

function fetchDetail() {
  http.get(`/dish/selectById/${route.params.id}`).then(res => {
    if (res.code === 200) {
      dish.value = res.data
    }
  })
}
function fetchCategories() {
  http.get('/category/list').then(res => {
    if (res.code === 200) {
      categoryList.value = res.data
    }
  })
}
function handleAdd() {
  if (dish.value.stock <= 0) {
    ElMessage.warning('该菜品已售罄')
    return
  }
  cartStore.addToCart(dish.value, quantity.value)
  ElMessage.success(`已将 ${quantity.value} 份 ${dish.value.name} 加入点菜单`)
}
</script>

<style scoped>
.pc-dish-detail {
  background: #12100E;
  min-height: 100vh;
  color: #F7F3EC;
  padding: 40px 0 80px;
}
.container {
  max-width: 1300px;
  margin: 0 auto;
  padding: 0 40px;
}

/* 返回按钮 */
.back-nav {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #888;
  cursor: pointer;
  font-size: 14px;
  margin-bottom: 40px;
  transition: color 0.3s;
}
.back-nav:hover { color: #D5A958; }

/* 左右布局 */
.detail-layout {
  display: flex;
  gap: 80px;
  align-items: center;
}

/* 左侧圆形图 */
.image-section {
  flex: 1;
  display: flex;
  justify-content: center;
}
.circle-mask {
  width: 500px;
  height: 500px;
  border-radius: 50%;
  overflow: hidden;
  border: 15px solid rgba(255, 255, 255, 0.03);
  box-shadow: 0 0 50px rgba(0, 0, 0, 0.5);
  background: #1A1816;
}
.circle-mask img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 右侧信息 */
.info-section {
  flex: 1;
  max-width: 500px;
}
.eyebrow {
  font-size: 14px;
  letter-spacing: 2px;
  margin-bottom: 20px;
  color: #888;
  display: flex;
  align-items: center;
  gap: 8px;
}
.tag-signature { color: #D5A958; font-weight: bold; }
.dot { color: #555; }

.title {
  font-family: 'STSong', serif;
  font-size: 48px;
  line-height: 1.2;
  margin: 0 0 10px 0;
  letter-spacing: 2px;
}
.en-title {
  font-size: 14px;
  color: #666;
  letter-spacing: 2px;
  text-transform: uppercase;
  margin-bottom: 30px;
}
.desc {
  font-size: 15px;
  line-height: 1.8;
  color: #A39F9A;
  margin-bottom: 40px;
}

/* 参数列表 */
.spec-list {
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  margin-bottom: 40px;
}
.spec-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px dashed rgba(255, 255, 255, 0.05);
  font-size: 14px;
}
.spec-row .k { color: #888; }
.spec-row .v { color: #F7F3EC; }
.spec-row .price {
  color: #D5A958;
  font-size: 24px;
  font-family: 'Georgia', serif;
}
.cur { font-size: 16px; margin-right: 2px; }

/* 辣度小圆点 */
.spicy-dots { display: flex; gap: 6px; }
.spicy-dots i {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #333;
}
.spicy-dots i.on { background: #D5A958; }
.no-spicy { font-size: 14px; }

/* 底部购买区 */
.buy-bar {
  display: flex;
  gap: 20px;
}
.stepper {
  display: flex;
  align-items: center;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  height: 50px;
}
.stepper button {
  width: 40px;
  background: transparent;
  border: none;
  color: #888;
  font-size: 18px;
  cursor: pointer;
  transition: color 0.2s;
}
.stepper button:hover { color: #D5A958; }
.stepper b {
  width: 40px;
  text-align: center;
  font-size: 16px;
  font-weight: normal;
}
.btn-primary {
  flex: 1;
  height: 50px;
  background: #D5A958;
  color: #12100E;
  border: none;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.3s;
}
.btn-primary:hover { background: #E5C07B; }
</style>