<template>
  <div>
    <t-card title="老人管理" :bordered="false">
      <template #actions>
        <t-button theme="primary" @click="openDialog(null)">
          <template #icon><t-icon name="add" /></template>
          新增老人
        </t-button>
      </template>
      <t-table :data="list" :columns="columns" row-key="id" :loading="loading" hover stripe>
        <template #operation="{ row }">
          <t-link theme="primary" @click="openDialog(row)">编辑</t-link>
          <t-link theme="danger" style="margin-left:8px" @click="handleDelete(row)">删除</t-link>
        </template>
      </t-table>
    </t-card>

    <t-dialog v-model:visible="dialogVisible" :header="editingId ? '编辑老人' : '新增老人'" @confirm="handleSave" :confirm-btn="{ loading: saving }">
      <t-form :data="formData" :rules="rules" ref="formRef" layout="vertical">
        <t-form-item label="姓名" name="name"><t-input v-model="formData.name" placeholder="请输入姓名" /></t-form-item>
        <t-form-item label="性别">
          <t-radio-group v-model="formData.gender">
            <t-radio value="男">男</t-radio>
            <t-radio value="女">女</t-radio>
          </t-radio-group>
        </t-form-item>
        <t-form-item label="年龄"><t-input-number v-model="formData.age" :min="1" :max="150" /></t-form-item>
        <t-form-item label="电话"><t-input v-model="formData.phone" placeholder="请输入电话" /></t-form-item>
        <t-form-item label="住址"><t-input v-model="formData.address" placeholder="请输入住址" /></t-form-item>
        <t-form-item label="紧急联系人"><t-input v-model="formData.emergencyContact" placeholder="请输入紧急联系人" /></t-form-item>
        <t-form-item label="紧急联系电话"><t-input v-model="formData.emergencyPhone" placeholder="请输入紧急联系电话" /></t-form-item>
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
const formData = ref({ name: '', gender: '男', age: 65, phone: '', address: '', emergencyContact: '', emergencyPhone: '' })
const formRef = ref(null)
const rules = {
  name: [{ required: true, message: '请输入姓名', type: 'error' }]
}

const columns = [
  { colKey: 'id', title: 'ID', width: 60 },
  { colKey: 'name', title: '姓名', width: 100 },
  { colKey: 'gender', title: '性别', width: 60 },
  { colKey: 'age', title: '年龄', width: 60 },
  { colKey: 'phone', title: '电话', width: 130 },
  { colKey: 'address', title: '住址' },
  { colKey: 'emergencyContact', title: '紧急联系人', width: 100 },
  { colKey: 'emergencyPhone', title: '紧急联系电话', width: 130 },
  { colKey: 'operation', title: '操作', width: 120 }
]

const loadList = async () => {
  loading.value = true
  try { list.value = await api.get('/elders') } finally { loading.value = false }
}

const openDialog = (row) => {
  if (row) {
    editingId.value = row.id
    formData.value = { ...row }
  } else {
    editingId.value = null
    formData.value = { name: '', gender: '男', age: 65, phone: '', address: '', emergencyContact: '', emergencyPhone: '' }
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  const validateResult = await formRef.value.validate()
  if (validateResult !== true) return
  saving.value = true
  try {
    if (editingId.value) {
      await api.put(`/elders/${editingId.value}`, formData.value)
      MessagePlugin.success('更新成功')
    } else {
      await api.post('/elders', formData.value)
      MessagePlugin.success('新增成功')
    }
    dialogVisible.value = false
    loadList()
  } finally { saving.value = false }
}

const handleDelete = async (row) => {
  await api.delete(`/elders/${row.id}`)
  MessagePlugin.success('删除成功')
  loadList()
}

onMounted(loadList)
</script>
