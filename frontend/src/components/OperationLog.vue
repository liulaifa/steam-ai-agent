<template>
  <div class="operation-log">
    <h2 class="page-title">操作日志</h2>

    <!-- 顶部操作栏 -->
    <div class="filter-bar">
      <div class="filter-actions">
        <el-button plain @click="exportLog">
          <el-icon>
            <Download />
          </el-icon>
          导出日志
        </el-button>
        <el-button type="danger" @click="openClearDialog">
          <el-icon>
            <Delete />
          </el-icon>
          清空日志
        </el-button>
      </div>
    </div>

    <!-- 日志列表 -->
    <div class="log-list">
      <el-table :data="logs" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="operatorName" label="操作人" />
        <el-table-column prop="content" label="操作内容" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </div>

    <!-- 清空日志确认弹窗 -->
    <el-dialog v-model="clearDialogVisible" title="确认清空" width="400px" center>
      <div class="clear-confirm-container">
        <p>确定清除30天前的日志吗？</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="clearDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmClearLog">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Download, Delete } from '@element-plus/icons-vue'
import { getLogList, exportLogs, cleanLogs } from '@/api/log'

// eslint-disable-next-line no-undef
const emit = defineEmits(['notify'])

// 日志列表数据
const logs = ref([])
const loading = ref(false)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 清空日志弹窗
const clearDialogVisible = ref(false)

// 加载日志列表
const loadLogs = async () => {
  loading.value = true
  try {
    const data = await getLogList({
      page: currentPage.value,
      pageSize: pageSize.value
    })
    logs.value = data.data.list || []
    total.value = data.data.total || 0
  } catch (error) {
    console.error('加载日志列表失败:', error)
    emit('notify', '网络错误，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}

// 分页相关方法
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadLogs()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadLogs()
}

// 导出日志
const exportLog = async () => {
  try {
    // 发送请求
    const response = await exportLogs()

    // 解析文件名
    const contentDisposition = response.headers['content-disposition']
    let fileName = '操作日志.xlsx'
    if (contentDisposition) {
      const match = contentDisposition.match(/filename\*=utf-8''(.+)/)
      if (match) fileName = decodeURIComponent(match[1])
    }

    // 触发下载
    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = fileName
    link.click()
    window.URL.revokeObjectURL(url)

    // 显示成功提示
    emit('notify', '导出成功', 'success')
  } catch (error) {
    console.error('导出日志失败:', error)
    emit('notify', error.message || '导出失败，请稍后重试', 'error')
  }
}

// 打开清空日志弹窗
const openClearDialog = () => {
  clearDialogVisible.value = true
}

// 确认清空日志
const confirmClearLog = async () => {
  try {
    const data = await cleanLogs()
    clearDialogVisible.value = false
    emit('notify', `已清除 ${data.data} 条日志`, 'success')
    // 刷新列表（回到第一页）
    currentPage.value = 1
    loadLogs()
  } catch (error) {
    console.error('清空日志失败:', error)
    emit('notify', '网络错误，请稍后重试', 'error')
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadLogs()
})
</script>

<style lang="scss" scoped>
.operation-log {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;

  .page-title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 20px;
    color: #333;
  }

  .filter-bar {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 20px;
  }

  .filter-actions {
    display: flex;
    gap: 10px;
  }

  .log-list {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    padding: 20px;

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .clear-confirm-container {
    text-align: center;
    padding: 20px 0;

    p {
      font-size: 14px;
      color: #666;
      margin: 0;
    }
  }

  .dialog-footer {
    text-align: center;
  }
}
</style>
