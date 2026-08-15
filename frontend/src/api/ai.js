import request from '@/utils/request'

// 获取 API 基础地址
const API_BASE_URL = '/api'

// AI 对话（非流式）
export const chat = async (prompt) => {
  const token = localStorage.getItem('token') || ''
  const userId = localStorage.getItem('userId') || ''

  const response = await fetch(API_BASE_URL + '/ai/chat?prompt=' + encodeURIComponent(prompt), {
    method: 'POST',
    headers: {
      'X-Gateway-Secret': '20260726',
      'Authorization': token,
      'X-User-Id': userId
    }
  })

  if (!response.ok) {
    const errText = await response.text()
    throw new Error(errText || '请求失败')
  }

  const data = await response.json()
  return data
}

// 查询聊天记录
export const getChatHistory = () => {
  return request.get('/ai/chatHistory/list')
}

// 清空聊天记录
export const clearChatHistory = () => {
  return request.delete('/ai/chatHistory/delete')
}