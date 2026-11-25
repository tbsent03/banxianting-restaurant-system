import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import 'vant/lib/index.css' // 全量引入 Vant 样式
import '@/styles/mobile.css'
import Vant from 'vant' // 🌟 新增：引入 Vant 组件库
const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(Vant)
app.mount('#mobile-app')