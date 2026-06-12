<template>
  <div>
    <t-card title="我的派单" :bordered="false">
      <template #actions>
        <t-tabs :value="activeStatus" @change="onStatusChange" size="small">
          <t-tab-panel value="all" label="全部" />
          <t-tab-panel value="已派单" label="待开始" />
          <t-tab-panel value="服务中" label="服务中" />
          <t-tab-panel value="已完成" label="已完成" />
        </t-tabs>
      </template>
      <t-table :data="list" :columns="columns" row-key="id" :loading="loading" hover stripe>
        <template #status="{ row }">
          <t-tag :theme="dispatchTheme(row.status)">{{ row.status }}</t-tag>
        </template>
        <template #timer="{ row }">
          <template v-if="row.status === '服务中'">
            <span class="timer-text">
              <t-icon name="time" /> {{ formatDuration(row._elapsed || 0) }}
            </span>
          </template>
          <template v-else-if="row.status === '已完成'">
            <span class="duration-text">{{ formatDuration(row.actualDuration || 0) }}</span>
          </template>
          <template v-else>
            <span class="text-gray">-</span>
          </template>
        </template>
        <template #timeInfo="{ row }">
          <div v-if="row.serviceStartTime" class="text-sm">
            <div>开始: {{ row.serviceStartTime }}</div>
            <div v-if="row.serviceEndTime">结束: {{ row.serviceEndTime }}</div>
          </div>
          <span v-else class="text-gray">-</span>
        </template>
        <template #operation="{ row }">
          <t-link v-if="row.status === '已派单'" theme="primary" @click="startService(row)">
            开始服务
          </t-link>
          <t-link v-if="row.status === '服务中'" theme="success" @click="completeService(row)">
            完成服务
          </t-link>
          <t-link theme="default" style="margin-left:8px" @click="viewDetail(row)">
            详情
          </t-link>
        </template>
      </t-table>
    </t-card>

    <t-dialog v-model:visible="detailVisible" header="派单详情" width="550px">
      <t-descriptions :column="1" bordered>
        <t-descriptions-item label="老人姓名">{{ currentOrder.elderName }}</t-descriptions-item>
        <t-descriptions-item label="服务类型">{{ currentOrder.serviceType }}</t-descriptions-item>
        <t-descriptions-item label="服务日期">{{ currentOrder.serviceDate }}</t-descriptions-item>
        <t-descriptions-item label="服务描述">{{ currentOrder.description || '-' }}</t-descriptions-item>
        <t-descriptions-item label="状态">
          <t-tag :theme="dispatchTheme(currentOrder.status)">{{ currentOrder.status }}</t-tag>
        </t-descriptions-item>
        <t-descriptions-item v-if="currentOrder.serviceStartTime" label="服务开始时间">
          {{ currentOrder.serviceStartTime }}
        </t-descriptions-item>
        <t-descriptions-item v-if="currentOrder.serviceEndTime" label="服务结束时间">
          {{ currentOrder.serviceEndTime }}
        </t-descriptions-item>
        <t-descriptions-item v-if="currentOrder.actualDuration" label="实际服务时长">
          {{ formatDuration(currentOrder.actualDuration) }}
        </t-descriptions-item>
        <t-descriptions-item v-if="currentOrder.rating" label="服务评分">
          <t-rate v-model="currentOrder.rating" :allow-half="false" readonly />
        </t-descriptions-item>
        <t-descriptions-item v-if="currentOrder.reviewComment" label="评价备注">
          {{ currentOrder.reviewComment }}
        </t-descriptions-item>
      </t-descriptions>
    </t-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import api from '../api'
import { user } from '../utils/auth'

const list = ref([])
const loading = ref(false)
const activeStatus = ref('all')
const detailVisible = ref(false)
const currentOrder = ref({})
let timerInterval = null

const columns = [
  { colKey: 'id', title: 'ID', width: 60 },
  { colKey: 'elderName', title: '老人姓名', width: 110 },
  { colKey: 'serviceType', title: '服务类型', width: 120 },
  { colKey: 'serviceDate', title: '服务日期', width: 120 },
  { colKey: 'timer', title: '服务时长', width: 120, cell: 'timer' },
  { colKey: 'timeInfo', title: '时间信息', width: 180, cell: 'timeInfo' },
  { colKey: 'status', title: '状态', width: 90, cell: 'status' },
  { colKey: 'operation', title: '操作', width: 160 }
]

const dispatchTheme = (s) => ({ '待派单': 'warning', '已派单': 'primary', '服务中': 'danger', '已完成': 'success' }[s] || 'default')

const formatDuration = (minutes) => {
  if (!minutes) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours > 0) {
    return `${hours}小时${mins}分钟`
  }
  return `${mins}分钟`
}

const calculateElapsed = (startTime) => {
  if (!startTime) return 0
  const start = new Date(startTime.replace(' ', 'T')).getTime()
  const now = Date.now()
  return Math.floor((now - start) / 60000)
}

const updateTimers = () => {
  list.value.forEach(item => {
    if (item.status === '服务中' && item.serviceStartTime) {
      item._elapsed = calculateElapsed(item.serviceStartTime)
    }
  })
}

const loadList = async () => {
  loading.value = true
  try {
    const params = { volunteerId: user.value.volunteerId }
    if (activeStatus.value !== 'all') {
      params.status = activeStatus.value
    }
    list.value = await api.get('/dispatches', { params })
    updateTimers()
  } finally {
    loading.value = false
  }
}

const onStatusChange = (val) => {
  activeStatus.value = val
  loadList()
}

const startService = async (row) => {
  try {
    await api.post(`/dispatches/${row.id}/start`)
    MessagePlugin.success('服务已开始')
    loadList()
  } catch (e) {
    MessagePlugin.error('开始服务失败')
  }
}

const completeService = async (row) => {
  try {
    await api.post(`/dispatches/${row.id}/complete`)
    MessagePlugin.success('服务已完成')
    loadList()
  } catch (e) {
    MessagePlugin.error('完成服务失败')
  }
}

const viewDetail = (row) => {
  currentOrder.value = { ...row }
  detailVisible.value = true
}

onMounted(() => {
  loadList()
  timerInterval = setInterval(updateTimers, 1000)
})

onUnmounted(() => {
  if (timerInterval) {
    clearInterval(timerInterval)
  }
})
</script>

<style scoped>
.timer-text {
  color: #e34d59;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}
.duration-text {
  color: #00a870;
  font-weight: 500;
}
.text-gray {
  color: #909399;
}
.text-sm {
  font-size: 12px;
  color: #606266;
}
</style>
