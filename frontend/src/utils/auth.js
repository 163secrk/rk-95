import { ref } from 'vue'

const user = ref(null)
const token = ref(null)

const STORAGE_KEY_USER = 'ec_user'
const STORAGE_KEY_TOKEN = 'ec_token'

function initAuth() {
  const savedUser = localStorage.getItem(STORAGE_KEY_USER)
  const savedToken = localStorage.getItem(STORAGE_KEY_TOKEN)
  if (savedUser) {
    try {
      user.value = JSON.parse(savedUser)
    } catch (e) {
      user.value = null
    }
  }
  if (savedToken) {
    token.value = savedToken
  }
}

function setAuth(userData, tokenData) {
  user.value = userData
  token.value = tokenData
  localStorage.setItem(STORAGE_KEY_USER, JSON.stringify(userData))
  if (tokenData) {
    localStorage.setItem(STORAGE_KEY_TOKEN, tokenData)
  }
}

function clearAuth() {
  user.value = null
  token.value = null
  localStorage.removeItem(STORAGE_KEY_USER)
  localStorage.removeItem(STORAGE_KEY_TOKEN)
}

function isLoggedIn() {
  return !!user.value
}

function isAdmin() {
  return user.value && user.value.role === 'admin'
}

function isVolunteer() {
  return user.value && user.value.role === 'volunteer'
}

export { user, token, initAuth, setAuth, clearAuth, isLoggedIn, isAdmin, isVolunteer }
