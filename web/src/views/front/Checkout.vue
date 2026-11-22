<template>
  <div class="checkout-page">
    <div class="checkout-container">
      <h2 class="title">确认订单</h2>

      <div class="form-section">
        <el-form :model="form" label-width="100px">
          <el-form-item label="用餐方式">
            <el-radio-group v-model="form.orderType">
              <el-radio label="堂食">堂食</el-radio>
              <el-radio label="自取">自取</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="联系人">
            <el-input v-model="form.contactName" placeholder="请输入称呼"></el-input>
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="form.contactTel" placeholder="请输入手机号"></el-input>
          </el-form-item>
          <el-form-item label="忌口备注">
            <el-input type="textarea" v-model="form.remark" placeholder="如有忌口请备注"></el-input>
          </el-form-item>
        </el-form>
      </div>

      <div class="summary-section">
        <div class="slogan-box">
          <div class="slogan-icon">🌿</div>
          <div class="slogan-text">
            <strong>半闲庭 · 绿色倡议</strong>
            <p>一粥一饭，当思来之不易。请按需点餐，践行“光盘行动”，拒绝舌尖上的浪费。</p>
          </div>
        </div>
        <div class="total-row">
          <span>合计金额：</span>
          <span class="price">¥{{ cartStore.totalAmount.toFixed(2) }}</span>
        </div>
        <button class="submit-btn" @click="submitOrder">确认下单</button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/store/cart'
import request from "@/utils/http.js"
import { ElMessage } from 'element-plus'
import { useDiningStore } from '@/store/dining'
const diningStore = useDiningStore()
const cartStore = useCartStore()
const router = useRouter()

const form = ref({
  orderType: diningStore.currentTable ? '堂食' : '自取',
  contactName: '',
  contactTel: '',
  remark: ''
})

function submitOrder() {
  if (cartStore.items.length === 0) return

  const requestItems = cartStore.items.map(item => {
    return {
      dishId: item.id,         // 前端叫 id，后端叫 dishId，这里做一层转换
      quantity: item.quantity
    }
  })

  const payload = {
    orderType: form.value.orderType,
    contactName: form.value.contactName,
    contactTel: form.value.contactTel,
    remark: form.value.remark,
    tableId: diningStore.currentTable ? diningStore.currentTable.id : null,
    items: requestItems // 直接把购物车数组传过去
  }

  request.post('/order/submit', payload).then(res => {
    if (res.code === 200) {
      ElMessage.success('下单成功！')
      cartStore.clearCart() // 清空本地购物车
      router.push('/myOrders') // 跳转到订单页
    }
  })
}

function formatImg(url) {
  if (!url) return '';
  // 如果当前不是用 localhost 访问的（比如用手机 IP 访问），就把图片路径里的 localhost 替换成当前 IP
  return url.replace('localhost', window.location.hostname);
}
</script>

<style scoped>
.checkout-page { background: #12100E; min-height: 100vh; padding: 60px 0; color: #F7F3EC; }
.checkout-container { max-width: 800px; margin: 0 auto; background: #1A1816; padding: 40px; border-radius: 8px; }
.title { font-family: 'STSong', serif; border-bottom: 1px solid rgba(255,255,255,0.1); padding-bottom: 20px; margin-bottom: 30px; }
.summary-section { text-align: right; margin-top: 40px; border-top: 1px solid rgba(255,255,255,0.1); padding-top: 20px; }
.total-row { font-size: 20px; margin-bottom: 20px; }
.total-row .price { color: #D5A958; font-weight: bold; }
.submit-btn { background: #D5A958; color: #12100E; border: none; padding: 15px 40px; font-size: 16px; font-weight: bold; cursor: pointer; border-radius: 4px; }
.slogan-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #f4f9f4; /* 极浅的环保绿 */
  justify-content: center;
  text-align: center;
  border: 1px solid #d4edda;
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.02);
}
.slogan-icon {
  font-size: 24px;
  margin-right: 12px;
}
.slogan-text strong {
  color: #2e7d32; /* 深绿 */
  font-size: 14px;
  display: block;
  margin-bottom: 4px;
}
.slogan-text p {
  color: #666;
  font-size: 12px;
  margin: 0;
  line-height: 1.5;
}
@media (max-width: 768px) {
  .checkout-container {
    width: 90% !important; /* 覆盖掉原本可能写死的宽度 */
    padding: 20px;
    margin: 20px auto;
  }
}
</style>