import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'

export const useCartStore = defineStore('cart', () => {
    // 1. 从 localStorage 读取初始数据，实现刷新不丢失
    const items = ref(JSON.parse(localStorage.getItem('cart_items')) || [])

    // 2. 监听 items 变化，自动存入 localStorage
    watch(items, (newVal) => {
        localStorage.setItem('cart_items', JSON.stringify(newVal))
    }, { deep: true })

    // 3. 计算总数量
    const totalCount = computed(() => {
        return items.value.reduce((sum, item) => sum + item.quantity, 0)
    })

    // 4. 计算总金额
    const totalAmount = computed(() => {
        return items.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
    })

    // 5. 加入购物车
    function addToCart(dish, quantity = 1) {
        const existingItem = items.value.find(item => item.id === dish.id)
        if (existingItem) {
            // 如果已经存在，数量累加
            existingItem.quantity += quantity
        } else {
            // 如果不存在，新增一行
            items.value.push({
                id: dish.id,
                name: dish.name,
                price: dish.price,
                coverUrl: dish.coverUrl,
                quantity: quantity
            })
        }
    }

    // 6. 增加数量
    function incQuantity(id) {
        const item = items.value.find(item => item.id === id)
        if (item) item.quantity++
    }

    // 7. 减少数量
    function decQuantity(id) {
        const item = items.value.find(item => item.id === id)
        if (item) {
            item.quantity--
            if (item.quantity <= 0) {
                removeItem(id) // 数量减到0直接移除
            }
        }
    }

    // 8. 移除特定菜品
    function removeItem(id) {
        items.value = items.value.filter(item => item.id !== id)
    }

    // 9. 清空购物车
    function clearCart() {
        items.value = []
    }

    return {
        items, totalCount, totalAmount,
        addToCart, incQuantity, decQuantity, removeItem, clearCart
    }
})