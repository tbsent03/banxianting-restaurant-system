import {createRouter, createWebHistory} from 'vue-router'
import AdminLayout from "@/views/layout/AdminLayout.vue";
import FrontLayout from "@/views/layout/FrontLayout.vue";

const router = createRouter({
    history: createWebHistory(),
    routes: getRoutes(),
    //新增：控制路由切换时的滚动行为
    scrollBehavior(to, from, savedPosition) {
        // 如果浏览器记录了之前的滚动位置（即点击了返回/后退），就恢复该位置
        if (savedPosition) {
            return savedPosition
        } else {
            // 如果是新打开的页面，则滚动到顶部
            return { top: 0 }
        }
    }
})

function getRoutes() {
    let defaultRoutes = [
        {
          path:'/',
          name:'front',
          component: FrontLayout,
          redirect: '/index',
          children: [
              {
              path: 'index',
              name: 'Index',
              component:()=>
              import('../views/front/index.vue')
              },
              {
                  path: 'menu',
                  name: 'Menu',
                  component: () => import('../views/front/Menu.vue')
              },
              //  新增详情页路由
              {
                  path: 'dishDetail/:id',
                  name: 'DishDetail',
                  component: () => import('../views/front/DishDetail.vue')
              },
              {
                  path: 'editCurrentUser',
                  name: 'front-editCurrentUser',
                  component: () =>
                      import ('../views/EditCurrentUser.vue')
              },
              {
                  path: 'editPassword',
                  name: 'front-editPassword',
                  component: () =>
                      import ('../views/EditPassword.vue')
              },
              {
                  path:'cart',
                  name:'Cart',
                  component:() =>
                      import('../views/front/Cart.vue')
              },
              {
                  path:'checkout',
                  name:'Checkout',
                  component:() => import('../views/front/Checkout.vue')
              },
              {
                  path:'myOrders',
                  name:'MyOrders',
                  component:() => import('../views/front/MyOrders.vue')
              },
              {
                  path: 'reservation',
                  name: 'Reservation',
                  component: () => import('../views/front/Reservation.vue')
              },
              {
                  path: 'about',
                  name: 'About',
                  component: () => import('../views/front/About.vue')
              },
              {
                  path: 'myReservations',
                  name: 'MyReservations',
                  component: () => import('../views/front/MyReservations.vue')
              },
          ]
        },
        {
            path: '/admin',
            name: 'admin',
            component: AdminLayout,
            redirect: "/admin/home",
            children: [{
                path: "home",
                name: "admin-home",
                component: () =>
                    import ('../views/admin/Home.vue')
            },
                {
                    path: 'editCurrentUser',
                    name: 'admin-editCurrentUser',
                    component: () =>
                        import ('../views/EditCurrentUser.vue')
                },
                {
                    path: 'editPassword',
                    name: 'admin-editPassword',
                    component: () =>
                        import ('../views/EditPassword.vue')
                },
                {
                    path: 'admin',
                    name: 'Admin',
                    component: () =>
                        import ('../views/admin/AdminManage.vue')
                },
                {
                    path:'user',
                    name:'User',
                    component:() =>
                        import('../views/admin/UserManage.vue')
                },
                {
                    path: 'category',
                    name: 'Category',
                    component: () => import('../views/admin/CategoryManage.vue')
                },
                {
                    path: 'dish',
                    name: 'Dish',
                    component: () => import('../views/admin/DishManage.vue')
                },
                {
                    path:'order',
                    name:'Order',
                    component:() => import('../views/admin/OrderManage.vue')
                },
                {
                    path: 'table',
                    name: 'Table',
                    component:() => import('../views/admin/TableManage.vue')
                },
                {
                    path: 'reservation',
                    name: 'AdminReservation',
                    component: () => import('../views/admin/ReservationManage.vue')
                },
                {
                    path: 'review',
                    name: 'AdminReview', // 注意别和前台重名
                    component: () => import('../views/admin/ReviewManage.vue')
                }
            ]
        },
        {
            path: "/login",
            name: "login",
            component: () =>
                import ('../views/Login.vue')
        }, {
            path: "/register",
            name: "register",
            component: () =>
                import ('../views/Register.vue')
        }, {
            path: "/retrievePassword",
            name: "front-retrievePassword",
            component: () =>
                import ('../views/RetrievePassword.vue')
        }];
    defaultRoutes.push({
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        meta: {
            name: ''
        },
        component: () => import ('../views/NotFound.vue')
    })
    console.log('getDynamicRoutes', defaultRoutes)
    return defaultRoutes;
}

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token');

    //定义不需要登录就能访问的白名单
    const whiteList = ['/login','/register','retrievePassword'];

    if(token){
        //如果有token，正常放行
        next();
    }
    else{
        //如果没有token
        if(whiteList.includes(to.path)){
            next();//如果在白名单里，放行
        }
        else{
            //未登录且去非白名单页面，跳转登录，并把原本的完整路径（含参数token）带过去
            next({
                path:'/login',
                query:{ redirect:to.fullPath }
            })
        }
    }
});
export default router
