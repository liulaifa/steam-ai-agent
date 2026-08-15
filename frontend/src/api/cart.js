import request from '@/utils/request'

// 购物车 API 模块

// 查询购物车（需要 cashierId 参数）
// 后端返回 List<CartVO>（数组），响应拦截器会直接返回数组
export const getCart = (cashierId) => {
  return request.get('/cart', { params: { cashierId } })
}

// 添加菜品到购物车
// CartAddDTO: { dishId, flavorId }
export const addToCart = (data) => {
  return request.post('/cart', data)
}

// 修改数量
// CartUpdateDTO: { dishId, flavorId, number }  number 正数加，负数减
export const updateCart = (data) => {
  return request.put('/cart', data)
}

// 删除单个菜品
export const deleteCartItem = (id) => {
  return request.delete(`/cart/${id}`)
}

// 清空购物车
export const clearCart = () => {
  return request.delete('/cart')
}
