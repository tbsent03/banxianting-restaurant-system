<template>
  <div class="m-menu">
    <!-- 顶部桌号提示 -->
    <div class="table-header" v-if="diningStore.currentTable">
      <van-icon name="location-o" color="#D5A958" />
      <span>{{ diningStore.currentTable.tableNo }} · {{ diningStore.currentTable.area }}</span>
    </div>

    <div class="layout">
      <!-- 左侧分类侧边栏 -->
      <div class="sidebar">
        <!-- 顶部标题 -->
        <div class="menu-header">
          <h2 class="menu-title">品鉴菜单</h2>
          <p class="menu-subtitle">MENU</p>
        </div>

        <div
            class="cate-item"
            :class="{ active: currentCateId === null }"
            @click="switchCategory(null)"
        >
          <span class="cn-name">全部菜品</span>
        </div>

        <div
            v-for="cate in categories"
            :key="cate.id"
            class="cate-item"
            :class="{ active: currentCateId === cate.id }"
            @click="switchCategory(cate.id)"
        >
          <span class="cn-name">{{ cate.name }}</span>
          <span class="en-name">{{ getEnName(cate.name) }}</span>
        </div>
      </div>

      <!-- 右侧菜品列表 -->
      <div class="dish-list">
        <div class="dish-item" v-for="dish in dishes" :key="dish.id" @click="goToDetail(dish.id)">
          <img :src="formatImg(dish.coverUrl)" class="cover" alt="dish">
          <div class="info">
            <h3>{{ dish.name }}</h3>
            <p class="desc">{{ dish.description }}</p>
            <div class="bottom-row">
              <span class="price">¥{{ dish.price }}</span>
              <!-- 售罄提示和按钮置灰 -->
              <span v-if="dish.stock <= 0" class="sold-out">已售罄</span>
              <div class="add-btn" :class="{ disabled: dish.stock <= 0 }" @click.stop="addToCart(dish)">
                <van-icon name="plus" />
              </div>
            </div>
          </div>
        </div>
        <van-empty v-if="dishes.length === 0" description="该分类下暂无菜品" />
      </div>
    </div>

    <!-- 吸底结算条 -->
    <MBottomBar />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showFailToast } from 'vant'
import http from '@/utils/http.js'
import { formatImg } from '@/utils/img.js'
import { useCartStore } from '@/store/cart'
import { useDiningStore } from '@/store/dining'
import MBottomBar from '@/mobile/components/MBottomBar.vue'

const router = useRouter()
const cartStore = useCartStore()
const diningStore = useDiningStore()

const categories = ref([])
const currentCateId = ref(null)
const dishes = ref([])

//  中英文映射字典（无需后端支持）
const enMap = {
  '前菜': 'APPETIZER',
  '主菜': 'MAIN COURSE',
  '汤品': 'SOUP',
  '甜点': 'DESSERT',
  '酒水': 'DRINKS',
  '主食': 'STAPLE',
  '特饮': 'BEVERAGE'
}

function getEnName(cnName) {
  return enMap[cnName] || cnName
}

onMounted(() => {
  fetchCategories()
  fetchDishes()
})

function fetchCategories() {
  http.get('/category/list').then(res => {
    if (res.code === 200) {
      categories.value = res.data
    }
  })
}

function fetchDishes() {
  const params = {
    pageNum: 1,
    pageSize: 100,
    status: '上架'
  }
  if (currentCateId.value) {
    params.categoryId = currentCateId.value
  }

  http.get('/dish/page', { params }).then(res => {
    if (res.code === 200) {
      dishes.value = res.data.list
    }
  })
}

function switchCategory(id) {
  currentCateId.value = id
  fetchDishes()
}

function goToDetail(id) {
  router.push(`/dish/${id}`)
}

function addToCart(dish) {
  if (dish.stock <= 0) {
    showFailToast('该菜品已售罄')
    return
  }
  cartStore.addToCart(dish, 1)
  showSuccessToast(`已将 ${dish.name} 加入点菜单`)
}
</script>

<style scoped>
.m-menu {
  display: flex;
  flex-direction: column;
  height: calc(100dvh - 50px - env(safe-area-inset-bottom) - 16px);
  background: var(--m-bg);
}
.table-header {
  padding: 10px 16px;
  font-size: 14px;
  color: var(--m-gold);
  background: var(--m-surface);
  display: flex;
  align-items: center;
  gap: 6px;
}
.layout {
  display: flex;
  flex: 1;
  overflow: hidden;
}

/*  左侧侧边栏修改 */
.sidebar {
  width: 100px;
  background: #0A0908;
  overflow-y: auto;
  flex-shrink: 0;
}

/* 侧边栏顶部标题 */
.menu-header {
  padding: 28px 10px 16px;
}
.menu-title {
  font-family: 'STSong', serif;
  font-size: 18px;
  color: #F7F3EC;
  margin: 0 0 4px 0;
  letter-spacing: 1px;
}
.menu-subtitle {
  font-size: 10px;
  color: var(--m-gold);
  letter-spacing: 2px;
  margin: 0;
}

/* 分类项 */
.cate-item {
  padding: 25px 10px;
  cursor: pointer;
  border-bottom: 1px solid rgba(255,255,255,0.03);
  transition: all 0.2s;
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

/* 中文在上 */
.cn-name {
  font-size: 14px;
  color: var(--m-muted);
  font-weight: 500;
}

/* 英文在下，字号极小并大写 */
.en-name {
  font-size: 9px;
  color: #555;
  text-transform: uppercase;
}

/* 选中状态 */
.cate-item.active {
  background: var(--m-surface);
}
.cate-item.active .cn-name {
  color: var(--m-gold);
  font-weight: bold;
}
.cate-item.active .en-name {
  color: var(--m-gold);
  opacity: 0.7;
}

/* 左侧金条 */
.cate-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 16px;
  background: var(--m-gold);
}

/* 右侧菜品列表保持原样 */
.dish-list {
  flex: 1;
  padding: 12px;
  overflow-y: auto;
}
.dish-item {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  background: var(--m-surface);
  padding: 10px;
  border-radius: var(--m-radius);
}
.cover {
  width: 90px;
  height: 90px;
  border-radius: 8px;
  object-fit: cover;
  background: var(--m-surface-2);
}
.info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.info h3 {
  font-size: 15px;
  margin: 0 0 4px 0;
  font-weight: 500;
}
.desc {
  font-size: 11px;
  color: var(--m-muted);
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}
.bottom-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 4px;
}
.price {
  font-size: 16px;
  color: var(--m-gold);
  font-family: 'Georgia', serif;
}
.sold-out {
  font-size: 12px;
  color: var(--m-danger);
}
.add-btn {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--m-gold);
  color: #12100E;
  display: flex;
  align-items: center;
  justify-content: center;
}
.add-btn.disabled {
  background: var(--m-muted);
  color: #666;
}
</style>