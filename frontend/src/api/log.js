import request from '@/utils/request'

// 日志 API 模块
export const getLogList = (params) => {
  return request.post('/log/list', params)
}

export const exportLogs = () => {
  return request.get('/log/export', {
    responseType: 'blob'
  })
}

export const cleanLogs = () => {
  return request.delete('/log/clean')
}
