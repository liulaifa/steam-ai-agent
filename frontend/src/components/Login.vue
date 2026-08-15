<template>
    <div class="login-container">
        <div class="login-box">
            <h2 class="login-title">欢迎登录</h2>
            <form class="login-form">
                <div class="form-item">
                    <label for="username">账号</label>
                    <input type="text" id="username" v-model="form.username" placeholder="请输入登录账号" autocomplete="off">
                </div>
                <div class="form-item">
                    <label for="password">密码</label>
                    <input type="password" id="password" v-model="form.password" placeholder="请输入密码" autocomplete="off">
                </div>
                <div v-if="error" class="error-message">{{ error }}</div>
                <button type="button" class="login-btn" @click="handleLogin" :disabled="loading">
                    <span v-if="loading">登录中...</span>
                    <span v-else>登录</span>
                </button>
            </form>
        </div>
    </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { login } from '@/api/cashier'

export default {
    name: 'LoginPage',
    emits: ['login-success', 'notify'],
    setup(props, { emit }) {
        const form = ref({
            username: '',
            password: ''
        })
        const loading = ref(false)
        const error = ref('')

        // 组件挂载时重置表单
        onMounted(() => {
            // 立即重置
            form.value.username = ''
            form.value.password = ''
            error.value = ''

            // 延迟重置，确保在浏览器自动填充后再清空
            setTimeout(() => {
                form.value.username = ''
                form.value.password = ''
            }, 100)
        })

        const handleLogin = async () => {
            loading.value = true
            error.value = ''

            try {
                // 直接发送请求，参数校验交给后端
                const response = await login(form.value)
                const data = response.data

                if (data.code === 200) {
                    // 成功：存 token（从响应头）
                    const token = response.headers['authorization'] || response.headers['Authorization']
                    if (token) {
                        localStorage.setItem('token', token)
                    }

                    // 成功：存 userId（从响应体 data.id）
                    if (data.data && data.data.id) {
                        localStorage.setItem('userId', data.data.id)
                    }

                    // 成功：存用户信息（整个 data 对象）
                    if (data.data) {
                        localStorage.setItem('cashier', JSON.stringify(data.data))
                    }

                    emit('notify', data.message || '登录成功', 'success')
                    emit('login-success')
                } else {
                    // 失败：显示后端返回的 message
                    error.value = data.message || '登录失败'
                    emit('notify', data.message || '登录失败', 'error')
                }
            } catch (err) {
                // 异常：显示后端返回的 message
                const message = err.response?.data?.message || err.message || '登录失败'
                error.value = message
                emit('notify', message, 'error')
            } finally {
                loading.value = false
            }
        }

        return {
            form,
            loading,
            error,
            handleLogin
        }
    }
}
</script>

<style scoped>
.login-container {
    width: 100vw;
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #fff8f0;
}

.login-box {
    width: 400px;
    padding: 40px;
    background-color: #ffffff;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(255, 165, 0, 0.15);
    border: 1px solid #ffe0b2;
}

.login-title {
    text-align: center;
    color: #ff9800;
    margin-bottom: 30px;
    font-size: 24px;
    font-weight: bold;
}

.login-form {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.form-item {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.form-item label {
    font-size: 14px;
    color: #666;
    font-weight: 500;
}

.form-item input {
    padding: 12px;
    border: 1px solid #ffcc80;
    border-radius: 4px;
    font-size: 14px;
    transition: border-color 0.3s;
}

.form-item input:focus {
    outline: none;
    border-color: #ff9800;
    box-shadow: 0 0 0 2px rgba(255, 152, 0, 0.2);
}

.login-btn {
    background-color: #ff9800;
    color: white;
    border: none;
    border-radius: 4px;
    padding: 12px;
    font-size: 16px;
    font-weight: 500;
    cursor: pointer;
    transition: background-color 0.3s;
    margin-top: 10px;
}

.login-btn:hover {
    background-color: #f57c00;
}

.login-btn:active {
    background-color: #e65100;
}

.login-btn:disabled {
    background-color: #ffcc80;
    cursor: not-allowed;
}

.error-message {
    color: #f56c6c;
    font-size: 12px;
    margin-top: -10px;
    margin-bottom: 10px;
}

/* 响应式设计 */
@media (max-width: 480px) {
    .login-box {
        width: 90%;
        padding: 30px;
    }
}
</style>