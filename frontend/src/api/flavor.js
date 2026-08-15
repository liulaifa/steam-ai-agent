import request from '@/utils/request'

// 口味 API 模块

// 根据口味ID查询
export const getFlavorById = (flavorId) => {
  return request.post(`/dish/public/flavor/${flavorId}`)
}

// 根据口味DTO查询（匹配口味）
export const matchFlavor = (data) => {
  return request.post('/dish/public/flavor/match', data)
}

// 批量查询口味
export const getFlavorList = (ids) => {
  // 将数组转为逗号分隔字符串，确保 Spring Boot 能正确解析 List<Long>
  const idsParam = Array.isArray(ids) ? ids.join(',') : ids
  return request.get('/dish/public/flavor/list', { params: { ids: idsParam } })
}
