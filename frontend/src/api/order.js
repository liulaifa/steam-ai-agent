import request from '@/utils/request'

// 订单 API 模块

// 确认下单
export const createOrder = (data) => {
  return request.post('/order', data)
}

// 订单列表
export const getOrderList = (params) => {
  return request.post('/order/list', params)
}

// 订单详情
export const getOrderDetail = (id) => {
  return request.get(`/order/${id}`)
}

// 确认收款
export const payOrder = (id, data) => {
  return request.put(`/order/pay/${id}`, data)
}

// 开始制作
export const cookOrder = (id) => {
  return request.put(`/order/cook/${id}`)
}

// 完成制作
export const completeOrder = (id) => {
  return request.put(`/order/complete/${id}`)
}

// 取消订单
export const cancelOrder = (id) => {
  return request.put(`/order/cancel/${id}`)
}

// 导出订单报表
export const exportOrders = (params) => {
  return request.post('/order/export', params, {
    responseType: 'blob'
  })
}
