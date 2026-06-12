<template>
  <div>
    <t-card title="健康登记" :bordered="false">
      <template #actions>
        <t-button theme="primary" @click="openDialog(null)">
          <template #icon><t-icon name="add" /></template>
          新增登记
        </t-button>
      </template>
      <t-table :data="list" :columns="columns" row-key="id" :loading="loading" hover stripe>
        <template #operation="{ row }">
          <t-link theme="primary" @click="openDialog(row)">编辑</t-link>
          <t-link theme="danger" style="margin-left:8px" @click="handleDelete(row)">删除</t-link>
        </template>
      </t-table>
    </t-card>

    <t-dialog v-model:visible="dialogVisible" :header="editingId ? '编辑健康记录' : '新增健康登记'" @confirm="handleSave" :confirm-btn="{ loading: saving }" width="500px">
      <t-form :data="formData" :rules="rules" ref="formRef" layout="vertical">
        <t-form-item label="老人" name="elderId">
          <t-select v-model="formData.elderId" placeholder="请选择老人" filterable>
            <t-option v-for="e in elders" :key="e.id" :value="e.id" :label="e.name" />
          </t-select>
        </t-form-item>
        <t-form-item label="登记日期" name="recordDate">
          <t-date-picker v-model="formData.recordDate" clearable style="width:100%" :disable-date="disablePastDate" />
        </t-form-item>
        <t-form-item label="血压" name="bloodPressure"><t-input v-model="formData.bloodPressure" placeholder="如 120/80 mmHg" /></t-form-item>
        <t-form-item label="心率(次/分)"><t-input-number v-model="formData.heartRate" :min="30" :max="220" /></t-form-item>
        <t-form-item label="血糖"><t-input v-model="formData.bloodSugar" placeholder="如 5.6 mmol/L" /></t-form-item>
        <t-form-item label="体温"><t-input v-model="formData.temperature" placeholder="如 36.5 ℃" /></t-form-item>
        <t-form-item label="用药情况"><t-textarea v-model="formData.medication" placeholder="请输入用药情况" /></t-form-item>
        <t-form-item label="备注"><t-textarea v-model="formData.notes" placeholder="请输入备注" /></t-form-item>
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
const formData = ref({ elderId: null, recordDate: '', bloodPressure: '', heartRate: null, bloodSugar: '', temperature: '', medication: '', notes: '' })
const formRef = ref(null)

const validateBloodPressure = (val) => {
  if (!val) return true
  const match = val.match(/(-?\d+)\s*\/\s*(-?\d+)/)
  if (match) {
    const systolic = parseInt(match[1])
    const diastolic = parseInt(match[2])
    return systolic >= 0 && diastolic >= 0
  }
  return true
}

const rules = {
  elderId: [{ required: true, message: '请选择老人', type: 'error' }],
  recordDate: [
    { required: true, message: '请选择登记日期', type: 'error' },
    { validator: (val) => {
      if (!val) return true;
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      const selected = new Date(val);
      return selected >= today;
    }, message: '登记日期不能是过去的日期', type: 'error' }
  ],
  bloodPressure: [
    { validator: validateBloodPressure, message: '血压不能为负数', type: 'error' }
  ]
}

const disablePastDate = ({ date }) => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return date < today;
}

const columns = [
  { colKey: 'id', title: 'ID', width: 60 },
  { colKey: 'elderName', title: '老人姓名', width: 100 },
  { colKey: 'recordDate', title: '登记日期', width: 120 },
  { colKey: 'bloodPressure', title: '血压', width: 110 },
  { colKey: 'heartRate', title: '心率', width: 80 },
  { colKey: 'bloodSugar', title: '血糖', width: 110 },
  { colKey: 'temperature', title: '体温', width: 80 },
  { colKey: 'medication', title: '用药情况' },
  { colKey: 'operation', title: '操作', width: 120 }
]

const loadList = async () => {
  loading.value = true
  try { list.value = await api.get('/health') } finally { loading.value = false }
}

const loadElders = async () => {
  elders.value = await api.get('/elders')
}

const openDialog = (row) => {
  if (row) {
    editingId.value = row.id
    formData.value = { elderId: row.elderId, recordDate: row.recordDate, bloodPressure: row.bloodPressure, heartRate: row.heartRate, bloodSugar: row.bloodSugar, temperature: row.temperature, medication: row.medication, notes: row.notes }
  } else {
    editingId.value = null
    formData.value = { elderId: null, recordDate: '', bloodPressure: '', heartRate: null, bloodSugar: '', temperature: '', medication: '', notes: '' }
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  const validateResult = await formRef.value.validate()
  if (validateResult !== true) return
  saving.value = true
  try {
    if (editingId.value) {
      await api.put(`/health/${editingId.value}`, formData.value)
      MessagePlugin.success('更新成功')
    } else {
      await api.post('/health', formData.value)
      MessagePlugin.success('登记成功')
    }
    dialogVisible.value = false
    loadList()
  } finally { saving.value = false }
}

const handleDelete = async (row) => {
  await api.delete(`/health/${row.id}`)
  MessagePlugin.success('删除成功')
  loadList()
}

onMounted(() => { loadList(); loadElders() })
</script>
