import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useDiningStore = defineStore('dining', () => {
    // 从本地存储读取，防止刷新丢失
    const currentTable = ref(JSON.parse(localStorage.getItem('current_table')) || null)

    // 设置当前桌位
    function setTable(table) {
        currentTable.value = table
        localStorage.setItem('current_table', JSON.stringify(table))
    }

    // 清除桌位（比如顾客结账离开后）
    function clearTable() {
        currentTable.value = null
        localStorage.removeItem('current_table')
    }

    return { currentTable, setTable, clearTable }
})