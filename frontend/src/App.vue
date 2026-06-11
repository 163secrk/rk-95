<template>
  <div v-if="isLoginPage" class="login-page">
    <router-view />
  </div>
  <t-layout v-else class="app-layout">
    <t-aside class="app-aside">
      <div class="logo">
        <t-icon name="home" size="24px" />
        <span>居家养老服务</span>
      </div>
      <t-menu :value="activeMenu" theme="dark" @change="onMenuChange">
        <template v-if="isAdmin">
          <t-menu-item value="Dashboard">
            <template #icon><t-icon name="dashboard" /></template>
            数据总览
          </t-menu-item>
          <t-menu-item value="Elders">
            <template #icon><t-icon name="user" /></template>
            老人管理
          </t-menu-item>
          <t-menu-item value="Meals">
            <template #icon><t-icon name="food" /></template>
            助餐预约
          </t-menu-item>
          <t-menu-item value="Health">
            <template #icon><t-icon name="heart" /></template>
            健康登记
          </t-menu-item>
          <t-menu-item value="Volunteers">
            <template #icon><t-icon name="usergroup" /></template>
            志愿者管理
          </t-menu-item>
          <t-menu-item value="Dispatches">
            <template #icon><t-icon name="assignment" /></template>
            志愿者派单
          </t-menu-item>
        </template>
        <template v-else>
          <t-menu-item value="MyDispatches">
            <template #icon><t-icon name="assignment" /></template>
            我的派单
          </t-menu-item>
        </template>
      </t-menu>
    </t-aside>
    <t-layout>
      <t-header class="app-header">
        <div class="header-right">
          <t-dropdown @click="handleUserMenu">
            <div class="user-info">
              <t-icon name="user-circle" size="20px" />
              <span>{{ user?.name || '用户' }}</span>
              <t-icon name="chevron-down" size="16px" />
            </div>
            <template #dropdown>
              <t-dropdown-menu>
                <t-dropdown-item value="profile">
                  <template #prefix><t-icon name="user" /></template>
                  {{ user?.role === 'admin' ? '管理员' : '志愿者' }}
                </t-dropdown-item>
                <t-dropdown-item value="logout">
                  <template #prefix><t-icon name="logout" /></template>
                  退出登录
                </t-dropdown-item>
              </t-dropdown-menu>
            </template>
          </t-dropdown>
        </div>
      </t-header>
      <t-content class="app-content">
        <router-view />
      </t-content>
    </t-layout>
  </t-layout>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { user, clearAuth, isAdmin as checkAdmin, isLoggedIn } from './utils/auth'
import { MessagePlugin } from 'tdesign-vue-next'

const router = useRouter()
const route = useRoute()
const activeMenu = computed(() => route.name)

const isLoginPage = computed(() => route.name === 'Login')
const isAdmin = computed(() => checkAdmin())

const onMenuChange = (val) => {
  router.push({ name: val })
}

const handleUserMenu = (value) => {
  if (value === 'logout') {
    clearAuth()
    MessagePlugin.success('已退出登录')
    router.push({ name: 'Login' })
  }
}
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
html, body, #app { height: 100%; }
.login-page { height: 100vh; }
.app-layout { height: 100vh; }
.app-aside {
  width: 220px !important;
  background: #001529;
}
.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.app-header {
  height: 64px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 24px;
}
.header-right {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #333;
  font-size: 14px;
}
.user-info:hover {
  color: #0052d9;
}
.app-content {
  padding: 24px;
  background: #f0f2f5;
  overflow-y: auto;
  height: calc(100vh - 64px);
}
</style>
