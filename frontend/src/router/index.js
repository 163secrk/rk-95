import { createRouter, createWebHistory } from 'vue-router'
import { initAuth, isLoggedIn, isAdmin } from '../utils/auth'

initAuth()

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue'), meta: { public: true } },
  { path: '/', name: 'Dashboard', component: () => import('../views/Dashboard.vue'), meta: { admin: true } },
  { path: '/elders', name: 'Elders', component: () => import('../views/Elders.vue'), meta: { admin: true } },
  { path: '/meals', name: 'Meals', component: () => import('../views/Meals.vue'), meta: { admin: true } },
  { path: '/health', name: 'Health', component: () => import('../views/Health.vue'), meta: { admin: true } },
  { path: '/volunteers', name: 'Volunteers', component: () => import('../views/Volunteers.vue'), meta: { admin: true } },
  { path: '/dispatches', name: 'Dispatches', component: () => import('../views/Dispatches.vue'), meta: { admin: true } },
  { path: '/my-dispatches', name: 'MyDispatches', component: () => import('../views/MyDispatches.vue'), meta: { volunteer: true } },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.public) {
    next()
    return
  }

  if (!isLoggedIn()) {
    next({ name: 'Login' })
    return
  }

  if (to.meta.admin && !isAdmin()) {
    next({ name: 'MyDispatches' })
    return
  }

  if (to.meta.volunteer && isAdmin()) {
    next({ name: 'Dashboard' })
    return
  }

  next()
})

export default router
