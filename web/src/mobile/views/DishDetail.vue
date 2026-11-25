<template>
  <div class="m-dish-detail" v-if="dish.id">
    <van-nav-bar
        title="菜品详情"
        left-arrow
        @click-left="$router.back()"
        :border="false"
        style="background: transparent; position: absolute; width: 100%; z-index: 10;"
    />

    <!-- 顶部大图 -->
    <div class="header-img">
      <img :src="formatImg(dish.coverUrl)" alt="dish">
    </div>

    <!-- 内容区 -->
    <div class="content">
      <!-- 动态分类标签 -->
      <div class="eyebrow">
        <span v-if="dish.isSignature" class="tag-signature">SIGNATURE</span>
        <span v-if="dish.isSignature" class="dot">·</span>
        <span>{{ currentCategoryName }}</span>
      </div>

      <h1 class="name">{{ dish.name }}</h1>
      <p class="en-name">{{ dish.nameEn }}</p>

      <div class="price-row">
        <span class="price">¥{{ dish.price }}</span>
        <span class="sales">已售 {{ dish.sales || 0 }}</span>
      </div>

      <div class="divider"></div>

      <h3 class="section-title">菜品介绍</h3>
      <p class="desc">{{ dish.description || '主厨精选当季食材，精心烹制。' }}</p>

      <!-- 参数列表区域 -->
      <div class="spec-list">
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

    </div>

    <!-- 底部操作栏 -->
    <div class="bottom-action">
      <div class="action-left">
        <span class="label">数量</span>
        <van-stepper v-model="quantity" min="1" :max="99" theme="round" button-size="22"/>
      </div>
      <van-button
          round
          color="#D5A958"
          class="add-btn"
          style="color: #12100E; font-weight: bold;"
          @click="handleAdd"
      >
        加入点菜单 ¥{{ (dish.price * quantity).toFixed(2) }}
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showSuccessToast, showFailToast } from 'vant'
import http from '@/utils/http.js'
import { formatImg } from '@/utils/img.js'
import { useCartStore } from '@/store/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const dish = ref({})
const quantity = ref(1)
const categoryList = ref([])

// 动态计算分类名称
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
    showFailToast('该菜品已售罄')
    return
  }
  cartStore.addToCart(dish.value, quantity.value)
  showSuccessToast(`已加入 ${quantity.value} 份`)
  setTimeout(() => {
    router.back()
  }, 500)
}
</script>

<style scoped>
.m-dish-detail {
  background: var(--m-bg);
  min-height: 100dvh;
  padding-bottom: 80px;
  --van-nav-bar-title-text-color: var(--m-gold);
  --van-nav-bar-icon-color: var(--m-gold);
}


.header-img {
  width: 100%;
  height: 300px;
  background: var(--m-surface-2);
}
.header-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.content {
  padding: 20px 16px;
  background: var(--m-bg);
  border-radius: 16px 16px 0 0;
  margin-top: -16px;
  position: relative;
  z-index: 2;
}

/* 动态分类标签 */
.eyebrow {
  font-size: 12px;
  letter-spacing: 1px;
  margin-bottom: 8px;
  color: var(--m-muted);
  display: flex;
  align-items: center;
  gap: 6px;
}
.tag-signature { color: var(--m-gold); font-weight: bold; }
.dot { color: #555; }

.name {
  font-size: 24px;
  color: var(--m-text);
  margin: 0 0 4px 0;
  font-family: 'Songti SC', serif;
}
.en-name {
  font-size: 12px;
  color: var(--m-gold);
  letter-spacing: 1px;
  text-transform: uppercase;
  margin-bottom: 16px;
}
.price-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}
.price {
  font-size: 26px;
  color: var(--m-gold);
  font-family: 'Georgia', serif;
  font-weight: bold;
}
.sales {
  font-size: 12px;
  color: var(--m-muted);
}
.divider {
  height: 1px;
  background: rgba(255,255,255,0.05);
  margin: 20px 0;
}
.section-title {
  font-size: 16px;
  color: var(--m-text);
  margin-bottom: 8px;
}
.desc {
  font-size: 13px;
  color: var(--m-text-2);
  line-height: 1.6;
  margin-bottom: 24px;
}

/*  参数列表样式 */
.spec-list {
  background: var(--m-surface);
  border-radius: var(--m-radius);
  padding: 0 16px;
  margin-bottom: 20px;
}
.spec-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px dashed rgba(255,255,255,0.05);
  font-size: 13px;
}
.spec-row:last-child {
  border-bottom: none;
}
.spec-row .k {
  color: var(--m-muted);
  width: 70px;
  flex-shrink: 0;
}
.spec-row .v {
  color: var(--m-text);
  text-align: right;
  flex: 1;
  padding-left: 10px;
}

/* 辣度小圆点 */
.spicy-dots {
  display: flex;
  justify-content: flex-end;
  gap: 4px;
}
.spicy-dots i {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #333;
}
.spicy-dots i.on { background: var(--m-gold); }
.no-spicy { font-size: 13px; color: var(--m-text); }

/* 底部操作栏 */
.bottom-action {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--m-surface);
  padding: 10px 16px calc(10px + env(safe-area-inset-bottom));
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid rgba(255,255,255,0.05);
  z-index: 100;
}
.action-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.label {
  font-size: 14px;
  color: var(--m-muted);
}
.add-btn {
  flex: 1;
  margin-left: 20px;
  height: 44px;
}
:deep(.van-stepper__input) {
  color: var(--m-text) !important;
  background-color: transparent !important;
  font-weight: bold;
  font-size: 15px;
}

</style>