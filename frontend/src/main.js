import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import Login from './views/Login.vue'
import UserDashboard from './views/UserDashboard.vue'
import HotelDashboard from './views/HotelDashboard.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/user/:id', component: UserDashboard, props: true },
  { path: '/hotel/:id', component: HotelDashboard, props: true }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

createApp(App).use(router).mount('#app')