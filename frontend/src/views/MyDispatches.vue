<template>
  <div>
    <t-card title="我的派单" :bordered="false">
      <template #actions>
        <t-tabs :value="activeStatus" @change="onStatusChange" size="small">
          <t-tab-panel value="all" label="全部" />
          <t-tab-panel value="已派单" label="进行中" />
          <t-tab-panel value="已完成" label="已完成" />
        </t-tabs>
      </template>
      <t-table :data="list" :columns="columns" row-key="id" :loading="loading" hover stripe>
        <template #status="{ row }">
          <t-tag :theme="dispatchTheme(row.status)">{{ row.status }}</t-tag>
        </template>
        <template #operation="{ row }">
          <t-link v-if="row.status === '已派单'" theme="success" @click="completeOrder(row)">
            标记完成
          </t-link>
        </template>
      </t-table>
    </t-card>

    <t-dialog v-model:visible="detailVisible" header="派单详情" width="500px">
      <t-descriptions :column="1" bordered>
        <t-descriptions-item label="老人姓名">{{ currentOrder.elderName }}</t-descriptions-item>
        <t-descriptions-item label="服务类型">{{ currentOrder.serviceType }}</t-descriptions-item>
        <t-descriptions-item label="服务日期">{{ currentOrder.serviceDate }}</t-descriptions-item>
        <t-descriptions-item label="服务描述">{{ currentOrder.description || '-' }}</t-descriptions-item>
        <t-descriptions-item label="状态">
          <t-tag :theme="dispatchTheme(currentOrder.status)">{{ currentOrder.status }}</t-tag>
        </t-descriptions-item>
      </t-descriptions>
    </t-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import api from '../api'
import { user } from '../utils/auth'

const list = ref([])
const loading = ref(false)
const activeStatus = ref('all')
const detailVisible = ref(false)
const currentOrder = ref({})

const columns = [
  { colKey: 'id', title: 'ID', width: 60 },
  { colKey: 'elderName', title: '老人姓名', width: 110 },
  { colKey: 'serviceType', title: '服务类型', width: 120 },
  { colKey: 'serviceDate', title: '服务日期', width: 120 },
  { colKey: 'description', title: '服务描述' },
  { colKey: 'status', title: '状态', width: 90, cell: 'status' },
  { colKey: 'operation', title: '操作', width: 100 }
]

const dispatchTheme = (s) => ({ '待派单': 'warning', '已派单': 'primary', '已完成': 'success' }[s] || 'default')

const loadList = async () => {
  loading.value = true
  try {
    const params = { volunteerId: user.value.volunteerId }
    if (activeStatus.value !== 'all') {
      params.status = activeStatus.value
    }
    list.value = await api.get('/dispatches', { params })
  } finally {
    loading.value = false
  }
}

const onStatusChange = (val) => {
  activeStatus.value = val
  loadList()
}

const completeOrder = async (row) => {
  await api.put(`/dispatches/${row.id}`, { ...row, status: '已完成' })
  MessagePlugin.success('已标记为完成')
  loadList()
}

onMounted(() => { loadList() })
</script>
