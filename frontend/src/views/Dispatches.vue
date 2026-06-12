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
        <template #duration="{ row }">
          <span v-if="row.actualDuration" class="duration-text">{{ formatDuration(row.actualDuration) }}</span>
          <span v-else class="text-gray">-</span>
        </template>
        <template #rating="{ row }">
          <template v-if="row.rating">
            <t-rate v-model="row.rating" :allow-half="false" readonly size="small" />
          </template>
          <span v-else class="text-gray">未评价</span>
        </template>
        <template #operation="{ row }">
          <t-link v-if="row.status === '待派单'" theme="primary" @click="openDialog(row)">派单</t-link>
          <t-link v-if="row.status === '已完成'" theme="warning" @click="openReviewDialog(row)">
            {{ row.rating ? '查看评价' : '评价' }}
          </t-link>
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

    <t-dialog v-model:visible="reviewDialogVisible" :header="reviewOrder.rating ? '查看评价' : '服务评价'" @confirm="handleReview" :confirm-btn="{ loading: reviewSaving }" width="500px">
      <t-descriptions :column="1" bordered size="small" style="margin-bottom: 16px;">
        <t-descriptions-item label="老人">{{ reviewOrder.elderName }}</t-descriptions-item>
        <t-descriptions-item label="志愿者">{{ reviewOrder.volunteerName }}</t-descriptions-item>
        <t-descriptions-item label="服务类型">{{ reviewOrder.serviceType }}</t-descriptions-item>
        <t-descriptions-item label="服务时长" v-if="reviewOrder.actualDuration">
          {{ formatDuration(reviewOrder.actualDuration) }}
        </t-descriptions-item>
        <t-descriptions-item label="服务时间" v-if="reviewOrder.serviceStartTime">
          {{ reviewOrder.serviceStartTime }} ~ {{ reviewOrder.serviceEndTime }}
        </t-descriptions-item>
      </t-descriptions>
      <t-form layout="vertical">
        <t-form-item label="服务评分">
          <t-rate v-model="reviewForm.rating" :allow-half="false" :disabled="!!reviewOrder.rating" />
        </t-form-item>
        <t-form-item label="评价备注">
          <t-textarea v-model="reviewForm.comment" placeholder="请输入评价备注" :disabled="!!reviewOrder.rating" :autosize="{ minRows: 3 }" />
        </t-form-item>
      </t-form>
      <template #footer v-if="reviewOrder.rating">
        <t-button theme="primary" @click="reviewDialogVisible = false">关闭</t-button>
      </template>
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

const reviewDialogVisible = ref(false)
const reviewSaving = ref(false)
const reviewOrder = ref({})
const reviewForm = ref({ rating: 0, comment: '' })

const columns = [
  { colKey: 'id', title: 'ID', width: 60 },
  { colKey: 'elderName', title: '老人姓名', width: 100 },
  { colKey: 'volunteerName', title: '志愿者', width: 100 },
  { colKey: 'serviceType', title: '服务类型', width: 110 },
  { colKey: 'serviceDate', title: '服务日期', width: 120 },
  { colKey: 'duration', title: '服务时长', width: 100, cell: 'duration' },
  { colKey: 'rating', title: '评分', width: 120, cell: 'rating' },
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

const openReviewDialog = (row) => {
  reviewOrder.value = { ...row }
  reviewForm.value = {
    rating: row.rating || 0,
    comment: row.reviewComment || ''
  }
  reviewDialogVisible.value = true
}

const handleReview = async () => {
  if (!reviewForm.value.rating) {
    MessagePlugin.warning('请选择评分')
    return
  }
  reviewSaving.value = true
  try {
    await api.post(`/dispatches/${reviewOrder.value.id}/review`, {
      rating: reviewForm.value.rating,
      comment: reviewForm.value.comment
    })
    MessagePlugin.success('评价成功')
    reviewDialogVisible.value = false
    loadList()
  } catch (e) {
    MessagePlugin.error('评价失败')
  } finally { reviewSaving.value = false }
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

const handleDelete = async (row) => {
  await api.delete(`/dispatches/${row.id}`)
  MessagePlugin.success('删除成功')
  loadList()
}

onMounted(() => { loadList(); loadElders(); loadVolunteers() })
</script>

<style scoped>
.duration-text {
  color: #00a870;
  font-weight: 500;
}
.text-gray {
  color: #909399;
}
</style>
