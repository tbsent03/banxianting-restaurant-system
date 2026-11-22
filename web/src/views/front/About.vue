<template>
  <div class="about-page">
    <!-- 1. 首屏 Hero -->
    <section class="hero">
      <div class="hero-bg"></div>
      <div class="hero-content">
        <p class="hero-sub">ABOUT BANXIAN TING</p>
        <h1 class="hero-title">大隐于市，半闲半庭</h1>
        <p class="hero-desc">关于半闲庭的东方美食哲学</p>
      </div>
    </section>

    <!-- 2. 品牌起源 -->
    <section class="section story">
      <div class="story-container">
        <div class="story-img">
          <img src="https://images.unsplash.com/photo-1550966871-3ed3cdb5ed0c?q=80&w=800" alt="空间">
        </div>
        <div class="story-text">
          <h2>始于 2001</h2>
          <p>半闲庭始于 2001 年的江南水乡。我们相信，最好的味道来自于四季的流转与自然的馈赠。</p>
          <p>“半闲”是一种生活态度，在快节奏的现代生活中，我们为您留出一半的闲适；“半庭”则是一处静谧的空间，青砖黛瓦，水墨屏风，隔绝了世俗的喧嚣。</p>
          <p>在这里，时间会慢下来，食材会在柴火与时间里慢慢成形。</p>
        </div>
      </div>
    </section>

    <!-- 3. 主厨理念 -->
    <section class="section philosophy">
      <div class="philosophy-content">
        <img class="chef-avatar" src="https://images.unsplash.com/photo-1583394838336-acd977736f90?q=80&w=400" alt="主厨">
        <h2 class="quote">“我们不创造味道，<br>我们只是山林与海洋的搬运工。”</h2>
        <p class="author">— 主厨 陈拾</p>
      </div>
    </section>

    <!-- 4. 空间展示 -->
    <section class="section spaces">
      <div class="spaces-header">
        <h2>一桌一景，一室一境</h2>
      </div>
      <div class="gallery">
        <img src="@/assets/hall.png" alt="大堂">
        <img src="@/assets/terrace.png" alt="包厢">
        <img src="@/assets/vip.png" alt="露台">
      </div>
    </section>
  </div>
</template>
<script setup>
import imgHall from '@/assets/hall.png'
import imgTerrace from '@/assets/terrace.png'
import imgVip from '@/assets/vip.png'
const spaces = [
  {
    id: 1,
    title: '大厅',
    subtitle: '墨影鎏金，人间烟火',
    desc: '步入半闲，光影是这里的第一道前菜...（你的文案）',
    img: imgHall
  },
  {
    id: 2,
    title: '露台',
    subtitle: '星空微风，隐世绿洲',
    desc: '于半空之上，偷得半日之闲...（你的文案）',
    img: imgTerrace
  },
  {
    id: 3,
    title: 'VIP包厢',
    subtitle: '隐世雅集，私享尊荣',
    desc: '一扇门，隔开两处世界...（你的文案）',
    img: imgVip
  }
]
</script>
<style scoped>
.about-page { background: #12100E; color: #F7F3EC; min-height: 100vh; }

/* 1. Hero */
.hero { position: relative; height: 70vh; display: flex; align-items: center; justify-content: center; text-align: center; }
.hero-bg { position: absolute; inset: 0; background: url('https://images.unsplash.com/photo-1514933651103-005eec06c04b?q=80&w=2000') center/cover; opacity: 0.3; }
.hero-content { position: relative; z-index: 1; }
.hero-sub { font-size: 12px; letter-spacing: 4px; color: #D5A958; margin-bottom: 20px; }
.hero-title { font-family: 'STSong', serif; font-size: 56px; letter-spacing: 4px; margin-bottom: 20px; font-weight: 500; }
.hero-desc { font-size: 16px; color: #A39F9A; letter-spacing: 2px; }

/* 通用 */
.section { padding: 100px 40px; max-width: 1200px; margin: 0 auto; }

/* 2. 品牌故事 */
.story-container { display: flex; gap: 60px; align-items: center; }
.story-img { flex: 1; }
.story-img img { width: 100%; aspect-ratio: 4/5; object-fit: cover; border-radius: 8px; filter: grayscale(20%); }
.story-text { flex: 1; }
.story-text h2 { font-family: 'STSong', serif; font-size: 36px; color: #D5A958; margin-bottom: 30px; }
.story-text p { font-size: 16px; color: #A39F9A; line-height: 2; margin-bottom: 20px; }

/* 3. 主厨理念 */
.philosophy { text-align: center; background: #1A1816; max-width: 100%; border-top: 1px solid rgba(255,255,255,0.05); border-bottom: 1px solid rgba(255,255,255,0.05); }
.philosophy-content { max-width: 800px; margin: 0 auto; }
.chef-avatar { width: 100px; height: 100px; border-radius: 50%; object-fit: cover; margin-bottom: 30px; border: 2px solid #D5A958; padding: 4px; }
.quote { font-family: 'STSong', serif; font-size: 32px; line-height: 1.6; margin-bottom: 20px; color: #F7F3EC; }
.author { font-size: 16px; color: #D5A958; letter-spacing: 2px; }

/* 4. 空间展示 */
.spaces-header { text-align: center; margin-bottom: 50px; }
.spaces-header h2 { font-family: 'STSong', serif; font-size: 32px; letter-spacing: 2px; }
.gallery { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.gallery img { width: 100%; aspect-ratio: 1/1; object-fit: cover; border-radius: 8px; transition: transform 0.5s; }
.gallery img:hover { transform: scale(1.02); }

@media (max-width: 768px) {
  .hero-title { font-size: 36px; }
  .story-container { flex-direction: column; gap: 30px; }
  .gallery { grid-template-columns: 1fr; }
  .quote { font-size: 24px; }
}
</style>