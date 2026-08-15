<template>
  <div class="ai-chat">
    <!-- 顶部标题栏 -->
    <div class="chat-header">
      <h2 class="page-title">AI 小助手</h2>
      <el-tooltip content="清空会话" placement="bottom">
        <el-button type="danger" :icon="Delete" circle size="small" @click="handleClear" />
      </el-tooltip>
    </div>

    <!-- 聊天记录区域 -->
    <div ref="chatContainer" class="chat-body">
      <div v-if="messages.length === 0" class="empty-chat">
        <el-empty description="开始和 AI 小助手对话吧" />
      </div>
      <div v-for="(msg, index) in messages" :key="index" :class="['chat-message', msg.role]">
        <div class="message-avatar">
          <el-avatar :size="32"
            :style="msg.role === 'user' ? { backgroundColor: '#409eff' } : { backgroundColor: '#67c23a' }">
            {{ msg.role === 'user' ? '我' : 'AI' }}
          </el-avatar>
        </div>
        <div class="message-bubble">
          <div class="message-text">{{ msg.content }}</div>
        </div>
      </div>
      <!-- 加载中提示 -->
      <div v-if="loading" class="chat-message assistant">
        <div class="message-avatar">
          <el-avatar :size="32" style="background-color: #67c23a">AI</el-avatar>
        </div>
        <div class="message-bubble">
          <div class="message-text typing">AI 正在思考中...</div>
        </div>
      </div>
    </div>

    <!-- 底部输入区域 -->
    <div class="chat-footer">
      <el-input v-model="inputText" placeholder="输入消息，按 Enter 发送" type="textarea" :rows="1" resize="none"
        @keydown.enter.prevent="handleSend" :disabled="loading" />
      <el-button type="primary" :icon="Promotion" :loading="loading" @click="handleSend" :disabled="!inputText.trim()">
        发送
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessageBox } from 'element-plus'
import { Delete, Promotion } from '@element-plus/icons-vue'
import { chat, getChatHistory, clearChatHistory } from '@/api/ai'  // ✅ 改这里

// eslint-disable-next-line no-undef
const emit = defineEmits(['notify'])

const messages = ref([])
const inputText = ref('')
const loading = ref(false)
const chatContainer = ref(null)

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

// 加载聊天记录
const loadHistory = async () => {
  try {
    const data = await getChatHistory()
    messages.value = data.data || []
    scrollToBottom()
  } catch (error) {
    console.error('加载聊天记录失败:', error)
    emit('notify', '加载历史记录失败', 'error')
  }
}

// ✅ 修改这里 - 发送消息（非流式）
const handleSend = async () => {
  const prompt = inputText.value.trim()
  if (!prompt || loading.value) return

  // 立即显示用户消息
  messages.value.push({ role: 'user', content: prompt })
  inputText.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const data = await chat(prompt)
    // 判断响应格式，根据你的后端实际返回调整
    const reply = data.data || data.msg || data.content || '收到回复'
    messages.value.push({ role: 'assistant', content: reply })
    scrollToBottom()
  } catch (error) {
    // 移除刚才的用户消息
    messages.value.pop()
    emit('notify', error.message || '发送失败，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}

// 清空会话
const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有聊天记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await clearChatHistory()
    messages.value = []
    emit('notify', '聊天记录已清空', 'success')
  } catch (error) {
    if (error === 'cancel') return
    emit('notify', error.message || '清空失败，请稍后重试', 'error')
  }
}

onMounted(() => {
  loadHistory()
})
</script>

<style lang="scss" scoped>
.ai-chat {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;

  .chat-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 20px;
    background-color: #fff;
    border-bottom: 1px solid #eaeaea;

    .page-title {
      font-size: 20px;
      font-weight: bold;
      margin: 0;
      color: #333;
    }
  }

  .chat-body {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 16px;

    .empty-chat {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .chat-message {
      display: flex;
      gap: 10px;
      max-width: 80%;

      .message-avatar {
        flex-shrink: 0;
      }

      .message-bubble {
        padding: 10px 14px;
        border-radius: 12px;
        line-height: 1.6;
        font-size: 14px;
        word-break: break-word;

        .message-text {
          white-space: pre-wrap;
        }
      }

      &.user {
        flex-direction: row-reverse;
        align-self: flex-end;

        .message-bubble {
          background-color: #409eff;
          color: #fff;
          border-top-right-radius: 4px;
        }
      }

      &.assistant {
        align-self: flex-start;

        .message-bubble {
          background-color: #fff;
          color: #333;
          border: 1px solid #eaeaea;
          border-top-left-radius: 4px;
        }

        .typing {
          color: #999;
        }
      }
    }
  }

  .chat-footer {
    display: flex;
    gap: 10px;
    padding: 16px 20px;
    background-color: #fff;
    border-top: 1px solid #eaeaea;

    .el-input {
      flex: 1;
    }
  }
}

@keyframes fade {
  0% {
    opacity: 0;
  }

  50% {
    opacity: 1;
  }

  100% {
    opacity: 0;
  }
}
</style>