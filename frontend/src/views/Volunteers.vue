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
        <template #account="{ row }">
          <t-tag v-if="row.hasAccount" theme="success">已设置</t-tag>
          <t-tag v-else theme="default">未设置</t-tag>
        </template>
        <template #operation="{ row }">
          <t-link theme="primary" @click="openDialog(row)">编辑</t-link>
          <t-link theme="primary" style="margin-left:8px" @click="openAccountDialog(row)">
            {{ row.hasAccount ? '修改账号' : '设置账号' }}
          </t-link>
          <t-link theme="danger" style="margin-left:8px" @click="handleDelete(row)">删除</t-link>
        </template>
      </t-table>
    </t-card>

    <t-dialog v-model:visible="dialogVisible" :header="editingId ? '编辑志愿者' : '新增志愿者'" @confirm="handleSave" :confirm-btn="{ loading: saving }" width="450px">
      <t-form :data="formData" :rules="rules" ref="formRef" layout="vertical">
        <t-form-item label="姓名" name="name"><t-input v-model="formData.name" placeholder="请输入姓名" /></t-form-item>
        <t-form-item label="电话" name="phone"><t-input v-model="formData.phone" placeholder="请输入电话" /></t-form-item>
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

    <t-dialog v-model:visible="accountDialogVisible" :header="accountEditingId ? '修改登录账号' : '设置登录账号'" @confirm="handleSaveAccount" :confirm-btn="{ loading: accountSaving }" width="450px">
      <t-form :data="accountFormData" layout="vertical">
        <t-form-item label="志愿者姓名">
          <t-input :value="currentVolunteerName" disabled />
        </t-form-item>
        <t-form-item label="用户名" name="username">
          <t-input v-model="accountFormData.username" placeholder="请输入登录用户名" />
        </t-form-item>
        <t-form-item label="密码" name="password">
          <t-input v-model="accountFormData.password" type="password" :placeholder="accountEditingId ? '不修改请留空' : '请输入密码'" />
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
const formRef = ref(null)
const rules = {
  name: [{ required: true, message: '请输入姓名', type: 'error' }],
  phone: [
    { required: true, message: '请输入电话', type: 'error' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', type: 'error' }
  ]
}

const accountDialogVisible = ref(false)
const accountSaving = ref(false)
const accountEditingId = ref(null)
const currentVolunteerName = ref('')
const currentVolunteerId = ref(null)
const accountFormData = ref({ username: '', password: '' })

const columns = [
  { colKey: 'id', title: 'ID', width: 60 },
  { colKey: 'name', title: '姓名', width: 100 },
  { colKey: 'phone', title: '电话', width: 140 },
  { colKey: 'skill', title: '特长', width: 100 },
  { colKey: 'status', title: '状态', width: 90, cell: 'status' },
  { colKey: 'account', title: '账号', width: 90, cell: 'account' },
  { colKey: 'operation', title: '操作', width: 220 }
]

const loadList = async () => {
  loading.value = true
  try {
    const volunteers = await api.get('/volunteers')
    const users = await api.get('/users')
    list.value = volunteers.map(v => {
      const hasAccount = users.some(u => u.volunteerId === v.id)
      return { ...v, hasAccount }
    })
  } finally { loading.value = false }
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
  const validateResult = await formRef.value.validate()
  if (validateResult !== true) return
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

const openAccountDialog = async (row) => {
  currentVolunteerId.value = row.id
  currentVolunteerName.value = row.name
  
  try {
    const user = await api.get(`/users/volunteer/${row.id}`)
    accountEditingId.value = user.id
    accountFormData.value = { username: user.username, password: '' }
  } catch (e) {
    accountEditingId.value = null
    accountFormData.value = { username: '', password: '' }
  }
  
  accountDialogVisible.value = true
}

const handleSaveAccount = async () => {
  if (!accountFormData.value.username) {
    MessagePlugin.warning('请输入用户名')
    return
  }
  if (!accountEditingId.value && !accountFormData.value.password) {
    MessagePlugin.warning('请输入密码')
    return
  }

  accountSaving.value = true
  try {
    const payload = {
      username: accountFormData.value.username,
      role: 'volunteer',
      volunteerId: currentVolunteerId.value,
      name: currentVolunteerName.value
    }
    if (accountFormData.value.password) {
      payload.password = accountFormData.value.password
    }

    if (accountEditingId.value) {
      await api.put(`/users/${accountEditingId.value}`, payload)
      MessagePlugin.success('修改成功')
    } else {
      await api.post('/users', payload)
      MessagePlugin.success('设置成功')
    }
    accountDialogVisible.value = false
    loadList()
  } catch (err) {
    MessagePlugin.error(err?.response?.data?.message || '操作失败')
  } finally { accountSaving.value = false }
}

onMounted(loadList)
</script>
