<template>
  <div id="app">
    <div v-if="!isLoggedIn">
      <LoginPage @login-success="handleLoginSuccess" @notify="showNotification" />
    </div>
    <div v-else>
      <MainLayoutPage @notify="showNotification" />
    </div>
    <AppNotification :visible="notification.visible" :message="notification.message" :type="notification.type"
      @close="closeNotification" />
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import LoginPage from './components/Login.vue'
import MainLayoutPage from './components/MainLayout.vue'
import AppNotification from './components/Notification.vue'

export default {
  name: 'App',
  components: {
    LoginPage,
    MainLayoutPage,
    AppNotification
  },
  setup() {
    const isLoggedIn = ref(false)
    const notification = ref({
      visible: false,
      message: '',
      type: 'success'
    })

    const handleLoginSuccess = () => {
      console.log('接收到login-success事件')
      isLoggedIn.value = true
      console.log('isLoggedIn设置为:', isLoggedIn.value)
    }

    const showNotification = (message, type = 'success') => {
      notification.value = {
        visible: true,
        message,
        type
      }
    }

    const closeNotification = () => {
      notification.value.visible = false
    }

    // 页面加载时检查本地存储中的token
    onMounted(() => {
      const token = localStorage.getItem('token')
      if (token) {
        // token 存在，直接进入系统
        isLoggedIn.value = true
      } else {
        // token 不存在，清除可能残留的本地存储并显示登录页面
        localStorage.removeItem('token')
        localStorage.removeItem('cashier')
        localStorage.removeItem('userId')
        isLoggedIn.value = false
      }
    })

    return {
      isLoggedIn,
      handleLoginSuccess,
      notification,
      showNotification,
      closeNotification
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Arial', sans-serif;
  background-color: #f5f5f5;
}

#app {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}
</style>
