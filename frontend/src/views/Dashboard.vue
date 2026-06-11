<template>
  <div>
    <t-row :gutter="[16, 16]">
      <t-col :span="3" v-for="card in statCards" :key="card.label">
        <t-card class="stat-card" :bordered="false">
          <div class="stat-icon" :style="{ background: card.color }">
            <t-icon :name="card.icon" size="28px" style="color:#fff" />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-label">{{ card.label }}</div>
          </div>
        </t-card>
      </t-col>
    </t-row>
    <t-row :gutter="[16, 16]" style="margin-top:16px">
      <t-col :span="12">
        <t-card title="快捷入口" :bordered="false">
          <t-row :gutter="[16, 16]">
            <t-col :span="4" v-for="action in quickActions" :key="action.label">
              <div class="quick-action" @click="$router.push(action.route)">
                <t-icon :name="action.icon" size="32px" :style="{ color: action.color }" />
                <div class="quick-label">{{ action.label }}</div>
              </div>
            </t-col>
          </t-row>
        </t-card>
      </t-col>
      <t-col :span="12">
        <t-card title="系统说明" :bordered="false">
          <t-list :split="true">
            <t-list-item>
              <t-list-item-meta title="助餐预约" description="为社区老人提供午餐、晚餐预约服务，确保营养膳食" />
            </t-list-item>
            <t-list-item>
              <t-list-item-meta title="健康登记" description="定期记录老人血压、心率、血糖等健康指标，跟踪健康状况" />
            </t-list-item>
            <t-list-item>
              <t-list-item-meta title="志愿者派单" description="根据老人需求指派志愿者上门服务，实现精准帮扶" />
            </t-list-item>
          </t-list>
        </t-card>
      </t-col>
    </t-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'

const stats = ref({
  elderCount: 0,
  todayMealCount: 0,
  todayHealthCount: 0,
  pendingDispatchCount: 0,
  volunteerCount: 0
})

const statCards = ref([])
const quickActions = [
  { label: '新增老人', icon: 'user-add', color: '#0052D9', route: '/elders' },
  { label: '助餐预约', icon: 'food', color: '#E37318', route: '/meals' },
  { label: '健康登记', icon: 'heart', color: '#D54941', route: '/health' },
  { label: '志愿者派单', icon: 'assignment', color: '#2BA471', route: '/dispatches' },
  { label: '志愿者管理', icon: 'usergroup', color: '#8B5CF6', route: '/volunteers' },
  { label: '老人管理', icon: 'user', color: '#029CD4', route: '/elders' }
]

const loadStats = async () => {
  try {
    stats.value = await api.get('/dashboard/stats')
    const s = stats.value
    statCards.value = [
      { label: '老人总数', value: s.elderCount, icon: 'user', color: '#0052D9' },
      { label: '今日助餐', value: s.todayMealCount, icon: 'food', color: '#E37318' },
      { label: '今日健康登记', value: s.todayHealthCount, icon: 'heart', color: '#D54941' },
      { label: '待派单', value: s.pendingDispatchCount, icon: 'assignment', color: '#8B5CF6' },
      { label: '志愿者数', value: s.volunteerCount, icon: 'usergroup', color: '#2BA471' },
      { label: '累计服务', value: s.elderCount + s.volunteerCount, icon: 'star', color: '#029CD4' },
      { label: '预约总量', value: s.todayMealCount, icon: 'calendar', color: '#F59E0B' },
      { label: '健康跟踪', value: s.todayHealthCount, icon: 'chart', color: '#10B981' }
    ]
  } catch (e) {
    console.error(e)
  }
}

onMounted(loadStats)
</script>

<style scoped>
.stat-card { display: flex; align-items: center; }
.stat-card :deep(.t-card__body) { display: flex; align-items: center; gap: 16px; width: 100%; }
.stat-icon { width: 56px; height: 56px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.stat-info { flex: 1; }
.stat-value { font-size: 28px; font-weight: 700; color: #1a1a1a; line-height: 1.2; }
.stat-label { font-size: 13px; color: #999; margin-top: 4px; }
.quick-action { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 20px 0; cursor: pointer; border-radius: 8px; transition: all 0.2s; }
.quick-action:hover { background: #f5f7fa; }
.quick-label { margin-top: 8px; font-size: 14px; color: #333; }
</style>
