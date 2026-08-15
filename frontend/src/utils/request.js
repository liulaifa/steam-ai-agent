import axios from 'axios'
import { ElMessage } from 'element-plus'

// token 相关错误码
const TOKEN_ERRORS = ['TOKEN_EXPIRED', 'TOKEN_IS_EMPTY', 'TOKEN_INVALID', 'UNAUTHORIZED']

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：只添加请求头
request.interceptors.request.use(
  config => {
    // 1. 每个请求带 X-Gateway-Secret
    config.headers['X-Gateway-Secret'] = '20260726'

    // 2. 有 token 就带 Authorization
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = token
    }

    // 3. 有 userId 就带 X-User-Id
    const userId = localStorage.getItem('userId')
    if (userId) {
      config.headers['X-User-Id'] = userId
    }

    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器：只处理3种情况
request.interceptors.response.use(
  response => {
    // blob 响应直接返回完整 response
    if (response.config.responseType === 'blob') {
      return response
    }

    const data = response.data

    // 数组响应（如 getCart 返回 List<CartVO>）直接返回
    if (Array.isArray(data)) {
      return data
    }

    if (data && typeof data === 'object') {
      // 1. code=200 正常返回
      if (data.code === 200) {
        return data
      }

      // 2. token 相关错误：清除存储，跳转登录页
      if (TOKEN_ERRORS.includes(data.code) || TOKEN_ERRORS.includes(data.message)) {
        localStorage.removeItem('token')
        localStorage.removeItem('userId')
        localStorage.removeItem('cashier')
        ElMessage.error(data.message || '登录已过期')
        setTimeout(() => {
          window.location.href = '/'
        }, 1500)
        return Promise.reject(new Error(data.message || '登录已过期'))
      }

      // 3. 其他错误：直接显示后端 message
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }

    return data
  },
  error => {
    // HTTP 401/403：token 相关错误
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('cashier')
      ElMessage.error(error.response.data?.message || '登录已过期')
      setTimeout(() => {
        window.location.href = '/'
      }, 1500)
      return Promise.reject(error)
    }

    // 其他错误：显示后端返回的 message
    ElMessage.error(error.response?.data?.message || error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
