<template>
  <div class="m-bottom-bar" v-if="cartStore.totalCount > 0">
    <div class="info">
      <div class="count">共 {{ cartStore.totalCount }} 份</div>
      <div class="price">¥{{ cartStore.totalAmount.toFixed(2) }}</div>
    </div>
    <button class="go-btn" @click="$router.push('/cart')">去结算</button>
  </div>
</template>

<script setup>
import { useCartStore } from '@/store/cart'
const cartStore = useCartStore()
</script>

<style scoped>
.m-bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  /* 避开底部的 Tabbar */
  bottom: calc(50px + env(safe-area-inset-bottom));
  z-index: 98;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  background: rgba(26, 24, 22, 0.95);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(213, 169, 88, 0.2);
}
.info {
  display: flex;
  flex-direction: column;
}
.count {
  font-size: 12px;
  color: var(--m-muted);
  margin-bottom: 2px;
}
.price {
  font-size: 20px;
  color: var(--m-gold);
  font-weight: bold;
  font-family: 'Georgia', serif;
}
.go-btn {
  min-width: 100px;
  height: 40px;
  background: var(--m-gold);
  color: #12100E;
  border: none;
  border-radius: 20px;
  font-size: 15px;
  font-weight: bold;
}
</style>