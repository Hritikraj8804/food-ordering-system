import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import Login from './views/Login.vue'
import UserDashboard from './views/UserDashboard.vue'
import HotelDashboard from './views/HotelDashboard.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
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
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard to protect routes
router.beforeEach((to, from, next) => {
  const currentUser = JSON.parse(localStorage.getItem('currentUser') || 'null')
  
  if (to.meta.requiresAuth && !currentUser) {
    next('/login')
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