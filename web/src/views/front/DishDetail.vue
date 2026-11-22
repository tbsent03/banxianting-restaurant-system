<template>
  <div class="detail-page" v-if="dish">
    <div class="detail-container">
      <div class="back-nav">
      <span class="back-btn" @click="$router.back()">
        <el-icon><ArrowLeft/></el-icon> 返回菜单
      </span>
      </div>
      <!-- 左侧：大图展示区 -->
      <div class="left-gallery">
        <div class="image-wrapper">
          <!-- 这里用了背景色+圆形裁切来模仿参考图的高级感 -->
          <img :src="formatImg(dish.coverUrl)" :alt="dish.name" class="dish-img">
        </div>
      </div>

      <!-- 右侧：信息详情区 -->
      <div class="right-info">
        <div class="header-info">
          <p class="tag">SIGNATURE · {{ dish.categoryName || '主菜' }}</p>
          <h1 class="title">{{ dish.name }}</h1>
          <p class="en-title">{{ dish.nameEn || 'SIGNATURE DISH' }}</p>
        </div>

        <div class="description">
          <p>{{ dish.description || '主厨精心研发，甄选当季顶级食材，为您呈现最纯粹的味觉体验。' }}</p>
        </div>

        <!-- 参数列表区 -->
        <ul class="attributes-list">
          <li>
            <span class="label">价格</span>
            <span class="value price-value">¥{{ dish.price }}</span>
          </li>
          <li>
            <span class="label">主要食材</span>
            <span class="value">{{ dish.ingredients || '主厨甄选当季食材' }}</span>
          </li>
          <li>
            <span class="label">辣度</span>
            <span class="value">
              {{ dish.spicyLevel === 0 ? '不辣' : dish.spicyLevel === 1 ? '微辣' : dish.spicyLevel === 2 ? '中辣' : '特辣' }}
            </span>
          </li>
          <li>
            <span class="label">推荐配酒</span>
            <span class="value">{{ dish.pairing || '暂无推荐' }}</span>
          </li>
          <li>
            <span class="label">出餐时间</span>
            <span class="value">约 {{ dish.id % 3 * 5 + 15 }} 分钟</span>
          </li>
        </ul>

        <p class="allergy-warning">过敏提示：本菜品不含坚果类、海鲜类。如您有忌口，请在备注中留言。</p>

        <!-- 底部操作栏 -->
        <div class="action-bar">
          <div class="stepper">
            <button class="step-btn" @click="quantity > 1 ? quantity-- : null">-</button>
            <span class="step-num">{{ quantity }}</span>
            <button class="step-btn" @click="quantity++">+</button>
          </div>
          <button class="add-btn" @click="addToCart">
            加入点菜单 · ¥{{ (dish.price * quantity).toFixed(2) }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useCartStore} from "../../store/cart";
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from "@/utils/http.js"
import { ElMessage } from 'element-plus'
import { ArrowLeft } from "@element-plus/icons-vue";

const cartStore = useCartStore()
const route = useRoute()
const router = useRouter()

const dish = ref(null)
const quantity = ref(1)

onMounted(() => {
  const id = route.params.id
  if (!id) {
    ElMessage.error("参数错误")
    router.push('/menu')
    return
  }

  // 请求单个菜品详情
  request.get(`/dish/selectById/${id}`).then(res => {
    if(res.code === 200) {
      dish.value = res.data
    }
  })
})

function addToCart() {
  cartStore.addToCart(dish.value, quantity.value)
  ElMessage.success(`已将 ${quantity.value} 份 ${dish.value.name} 加入点菜单`)
}
function formatImg(url) {
  if (!url) return '';
  return url.replace('localhost', window.location.hostname);
}
</script>

<style scoped>
.detail-page { background: #12100E; min-height: calc(100vh - 80px); color: #F7F3EC; padding: 60px 0; }
.detail-container { max-width: 1200px; width: 100%; display: flex;margin: 0 auto; position: relative; gap: 80px; padding: 0 40px; padding-top: 50px; }

/* 左侧大图 */
.left-gallery { flex: 1; display: flex; justify-content: center; align-items: flex-start; padding-top: 40px; }
.image-wrapper { width: 100%; max-width: 450px; aspect-ratio: 1/1; border-radius: 50%; overflow: hidden; border: 15px solid #1C1B19; box-shadow: 0 20px 50px rgba(0,0,0,0.5); background: #2A2826; }
.dish-img { width: 100%; height: 100%; object-fit: cover; }

/* 右侧信息 */
.right-info { flex: 1; display: flex; flex-direction: column; }
.header-info { margin-bottom: 30px; }
.tag { font-size: 12px; color: #D5A958; letter-spacing: 2px; margin-bottom: 15px; text-transform: uppercase; }
.title { font-family: 'STSong', serif; font-size: 48px; margin: 0 0 10px 0; letter-spacing: 2px; line-height: 1.2; }
.en-title { font-size: 14px; color: #888; letter-spacing: 2px; text-transform: uppercase; }

.description { font-size: 15px; color: #A39F9A; line-height: 1.8; margin-bottom: 40px; }

/* 参数列表（虚线边框） */
.attributes-list { list-style: none; padding: 0; margin: 0 0 30px 0; border-top: 1px dashed rgba(255,255,255,0.1); }
.attributes-list li { display: flex; justify-content: space-between; padding: 18px 0; border-bottom: 1px dashed rgba(255,255,255,0.1); font-size: 14px; }
.label { color: #888; }
.value { color: #F7F3EC; text-align: right; }
.price-value { font-size: 20px; color: #D5A958; font-family: Georgia, serif; }

.allergy-warning { font-size: 12px; color: #555; margin-bottom: 40px; }

/* 底部操作栏 */
.action-bar { display: flex; gap: 20px; margin-top: auto; padding-top: 20px; }
.stepper { display: flex; align-items: center; border: 1px solid rgba(213, 169, 88, 0.3); background: #1A1816; }
.step-btn { width: 50px; height: 50px; background: transparent; border: none; color: #D5A958; font-size: 20px; cursor: pointer; transition: background 0.3s; }
.step-btn:hover { background: rgba(213, 169, 88, 0.1); }
.step-num { width: 40px; text-align: center; font-size: 16px; color: #F7F3EC; }

.add-btn { flex: 1; height: 52px; background: #D5A958; color: #12100E; border: none; font-size: 16px; font-weight: 500; letter-spacing: 1px; cursor: pointer; transition: all 0.3s; }
.add-btn:hover { background: #E5C07B; }

.back-nav{
  position: absolute;
  top: 0;
  left: 40px;
}
.back-btn{
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #888;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.3s;
}
.back-btn:hover{
  color: #D5A958;
  transform: translateX(-3px);
}
</style>