import request from '@/utils/request';

// 获取所有分类（工作台用）
export const getEnabledCategories = () => {
  return request.get('/dish/public/categories');
};

// 获取分类列表（管理后台用，含搜索）
export const getCategoryList = (name) => {
  return request.get('/dish/public/category/list', { params: { name } });
};

// 根据ID查询分类
export const getCategoryById = (id) => {
  return request.get(`/dish/public/category/${id}`);
};

// 新增分类
export const addCategory = (data) => {
  return request.post('/dish/category', data);
};

// 修改分类
export const updateCategory = (id, data) => {
  return request.put(`/dish/category/${id}`, data);
};

// 分类状态切换
export const toggleCategoryStatus = (id) => {
  return request.put(`/dish/category/status/${id}`);
};

// 删除分类
export const deleteCategory = (id) => {
  return request.delete(`/dish/category/${id}`);
};
