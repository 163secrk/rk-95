import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Dashboard', component: () => import('../views/Dashboard.vue') },
  { path: '/elders', name: 'Elders', component: () => import('../views/Elders.vue') },
  { path: '/meals', name: 'Meals', component: () => import('../views/Meals.vue') },
  { path: '/health', name: 'Health', component: () => import('../views/Health.vue') },
  { path: '/volunteers', name: 'Volunteers', component: () => import('../views/Volunteers.vue') },
  { path: '/dispatches', name: 'Dispatches', component: () => import('../views/Dispatches.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
