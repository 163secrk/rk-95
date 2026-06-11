<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <t-icon name="home" size="48px" color="#0052d9" />
        <h2>社区居家养老服务系统</h2>
        <p>请登录您的账号</p>
      </div>
      <t-form :data="formData" :rules="rules" ref="formRef" layout="vertical" @submit="handleLogin">
        <t-form-item label="用户名" name="username">
          <t-input v-model="formData.username" placeholder="请输入用户名" size="large">
            <template #prefix-icon><t-icon name="user" /></template>
          </t-input>
        </t-form-item>
        <t-form-item label="密码" name="password">
          <t-input v-model="formData.password" type="password" placeholder="请输入密码" size="large" @enter="handleLogin">
            <template #prefix-icon><t-icon name="lock" /></template>
          </t-input>
        </t-form-item>
        <t-button theme="primary" size="large" block :loading="loading" @click="handleLogin">登录</t-button>
      </t-form>
      <div class="login-tip">
        <p>管理员账号：admin / admin123</p>
        <p>志愿者账号需由管理员分配</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { MessagePlugin } from 'tdesign-vue-next'
import api from '../api'
import { setAuth } from '../utils/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const formData = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', type: 'error' }],
  password: [{ required: true, message: '请输入密码', type: 'error' }]
}

const handleLogin = async () => {
  const { valid } = await formRef.value.validate()
  if (!valid) return

  loading.value = true
  try {
    const res = await api.post('/auth/login', formData)
    setAuth(res, null)
    MessagePlugin.success('登录成功')
    
    if (res.role === 'admin') {
      router.push('/')
    } else {
      router.push('/my-dispatches')
    }
  } catch (err) {
    MessagePlugin.error(err?.response?.data?.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e6f0ff 0%, #f0f5ff 100%);
}
.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}
.login-header {
  text-align: center;
  margin-bottom: 32px;
}
.login-header h2 {
  margin: 16px 0 8px;
  font-size: 22px;
  color: #333;
}
.login-header p {
  color: #999;
  font-size: 14px;
}
.login-tip {
  margin-top: 20px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
  font-size: 12px;
  color: #999;
  line-height: 1.8;
}
</style>
