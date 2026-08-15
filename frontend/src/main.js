import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 处理 ResizeObserver 错误
if (typeof window !== 'undefined') {
  // 方案1：重写 ResizeObserver
  const originalResizeObserver = window.ResizeObserver
  if (originalResizeObserver) {
    window.ResizeObserver = class extends originalResizeObserver {
      constructor(callback) {
        super((entries, observer) => {
          try {
            callback(entries, observer)
          } catch (e) {
            console.error('ResizeObserver error:', e)
          }
        })
      }
    }
  }
  
  // 方案2：添加全局错误处理
  window.addEventListener('error', (event) => {
    if (event.message && event.message.includes('ResizeObserver loop completed with undelivered notifications')) {
      event.preventDefault()
    }
  })
}

const app = createApp(App)
app.use(ElementPlus)
app.mount('#app')
