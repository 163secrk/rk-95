<template>
  <div>
    <t-card title="助餐预约" :bordered="false">
      <template #actions>
        <t-button theme="primary" @click="openDialog(null)">
          <template #icon><t-icon name="add" /></template>
          新增预约
        </t-button>
      </template>
      <t-table :data="list" :columns="columns" row-key="id" :loading="loading" hover stripe>
        <template #status="{ row }">
          <t-tag :theme="statusTheme(row.status)">{{ row.status }}</t-tag>
        </template>
        <template #mealType="{ row }">
          <t-tag variant="light">{{ row.mealType }}</t-tag>
        </template>
        <template #operation="{ row }">
          <t-link theme="primary" @click="openDialog(row)">编辑</t-link>
          <t-link theme="danger" style="margin-left:8px" @click="handleDelete(row)">删除</t-link>
        </template>
      </t-table>
    </t-card>

    <t-dialog v-model:visible="dialogVisible" :header="editingId ? '编辑预约' : '新增预约'" @confirm="handleSave" :confirm-btn="{ loading: saving }" width="500px">
      <t-form :data="formData" :rules="rules" ref="formRef" layout="vertical">
        <t-form-item label="老人" name="elderId">
          <t-select v-model="formData.elderId" placeholder="请选择老人" filterable>
            <t-option v-for="e in elders" :key="e.id" :value="e.id" :label="e.name" />
          </t-select>
        </t-form-item>
        <t-form-item label="用餐日期"><t-date-picker v-model="formData.mealDate" clearable style="width:100%" /></t-form-item>
        <t-form-item label="餐次">
          <t-select v-model="formData.mealType" placeholder="请选择餐次">
            <t-option value="早餐" label="早餐" />
            <t-option value="午餐" label="午餐" />
            <t-option value="晚餐" label="晚餐" />
          </t-select>
        </t-form-item>
        <t-form-item label="菜品" name="menuItem"><t-input v-model="formData.menuItem" placeholder="请输入菜品" /></t-form-item>
        <t-form-item label="状态">
          <t-select v-model="formData.status" placeholder="请选择状态">
            <t-option value="已预约" label="已预约" />
            <t-option value="已就餐" label="已就餐" />
            <t-option value="已取消" label="已取消" />
          </t-select>
        </t-form-item>
        <t-form-item label="备注"><t-textarea v-model="formData.remark" placeholder="请输入备注" /></t-form-item>
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
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const editingId = ref(null)
const formData = ref({ elderId: null, mealDate: '', mealType: '午餐', menuItem: '', status: '已预约', remark: '' })
const formRef = ref(null)
const rules = {
  elderId: [{ required: true, message: '请选择老人', type: 'error' }],
  menuItem: [{ required: true, message: '请输入菜品', type: 'error' }]
}

const columns = [
  { colKey: 'id', title: 'ID', width: 60 },
  { colKey: 'elderName', title: '老人姓名', width: 100 },
  { colKey: 'mealDate', title: '用餐日期', width: 120 },
  { colKey: 'mealType', title: '餐次', width: 80, cell: 'mealType' },
  { colKey: 'menuItem', title: '菜品' },
  { colKey: 'status', title: '状态', width: 90, cell: 'status' },
  { colKey: 'remark', title: '备注' },
  { colKey: 'operation', title: '操作', width: 120 }
]

const statusTheme = (s) => ({ '已预约': 'primary', '已就餐': 'success', '已取消': 'default' }[s] || 'default')

const loadList = async () => {
  loading.value = true
  try { list.value = await api.get('/meals') } finally { loading.value = false }
}

const loadElders = async () => {
  elders.value = await api.get('/elders')
}

const openDialog = (row) => {
  if (row) {
    editingId.value = row.id
    formData.value = { elderId: row.elderId, mealDate: row.mealDate, mealType: row.mealType, menuItem: row.menuItem, status: row.status, remark: row.remark }
  } else {
    editingId.value = null
    formData.value = { elderId: null, mealDate: '', mealType: '午餐', menuItem: '', status: '已预约', remark: '' }
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  const validateResult = await formRef.value.validate()
  if (validateResult !== true) return
  saving.value = true
  try {
    if (editingId.value) {
      await api.put(`/meals/${editingId.value}`, formData.value)
      MessagePlugin.success('更新成功')
    } else {
      await api.post('/meals', formData.value)
      MessagePlugin.success('预约成功')
    }
    dialogVisible.value = false
    loadList()
  } finally { saving.value = false }
}

const handleDelete = async (row) => {
  await api.delete(`/meals/${row.id}`)
  MessagePlugin.success('删除成功')
  loadList()
}

onMounted(() => { loadList(); loadElders() })
</script>
