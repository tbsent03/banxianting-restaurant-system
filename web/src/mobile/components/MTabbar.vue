<template>
  <van-tabbar
      route
      safe-area-inset-bottom
      active-color="#D5A958"
      inactive-color="rgba(213, 169, 88, 0.6)"
      class="black-gold-tabbar"
  >
    <van-tabbar-item replace to="/home" icon="home-o">首页</van-tabbar-item>
    <van-tabbar-item replace to="/menu" icon="shop-o">点餐</van-tabbar-item>
    <van-tabbar-item replace to="/orders" icon="orders-o">订单</van-tabbar-item>
    <van-tabbar-item replace to="/reservation" icon="calendar-o">订位</van-tabbar-item>
    <van-tabbar-item replace to="/me" icon="user-o">我的</van-tabbar-item>
  </van-tabbar>
</template>

<!-- 去掉了 scoped，使用全局最高优先级覆盖 -->
<style>
:root {
  --van-tabbar-background: #12100E;
}

/* 强制 Tabbar 本身和它周围的容器都是纯黑 */
.black-gold-tabbar,
.van-tabbar,
.van-tabbar__placeholder,
.van-safe-area-bottom {
  background-color: #12100E !important;
  background: #12100E !important;
  box-shadow: none !important;
}

/* 3. 加一条细金线作为分隔，防止底部糊在一起 */
.van-tabbar {
  border-top: 1px solid rgba(213, 169, 88, 0.15) !important;
}

/* 4. 让图标和文字精致一点 */
.van-tabbar-item__icon {
  font-size: 20px;
  margin-bottom: 3px;
}
.van-tabbar-item__text {
  font-size: 12px;
  letter-spacing: 1px;
  font-weight: 500;
}
</style>