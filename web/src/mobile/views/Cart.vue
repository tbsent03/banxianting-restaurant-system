<template>
  <div class="m-cart">
    <div class="header-title">
      <h2>点菜单</h2>
    </div>
    <div class="cart-list" v-if="cartStore.items.length > 0">
      <div class="cart-item" v-for="item in cartStore.items" :key="item.id">
        <img :src="formatImg(item.coverUrl)" class="cover">
        <div class="info">
          <h3>{{ item.name }}</h3>
          <div class="bottom">
            <span class="price">¥{{ item.price }}</span>
            <div class="stepper-wrap">
              <van-stepper
                  :model-value="item.quantity"
                  min="1"
                  theme="round"
                  button-size="22"
                  @plus="cartStore.incQuantity(item.id)"
                  @minus="cartStore.decQuantity(item.id)"
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <van-empty v-else description="您的点菜单空空如也" />

    <!-- 吸底结算条 -->
    <div class="bottom-bar" v-if="cartStore.items.length > 0">
      <div class="left">
        <span class="total-label">合计</span>
        <span class="total-price">¥{{ cartStore.totalAmount.toFixed(2) }}</span>
      </div>
      <van-button round color="#D5A958" style="color: #12100E; font-weight: bold; width: 120px;" @click="$router.push('/checkout')">
        去结算
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { useCartStore } from '@/store/cart'
import { formatImg } from '@/utils/img.js'

const cartStore = useCartStore()
</script>

<style scoped>
.m-cart {
  padding: 16px;
  min-height: calc(100dvh - 50px);
  background: var(--m-bg);
  padding-bottom: 80px;
}
.header-title h2 {
  font-family: 'Songti SC', serif;
  font-size: 24px;
  color: var(--m-text);
  margin-bottom: 20px;
}
.cart-item {
  display: flex;
  gap: 12px;
  background: var(--m-surface);
  padding: 12px;
  border-radius: var(--m-radius);
  margin-bottom: 12px;
}
.cover {
  width: 70px;
  height: 70px;
  border-radius: 8px;
  object-fit: cover;
}
.info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.info h3 {
  font-size: 15px;
  margin: 0;
}
.bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  color: var(--m-gold);
  font-size: 16px;
  font-family: 'Georgia', serif;
}
.bottom-bar {
  position: fixed;
  bottom: calc(50px + env(safe-area-inset-bottom));
  left: 0;
  right: 0;
  background: var(--m-surface);
  padding: 10px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid rgba(213, 169, 88, 0.2);
  z-index: 98;
}
.total-label {
  font-size: 13px;
  color: var(--m-muted);
  margin-right: 8px;
}
.total-price {
  font-size: 22px;
  color: var(--m-gold);
  font-weight: bold;
  font-family: 'Georgia', serif;
}
</style>