<template>
  <div>
    <t-card title="志愿者管理" :bordered="false">
      <template #actions>
        <t-button theme="primary" @click="openDialog(null)">
          <template #icon><t-icon name="add" /></template>
          新增志愿者
        </t-button>
      </template>
      <t-table :data="list" :columns="columns" row-key="id" :loading="loading" hover stripe>
        <template #status="{ row }">
          <t-tag :theme="row.status === '空闲' ? 'success' : row.status === '服务中' ? 'warning' : 'default'">{{ row.status }}</t-tag>
        </template>
        <template #operation="{ row }">
          <t-link theme="primary" @click="openDialog(row)">编辑</t-link>
          <t-link theme="danger" style="margin-left:8px" @click="handleDelete(row)">删除</t-link>
        </template>
      </t-table>
    </t-card>

    <t-dialog v-model:visible="dialogVisible" :header="editingId ? '编辑志愿者' : '新增志愿者'" @confirm="handleSave" :confirm-btn="{ loading: saving }" width="450px">
      <t-form :data="formData" layout="vertical">
        <t-form-item label="姓名"><t-input v-model="formData.name" placeholder="请输入姓名" /></t-form-item>
        <t-form-item label="电话"><t-input v-model="formData.phone" placeholder="请输入电话" /></t-form-item>
        <t-form-item label="特长">
          <t-select v-model="formData.skill" placeholder="请选择特长" clearable>
            <t-option value="陪聊" label="陪聊" />
            <t-option value="做饭" label="做饭" />
            <t-option value="清洁" label="清洁" />
            <t-option value="医疗护理" label="医疗护理" />
            <t-option value="代购" label="代购" />
            <t-option value="出行陪护" label="出行陪护" />
          </t-select>
        </t-form-item>
        <t-form-item label="状态">
          <t-select v-model="formData.status" placeholder="请选择状态">
            <t-option value="空闲" label="空闲" />
            <t-option value="服务中" label="服务中" />
            <t-option value="休假" label="休假" />
          </t-select>
        </t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import api from '../api'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const editingId = ref(null)
const formData = ref({ name: '', phone: '', skill: '', status: '空闲' })

const columns = [
  { colKey: 'id', title: 'ID', width: 60 },
  { colKey: 'name', title: '姓名', width: 100 },
  { colKey: 'phone', title: '电话', width: 140 },
  { colKey: 'skill', title: '特长', width: 100 },
  { colKey: 'status', title: '状态', width: 90, cell: 'status' },
  { colKey: 'operation', title: '操作', width: 120 }
]

const loadList = async () => {
  loading.value = true
  try { list.value = await api.get('/volunteers') } finally { loading.value = false }
}

const openDialog = (row) => {
  if (row) {
    editingId.value = row.id
    formData.value = { name: row.name, phone: row.phone, skill: row.skill, status: row.status }
  } else {
    editingId.value = null
    formData.value = { name: '', phone: '', skill: '', status: '空闲' }
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try {
    if (editingId.value) {
      await api.put(`/volunteers/${editingId.value}`, formData.value)
      MessagePlugin.success('更新成功')
    } else {
      await api.post('/volunteers', formData.value)
      MessagePlugin.success('新增成功')
    }
    dialogVisible.value = false
    loadList()
  } finally { saving.value = false }
}

const handleDelete = async (row) => {
  await api.delete(`/volunteers/${row.id}`)
  MessagePlugin.success('删除成功')
  loadList()
}

onMounted(loadList)
</script>
