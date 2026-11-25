<template>
  <div class="m-checkout">
    <van-nav-bar title="确认订单" left-arrow @click-left="$router.back()" :border="false" style="background: var(--m-surface);" title-class="nav-text" />

    <!-- 桌位/用餐方式 -->
    <div class="section">
      <div class="table-info" v-if="diningStore.currentTable">
        <van-icon name="location" color="#D5A958" size="20" />
        <div>
          <h3>堂食 · {{ diningStore.currentTable.tableNo }}</h3>
          <p>{{ diningStore.currentTable.area }}</p>
        </div>
      </div>
      <div class="table-info" v-else>
        <van-icon name="shop-o" color="#D5A958" size="20" />
        <div>
          <h3>自取</h3>
          <p>未绑定桌位，默认自取</p>
        </div>
      </div>
    </div>

    <!-- 表单区 -->
    <div class="section form-section">
      <van-form @submit="onSubmit">
        <van-field
            v-model="formData.contactName"
            name="contactName"
            label="联系人"
            placeholder="请输入姓名"
            :rules="[{ required: true, message: '请填写联系人' }]"
        />
        <van-field
            v-model="formData.contactTel"
            name="contactTel"
            type="tel"
            label="手机号"
            placeholder="请输入手机号"
            :rules="[{ required: true, message: '请填写手机号' }]"
        />
        <van-field
            v-model="formData.remark"
            name="remark"
            label="备注"
            type="textarea"
            placeholder="口味偏好、忌口等（选填）"
            rows="2"
            autosize
        />

        <!-- 菜品清单简略版 -->
        <div class="order-summary">
          <div class="summary-title">菜品明细</div>
          <div class="summary-item" v-for="item in cartStore.items" :key="item.id">
            <span>{{ item.name }} <span class="multiply">x{{ item.quantity }}</span></span>
            <span>¥{{ (item.price * item.quantity).toFixed(2) }}</span>
          </div>
          <div class="summary-total">
            合计 <span>¥{{ cartStore.totalAmount.toFixed(2) }}</span>
          </div>
        </div>

        <div class="submit-wrap">
          <van-button
              round
              block
              color="#D5A958"
              style="color: #12100E; font-weight: bold; height: 44px;"
              native-type="submit"
              :loading="loading"
          >
            确认下单
          </van-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showFailToast } from 'vant'
import http from '@/utils/http.js'
import { useCartStore } from '@/store/cart'
import { useDiningStore } from '@/store/dining'

const router = useRouter()
const cartStore = useCartStore()
const diningStore = useDiningStore()

const loading = ref(false)
const formData = ref({
  contactName: '',
  contactTel: '',
  remark: ''
})

const onSubmit = () => {
  if (cartStore.items.length === 0) {
    showFailToast('点菜单为空，无法下单')
    return
  }

  loading.value = true

  // 构建载荷（与原有的 PC 端接口对齐）
  const payload = {
    orderType: diningStore.currentTable ? '堂食' : '自取',
    contactName: formData.value.contactName,
    contactTel: formData.value.contactTel,
    remark: formData.value.remark,
    tableId: diningStore.currentTable?.id || null,
    items: cartStore.items.map(item => ({
      dishId: item.id,
      quantity: item.quantity
    }))
  }

  http.post('/order/submit', payload).then(res => {
    if (res.code === 200) {
      showSuccessToast('下单成功')
      cartStore.clearCart() // 清空本地购物车

      // 延迟跳转到订单列表页
      setTimeout(() => {
        router.replace('/orders')
      }, 800)
    } else {
      showFailToast(res.msg || '下单失败')
    }
  }).catch((error) => {
    // 新增：拦截 409 错误，通常是桌位被占用或库存不足
    if (error.response && error.response.status === 409) {
      // 提示用户具体的错误信息（后端返回的 msg 或者 data）
      const errorMsg = error.response.data?.data || error.response.data?.msg || '订单提交冲突'
      showFailToast(errorMsg)

      // 如果是因为桌位原因，强制清除桌位，让用户重新扫
      if (errorMsg.includes('桌') || errorMsg.includes('占用')) {
        diningStore.clearTable()
        setTimeout(() => {
          router.replace('/index')
        }, 1500)
      }
    }
  }).finally(() => {
    loading.value = false
  })
}
</script>

<style scoped>
.m-checkout {
  min-height: 100dvh;
  background: var(--m-bg);
  padding-bottom: 30px;
}
.nav-text {
  color: var(--m-text) !important;
}
.section {
  margin: 12px 16px;
  background: var(--m-surface);
  border-radius: var(--m-radius);
  overflow: hidden;
}
.table-info {
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.table-info h3 {
  font-size: 16px;
  margin: 0 0 4px 0;
  color: var(--m-gold);
}
.table-info p {
  font-size: 12px;
  color: var(--m-muted);
  margin: 0;
}
.form-section {
  background: transparent;
}
/* 覆盖 Vant 的默认白底 */
:deep(.van-cell-group), :deep(.van-field) {
  background: var(--m-surface);
  color: var(--m-text);
}
:deep(.van-field__label) {
  color: var(--m-muted);
}
:deep(.van-field__control) {
  color: var(--m-text);
}
.order-summary {
  background: var(--m-surface);
  padding: 16px;
  margin-top: 12px;
  border-radius: var(--m-radius);
}
.summary-title {
  font-size: 14px;
  color: var(--m-muted);
  margin-bottom: 12px;
}
.summary-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  margin-bottom: 8px;
}
.multiply {
  color: var(--m-muted);
  font-size: 12px;
}
.summary-total {
  text-align: right;
  font-size: 14px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed rgba(255,255,255,0.1);
}
.summary-total span {
  color: var(--m-gold);
  font-size: 18px;
  font-weight: bold;
  font-family: 'Georgia', serif;
}
.submit-wrap {
  margin-top: 24px;
}
</style>