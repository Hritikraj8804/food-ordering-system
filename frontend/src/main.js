import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import Login from './views/Login.vue'
import UserDashboard from './views/UserDashboard.vue'
import HotelDashboard from './views/HotelDashboard.vue'
import ReviewsPage from './views/ReviewsPage.vue'
import OrderHistory from './views/OrderHistory.vue'
import UserOrderHistory from './views/UserOrderHistory.vue'
import ProfilePage from './views/ProfilePage.vue'

const routes = [
  { path: '/', component: Login },
  { 
    path: '/user/:id', 
    component: UserDashboard, 
    props: true,
    meta: { requiresAuth: true, role: 'USER' }
  },
  { 
    path: '/hotel/:id', 
    component: HotelDashboard, 
    props: true,
    meta: { requiresAuth: true, role: 'HOTEL' }
  },
  { 
    path: '/hotel/:id/reviews', 
    component: ReviewsPage, 
    props: true,
    meta: { requiresAuth: true, role: 'HOTEL' }
  },
  { 
    path: '/hotel/:id/orders', 
    component: OrderHistory, 
    props: true,
    meta: { requiresAuth: true, role: 'HOTEL' }
  },
  { 
    path: '/user/:id/orders', 
    component: UserOrderHistory, 
    props: true,
    meta: { requiresAuth: true, role: 'USER' }
  },
  { 
    path: '/profile/:id', 
    component: ProfilePage, 
    props: true,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard to protect routes
router.beforeEach((to, from, next) => {
  const currentUser = JSON.parse(localStorage.getItem('user') || 'null')
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && (!currentUser || !token)) {
    next('/')
  } else if (to.meta.role && currentUser && currentUser.role !== to.meta.role) {
    // Redirect to appropriate dashboard if user tries to access wrong role page
    if (currentUser.role === 'USER') {
      next(`/user/${currentUser.id}`)
    } else {
      next(`/hotel/${currentUser.id}`)
    }
  } else {
    next()
  }
})

createApp(App).use(router).mount('#app')