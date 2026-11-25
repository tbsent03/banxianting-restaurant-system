<template>
  <div class="m-home">
    <!-- 1. Hero 首屏区 -->
    <section class="hero">
      <div class="hero-bg"></div>
      <div class="hero-content">
        <p class="hero-sub">MODERN CHINESE FINE DINING</p>
        <h1 class="hero-title">半闲半隐，<br>品味山野海河之当季光景</h1>
        <p class="hero-desc">每日清晨从产地直送的食材，<br>在柴火与时间里慢慢成形。</p>

        <div class="hero-btns">
          <van-button class="btn-solid" @click="$router.push('/menu')">查看今日菜单</van-button>
          <van-button class="btn-outline" @click="$router.push('/reservation')">在线订位</van-button>
        </div>

        <div class="hero-meta">
          <span>营业时间
            <p>11:30 - 22:00</p>
          </span>
          <span>每人
            <p>¥ 688 起</p></span>
          <span>米其林
            <p>五星指南推荐</p>
          </span>
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
        <span class="view-all" @click="$router.push('/menu')">全部菜单 →</span>
      </div>

      <div class="dish-list">
        <div class="dish-card" v-for="dish in signatureDishes" :key="dish.id" @click="goToDetail(dish.id)">
          <div class="img-wrap">
            <img :src="formatImg(dish.coverUrl)" alt="dish">
          </div>
          <div class="dish-info">
            <h3>{{ dish.name }}</h3>
            <p class="en-name">{{ dish.nameEn }}</p>
            <p class="desc">{{ dish.description }}</p>
            <div class="price-row">
              <span class="price">¥{{ dish.price }}</span>
              <div class="add-btn" @click.stop="addToCart(dish)">
                <van-icon name="plus" />
              </div>
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
          <p class="author">— 主厨 &nbsp; 陈拾</p>
        </div>
      </div>
    </section>

    <!-- 4. 预订引导 CTA -->
    <section class="section cta-section">
      <div class="cta-box">
        <p class="subtitle">RESERVATION</p>
        <h2>预订今晚的餐桌</h2>
        <p class="desc">包厢需提前一日预订 · 每位 ¥688 起</p>
        <van-button round color="#D5A958" style="color: #12100E; font-weight: bold; width: 140px; margin-top: 10px;" @click="$router.push('/reservation')">在线订位</van-button>
      </div>
    </section>

    <footer class="footer">
      <div class="footer-container">
        <div class="footer-col">
          <h3 class="footer-logo">半闲庭</h3>
          <p>现代中华料理 · 当季八道</p>
          <p>上海市静安区和平路 88 号 2F</p>
        </div>
        <div class="footer-col">
          <h4>营业时间</h4>
          <p>午市 11:30 - 14:00</p>
          <p>晚市 18:30 - 22:00</p>
        </div>
        <div class="footer-col">
          <h4>联系方式</h4>
          <p>021-6288 0088</p>
          <p>hello@banxianting.com</p>
        </div>
        <div class="footer-col">
          <h4>关注我们</h4>
          <p>微信公众号 · 大众点评</p>
          <p>© 2026 半闲庭餐饮</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p class="culture-slogan">品味中华美食 · 传承节俭美德 · 倡导绿色餐饮</p>
        <p class="copyright-text">© 2026 半闲庭 BANXIANTING. All Rights Reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast } from 'vant'
import http from '@/utils/http.js'
import { formatImg } from '@/utils/img.js'
import { useCartStore } from '@/store/cart'

const router = useRouter()
const cartStore = useCartStore()
const signatureDishes = ref([])

onMounted(() => {
  fetchSignature()
})

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

function addToCart(dish) {
  cartStore.addToCart(dish, 1)
  showSuccessToast(`已将 ${dish.name} 加入点菜单`)
}
</script>

<style scoped>
.m-home { background: var(--m-bg); color: var(--m-text); overflow: hidden; padding-bottom: 20px; }

/* 1. Hero 首屏 */
.hero { position: relative; height: 65vh; display: flex; align-items: center; justify-content: center; text-align: center; }
.hero-bg {
  position: absolute; inset: 0;
  background: url('https://images.unsplash.com/photo-1514933651103-005eec06c04b?q=80&w=800') center/cover;
  opacity: 0.55;
  filter: brightness(1.1);
}
.hero-bg::after {
  content: ''; position: absolute; inset: 0;
  background: linear-gradient(to bottom, rgba(18,16,14,0) 0%, rgba(18,16,14,0.3) 40%, var(--m-bg) 100%);
}
.hero-content { position: relative; z-index: 1; padding: 0 20px; width: 100%; }
.hero-sub { font-size: 11px; letter-spacing: 2px; color: var(--m-gold); margin-bottom: 20px; }
.hero-title { font-family: 'Songti SC', serif; font-size: 32px; line-height: 1.3; letter-spacing: 2px; margin-bottom: 20px; font-weight: 500; }
.hero-desc { font-size: 13px; line-height: 1.8; color: var(--m-text-2); margin-bottom: 40px; }
.hero-btns { display: flex; gap: 12px; justify-content: center; margin-bottom: 40px;}
.btn-solid {
  background: #D5A958 !important;
  color: #12100E !important;
  border: 1px solid #D5A958 !important;
  border-radius: 2px !important; /* 方框质感 */
  height: 40px !important;
  padding: 0 24px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
}
.btn-outline {
  background: transparent !important;
  color: #D5A958 !important;
  border: 1px solid #D5A958 !important;
  border-radius: 2px !important;
  height: 40px !important;
  padding: 0 24px !important;
  font-size: 14px !important;
}
.hero-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  color: var(--m-muted);
  letter-spacing: 1px;
  padding: 0 10px;
}

/* 通用区块 */
.section { padding: 40px 16px; }
.section-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 24px; }
.title { font-family: 'Songti SC', serif; font-size: 22px; margin: 0 0 6px 0; letter-spacing: 1px; }
.subtitle { font-size: 10px; letter-spacing: 2px; color: var(--m-muted); margin: 0; }
.view-all { color: var(--m-gold); font-size: 13px; }

/* 2. 招牌菜（移动端改为一列纵向卡片） */
.dish-list { display: flex; flex-direction: column; gap: 16px; }
.dish-card { background: var(--m-surface); border: 1px solid rgba(255,255,255,0.03); border-radius: var(--m-radius); overflow: hidden; }
.img-wrap { width: 100%; aspect-ratio: 16/9; background: var(--m-surface-2); }
.img-wrap img { width: 100%; height: 100%; object-fit: cover; }
.dish-info { padding: 16px; }
.dish-info h3 { font-family: 'Songti SC', serif; font-size: 18px; margin: 0 0 4px 0; }
.en-name { font-size: 10px; color: var(--m-gold); letter-spacing: 1px; margin-bottom: 10px; text-transform: uppercase; }
.desc { font-size: 12px; color: var(--m-muted); line-height: 1.5; margin-bottom: 16px; display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 2; overflow: hidden; }
.price-row { display: flex; justify-content: space-between; align-items: center; border-top: 1px solid rgba(255,255,255,0.05); padding-top: 12px; }
.price { font-size: 18px; color: var(--m-gold); font-family: 'Georgia', serif; }
.add-btn { width: 28px; height: 28px; border-radius: 50%; background: transparent; border: 1px solid var(--m-gold); color: var(--m-gold); display: flex; align-items: center; justify-content: center; font-size: 16px; }

/* 3. 主厨手记 */
.chef-note { background: #0A0908; border-top: 1px solid rgba(255,255,255,0.02); border-bottom: 1px solid rgba(255,255,255,0.02); }
.note-header { margin-bottom: 20px; }
.note-container { display: flex; flex-direction: column; gap: 20px; }
.note-img { width: 100%; aspect-ratio: 4/3; overflow: hidden; border-radius: var(--m-radius); }
.note-img img { width: 100%; height: 100%; object-fit: cover; opacity: 0.8; }
.tag { font-size: 12px; color: var(--m-gold); letter-spacing: 1px; margin-bottom: 10px; }
.note-text h2 { font-family: 'Songti SC', serif; font-size: 22px; margin-bottom: 16px; }
.note-text .desc { font-size: 13px; line-height: 1.8; margin-bottom: 20px; color: var(--m-text-2); display: block; }
.author { font-size: 12px; color: var(--m-muted); font-style: italic; text-align: right; }

/* 4. CTA 预订引导 */
.cta-section { padding: 20px 16px 40px; }
.cta-box {
  border-radius: var(--m-radius); padding: 40px 20px; text-align: center;
  background: url('@/assets/reservation-bg.jpeg') center/cover no-repeat; position: relative;
  border: 1px solid rgba(213, 169, 88, 0.3); overflow: hidden;
}
.cta-box::before { content: ''; position: absolute; inset: 0; background: rgba(18, 16, 14, 0.3); }
.cta-box > * { position: relative; z-index: 1; }
.cta-box h2 { font-family: 'Songti SC', serif; font-size: 22px; margin: 10px 0; letter-spacing: 2px; }
.cta-box .desc { font-size: 12px; color: var(--m-muted); margin-bottom: 20px; }

.footer { background: #0A0908; padding: 40px 0 80px; border-top: 1px solid rgba(255,255,255,0.05); margin-top: 20px;}
.footer-container { display: grid; grid-template-columns: 1fr 1fr; gap: 30px 10px; padding: 0 16px; }
.footer-logo { font-family: 'Songti SC', serif; font-size: 18px; letter-spacing: 2px; margin-bottom: 12px; color: var(--m-gold); }
.footer-col h4 { font-size: 13px; color: var(--m-text); margin-bottom: 12px; font-weight: normal; }
.footer-col p { font-size: 11px; color: var(--m-muted); margin: 8px 0; line-height: 1.6; }

.footer-bottom {
  text-align: center;
  padding: 20px 16px 0;
  margin-top: 30px;
  border-top: 1px solid rgba(212, 175, 55, 0.2);
}
.culture-slogan {
  color: var(--m-gold);
  font-size: 12px;
  letter-spacing: 1px;
  margin-bottom: 8px;
  font-weight: 500;
}
.copyright-text {
  color: #666;
  font-size: 10px;
  letter-spacing: 1px;
}
</style>