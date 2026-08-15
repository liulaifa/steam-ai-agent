import axios from 'axios'
import request from '@/utils/request'

// 收银员 API 模块

// 登录（用原生 axios，因为需要获取响应头中的 token）
// 不经过 request 拦截器的响应处理，直接返回完整 response 对象
export const login = (data) => {
  return axios.post('/api/cashier/public/login', data, {
    headers: {
      'X-Gateway-Secret': '20260726',
      'Content-Type': 'application/json'
    },
    timeout: 10000
  })
}

// 登出
export const logout = () => {
  return request.post('/cashier/public/logout')
}

// 收银员列表
export const getCashierList = (params) => {
  return request.post('/cashier/list', params)
}

// 查收银员
export const getCashierById = (id) => {
  return request.get(`/cashier/${id}`)
}

// 新增收银员
export const addCashier = (data) => {
  return request.post('/cashier/add', data)
}

// 修改收银员
export const updateCashier = (id, data) => {
  return request.put(`/cashier/${id}`, data)
}

// 状态切换
export const toggleStatus = (id) => {
  return request.put(`/cashier/status/${id}`)
}

// 重置密码
export const resetPassword = (id) => {
  return request.put(`/cashier/password/${id}`)
}

// 删除收银员
export const deleteCashier = (id) => {
  return request.delete(`/cashier/${id}`)
}
