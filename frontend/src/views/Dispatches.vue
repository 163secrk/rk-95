<template>
  <div>
    <t-card title="志愿者派单" :bordered="false">
      <template #actions>
        <t-button theme="primary" @click="openDialog(null)">
          <template #icon><t-icon name="add" /></template>
          新增派单
        </t-button>
      </template>
      <t-table :data="list" :columns="columns" row-key="id" :loading="loading" hover stripe>
        <template #status="{ row }">
          <t-tag :theme="dispatchTheme(row.status)">{{ row.status }}</t-tag>
        </template>
        <template #operation="{ row }">
          <t-link v-if="row.status === '待派单'" theme="primary" @click="openDialog(row)">派单</t-link>
          <t-link v-if="row.status === '已派单'" theme="success" @click="completeOrder(row)">完成</t-link>
          <t-link theme="danger" style="margin-left:8px" @click="handleDelete(row)">删除</t-link>
        </template>
      </t-table>
    </t-card>

    <t-dialog v-model:visible="dialogVisible" :header="editingId ? '派单' : '新增派单'" @confirm="handleSave" :confirm-btn="{ loading: saving }" width="500px">
      <t-form :data="formData" layout="vertical">
        <t-form-item label="老人">
          <t-select v-model="formData.elderId" placeholder="请选择老人" filterable :disabled="!!editingId">
            <t-option v-for="e in elders" :key="e.id" :value="e.id" :label="e.name" />
          </t-select>
        </t-form-item>
        <t-form-item label="服务类型">
          <t-select v-model="formData.serviceType" placeholder="请选择服务类型">
            <t-option value="上门陪聊" label="上门陪聊" />
            <t-option value="助餐服务" label="助餐服务" />
            <t-option value="家政清洁" label="家政清洁" />
            <t-option value="医疗陪护" label="医疗陪护" />
            <t-option value="代购代办" label="代购代办" />
            <t-option value="出行陪护" label="出行陪护" />
          </t-select>
        </t-form-item>
        <t-form-item label="服务日期"><t-date-picker v-model="formData.serviceDate" clearable style="width:100%" /></t-form-item>
        <t-form-item label="指派志愿者" v-if="editingId">
          <t-select v-model="formData.volunteerId" placeholder="请选择志愿者" filterable clearable>
            <t-option v-for="v in volunteers" :key="v.id" :value="v.id" :label="`${v.name} - ${v.skill || '无特长'}`" />
          </t-select>
        </t-form-item>
        <t-form-item label="服务描述"><t-textarea v-model="formData.description" placeholder="请输入服务描述" /></t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import api from '../api'

const list = ref([])
const elders = ref([])
const volunteers = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const editingId = ref(null)
const formData = ref({ elderId: null, volunteerId: null, serviceType: '', serviceDate: '', description: '' })

const columns = [
  { colKey: 'id', title: 'ID', width: 60 },
  { colKey: 'elderName', title: '老人姓名', width: 100 },
  { colKey: 'volunteerName', title: '志愿者', width: 100 },
  { colKey: 'serviceType', title: '服务类型', width: 110 },
  { colKey: 'serviceDate', title: '服务日期', width: 120 },
  { colKey: 'description', title: '服务描述' },
  { colKey: 'status', title: '状态', width: 90, cell: 'status' },
  { colKey: 'operation', title: '操作', width: 140 }
]

const dispatchTheme = (s) => ({ '待派单': 'warning', '已派单': 'primary', '已完成': 'success' }[s] || 'default')

const loadList = async () => {
  loading.value = true
  try { list.value = await api.get('/dispatches') } finally { loading.value = false }
}

const loadElders = async () => {
  elders.value = await api.get('/elders')
}

const loadVolunteers = async () => {
  volunteers.value = await api.get('/volunteers')
}

const openDialog = (row) => {
  if (row) {
    editingId.value = row.id
    formData.value = { elderId: row.elderId, volunteerId: row.volunteerId, serviceType: row.serviceType, serviceDate: row.serviceDate, description: row.description }
  } else {
    editingId.value = null
    formData.value = { elderId: null, volunteerId: null, serviceType: '', serviceDate: '', description: '' }
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try {
    if (editingId.value) {
      const payload = { ...formData.value, status: formData.value.volunteerId ? '已派单' : '待派单' }
      await api.put(`/dispatches/${editingId.value}`, payload)
      MessagePlugin.success('派单成功')
    } else {
      await api.post('/dispatches', formData.value)
      MessagePlugin.success('新增成功')
    }
    dialogVisible.value = false
    loadList()
    loadVolunteers()
  } finally { saving.value = false }
}

const completeOrder = async (row) => {
  await api.put(`/dispatches/${row.id}`, { ...row, status: '已完成' })
  MessagePlugin.success('已标记为完成')
  loadList()
  loadVolunteers()
}

const handleDelete = async (row) => {
  await api.delete(`/dispatches/${row.id}`)
  MessagePlugin.success('删除成功')
  loadList()
}

onMounted(() => { loadList(); loadElders(); loadVolunteers() })
</script>
