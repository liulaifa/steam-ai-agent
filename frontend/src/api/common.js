import request from '@/utils/request'

// 通用 API 模块

// 获取 API 基础地址
const API_BASE_URL = '/api'

// 获取上传地址（根据环境切换）
export const getUploadUrl = () => {
  return API_BASE_URL + '/file/public/upload'
}

// 图片上传接口
export const uploadFile = (formData) => {
  return request.post('/file/public/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
