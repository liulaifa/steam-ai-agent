<template>
  <div class="main-layout">
    <!-- 顶部标题栏 -->
    <header class="header">
      <div class="header-left">
        <div class="logo">🍜</div>
        <h1 class="restaurant-name">小刘面馆</h1>
      </div>
      <div class="header-right">
        <span class="employee-name">{{ employeeName }}</span>
        <el-button type="primary" @click="handleLogout" size="small">退出</el-button>
      </div>
    </header>

    <div class="main-content">
      <!-- 左侧导航菜单 -->
      <aside class="sidebar">
        <el-menu :default-active="activeMenu" class="sidebar-menu" @select="handleMenuSelect">
          <el-menu-item index="workbench">
            <el-icon>
              <HomeFilled />
            </el-icon>
            <span>工作台</span>
          </el-menu-item>
          <el-menu-item index="dishes">
            <el-icon>
              <ForkSpoon />
            </el-icon>
            <span>菜品管理</span>
          </el-menu-item>
          <el-menu-item index="categories">
            <el-icon>
              <Menu />
            </el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="orders">
            <el-icon>
              <List />
            </el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="cashiers">
            <el-icon>
              <UserFilled />
            </el-icon>
            <span>收银员管理</span>
          </el-menu-item>
          <el-menu-item index="logs">
            <el-icon>
              <Timer />
            </el-icon>
            <span>操作日志</span>
          </el-menu-item>
          <el-menu-item index="ai">
            <el-icon>
              <ChatDotRound />
            </el-icon>
            <span>AI 小助手</span>
          </el-menu-item>
        </el-menu>
      </aside>

      <!-- 右侧内容展示区 -->
      <main class="content">
        <WorkbenchPage v-if="activeMenu === 'workbench'" @notify="(message, type) => $emit('notify', message, type)" />
        <DishManagement v-else-if="activeMenu === 'dishes'"
          @notify="(message, type) => $emit('notify', message, type)" />
        <CategoryManagement v-else-if="activeMenu === 'categories'"
          @notify="(message, type) => $emit('notify', message, type)" />
        <OrderManagement v-else-if="activeMenu === 'orders'"
          @notify="(message, type) => $emit('notify', message, type)" />
        <CashierManagement v-else-if="activeMenu === 'cashiers'"
          @notify="(message, type) => $emit('notify', message, type)" />
        <OperationLog v-else-if="activeMenu === 'logs'" @notify="(message, type) => $emit('notify', message, type)" />
        <AIChat v-else-if="activeMenu === 'ai'" @notify="(message, type) => $emit('notify', message, type)" />
        <div v-else class="empty-content">
          <el-empty description="请选择左侧菜单" />
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import WorkbenchPage from './Workbench.vue'
import DishManagement from './DishManagement.vue'
import CategoryManagement from './CategoryManagement.vue'
import OrderManagement from './OrderManagement.vue'
import CashierManagement from './CashierManagement.vue'
import OperationLog from './OperationLog.vue'
import AIChat from './AIChat.vue'
import { HomeFilled, ForkSpoon, Menu, List, UserFilled, Timer, ChatDotRound } from '@element-plus/icons-vue'
import { logout } from '@/api/cashier'

export default {
  name: 'MainLayoutPage',
  components: {
    WorkbenchPage,
    DishManagement,
    CategoryManagement,
    OrderManagement,
    CashierManagement,
    OperationLog,
    AIChat,
    HomeFilled,
    ForkSpoon,
    Menu,
    List,
    UserFilled,
    Timer,
    ChatDotRound
  },
  emits: ['notify'],
  setup(props, { emit }) {
    const activeMenu = ref('workbench')
    const employeeName = ref('')

    // 初始化员工姓名
    const initEmployeeName = () => {
      const cashierStr = localStorage.getItem('cashier')
      if (cashierStr) {
        try {
          const cashier = JSON.parse(cashierStr)
          // 尝试读取 realName 或 real_name 字段
          employeeName.value = cashier.realName || cashier.real_name || '未知'
          console.log('员工姓名:', employeeName.value)
        } catch (error) {
          console.error('解析 cashier 数据失败:', error)
          employeeName.value = '未知'
        }
      } else {
        employeeName.value = '未知'
      }
    }

    // 页面加载时初始化
    initEmployeeName()

    const handleMenuSelect = (key) => {
      activeMenu.value = key
    }

    const handleLogout = async () => {
      try {
        // 发送登出请求，使用后端返回的 message
        const data = await logout()
        emit('notify', data.message || '登出成功', 'success')
      } catch (error) {
        // 失败：显示后端返回的 message
        const message = error.response?.data?.message || error.message || '登出失败'
        emit('notify', message, 'error')
      } finally {
        // 无论成功失败都清除本地存储并跳转
        localStorage.removeItem('token')
        localStorage.removeItem('cashier')
        localStorage.removeItem('userId')
        window.location.href = '/'
      }
    }

    return {
      activeMenu,
      employeeName,
      handleMenuSelect,
      handleLogout
    }
  }
}
</script>

<style lang="scss" scoped>
.main-layout {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;

  .header {
    height: 60px;
    background-color: #ff9800;
    color: white;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .header-left {
      display: flex;
      align-items: center;
      gap: 10px;

      .logo {
        font-size: 24px;
      }

      .restaurant-name {
        font-size: 18px;
        font-weight: bold;
        margin: 0;
      }
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 15px;

      .employee-name {
        font-size: 14px;
      }
    }
  }

  .main-content {
    flex: 1;
    display: flex;
    overflow: hidden;

    .sidebar {
      width: 200px;
      background-color: white;
      border-right: 1px solid #eaeaea;

      .sidebar-menu {
        height: 100%;
        border-right: none;

        .el-menu-item {
          height: 50px;
          line-height: 50px;

          &.is-active {
            background-color: #fff3e0 !important;
            color: #ff9800 !important;
          }
        }
      }
    }

    .content {
      flex: 1;
      padding: 20px;
      overflow-y: auto;

      .empty-content {
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }
}
</style>