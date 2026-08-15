<template>
    <div class="dish-management">
        <!-- 顶部操作栏 -->
        <div class="top-bar">
            <div class="search-section">
                <el-input v-model="searchQuery" placeholder="按菜品名称搜索" prefix-icon="el-icon-search" style="width: 300px"
                    @input="handleSearch" />
            </div>
            <div class="status-section">
                <el-select v-model="statusFilter" placeholder="选择状态" @change="handleStatusChange">
                    <el-option label="全部" value="-1" />
                    <el-option label="上架" value="1" />
                    <el-option label="下架" value="0" />
                </el-select>
            </div>
            <div class="action-section">
                <el-button type="primary" @click="openAddDialog">
                    <el-icon>
                        <Plus />
                    </el-icon>
                    新增菜品
                </el-button>
            </div>
        </div>

        <!-- 菜品列表 -->
        <div class="dish-list">
            <el-table :data="filteredDishes" style="width: 100%" v-loading="loading">
                <el-table-column label="图片" width="100">
                    <template #default="scope">
                        <el-image :src="scope.row.img || 'https://via.placeholder.com/150'" fit="cover"
                            style="width: 80px; height: 80px; border-radius: 4px" />
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="菜品名称" />
                <el-table-column prop="categoryName" label="所属分类" />
                <el-table-column prop="price" label="价格(元)" />
                <el-table-column label="是否有口味" width="120">
                    <template #default="scope">
                        <span>{{ scope.row.hasFlavor === 1 ? '是' : '否' }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="状态" width="120">
                    <template #default="scope">
                        <el-switch v-model="scope.row.status" active-value="1" inactive-value="0"
                            @change="handleStatusSwitch(scope.row)" />
                    </template>
                </el-table-column>
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

            <!-- 分页 -->
            <div class="pagination">
                <el-pagination :current-page="currentPage" :page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
                    layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
                    @current-change="handleCurrentChange" />
            </div>
        </div>

        <!-- 新增/编辑菜品弹窗 -->
        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" center>
            <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" :validate-event="false">
                <el-form-item label="菜品名称" prop="name">
                    <el-input v-model="formData.name" placeholder="请输入菜品名称" maxlength="10" />
                </el-form-item>
                <el-form-item label="所属分类" prop="categoryId">
                    <el-select v-model="formData.categoryId" placeholder="请选择所属分类">
                        <el-option v-for="category in categories" :key="category.id" :label="category.name"
                            :value="category.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="价格" prop="price">
                    <el-input-number v-model="formData.price" :min="0" :step="1" placeholder="请输入价格" />
                </el-form-item>
                <el-form-item label="图片上传" prop="img">
                    <el-upload class="avatar-uploader" :action="commonApi.getUploadUrl()" :headers="getUploadHeaders()"
                        :show-file-list="false" :on-success="handleUploadSuccess" :on-error="handleUploadError"
                        :before-upload="beforeUpload">
                        <img v-if="formData.img" :src="formData.img" class="avatar">
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="菜品描述" prop="description">
                    <el-input v-model="formData.description" type="textarea" placeholder="请输入菜品描述" maxlength="20"
                        :rows="2" />
                </el-form-item>
                <el-form-item label="是否有口味" prop="hasFlavor">
                    <el-switch v-model="formData.hasFlavor" active-value="1" inactive-value="0" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="formData.status">
                        <el-radio label="1">上架</el-radio>
                        <el-radio label="0">下架</el-radio>
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
            <span>确定要删除菜品"{{ deleteName }}"吗？删除后无法恢复。</span>
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
import { ref, computed, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import * as dishApi from '@/api/dish'
import * as categoryApi from '@/api/category'
import * as commonApi from '@/api/common'

// 定义emit
// eslint-disable-next-line no-undef
const emit = defineEmits(['notify'])

// 搜索和筛选
const searchQuery = ref('')
const statusFilter = ref('-1')

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 加载状态
const loading = ref(false)

// 弹窗
const dialogVisible = ref(false)
const deleteDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const deleteId = ref(null)
const deleteName = ref('')

// 表单数据
const formData = ref({
    id: '',
    name: '',
    categoryId: '',
    price: '',
    img: '',
    description: '',
    hasFlavor: '0',
    status: '0'
})

// 图片上传相关方法
const handleUploadSuccess = (response) => {
    if (response.code === 200) {
        formData.value.img = response.data
        emit('notify', '图片上传成功', 'success')
    } else {
        emit('notify', response.message || '图片上传失败', 'error')
    }
}

const handleUploadError = (error) => {
    console.error('图片上传失败:', error)
    emit('notify', '图片上传失败', 'error')
}

const beforeUpload = (file) => {
    // 检查文件类型
    const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg'
    if (!isJpgOrPng) {
        emit('notify', '只能上传 JPG、PNG 格式的图片', 'error')
        return false
    }
    // 检查文件大小
    const isLt2M = file.size / 1024 / 1024 < 2
    if (!isLt2M) {
        emit('notify', '图片大小不能超过 2MB', 'error')
        return false
    }
    return true
}

// 表单验证规则
const formRules = ref({
    name: [
        { required: true, message: '请输入菜品名称', trigger: 'blur' },
        { max: 10, message: '菜品名称最多10个字符', trigger: 'blur' }
    ],
    categoryId: [
        { required: true, message: '请选择所属分类', trigger: 'blur' }
    ],
    price: [
        { required: true, message: '请输入价格', trigger: 'blur' }
    ]
})

// 图片上传
const fileList = ref([])

// 获取上传请求头（通过 devServer 代理转发，需带网关密钥和 Token）
const getUploadHeaders = () => {
    const headers = {
        'X-Gateway-Secret': '20260726'
    }
    const token = localStorage.getItem('token')
    if (token) {
        headers['Authorization'] = token
    }
    return headers
}

// 菜品数据
const dishes = ref([])

// 分类数据
const categories = ref([])

// 加载分类列表
const loadCategories = async () => {
    try {
        const data = await categoryApi.getCategoryList()
        categories.value = data.data || []
    } catch (error) {
        console.error('获取分类失败:', error)
    }
}

// 根据 categoryId 获取分类名称的辅助函数
const getCategoryNameById = (categoryId) => {
    if (!categoryId) return '未知分类'
    const id = String(categoryId)
    const category = categories.value.find(c => String(c.id) === id)
    return category ? category.name : '未知分类'
}

// 根据 categoryName 获取 categoryId 的辅助函数
const getCategoryIdByName = (categoryName) => {
    if (!categoryName) return ''
    const category = categories.value.find(c => c.name === categoryName)
    return category ? category.id : ''
}

// 加载菜品数据
const loadDishes = async () => {
    loading.value = true
    try {
        const requestBody = {
            page: currentPage.value,
            pageSize: pageSize.value,
            name: searchQuery.value
        }

        // 状态筛选
        if (statusFilter.value !== '-1') {
            requestBody.status = parseInt(statusFilter.value)
        }

        const data = await dishApi.getDishPage(requestBody)
        // 确保 status 字段是字符串类型，与开关组件的绑定值类型匹配
        // 同时尝试通过 categoryId 回填 categoryName（修复后端 bug）
        dishes.value = data.data.list.map(item => {
            let categoryName = item.categoryName
            // 如果 categoryName 异常或为空，尝试通过 categoryId 查找
            if (!categoryName || categoryName === '未知分类') {
                categoryName = getCategoryNameById(item.categoryId)
            }
            return {
                ...item,
                status: String(item.status),
                categoryName
            }
        })
        total.value = data.data.total
    } catch (error) {
        console.error('获取菜品失败:', error)
    } finally {
        loading.value = false
    }
}

// 筛选后的菜品（直接使用dishes，因为后端已经处理了分页和筛选）
const filteredDishes = computed(() => {
    return dishes.value
})

// 搜索
const handleSearch = () => {
    currentPage.value = 1
    loadDishes()
}

// 状态筛选
const handleStatusChange = () => {
    currentPage.value = 1
    loadDishes()
}

// 分页大小变化
const handleSizeChange = (size) => {
    pageSize.value = size
    currentPage.value = 1
    loadDishes()
}

// 当前页变化
const handleCurrentChange = (current) => {
    currentPage.value = current
    loadDishes()
}



// 打开新增弹窗
const openAddDialog = () => {
    dialogTitle.value = '新增菜品'
    formData.value = {
        id: '',
        name: '',
        categoryId: '',
        price: 0,
        img: '',
        description: '',
        hasFlavor: '0',
        status: '0'
    }
    fileList.value = []
    dialogVisible.value = true
    // 重置表单验证状态
    if (formRef.value) {
        formRef.value.resetFields()
    }
}

// 打开编辑弹窗
const openEditDialog = (row) => {
    console.log('编辑菜品数据:', row)
    dialogTitle.value = '编辑菜品'
    // 优先使用 row 自带的 categoryId；如果没有（后端 DishPageVO 可能缺失），则通过 categoryName 反查
    let categoryId = row.categoryId || getCategoryIdByName(row.categoryName)
    formData.value = {
        ...row,
        categoryId: categoryId,
        hasFlavor: row.hasFlavor === 1 ? '1' : '0',
        status: row.status === '1' || row.status === 1 ? '1' : '0'
    }
    console.log('设置后的 formData:', formData.value)
    fileList.value = row.img ? [{ url: row.img }] : []
    dialogVisible.value = true
    // 重置表单验证状态
    if (formRef.value) {
        formRef.value.resetFields()
    }
}

// 打开删除弹窗
const openDeleteDialog = (row) => {
    deleteId.value = row.id
    deleteName.value = row.name
    deleteDialogVisible.value = true
}

// 提交表单
const submitForm = async () => {
    if (!formRef.value) return

    try {
        await formRef.value.validate()

        // 构建请求体，字段与后端 DTO 完全一致
        const requestBody = {
            name: formData.value.name,
            categoryId: formData.value.categoryId,
            price: formData.value.price
        }

        // 可选字段，只有填写了才传给后端
        if (formData.value.img) {
            requestBody.img = formData.value.img
        }
        if (formData.value.description) {
            requestBody.description = formData.value.description
        }
        // hasFlavor: 后端期望 Integer (0 或 1)
        if (formData.value.hasFlavor !== null && formData.value.hasFlavor !== undefined && formData.value.hasFlavor !== '') {
            requestBody.hasFlavor = parseInt(formData.value.hasFlavor)
        }
        // status: 后端期望 Integer (0 或 1)
        requestBody.status = formData.value.status !== null && formData.value.status !== undefined && formData.value.status !== ''
            ? parseInt(formData.value.status)
            : 0

        if (formData.value.id) {
            // 编辑
            await dishApi.updateDish(formData.value.id, requestBody)
            dialogVisible.value = false
            // 重置表单
            formData.value = {
                id: '',
                name: '',
                categoryId: '',
                price: 0,
                img: '',
                description: '',
                hasFlavor: 0,
                status: null
            }
            fileList.value = []
            // 重新加载数据
            loadDishes()
            // 显示成功提示
            emit('notify', '编辑菜品成功', 'success')
        } else {
            // 新增
            await dishApi.addDish(requestBody)
            dialogVisible.value = false
            // 重置表单
            formData.value = {
                id: '',
                name: '',
                categoryId: '',
                price: 0,
                img: '',
                description: '',
                hasFlavor: 0,
                status: null
            }
            fileList.value = []
            // 重新加载数据
            loadDishes()
            // 显示成功提示
            emit('notify', '新增菜品成功', 'success')
        }
    } catch (error) {
        const msg = error.response?.data?.message || error.message || '操作失败，请重试'
        emit('notify', msg, 'error')
    }
}

// 确认删除
const confirmDelete = async () => {
    if (!deleteId.value) return

    try {
        await dishApi.deleteDish(deleteId.value)
        deleteDialogVisible.value = false
        deleteId.value = null
        deleteName.value = ''
        // 重新加载数据
        loadDishes()
        // 显示成功提示
        emit('notify', '删除成功', 'success')
    } catch (error) {
        console.error('删除失败:', error)
        emit('notify', '网络错误，请重试', 'error')
    }
}

// 处理状态切换
const handleStatusSwitch = async (row) => {
    try {
        // 调用后端接口
        const response = await dishApi.toggleDishStatus(row.id)
        // 成功：用返回的状态更新表格，确保是字符串类型
        row.status = String(response.data)
    } catch (error) {
        console.error('状态切换失败:', error)
        emit('notify', '网络错误，请重试', 'error')
        // 恢复原来的状态
        row.status = row.status === '1' ? '0' : '1'
    }
}

// 组件挂载时初始化
onMounted(() => {
    // 初始化数据
    console.log('菜品管理页面初始化')
    // 先加载分类列表，再加载菜品数据
    loadCategories().then(() => {
        loadDishes()
    })
})
</script>

<style scoped lang="scss">
.dish-management {
    padding: 20px;
    background-color: #f5f7fa;
    min-height: 100vh;

    .top-bar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        padding: 20px;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

        .search-section {
            flex: 1;
        }

        .status-section {
            margin: 0 20px;
        }

        .action-section {
            flex: 1;
            display: flex;
            justify-content: flex-end;
        }
    }

    .dish-list {
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        padding: 20px;

        .pagination {
            margin-top: 20px;
            display: flex;
            justify-content: flex-end;
        }
    }

    .dialog-footer {
        display: flex;
        justify-content: flex-end;
        gap: 10px;
    }

    // 响应式设计
    @media (max-width: 768px) {
        .top-bar {
            flex-direction: column;
            align-items: flex-start;
            gap: 10px;

            .search-section,
            .status-section,
            .action-section {
                width: 100%;
                margin: 0;
            }

            .action-section {
                justify-content: flex-start;
            }
        }
    }

    /* 图片上传样式 */
    .avatar-uploader {
        display: flex;
        align-items: center;
    }

    .avatar-uploader-icon {
        font-size: 28px;
        color: #999;
    }

    .avatar {
        width: 100px;
        height: 100px;
        object-fit: cover;
        border-radius: 4px;
    }
}
</style>