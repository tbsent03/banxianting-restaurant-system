import { createRouter, createWebHashHistory } from 'vue-router'
import MLayout from '@/mobile/layout/MLayout.vue'

const routes = [
    { path: '/', redirect: '/home' },
    { path: '/login', component: () => import('@/mobile/views/Login.vue') },
    { path: '/register', component: () => import('@/mobile/views/Register.vue') },
    {
        path: '/',
        component: MLayout,
        children: [
            { path: 'home', component: () => import('@/mobile/views/Home.vue') },
            { path: 'index', component: () => import('@/mobile/views/Index.vue') },
            { path: 'menu', component: () => import('@/mobile/views/Menu.vue') },
            { path: 'dish/:id', component: () => import('@/mobile/views/DishDetail.vue') },
            { path: 'cart', component: () => import('@/mobile/views/Cart.vue'), meta: { requiresAuth: true } },
            { path: 'checkout', component: () => import('@/mobile/views/Checkout.vue'), meta: { requiresAuth: true } },
            { path: 'orders', component: () => import('@/mobile/views/Orders.vue'), meta: { requiresAuth: true } },
            { path: 'reservation', component: () => import('@/mobile/views/Reservation.vue') },
            { path: 'myReservations', component: () => import('@/mobile/views/MyReservations.vue'), meta: { requiresAuth: true } },
            { path: 'me', component: () => import('@/mobile/views/Me.vue'), meta: { requiresAuth: true } },
            { path: 'profile', component: () => import('@/mobile/views/Profile.vue'), meta: { requiresAuth: true } },
        ]
    },
]

const router = createRouter({
    history: createWebHashHistory(),
    routes,
})

router.beforeEach((to, from, next) => {
    document.title = '半闲庭 · 点餐'
    const token = localStorage.getItem('token')
    const requiresAuth = to.meta.requiresAuth

    if (requiresAuth && !token) {
        next({ path: '/login', query: { redirect: to.fullPath } })
    } else {
        next()
    }
})

export default router