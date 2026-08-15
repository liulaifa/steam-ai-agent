<template>
  <div class="cashier-management">
    <h2 class="page-title">收银员管理</h2>

    <!-- 顶部操作栏 -->
    <div class="filter-bar">
      <div class="filter-item">
        <el-input v-model="searchQuery" placeholder="按姓名或账号搜索" clearable @input="handleSearch" />
      </div>
      <div class="filter-actions">
        <el-button type="primary" @click="openAddDialog">新增收银员</el-button>
      </div>
    </div>

    <!-- 收银员列表 -->
    <div class="cashier-list">
      <el-table v-loading="loading" :data="cashiers" style="width: 100%">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="username" label="登录账号" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0"
              @change="() => handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录时间" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEditDialog(row)">
              编辑
            </el-button>
            <el-button size="small" @click="openResetPasswordDialog(row)">
              重置密码
            </el-button>
            <el-button type="danger" size="small" @click="openDeleteDialog(row)"
              :disabled="String(row.id) === currentUserId">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination :current-page="currentPage" :page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </div>

    <!-- 新增/编辑收银员弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" center>
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-form-item label="登录账号" required>
          <el-input v-model="formData.username" placeholder="请输入登录账号" maxlength="20" autocomplete="new-username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="formData.password" placeholder="不填默认123456" maxlength="20" type="password"
            autocomplete="new-password" />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="formData.realName" placeholder="请输入姓名" maxlength="10" />
        </el-form-item>
        <el-form-item label="手机号" required>
          <el-input v-model="formData.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio label="1">在职</el-radio>
            <el-radio label="0">离职</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 重置密码确认弹窗 -->
    <el-dialog v-model="resetPasswordDialogVisible" title="确认重置密码" width="400px" center>
      <div class="reset-password-container">
        <p>确定要重置【{{ resetPasswordName }}】的密码吗？密码将恢复为默认密码123456。</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmResetPassword">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认弹窗 -->
    <el-dialog v-model="deleteDialogVisible" title="确认删除" width="400px" center>
      <div class="delete-container">
        <p>确定要删除收银员【{{ deleteName }}】吗？删除后无法恢复。</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCashierList, getCashierById, addCashier, updateCashier, resetPassword, toggleStatus, deleteCashier } from '@/api/cashier'

// eslint-disable-next-line no-undef
const emit = defineEmits(['notify'])

// 收银员列表数据
const cashiers = ref([])
const loading = ref(false)

// 搜索和分页
const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增收银员')
const formRef = ref(null)
const formData = ref({
  id: '',
  username: '',
  password: '',
  realName: '',
  phone: '',
  status: '0'
})

// 重置密码弹窗
const resetPasswordDialogVisible = ref(false)
const resetPasswordName = ref('')
const resetPasswordId = ref('')

// 删除弹窗
const deleteDialogVisible = ref(false)
const deleteName = ref('')
const deleteId = ref('')

// 当前登录用户ID（从 localStorage 获取，用于禁止删除自己）
const currentUserId = ref(localStorage.getItem('userId') || '')

// 加载收银员列表
const loadCashiers = async () => {
  loading.value = true
  try {
    const data = await getCashierList({
      keyword: searchQuery.value || '',
      page: currentPage.value,
      pageSize: pageSize.value
    })
    cashiers.value = data.data.list || []
    total.value = data.data.total || 0
  } catch (error) {
    console.error('获取收银员列表失败:', error)
    emit('notify', '网络错误，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}



// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadCashiers()
}

// 分页相关方法
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadCashiers()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadCashiers()
}

// 页面加载时获取数据
onMounted(() => {
  loadCashiers()
})

// 打开新增弹窗
const openAddDialog = () => {
  // 先关闭弹窗，确保状态重置
  dialogVisible.value = false

  // 延迟设置表单数据和打开弹窗，确保DOM更新
  setTimeout(() => {
    dialogTitle.value = '新增收银员'
    // 重置表单数据
    formData.value = {
      id: '',
      username: '',
      password: '',
      realName: '',
      phone: '',
      status: '0'
    }
    dialogVisible.value = true

    // 重置表单验证状态
    if (formRef.value) {
      formRef.value.resetFields()
    }

    // 强制清空输入框值，防止浏览器自动填充
    setTimeout(() => {
      formData.value.username = ''
      formData.value.password = ''
      formData.value.realName = ''
      formData.value.phone = ''
    }, 100)
  }, 100)
}

// 打开编辑弹窗
const openEditDialog = async (row) => {
  try {
    const data = await getCashierById(row.id)
    dialogTitle.value = '编辑收银员'
    formData.value = {
      id: data.data.id,
      username: data.data.username,
      password: '',
      realName: data.data.realName,
      phone: data.data.phone,
      status: String(data.data.status)
    }
    dialogVisible.value = true

    // 重置表单验证状态
    if (formRef.value) {
      formRef.value.resetFields()
    }
  } catch (error) {
    console.error('获取收银员详情失败:', error)
    emit('notify', '网络错误，请稍后重试', 'error')
  }
}

// 提交表单
const submitForm = async () => {
  if (formData.value.id) {
    // 编辑
    try {
      // 构建请求体
      const requestBody = {
        realName: formData.value.realName,
        username: formData.value.username,
        phone: formData.value.phone,
        status: parseInt(formData.value.status)
      }

      // 如果密码不为空，添加到请求体
      if (formData.value.password) {
        requestBody.password = formData.value.password
      }

      await updateCashier(formData.value.id, requestBody)
      emit('notify', '编辑成功', 'success')
      dialogVisible.value = false
      loadCashiers()
    } catch (error) {
      console.error('编辑失败:', error)
      emit('notify', '网络错误，请稍后重试', 'error')
    }
  } else {
    // 新增
    try {
      // 构建请求体
      const requestBody = {
        username: formData.value.username,
        realName: formData.value.realName,
        phone: formData.value.phone,
        status: parseInt(formData.value.status)
      }

      // 如果密码不为空，添加到请求体
      if (formData.value.password) {
        requestBody.password = formData.value.password
      }

      await addCashier(requestBody)
      emit('notify', '新增成功', 'success')
      dialogVisible.value = false
      loadCashiers()
    } catch (error) {
      console.error('新增失败:', error)
      emit('notify', '网络错误，请稍后重试', 'error')
    }
  }
}

// 状态切换
const handleStatusChange = async (row) => {
  try {
    const response = await toggleStatus(row.id)
    const statusText = response.data === 1 ? '在职' : '离职'
    emit('notify', `状态已切换为${statusText}`, 'success')
    // 重新加载数据以确保状态同步
    loadCashiers()
  } catch (error) {
    // 状态切换失败，恢复原状态
    row.status = row.status === 1 ? 0 : 1
    console.error('状态切换失败:', error)
    emit('notify', '网络错误，请稍后重试', 'error')
  }
}

// 打开重置密码弹窗
const openResetPasswordDialog = (row) => {
  resetPasswordName.value = row.realName
  resetPasswordId.value = row.id
  resetPasswordDialogVisible.value = true
}

// 确认重置密码
const confirmResetPassword = async () => {
  try {
    await resetPassword(resetPasswordId.value)
    emit('notify', '密码已重置为123456', 'success')
    resetPasswordDialogVisible.value = false
  } catch (error) {
    console.error('重置密码失败:', error)
    emit('notify', '网络错误，请稍后重试', 'error')
  }
}

// 打开删除弹窗
const openDeleteDialog = (row) => {
  if (String(row.id) === currentUserId.value) {
    emit('notify', '无法删除当前登录账号', 'error')
    return
  }
  deleteName.value = row.realName
  deleteId.value = row.id
  deleteDialogVisible.value = true
}

// 确认删除
const confirmDelete = async () => {
  try {
    await deleteCashier(deleteId.value)
    emit('notify', '删除成功', 'success')
    deleteDialogVisible.value = false
    loadCashiers()
  } catch (error) {
    console.error('删除失败:', error)
    emit('notify', '网络错误，请稍后重试', 'error')
  }
}
</script>

<style lang="scss" scoped>
.cashier-management {
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
    align-items: center;
    gap: 16px;
    padding: 20px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;

    .filter-item {
      flex: 1;
    }

    .filter-actions {
      margin-left: auto;
    }
  }

  .cashier-list {
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

  .reset-password-container,
  .delete-container {
    text-align: center;
    padding: 20px 0;
    font-size: 16px;
    color: #333;
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
}
</style>