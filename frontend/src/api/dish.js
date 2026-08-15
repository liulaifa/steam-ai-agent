import request from '@/utils/request'

// 菜品 API 模块

// 按分类查询菜品
export const getDishesByCategory = (categoryId) => {
  return request.get(`/dish/public/${categoryId}`)
}

// 获取所有菜品
export const getAllDishes = () => {
  return request.get('/dish/public/all')
}

// 根据ID查询菜品详情
export const getDishById = (id) => {
  return request.get(`/dish/public/detail/${id}`)
}

// 菜品分页查询
export const getDishPage = (params) => {
  return request.post('/dish/public/page', params)
}

// 新增菜品
export const addDish = (data) => {
  return request.post('/dish', data)
}

// 修改菜品
export const updateDish = (id, data) => {
  return request.put(`/dish/${id}`, data)
}

// 菜品状态切换
export const toggleDishStatus = (id) => {
  return request.put(`/dish/status/${id}`)
}

// 删除菜品
export const deleteDish = (id) => {
  return request.delete(`/dish/${id}`)
}

// 获取热门菜品
export const getHotDishes = () => {
  return request.get('/dish/public/hot')
}

// 菜品销量+1
export const incrementSales = (id) => {
  return request.put(`/dish/sales/${id}`)
}
