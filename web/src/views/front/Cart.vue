<template>
  <div class="cart-page">
    <div class="cart-container">
      <h2 class="title">我的点菜单</h2>

      <div v-if="cartStore.items.length === 0" class="empty-state">
        <el-empty description="您的点菜单还是空的" />
        <el-button type="primary" @click="$router.push('/menu')">去逛逛菜单</el-button>
      </div>

      <div v-else class="cart-content">
        <!--左侧：购物车列表-->
        <div class="cart-list">
          <div class="cart-item" v-for="item in cartStore.items" :key="item.id">
            <img :src="formatImg(item.coverUrl)" class="item-img">
            <div class="item-info">
              <h3>{{ item.name }}</h3>
              <p class="price">¥{{ item.price }}</p>
            </div>
            <div class="item-actions">
              <div class="stepper">
                <button @click="cartStore.decQuantity(item.id)">-</button>
                <span>{{ item.quantity }}</span>
                <button @click="cartStore.incQuantity(item.id)">+</button>
              </div>
              <button class="delete-btn" @click="cartStore.removeItem(item.id)">删除</button>
            </div>
          </div>
        </div>
        <!--右侧：结算汇总-->
        <div class="cart-summary">
          <h3>订单汇总</h3>
          <div class="summary-row">
            <span>菜品总数</span>
            <span>{{ cartStore.totalCount }} 份</span>
          </div>
          <div class="summary-row total">
            <span>合计金额</span>
            <span class="price">¥{{ cartStore.totalAmount.toFixed(2) }}</span>
          </div>
          <button class="checkout-btn" @click="goToCheckout">
            去结算
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useCartStore } from "../../store/cart";
import { useRouter } from "vue-router";

const cartStore = useCartStore()
const router = useRouter()

function goToCheckout(){
  //结算页
  router.push('/checkout')
}

function formatImg(url) {
  if (!url) return '';
  // 如果当前不是用 localhost 访问的（比如用手机 IP 访问），就把图片路径里的 localhost 替换成当前 IP
  return url.replace('localhost', window.location.hostname);
}
</script>

<style scoped>
.cart-page { background: #12100E; min-height: 100vh; padding: 60px 0; color: #F7F3EC; }
.cart-container { max-width: 1000px; margin: 0 auto; padding: 0 40px; }
.title { font-family: 'STSong', serif; font-size: 32px; margin-bottom: 40px; border-bottom: 1px solid rgba(255,255,255,0.1); padding-bottom: 20px; }

.cart-content { display: flex; gap: 40px; }
.cart-list { flex: 1; }
.cart-item { display: flex; align-items: center; background: #1A1816; padding: 20px; border-radius: 8px; margin-bottom: 20px; }
.item-img { width: 80px; height: 80px; object-fit: cover; border-radius: 8px; margin-right: 20px; }
.item-info { flex: 1; }
.item-info h3 { font-size: 18px; margin: 0 0 10px 0; font-family: 'STSong', serif; }
.item-info .price { color: #D5A958; font-size: 18px; }
.item-actions { display: flex; flex-direction: column; align-items: flex-end; gap: 10px; }
.stepper { display: flex; align-items: center; border: 1px solid rgba(255,255,255,0.2); border-radius: 4px; }
.stepper button { background: none; border: none; color: #fff; padding: 5px 10px; cursor: pointer; }
.stepper span { padding: 0 10px; }
.delete-btn { background: none; border: none; color: #C0705C; font-size: 12px; cursor: pointer; }

.cart-summary { width: 300px; background: #1A1816; padding: 30px; border-radius: 8px; height: fit-content; }
.cart-summary h3 { margin-top: 0; border-bottom: 1px solid rgba(255,255,255,0.1); padding-bottom: 15px; }
.summary-row { display: flex; justify-content: space-between; margin: 20px 0; color: #888; }
.summary-row.total { color: #F7F3EC; font-size: 20px; }
.summary-row.total .price { color: #D5A958; font-weight: bold; }
.checkout-btn { width: 100%; background: #D5A958; color: #12100E; border: none; padding: 15px; font-size: 16px; font-weight: bold; margin-top: 20px; cursor: pointer; border-radius: 4px; }
.empty-state { text-align: center; padding: 100px 0; }
@media (max-width: 768px) {
  .cart-container { padding: 0 20px; }
  .cart-content { flex-direction: column; gap: 20px; }
  .cart-summary { width: 100%; box-sizing: border-box; }
  .cart-item { padding: 15px; }
  .item-img { width: 60px; height: 60px; margin-right: 15px; }
  .item-info h3 { font-size: 16px; }
}
</style>