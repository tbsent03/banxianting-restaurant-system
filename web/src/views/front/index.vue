<template>
  <div class="home-page">
    <!-- 1. Hero 首屏区 -->
    <section class="hero">
      <div class="hero-bg"></div>
      <div class="hero-content">
        <p class="hero-sub">MODERN CHINESE FINE DINING · SINCE 2001</p>
        <h1 class="hero-title">半闲半隐，<br>品味山野海河之当季光景</h1>
        <p class="hero-desc">每日清晨从产地直送的食材，在柴火与时间里慢慢成形。<br>我们只做当季的八道菜。</p>

        <div class="hero-btns">
          <button class="btn-primary" @click="$router.push('/menu')">查看今日菜单</button>
          <button class="btn-outline" @click="$router.push('/reservation')">在线订位</button>
        </div>

        <div class="hero-meta">
          <span>营业 11:30 - 22:00</span>
          <span>每人 ¥ 688 起</span>
          <span>米其林指南推荐</span>
        </div>
      </div>
    </section>

    <!-- 2. 招牌菜区 -->
    <section class="section signature">
      <div class="section-header">
        <div>
          <h2 class="title">招牌菜</h2>
          <p class="subtitle">SIGNATURE</p>
        </div>
<!--        <a href="#" class="view-all">全部菜单 →</a>-->
            <span class="view-all" @click="$router.push('/menu')">全部菜单 →</span>
      </div>

      <div class="dish-grid">
        <div class="dish-card" v-for="(dish, index) in signatureDishes" :key="index" @click="goToDetail(dish.id)">
          <div class="img-wrap">
            <img :src="formatImg(dish.coverUrl)" alt="dish">
          </div>
          <div class="dish-info">
            <h3>{{ dish.name }}</h3>
            <p class="en-name">{{ dish.nameEn }}</p>
            <p class="desc">{{ dish.description }}</p>
            <div class="price-row">
              <span class="price">¥{{ dish.price }}</span>
              <button class="add-btn" @click.stop="addToCart(dish)">+</button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 3. 主厨手记 -->
    <section class="section chef-note">
      <div class="note-header">
        <h2 class="title">主厨手记</h2>
        <p class="subtitle">FROM THE KITCHEN</p>
      </div>
      <div class="note-container">
        <div class="note-img">
          <img src="@/assets/spring.avif" alt="chef">
        </div>
        <div class="note-text">
          <p class="tag">NO. 07 · 当季</p>
          <h2>春天该吃一点苦</h2>
          <p class="desc">三月的云南山野刚刚醒来。我们把苦刺花、刺嫩芽与春笋做出一道前菜，苦味在前面，回甘在后面——这是这个季节最诚实的表达。</p>
          <p class="author">— 主厨  &nbsp;&nbsp;&nbsp;陈拾</p>
        </div>
      </div>
    </section>

    <!-- 4. 预订引导 CTA -->
    <section class="section cta-section">
      <div class="cta-box">
        <p class="subtitle">RESERVATION</p>
        <h2>预订今晚的餐桌</h2>
        <p class="desc">包厢需提前一日预订 · 每位 ¥688 起 · 含八道菜与茶配</p>
        <button class="btn-primary" @click="$router.push('/reservation')">在线订位</button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute,useRouter } from 'vue-router'
import { useDiningStore } from '@/store/dining'
import request from "@/utils/http.js"
import { ElMessage,ElMessageBox } from "element-plus"
import { useCartStore} from "../../store/cart";

// 初始为空数组，等待接口填充
const signatureDishes = ref([])
const route = useRoute()
const router = useRouter()
const diningStore = useDiningStore()
const cartStore = useCartStore()

// 页面加载时去后端拿数据
onMounted(() => {
  // 1. 判断是否是手机环境
  const isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
  // 2. 判断是否带有桌号参数
  const tableToken = route.query.table

  if (isMobile && tableToken) {
    // 3. 强制跳转到移动端 hash 路由页面
    window.location.replace(`${window.location.origin}/mobile.html#/index?table=${tableToken}`)
  }
  // 1.扫码桌号拦截逻辑
  const token = route.query.table
  if (token) {
    request.get(`/table/token/${token}`).then(res => {
      if (res.code === 200 && res.data) {

        // 👇 核心修改：使用模态框强制拦截 👇
        if (res.data.status === '占用') {
          ElMessageBox.alert(
              `当前 ${res.data.tableNo} 桌正在用餐中，为避免错单，请扫码其他空闲桌位。`,
              '桌位已被占用',
              {
                confirmButtonText: '我知道了',
                type: 'warning',
                showClose: false,
                callback: () => {
                  // 用户点击“我知道了”后，清空桌位状态，让他以“自取”模式浏览
                  diningStore.clearTable()
                }
              }
          )
          return // 直接阻断，不保存桌号
        }

        // 如果是空闲，正常存入
        diningStore.setTable(res.data)
        setTimeout(() => {
          ElMessage.success(`欢迎光临半闲庭，当前桌号：${res.data.tableNo} (${res.data.area})`)
        }, 500)

      } else {
        ElMessage.error("无效的桌码，请重新扫码")
      }
    }).catch(err => {
      console.error("获取桌号失败", err)
    })
  }
  // 2.获取招牌菜逻辑
  request.get('/dish/page', {
    params: {
      pageNum: 1,
      pageSize: 3,
      isSignature: 1
    }
  }).then(res => {
    if(res.code === 200) {
      signatureDishes.value = res.data.list
    }else{
      ElMessage.error(res.msg || "获取菜品失败")
    }
  }).catch(err => {
    console.error("加载菜品失败", err)
    ElMessage.error("网络异常，无法加载菜品")
  })
})

//  自动适配局域网图片路径的函数
function formatImg(url) {
  if (!url) return '';
  // 如果当前不是用 localhost 访问的（比如用手机 IP 访问），就把图片路径里的 localhost 替换成当前 IP
  return url.replace('localhost', window.location.hostname);
}
// 跳转到详情页
function goToDetail(id) {
  router.push(`/dishDetail/${id}`)
}
//加入到购物车
function addToCart(dish) {
  cartStore.addToCart(dish, 1)
  ElMessage.success(`已将 ${dish.name} 加入点菜单`)
}
</script>

<style scoped>
.home-page { background: #12100E; color: #F7F3EC; overflow: hidden; }

/* 1. Hero 首屏 */
.hero { position: relative; height: 85vh; display: flex; align-items: center; justify-content: center; text-align: center; }
.hero-bg { position: absolute; inset: 0; background: url('https://images.unsplash.com/photo-1514933651103-005eec06c04b?q=80&w=2000') center/cover; opacity: 0.25; }
.hero-content { position: relative; z-index: 1; max-width: 800px; padding: 0 20px; }
.hero-sub { font-size: 12px; letter-spacing: 4px; color: #D5A958; margin-bottom: 30px; }
.hero-title { font-family: 'STSong', serif; font-size: 64px; line-height: 1.2; letter-spacing: 4px; margin-bottom: 30px; font-weight: 500; }
.hero-desc { font-size: 15px; line-height: 1.8; color: #A39F9A; margin-bottom: 50px; }
.hero-btns { display: flex; gap: 20px; justify-content: center; margin-bottom: 60px; }
.hero-meta { display: flex; justify-content: center; gap: 40px; font-size: 12px; color: #666; letter-spacing: 1px; }

/* 按钮样式 */
.btn-primary { background: #D5A958; color: #12100E; border: none; padding: 12px 32px; font-size: 14px; cursor: pointer; transition: all 0.3s; }
.btn-primary:hover { background: #E5C07B; transform: translateY(-2px); }
.btn-outline { background: transparent; color: #D5A958; border: 1px solid #D5A958; padding: 12px 32px; font-size: 14px; cursor: pointer; transition: all 0.3s; }
.btn-outline:hover { background: rgba(213, 169, 88, 0.1); }

/* 通用区块样式 */
.section { padding: 100px 0; max-width: 1300px; margin: 0 auto; padding-left: 40px; padding-right: 40px; }
.section-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 50px; }
.title { font-family: 'STSong', serif; font-size: 32px; margin: 0 0 10px 0; letter-spacing: 2px; }
.subtitle { font-size: 12px; letter-spacing: 3px; color: #666; margin: 0; }
.view-all { color: #D5A958; font-size: 14px; text-decoration: none;cursor: pointer; }

/* 2. 招牌菜卡片 */
.dish-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 30px; }
.dish-card { background: #1A1816; border: 1px solid rgba(255,255,255,0.03); padding: 20px; transition: transform 0.3s; }
.dish-card:hover { transform: translateY(-5px); }
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

/* 3. 主厨手记 */
.chef-note { background: #0A0908; border-top: 1px solid rgba(255,255,255,0.02); border-bottom: 1px solid rgba(255,255,255,0.02); max-width: 100%; }
/*主厨手记的标题样式 */
.note-header {
  /* 宽度、居中和内边距必须和下方的 note-container 保持一致 */
  max-width: 1300px;
  margin: 0 auto;
  padding: 0 40px;
  margin-bottom: 40px; /* 标题和下方图片的间距 */
  text-align: left; /* 明确左对齐 */
}

/* 标题字体样式，复用你招牌菜的样式 */
.note-header .title {
  font-family: 'STSong', serif;
  font-size: 32px;
  letter-spacing: 2px;
  margin: 0 0 10px 0;
  color: #F7F3EC; /* 确保文字是白色 */
}
.note-container { max-width: 1300px; margin: 0 auto; display: grid; grid-template-columns: 1fr 1fr; align-items: center; gap: 80px; padding: 0 40px; }
.note-img { width: 100%; aspect-ratio: 4/3; overflow: hidden; }
.note-img img { width: 100%; height: 100%; object-fit: cover; opacity: 0.8; }
.note-text { padding: 40px 0; }
.tag { font-size: 16px; color: #D5A958; letter-spacing: 2px; margin-bottom: 20px; }
.note-text h2 { font-family: 'STSong', serif; font-size: 36px; margin-bottom: 30px; letter-spacing: 2px; }
.note-text .desc { font-size: 15px; line-height: 2; margin-bottom: 40px; height: auto; }
.author { font-size: 14px; color: #666; font-style: italic; }

/* 4. CTA 预订引导 */
.cta-section { text-align: center; padding: 120px 20px; }
.cta-box { border: 1px solid rgba(213, 169, 88, 0.3); padding: 100px 40px; max-width: 2000px; width: 100%; margin: 0 auto;
  background: url('@/assets/reservation-bg.jpeg') center/cover no-repeat;
  position: relative; }
.cta-box::before { content: ''; position: absolute; inset: 0; background: rgba(18, 16, 14, 0.4); }
.cta-box > * { position: relative; z-index: 1; }
.cta-box h2 { font-family: 'STSong', serif; font-size: 36px; margin-bottom: 20px; letter-spacing: 4px; }
.cta-box .desc { font-size: 13px; color: #888; margin-bottom: 40px; }
</style>