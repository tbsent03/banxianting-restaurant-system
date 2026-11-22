<template>
  <div class="menu-page">
    <div class="menu-container">
      <!-- 左侧分类导航 -->
      <aside class="sidebar">
        <h2 class="menu-title">品鉴菜单</h2>
        <p class="menu-subtitle">MENU</p>

        <ul class="category-list">
          <li
              :class="['category-item', { active: currentCategoryId === null }]"
              @click="selectCategory(null)">
            全部菜品
          </li>
          <li
              v-for="cat in categoryList"
              :key="cat.id"
              :class="['category-item', { active: currentCategoryId === cat.id }]"
              @click="selectCategory(cat.id)">
            {{ cat.name }}
            <span class="en-name">{{ cat.nameEn }}</span>
          </li>
        </ul>
      </aside>

      <!-- 右侧菜品列表 -->
      <main class="dish-content" v-loading="loading" element-loading-background="#12100E">
        <div class="dish-grid" v-if="dishList.length > 0">
          <div class="dish-card" v-for="dish in dishList" :key="dish.id" @click="goToDetail(dish.id)">
            <div class="img-wrap">
              <img :src="formatImg(dish.coverUrl)" :alt="dish.name">
            </div>
            <div class="dish-info">
              <h3>{{ dish.name }}</h3>
              <p class="en-name">{{ dish.nameEn || 'SIGNATURE DISH' }}</p>
              <p class="desc">{{ dish.description || '主厨特制，精选当季新鲜食材。' }}</p>
              <div class="price-row">
                <span class="price">¥{{ dish.price }}</span>
                <!-- 阻止冒泡，防止点击加号时触发卡片跳转 -->
                <button class="add-btn" @click.stop="addToCart(dish)">+</button>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty v-else description="该分类下暂无菜品" :image-size="100"></el-empty>
      </main>
    </div>
  </div>
</template>

<script setup>
import { useCartStore } from "../../store/cart";
import { ref, onMounted } from 'vue'
import { useRouter,useRoute } from 'vue-router'
import request from "@/utils/http.js"
import { ElMessage } from 'element-plus'

const cartStore = useCartStore()
const router = useRouter()
const route = useRoute()

// 状态数据
const categoryList = ref([])
const dishList = ref([])
const currentCategoryId = ref(null)
const loading = ref(false)

// 初始化加载
onMounted(() => {
  //从URL参数中读取categoryId，如果是null就代表全部
  const urlCategoryId = route.query.categoryId
  if (urlCategoryId) {
    currentCategoryId.value = Number(urlCategoryId)
  }
  getCategories()
  getDishes()
})

function formatImg(url) {
  if (!url) return '';
  return url.replace('localhost', window.location.hostname);
}


// 获取分类列表
function getCategories() {
  request.get('/category/list').then(res => {
    if(res.code === 200) {
      categoryList.value = res.data
    }
  })
}

// 获取菜品列表（根据分类）
function getDishes() {
  loading.value = true
  const params = {
    pageNum: 1,
    pageSize: 100, // 前台一次性展示多点
    status: '上架' // 只查上架的
  }
  if (currentCategoryId.value !== null) {
    params.categoryId = currentCategoryId.value
  }

  request.get('/dish/page', { params }).then(res => {
    if(res.code === 200) {
      dishList.value = res.data.list
    }
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

// 切换分类
function selectCategory(id) {
  currentCategoryId.value = id
  getDishes()

  //把选中的分类ID写入到浏览器的URL中
  router.replace({
    path: '/menu',
    query: id ? { categoryId: id } : {}
  })
}

// 跳转详情
function goToDetail(id) {
  router.push(`/dishDetail/${id}`)
}

// 加入购物车
function addToCart(dish) {
  cartStore.addToCart(dish,1)
  ElMessage.success(`已将 ${dish.name} 加入点菜单`)
}
</script>

<style scoped>
.menu-page { background: #12100E; min-height: 100vh; padding: 60px 0; color: #F7F3EC; }
.menu-container { max-width: 1300px; margin: 0 auto; display: flex; gap: 60px; padding: 0 40px; }

/* 左侧侧边栏 */
.sidebar { width: 200px; flex-shrink: 0; position: sticky; top: 100px; height: fit-content; }
.menu-title { font-family: 'STSong', serif; font-size: 28px; margin: 0 0 5px 0; letter-spacing: 2px; }
.menu-subtitle { font-size: 12px; color: #D5A958; letter-spacing: 4px; margin-bottom: 40px; }
.category-list { list-style: none; padding: 0; margin: 0; }
.category-item {
  font-size: 16px; color: #888; padding: 15px 0; cursor: pointer;
  border-bottom: 1px solid rgba(255,255,255,0.05); transition: all 0.3s;
  display: flex; justify-content: space-between; align-items: center;
}
.category-item:hover, .category-item.active { color: #D5A958; padding-left: 10px; }
.category-item .en-name { font-size: 10px; color: #555; text-transform: uppercase; }

/* 右侧主内容 */
.dish-content { flex: 1; }
.dish-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 30px; }

/* 复用首页的卡片样式 */
.dish-card { background: #1A1816; border: 1px solid rgba(255,255,255,0.03); padding: 20px; transition: transform 0.3s; cursor: pointer; }
.dish-card:hover { transform: translateY(-5px); border-color: rgba(213, 169, 88, 0.3); }
.img-wrap { width: 100%; aspect-ratio: 4/3; overflow: hidden; margin-bottom: 25px; background: #2A2826; }
.img-wrap img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s; }
.dish-card:hover .img-wrap img { transform: scale(1.05); }
.dish-info h3 { font-family: 'STSong', serif; font-size: 20px; margin: 0 0 5px 0; letter-spacing: 1px; }
.en-name { font-size: 10px; color: #D5A958; letter-spacing: 2px; margin-bottom: 15px; text-transform: uppercase; }
.desc { font-size: 13px; color: #888; line-height: 1.6; margin-bottom: 25px; height: 44px; overflow: hidden; }
.price-row { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid rgba(255,255,255,0.05); padding-top: 15px; }
.price { font-size: 18px; color: #D5A958; font-family: 'Georgia', serif; }
.add-btn { width: 30px; height: 30px; border-radius: 50%; background: transparent; border: 1px solid #D5A958; color: #D5A958; cursor: pointer; font-size: 18px; display: flex; align-items: center; justify-content: center; transition: all 0.3s; }
.add-btn:hover { background: #D5A958; color: #12100E; }
</style>