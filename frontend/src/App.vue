<template>
  <t-layout class="app-layout">
    <t-aside class="app-aside">
      <div class="logo">
        <t-icon name="home" size="24px" />
        <span>居家养老服务</span>
      </div>
      <t-menu :value="activeMenu" theme="dark" @change="onMenuChange">
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
      </t-menu>
    </t-aside>
    <t-layout>
      <t-content class="app-content">
        <router-view />
      </t-content>
    </t-layout>
  </t-layout>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const activeMenu = computed(() => route.name)

const onMenuChange = (val) => {
  router.push({ name: val })
}
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
html, body, #app { height: 100%; }
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
.app-content {
  padding: 24px;
  background: #f0f2f5;
  overflow-y: auto;
  height: calc(100vh);
}
</style>
