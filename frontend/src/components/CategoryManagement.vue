<template>
  <div class="category-management">
    <h2 class="page-title">分类管理</h2>

    <!-- 顶部操作栏 -->
    <div class="top-bar">
      <el-input v-model="searchQuery" placeholder="按分类名称搜索" :prefix-icon="Search" class="search-input" />
      <el-button type="primary" @click="openAddDialog">
        <el-icon>
          <Plus />
        </el-icon>
        新增分类
      </el-button>
    </div>

    <!-- 分类列表 -->
    <div class="category-list">
      <el-table :data="filteredCategories" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="分类名称" />
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-switch v-model="scope.row.status" active-value="1" inactive-value="0"
              @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column label="菜品数量" width="100" align="center">
          <template #default="scope">
            {{ scope.row.dishCount || 0 }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEditDialog(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="openDeleteDialog(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑分类弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px" center>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入分类名称" maxlength="10" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio label="1">启用</el-radio>
            <el-radio label="2">停用</el-radio>
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

    <!-- 删除确认弹窗 -->
    <el-dialog v-model="deleteDialogVisible" title="确认删除" width="400px" center>
      <span v-if="deleteRow && deleteRow.dishCount > 0">
        该分类下有 {{ deleteRow.dishCount }} 个菜品，请先删除或转移菜品
      </span>
      <span v-else-if="deleteRow && deleteRow.status === '1'">
        该分类为启用状态，需先停用后再删除
      </span>
      <span v-else>
        确定要删除分类"{{ deleteRow ? deleteRow.name : '' }}"吗？删除后无法恢复。
      </span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete"
            :disabled="deleteRow && (deleteRow.dishCount > 0 || deleteRow.status === '1')">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Plus, Search } from '@element-plus/icons-vue'
import { getCategoryList, getCategoryById, addCategory, updateCategory, deleteCategory, toggleCategoryStatus } from '@/api/category'

// 定义emit
// eslint-disable-next-line no-undef
const emit = defineEmits(['notify'])

// 分类列表
const categories = ref([])
const loading = ref(false)

// 获取分类列表
const fetchCategories = async (name = '') => {
  loading.value = true
  try {
    const data = await getCategoryList(name)
    // 后端：0=停用(DISABLE), 1=启用(ENABLE)
    // 转换为字符串以便 el-switch 使用
    categories.value = data.data.map(category => ({
      ...category,
      status: String(category.status)
    }))
  } catch (error) {
    console.error('获取分类列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const searchQuery = ref('')

// 过滤后的分类列表
const filteredCategories = computed(() => {
  return categories.value
})

// 组件挂载后获取分类列表
onMounted(() => {
  fetchCategories()
})

// 监听搜索输入
watch(searchQuery, (newValue) => {
  fetchCategories(newValue)
})

// 弹窗
const dialogVisible = ref(false)
const deleteDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const deleteRow = ref(null)

// 表单数据
const formData = ref({
  id: '',
  name: '',
  status: '1'
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { max: 10, message: '分类名称最多10字', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 打开新增弹窗
const openAddDialog = () => {
  dialogTitle.value = '新增分类'
  formData.value = {
    id: '',
    name: '',
    status: '0' // 后端默认 0=停用
  }
  dialogVisible.value = true
}

// 获取分类详情
const fetchCategoryById = async (id) => {
  try {
    const data = await getCategoryById(id)
    // 后端返回的 status: 0=停用, 1=启用，转为字符串
    const status = String(data.data.status)
    return {
      ...data.data,
      status
    }
  } catch (error) {
    console.error('获取分类详情失败:', error)
    return null
  }
}

// 打开编辑弹窗
const openEditDialog = async (row) => {
  dialogTitle.value = '编辑分类'

  // 调用接口获取分类详情
  const category = await fetchCategoryById(row.id)
  if (category) {
    formData.value = category
  } else {
    formData.value = { ...row }
  }

  dialogVisible.value = true
}

// 打开删除弹窗
const openDeleteDialog = (row) => {
  deleteRow.value = row
  deleteDialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    // 准备请求数据
    const requestData = {
      name: formData.value.name,
      // 状态：前端字符串转后端整数（0=停用, 1=启用）
      status: parseInt(formData.value.status)
    }

    if (formData.value.id) {
      // 编辑
      await updateCategory(formData.value.id, requestData)
      // 成功
      dialogVisible.value = false
      // 刷新分类列表
      fetchCategories()
      // 显示成功提示
      emit('notify', '编辑分类成功', 'success')
    } else {
      // 新增
      await addCategory(requestData)
      // 成功
      dialogVisible.value = false
      // 刷新分类列表
      fetchCategories()
      // 显示成功提示
      emit('notify', '新增分类成功', 'success')
    }
  } catch (error) {
    const msg = error.response?.data?.message || error.message || '操作失败，请重试'
    emit('notify', msg, 'error')
  }
}

// 确认删除
const confirmDelete = async () => {
  if (!deleteRow.value) return

  try {
    await deleteCategory(deleteRow.value.id)
    // 成功
    deleteDialogVisible.value = false
    deleteRow.value = null
    // 刷新分类列表
    fetchCategories()
    // 显示成功提示
    emit('notify', '删除分类成功', 'success')
  } catch (error) {
    console.error('删除分类失败:', error)
    emit('notify', '网络错误，请重试', 'error')
  }
}

// 处理状态切换
const handleStatusChange = async (row) => {
  try {
    // 调用后端接口
    const response = await toggleCategoryStatus(row.id)
    // 成功：用返回的状态更新表格，确保是字符串类型
    row.status = String(response.data)
  } catch (error) {
    console.error('状态切换失败:', error)
    emit('notify', '网络错误，请重试', 'error')
    // 恢复原来的状态
    row.status = row.status === '1' ? '0' : '1'
  }
}
</script>

<style lang="scss" scoped>
.category-management {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;

  .page-title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 20px;
    color: #333;
  }

  .top-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 20px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .search-input {
      width: 300px;
    }
  }

  .category-list {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    overflow: hidden;
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
}
</style>