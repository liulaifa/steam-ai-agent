<template>
  <div class="order-management">
    <h2 class="page-title">订单管理</h2>

    <!-- 顶部筛选栏 -->
    <div class="filter-bar">
      <div class="filter-item">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="起始日期"
          end-placeholder="结束日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
      </div>
      <div class="filter-item">
        <el-select v-model="statusFilter" placeholder="订单状态">
          <el-option label="全部" value="-1" />
          <el-option label="待支付" value="1" />
          <el-option label="已支付" value="2" />
          <el-option label="制作中" value="3" />
          <el-option label="已完成" value="4" />
          <el-option label="已取消" value="5" />
        </el-select>
      </div>
      <div class="filter-actions">
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button plain @click="exportReport">
          <el-icon>
            <Download />
          </el-icon>
          导出报表
        </el-button>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="order-list">
      <el-empty v-if="filteredOrders.length === 0" description="暂无订单" />
      <div v-else v-for="order in filteredOrders" :key="order.id" class="order-card" @click="openOrderDetail(order)">
        <div class="order-header">
          <div class="order-info">
            <span class="order-number">订单号：{{ order.orderNumber }}</span>
            <span class="order-time">{{ order.createTime }}</span>
            <span class="dine-type">{{ order.dineType === 1 ? '堂食' : '打包' }}</span>
            <span :class="['order-status', getStatusClass(order.status)]">{{ getStatusText(order.status) }}</span>
            <span class="order-price">¥{{ order.price }}</span>
          </div>
        </div>
        <div class="order-items">
          <div v-for="(item, index) in order.items" :key="index" class="order-item">
            <div class="item-info">
              <span class="item-name">{{ item.dishName }}</span>
              <span class="item-flavor">{{ item.flavorText }}</span>
            </div>
            <div class="item-price">
              ¥{{ item.price }} × {{ item.number }}
            </div>
          </div>
        </div>
        <div class="order-actions">
          <el-button v-if="order.status === 1" type="danger" @click.stop="handleCancelOrder(order)">
            取消订单
          </el-button>
          <el-button v-if="order.status === 1" type="primary" @click.stop="handleConfirmPayment(order)">
            确认收款
          </el-button>
          <el-button v-else-if="order.status === 2" type="primary" @click.stop="handleStartProduction(order)">
            开始制作
          </el-button>
          <el-button v-else-if="order.status === 3" type="primary" @click.stop="handleFinishProduction(order)">
            完成制作
          </el-button>
        </div>
      </div>

      <!-- 分页组件 -->
      <div class="pagination-container" v-if="filteredOrders.length > 0">
        <el-pagination :current-page="currentPage" :page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="600px" center>
      <div v-if="currentOrder" class="order-detail">
        <div class="detail-item">
          <label>订单号：</label>
          <span>{{ currentOrder.orderNumber }}</span>
        </div>
        <div class="detail-item">
          <label>下单时间：</label>
          <span>{{ currentOrder.createTime }}</span>
        </div>
        <div class="detail-item">
          <label>就餐方式：</label>
          <span>{{ currentOrder.dineType === 1 ? '堂食' : '打包' }}</span>
        </div>
        <div class="detail-item">
          <label>订单状态：</label>
          <span :class="['status-tag', getStatusClass(currentOrder.status)]">{{ getStatusText(currentOrder.status)
            }}</span>
        </div>
        <div class="detail-item">
          <label>支付方式：</label>
          <span>{{ getPayMethodText(currentOrder.payMethod) }}</span>
        </div>
        <div class="detail-item">
          <label>支付时间：</label>
          <span>{{ currentOrder.payTime || '-' }}</span>
        </div>
        <div class="detail-item">
          <label>备注：</label>
          <span>{{ currentOrder.remark || '-' }}</span>
        </div>
        <div class="detail-item">
          <label>总金额：</label>
          <span>¥{{ currentOrder.price }}</span>
        </div>
        <div class="detail-section">
          <h3>菜品列表</h3>
          <div class="detail-items">
            <div v-for="(item, index) in currentOrder.items" :key="index" class="detail-item">
              <div class="item-info">
                <span class="item-name">{{ item.dishName }}</span>
                <span class="item-flavor">{{ item.flavorText }}</span>
              </div>
              <div class="item-price">
                <span>¥{{ item.price }} × {{ item.number }}</span>
                <span class="item-subtotal">小计：¥{{ (item.price * item.number).toFixed(2) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 支付方式选择弹窗 -->
    <el-dialog v-model="payDialogVisible" title="选择支付方式" width="400px" center>
      <div class="pay-method-container">
        <el-radio-group v-model="selectedPayMethod">
          <el-radio label="1">微信</el-radio>
          <el-radio label="2">现金</el-radio>
          <el-radio label="3">支付宝</el-radio>
        </el-radio-group>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="payDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmPayMethod">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 开始制作确认弹窗 -->
    <el-dialog v-model="cookDialogVisible" title="确认开始制作" width="400px" center>
      <div class="cook-confirm-container">
        <p>确定开始制作该订单吗？</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cookDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmStartProduction">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 完成制作确认弹窗 -->
    <el-dialog v-model="completeDialogVisible" title="确认完成制作" width="400px" center>
      <div class="cook-confirm-container">
        <p>确定已完成制作吗？</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="completeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmFinishProduction">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 取消订单确认弹窗 -->
    <el-dialog v-model="cancelDialogVisible" title="确认取消订单" width="400px" center>
      <div class="cook-confirm-container">
        <p>确定要取消该订单吗？取消后无法恢复。</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmCancelOrder">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Download } from '@element-plus/icons-vue'
import { getOrderList, getOrderDetail, payOrder, cookOrder, completeOrder, cancelOrder, exportOrders } from '@/api/order'

// eslint-disable-next-line no-undef
const emit = defineEmits(['notify'])

// 订单列表
const orders = ref([])
const loading = ref(false)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 筛选条件
const dateRange = ref([])
const statusFilter = ref('-1')

// 订单详情弹窗
const detailDialogVisible = ref(false)
const currentOrder = ref(null)

// 支付方式选择弹窗
const payDialogVisible = ref(false)
const selectedPayMethod = ref('')
const currentOrderId = ref(null)

// 开始制作确认弹窗
const cookDialogVisible = ref(false)
const cookOrderId = ref(null)

// 完成制作确认弹窗
const completeDialogVisible = ref(false)
const completeOrderId = ref(null)

// 取消订单确认弹窗
const cancelDialogVisible = ref(false)
const cancelOrderId = ref(null)

// 加载订单列表
const loadOrders = async () => {
  try {
    loading.value = true
    // 构建请求参数
    const requestData = {
      page: currentPage.value,
      pageSize: pageSize.value
    }

    // 添加日期筛选（后端要求 yyyy-MM-dd HH:mm:ss 格式）
    if (dateRange.value && dateRange.value.length === 2) {
      requestData.startDate = dateRange.value[0] + ' 00:00:00'
      requestData.endDate = dateRange.value[1] + ' 23:59:59'
    }

    // 添加状态筛选
    if (statusFilter.value !== '-1') {
      requestData.status = parseInt(statusFilter.value)
    }

    // 调用后端接口
    const data = await getOrderList(requestData)
    orders.value = data.data.list
    total.value = data.data.total
  } catch (error) {
    console.error('获取订单列表失败:', error)
    emit('notify', '网络错误，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载订单
onMounted(() => {
  loadOrders()
})

// 订单列表（直接使用接口返回的数据）
const filteredOrders = computed(() => {
  return orders.value.map(order => ({
    ...order,
    items: (order.items || []).map(item => ({
      ...item,
      flavorText: getFlavorText(item.dishFlavorVO)
    }))
  }))
})

// 根据 DishFlavorVO 生成口味文本
const getFlavorText = (flavor) => {
  if (!flavor) return ''
  const texts = []
  if (flavor.sweet === 1) texts.push('加糖')
  if (flavor.scallion === 1) texts.push('加葱')
  if (flavor.coriander === 1) texts.push('加香菜')
  if (flavor.spicy === 1) texts.push('微辣')
  else if (flavor.spicy === 2) texts.push('中辣')
  else if (flavor.spicy === 3) texts.push('特辣')
  return texts.join('，')
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    1: '待支付',
    2: '已支付',
    3: '制作中',
    4: '已完成',
    5: '已取消'
  }
  return statusMap[status] || ''
}

// 获取状态样式类
const getStatusClass = (status) => {
  const classMap = {
    1: 'status-pending',
    2: 'status-paid',
    3: 'status-processing',
    4: 'status-completed',
    5: 'status-canceled'
  }
  return classMap[status] || ''
}

// 获取支付方式文本
const getPayMethodText = (payMethod) => {
  const payMethodMap = {
    1: '微信',
    2: '现金',
    3: '支付宝'
  }
  return payMethodMap[payMethod] || '未支付'
}

// 搜索
const handleSearch = () => {
  // 重置页码
  currentPage.value = 1
  // 重新加载订单
  loadOrders()
}

// 重置
const handleReset = () => {
  // 清空筛选条件
  dateRange.value = []
  statusFilter.value = '-1'
  // 重置页码
  currentPage.value = 1
  // 重新加载订单
  loadOrders()
}

// 分页相关方法
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadOrders()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  loadOrders()
}

// 打开订单详情
const openOrderDetail = async (order) => {
  try {
    loading.value = true
    const data = await getOrderDetail(order.id)
    const orderData = data.data
    // 为详情中的菜品项添加口味文本
    if (orderData.items) {
      orderData.items = orderData.items.map(item => ({
        ...item,
        flavorText: getFlavorText(item.dishFlavorVO)
      }))
    }
    currentOrder.value = orderData
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取订单详情失败:', error)
    emit('notify', '网络错误，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}

// 确认收款
const handleConfirmPayment = (order) => {
  // 打开支付方式选择弹窗
  currentOrderId.value = order.id
  selectedPayMethod.value = ''
  payDialogVisible.value = true
}

// 确认支付方式
const confirmPayMethod = async () => {
  if (!selectedPayMethod.value) {
    return
  }

  try {
    loading.value = true
    // 调用后端接口确认收款
    await payOrder(currentOrderId.value, { payMethod: parseInt(selectedPayMethod.value) })
    // 成功
    payDialogVisible.value = false
    // 提示收款成功
    emit('notify', '收款成功', 'success')
    // 刷新订单列表
    loadOrders()
  } catch (error) {
    console.error('收款失败:', error)
    emit('notify', '网络错误，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}

// 开始制作
const handleStartProduction = (order) => {
  cookOrderId.value = order.id
  cookDialogVisible.value = true
}

// 确认开始制作
const confirmStartProduction = async () => {
  try {
    loading.value = true
    await cookOrder(cookOrderId.value)
    cookDialogVisible.value = false
    emit('notify', '已开始制作', 'success')
    loadOrders()
  } catch (error) {
    console.error('开始制作失败:', error)
    emit('notify', '网络错误，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}

// 完成制作
const handleFinishProduction = (order) => {
  completeOrderId.value = order.id
  completeDialogVisible.value = true
}

// 确认完成制作
const confirmFinishProduction = async () => {
  try {
    loading.value = true
    await completeOrder(completeOrderId.value)
    completeDialogVisible.value = false
    emit('notify', '已完成制作', 'success')
    loadOrders()
  } catch (error) {
    console.error('完成制作失败:', error)
    emit('notify', '网络错误，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}

// 取消订单
const handleCancelOrder = (order) => {
  cancelOrderId.value = order.id
  cancelDialogVisible.value = true
}

// 确认取消订单
const confirmCancelOrder = async () => {
  try {
    loading.value = true
    await cancelOrder(cancelOrderId.value)
    cancelDialogVisible.value = false
    emit('notify', '订单已取消', 'success')
    loadOrders()
  } catch (error) {
    console.error('取消订单失败:', error)
    emit('notify', '网络错误，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}

// 导出报表
const exportReport = async () => {
  try {
    const filterParams = {}

    // 添加日期范围（后端 OrderExportDTO 用 String 接收）
    if (dateRange.value && dateRange.value.length === 2) {
      filterParams.startDate = dateRange.value[0] + ' 00:00:00'
      filterParams.endDate = dateRange.value[1] + ' 23:59:59'
    }

    // 添加订单状态
    if (statusFilter.value !== '-1') {
      filterParams.status = parseInt(statusFilter.value)
    }

    const response = await exportOrders(filterParams)

    const contentDisposition = response.headers['content-disposition']
    let fileName = '订单报表.xlsx'
    if (contentDisposition) {
      const match = contentDisposition.match(/filename\*=utf-8''(.+)/)
      if (match) fileName = decodeURIComponent(match[1])
    }

    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = fileName
    link.click()
    window.URL.revokeObjectURL(url)

    emit('notify', '导出成功', 'success')
  } catch (error) {
    console.error('导出报表失败:', error)
    emit('notify', error.message || '导出失败，请稍后重试', 'error')
  }
}
</script>

<style lang="scss" scoped>
.order-management {
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
      flex: 0 0 auto;
    }

    .filter-actions {
      margin-left: auto;
      display: flex;
      gap: 10px;
    }
  }

  .order-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .order-card {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    padding: 20px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
    }

    .order-header {
      margin-bottom: 15px;

      .order-info {
        display: flex;
        flex-wrap: wrap;
        gap: 15px;
        align-items: center;

        .order-number {
          font-weight: bold;
          color: #333;
        }

        .order-time {
          color: #666;
        }

        .dine-type {
          padding: 2px 8px;
          background-color: #e6f7ff;
          color: #1890ff;
          border-radius: 12px;
          font-size: 12px;
        }

        .order-status {
          padding: 2px 8px;
          border-radius: 12px;
          font-size: 12px;
          font-weight: bold;

          &.status-pending {
            background-color: #fff7e6;
            color: #fa8c16;
          }

          &.status-paid {
            background-color: #e6f7ff;
            color: #1890ff;
          }

          &.status-processing {
            background-color: #f0f5ff;
            color: #722ed1;
          }

          &.status-completed {
            background-color: #f6ffed;
            color: #52c41a;
          }

          &.status-canceled {
            background-color: #f5f5f5;
            color: #8c8c8c;
          }
        }

        .order-price {
          margin-left: auto;
          font-weight: bold;
          color: #ff4d4f;
          font-size: 18px;
        }
      }
    }

    .order-items {
      border-top: 1px solid #f0f0f0;
      border-bottom: 1px solid #f0f0f0;
      padding: 15px 0;
      margin-bottom: 15px;

      .order-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;

        &:last-child {
          margin-bottom: 0;
        }

        .item-info {
          display: flex;
          align-items: center;
          gap: 10px;

          .item-name {
            font-weight: bold;
            color: #333;
          }

          .item-flavor {
            color: #666;
            font-size: 14px;
          }
        }

        .item-price {
          color: #666;
          display: flex;
          flex-direction: column;
          align-items: flex-end;

          .item-subtotal {
            font-weight: bold;
            color: #333;
            margin-top: 5px;
          }
        }
      }
    }

    .order-actions {
      display: flex;
      justify-content: flex-end;
    }
  }

  .order-detail {
    .detail-item {
      display: flex;
      margin-bottom: 15px;

      label {
        width: 100px;
        font-weight: bold;
        color: #333;
      }

      span {
        flex: 1;
      }

      .status-tag {
        padding: 2px 8px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: bold;

        &.status-pending {
          background-color: #fff7e6;
          color: #fa8c16;
        }

        &.status-paid {
          background-color: #e6f7ff;
          color: #1890ff;
        }

        &.status-processing {
          background-color: #f0f5ff;
          color: #722ed1;
        }

        &.status-completed {
          background-color: #f6ffed;
          color: #52c41a;
        }

        &.status-canceled {
          background-color: #f5f5f5;
          color: #8c8c8c;
        }
      }
    }

    .detail-section {
      margin-top: 20px;

      h3 {
        margin-bottom: 15px;
        font-size: 16px;
        font-weight: bold;
      }

      .detail-items {
        border-top: 1px solid #f0f0f0;
        padding-top: 15px;
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }

  .pay-method-container {
    padding: 20px 0;

    .el-radio {
      display: block;
      margin-bottom: 15px;
    }
  }

  .cook-confirm-container {
    text-align: center;
    padding: 20px 0;
    font-size: 16px;
    color: #333;
  }
}
</style>