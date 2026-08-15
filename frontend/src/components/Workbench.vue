<template>
  <div class="workbench">
    <!-- 顶部横向分类标签 -->
    <div class="category-tabs">
      <div v-for="category in categories" :key="category.id" class="category-tab"
        :class="{ active: activeCategory === category.id, hot: category.id === 'hot' }"
        @click="activeCategory = category.id">
        {{ category.name }}
      </div>
    </div>

    <!-- 中间菜品卡片网格 -->
    <div class="dish-grid-container">
      <div class="dish-grid" ref="dishGridRef">
        <div v-for="dish in filteredDishes" :key="dish.id" class="dish-card">
          <div class="dish-image" @click="addDishToCart(dish)">
            <img :src="dish.img" :alt="dish.name" />
            <div v-if="dish.salesCount" class="sales-tag">已售{{ dish.salesCount }}</div>
          </div>
          <div class="dish-info">
            <h3 class="dish-name" @click="addDishToCart(dish)">{{ dish.name }}</h3>
            <div class="dish-actions">
              <p class="dish-price" @click="addDishToCart(dish)">¥{{ dish.price }}</p>
              <el-button v-if="dish.hasFlavor === 1" size="small" type="primary"
                @click.stop="openFlavorDialog(dish)">口味</el-button>
            </div>
          </div>
        </div>

        <!-- 口味选择弹窗 -->
        <el-dialog v-model="showFlavorDialog" :title="currentDish ? `${currentDish.name} - 口味选择` : '口味选择'" width="400px"
          center>
          <div class="flavor-options">
            <div class="flavor-item">
              <span class="flavor-label">甜</span>
              <div class="flavor-buttons">
                <el-button :type="currentFlavor.sweet === 0 ? 'primary' : 'default'" size="small"
                  @click="currentFlavor.sweet = 0">不加</el-button>
                <el-button :type="currentFlavor.sweet === 1 ? 'primary' : 'default'" size="small"
                  @click="currentFlavor.sweet = 1">加</el-button>
              </div>
            </div>
            <div class="flavor-item">
              <span class="flavor-label">葱</span>
              <div class="flavor-buttons">
                <el-button :type="currentFlavor.scallion === 0 ? 'primary' : 'default'" size="small"
                  @click="currentFlavor.scallion = 0">不加</el-button>
                <el-button :type="currentFlavor.scallion === 1 ? 'primary' : 'default'" size="small"
                  @click="currentFlavor.scallion = 1">加</el-button>
              </div>
            </div>
            <div class="flavor-item">
              <span class="flavor-label">香菜</span>
              <div class="flavor-buttons">
                <el-button :type="currentFlavor.coriander === 0 ? 'primary' : 'default'" size="small"
                  @click="currentFlavor.coriander = 0">不加</el-button>
                <el-button :type="currentFlavor.coriander === 1 ? 'primary' : 'default'" size="small"
                  @click="currentFlavor.coriander = 1">加</el-button>
              </div>
            </div>
            <div class="flavor-item">
              <span class="flavor-label">辣度</span>
              <div class="flavor-buttons spicy-buttons">
                <el-button :type="currentFlavor.spicy === 0 ? 'primary' : 'default'" size="small"
                  @click="currentFlavor.spicy = 0">不辣</el-button>
                <el-button :type="currentFlavor.spicy === 1 ? 'primary' : 'default'" size="small"
                  @click="currentFlavor.spicy = 1">微辣</el-button>
                <el-button :type="currentFlavor.spicy === 2 ? 'primary' : 'default'" size="small"
                  @click="currentFlavor.spicy = 2">中辣</el-button>
                <el-button :type="currentFlavor.spicy === 3 ? 'primary' : 'default'" size="small"
                  @click="currentFlavor.spicy = 3">特辣</el-button>
              </div>
            </div>
          </div>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="showFlavorDialog = false">取消</el-button>
              <el-button type="primary" @click="addToCartWithFlavor">确定</el-button>
            </span>
          </template>
        </el-dialog>
      </div>
    </div>

    <!-- 购物车容器 -->
    <div class="cart-container">
      <!-- 底部购物车栏 -->
      <div class="cart-bar">
        <div class="cart-info">
          <span class="cart-count">{{ cartCount }}件</span>
          <span class="cart-total">¥{{ cartTotal }}</span>
        </div>
        <div class="cart-actions">
          <el-button @click="clearCartItems" size="small">清空</el-button>
          <el-button @click="toggleCartExpand" size="small">
            {{ isCartExpanded ? '收起' : '展开' }}
          </el-button>
          <el-button type="primary" class="checkout-btn" @click="openOrderDialog"
            :disabled="cart.length === 0">结算</el-button>
        </div>
      </div>

      <!-- 下单确认弹窗 -->
      <el-dialog v-model="showOrderDialog" title="确认下单" width="400px" center>
        <div class="order-form">
          <div class="form-item">
            <span class="form-label">就餐方式：</span>
            <el-radio-group v-model="orderForm.dineType">
              <el-radio label="1">堂食</el-radio>
              <el-radio label="2">打包</el-radio>
            </el-radio-group>
          </div>
          <div class="form-item">
            <span class="form-label">备注：</span>
            <el-input v-model="orderForm.remark" placeholder="选填" />
          </div>
        </div>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="showOrderDialog = false">取消</el-button>
            <el-button type="primary" @click="confirmOrder">确认下单</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 购物车展开详情 -->
      <div v-if="isCartExpanded" class="cart-details" ref="cartDetailsRef">
        <div v-if="cart.length === 0" class="empty-cart">
          购物车为空
        </div>
        <div v-else class="cart-items">
          <div v-for="item in cart" :key="item.id" class="cart-item">
            <div class="cart-item-info">
              <div class="cart-item-details">
                <span class="cart-item-name">{{ item.name }}</span>
                <span v-if="item.hasFlavor === 1 && getFlavorText(item.flavor)" class="cart-item-flavor">{{
                  getFlavorText(item.flavor)
                  }}</span>
              </div>
              <span class="cart-item-price">¥{{ item.price }}</span>
            </div>
            <div class="cart-item-quantity">
              <el-button @click="decreaseQuantity(item)" size="small">-
              </el-button>
              <span class="quantity">{{ item.quantity }}</span>
              <el-button @click="increaseQuantity(item)" size="small">+
              </el-button>
            </div>
            <el-button @click="removeItem(item)" size="small" type="danger">
              <el-icon>
                <Delete />
              </el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { Delete } from '@element-plus/icons-vue'
import { getEnabledCategories } from '@/api/category'
import { getCart, addToCart, updateCart, deleteCartItem, clearCart } from '@/api/cart'
import { getDishesByCategory, getAllDishes, getHotDishes } from '@/api/dish'
import { matchFlavor } from '@/api/flavor'
import { createOrder } from '@/api/order'

export default {
  name: 'WorkbenchPage',
  emits: ['notify'],
  setup(props, { emit }) {
    // 状态
    const categories = ref([])
    const dishes = ref([])
    const activeCategory = ref(null)
    const cart = ref([])
    const isCartExpanded = ref(false)
    const showFlavorDialog = ref(false)
    const currentDish = ref(null)
    const currentFlavor = ref({
      sweet: 0, // 0不加 1加
      spicy: 0, // 0不辣 1微辣 2中辣 3特辣
      scallion: 0, // 0不加 1加
      coriander: 0 // 0不加 1加
    })
    const dishGridRef = ref(null)
    const cartDetailsRef = ref(null)
    const loading = ref(false)
    const showOrderDialog = ref(false)
    const orderForm = ref({
      dineType: '1', // 默认堂食
      remark: ''
    })

    // 计算属性
    const filteredDishes = computed(() => {
      return dishes.value
    })

    const cartCount = computed(() => {
      return cart.value.reduce((total, item) => total + (item.quantity || 0), 0)
    })

    const cartTotal = computed(() => {
      return cart.value.reduce((total, item) => total + (item.price || 0) * (item.quantity || 0), 0)
    })

    // 方法

    // 获取分类列表
    const fetchCategories = async () => {
      try {
        const data = await getEnabledCategories()
        categories.value = data.data
        // 添加"全部"分类
        categories.value.unshift({ id: null, name: '全部' })
        // 添加"热门"分类
        categories.value.unshift({ id: 'hot', name: '热门' })
        // 默认选择第一个分类
        if (categories.value.length > 0) {
          activeCategory.value = categories.value[0].id
          // 立即获取对应分类的菜品
          fetchDishesByCategory(activeCategory.value)
        }
      } catch (error) {
        console.error('获取分类失败:', error)
        emit('notify', '网络错误，请稍后重试', 'error')
      }
    }

    // 根据分类获取菜品
    const fetchDishesByCategory = async (categoryId) => {
      try {
        loading.value = true
        let data
        if (categoryId === 'hot') {
          // 热门菜品
          data = await getHotDishes()
        } else if (categoryId === null) {
          // 全部菜品
          data = await getAllDishes()
        } else {
          // 按分类获取菜品
          data = await getDishesByCategory(categoryId)
        }
        // 确保菜品数据结构正确，适配后端返回的数据
        if (Array.isArray(data.data)) {
          // 如果返回的是数组，直接映射
          dishes.value = data.data.map(dish => ({
            ...dish,
            id: dish.id,
            name: dish.name,
            price: dish.price,
            img: dish.img,
            hasFlavor: dish.hasFlavor,
            status: dish.status,
            categoryId: dish.categoryId,
            categoryName: dish.categoryName,
            description: dish.description,
            salesCount: dish.salesCount
          }))
        } else {
          // 如果返回的是单个对象，包装成数组
          dishes.value = [{
            ...data.data,
            id: data.data.id,
            name: data.data.name,
            price: data.data.price,
            img: data.data.img,
            hasFlavor: data.data.hasFlavor,
            status: data.data.status,
            categoryId: data.data.categoryId,
            categoryName: data.data.categoryName,
            description: data.data.description,
            salesCount: data.data.salesCount
          }]
        }
      } catch (error) {
        console.error('获取菜品失败:', error)
        emit('notify', '网络错误，请稍后重试', 'error')
      } finally {
        loading.value = false
      }
    }

    // 获取购物车
    const fetchCart = async () => {
      try {
        const cashierId = localStorage.getItem('userId')
        // 后端返回 { code: 200, data: [...] } 格式
        const data = await getCart(cashierId)
        const list = data.data || []
        cart.value = list.map(item => ({
          id: String(item.id),
          dishId: item.dishId ? String(item.dishId) : null,
          flavorId: item.flavorId ? String(item.flavorId) : null,
          quantity: item.number || 1,
          price: item.price || 0,
          name: item.dishName || '未知菜品',
          hasFlavor: item.hasFlavor || 0,
          flavor: item.dishFlavorVO || {
            sweet: 0,
            spicy: 0,
            scallion: 0,
            coriander: 0
          }
        }))
      } catch (error) {
        console.error('获取购物车失败:', error)
        emit('notify', '网络错误，请稍后重试', 'error')
      }
    }

    // 添加到购物车
    const addDishToCart = async (dish) => {
      try {
        // CartAddDTO: { dishId, flavorId(可选) }
        const cartAddDTO = {
          dishId: dish.id
        }
        await addToCart(cartAddDTO)
        await fetchCart()
        emit('notify', '添加成功', 'success')
      } catch (error) {
        console.error('添加购物车失败:', error)
        emit('notify', '网络错误，请稍后重试', 'error')
      }
    }

    // 带口味添加到购物车
    const addToCartWithFlavor = async () => {
      if (currentDish.value) {
        try {
          // 先获取口味ID
          const flavorData = await matchFlavor(currentFlavor.value)
          const flavorId = flavorData.data.id
          // CartAddDTO: { dishId, flavorId }
          const cartAddDTO = {
            dishId: currentDish.value.id,
            flavorId: flavorId
          }
          await addToCart(cartAddDTO)
          await fetchCart()
          showFlavorDialog.value = false
          emit('notify', '添加成功', 'success')
        } catch (error) {
          console.error('添加购物车失败:', error)
          emit('notify', '网络错误，请稍后重试', 'error')
        }
      }
    }

    // 减少数量
    const decreaseQuantity = async (item) => {
      if (item.quantity > 1) {
        try {
          // CartUpdateDTO: { dishId, flavorId, number }  number 负数减少
          const cartUpdateDTO = {
            dishId: item.dishId,
            flavorId: item.flavorId,
            number: -1
          }
          await updateCart(cartUpdateDTO)
          await fetchCart()
          emit('notify', '减少成功', 'success')
        } catch (error) {
          console.error('更新购物车失败:', error)
          emit('notify', '网络错误，请稍后重试', 'error')
        }
      } else {
        // 数量为1时删除
        await removeItem(item)
      }
    }

    // 增加数量
    const increaseQuantity = async (item) => {
      try {
        // CartUpdateDTO: { dishId, flavorId, number }  number 正数增加
        const cartUpdateDTO = {
          dishId: item.dishId,
          flavorId: item.flavorId,
          number: 1
        }
        await updateCart(cartUpdateDTO)
        await fetchCart()
        emit('notify', '增加成功', 'success')
      } catch (error) {
        console.error('更新购物车失败:', error)
        emit('notify', '网络错误，请稍后重试', 'error')
      }
    }

    // 删除商品
    const removeItem = async (item) => {
      try {
        await deleteCartItem(item.id)
        // 重新获取购物车
        await fetchCart()
        emit('notify', '删除成功', 'success')
      } catch (error) {
        console.error('删除购物车商品失败:', error)
        emit('notify', '网络错误，请稍后重试', 'error')
      }
    }

    // 清空购物车
    const clearCartItems = async () => {
      try {
        await clearCart()
        // 重新获取购物车
        await fetchCart()
        emit('notify', '清空成功', 'success')
      } catch (error) {
        console.error('清空购物车失败:', error)
        emit('notify', '网络错误，请稍后重试', 'error')
      }
    }

    // 切换购物车展开状态
    const toggleCartExpand = () => {
      isCartExpanded.value = !isCartExpanded.value
    }

    // 口味相关方法
    const openFlavorDialog = (dish) => {
      currentDish.value = dish
      // 重置口味为默认值
      currentFlavor.value = {
        sweet: 0,
        spicy: 0,
        scallion: 0,
        coriander: 0
      }
      showFlavorDialog.value = true
    }

    const getFlavorText = (flavor) => {
      if (!flavor) return ''
      const texts = []
      // 检查各个口味属性，处理可能的null或undefined值
      // 按照甜，葱，香菜，辣度的顺序生成文本
      if (flavor.sweet === 1) texts.push('加糖')
      if (flavor.sweet === 0) texts.push('不加糖')
      if (flavor.scallion === 1) texts.push('加葱')
      if (flavor.scallion === 0) texts.push('不加葱')
      if (flavor.coriander === 1) texts.push('加香菜')
      if (flavor.coriander === 0) texts.push('不加香菜')
      if (flavor.spicy === 1) texts.push('微辣')
      if (flavor.spicy === 2) texts.push('中辣')
      if (flavor.spicy === 3) texts.push('特辣')
      if (flavor.spicy === 0) texts.push('不辣')
      console.log('口味数据:', flavor, '生成的文本:', texts.join(', '))
      return texts.join(', ')
    }

    // 滑动缓冲效果
    const handleScroll = (element) => {
      return () => {
        const { scrollTop, scrollHeight, clientHeight } = element

        // 当滚动到顶部时
        if (scrollTop <= 0) {
          // 回弹到顶部
          element.scrollTop = 1
        }

        // 当滚动到底部时
        if (scrollTop + clientHeight >= scrollHeight - 1) {
          // 回弹到底部
          element.scrollTop = scrollHeight - clientHeight - 1
        }
      }
    }

    // 监听分类变化
    watch(activeCategory, (newCategoryId) => {
      fetchDishesByCategory(newCategoryId)
    })

    // 组件挂载后添加滚动事件监听器
    onMounted(async () => {
      // 获取分类
      await fetchCategories()
      // 获取购物车
      await fetchCart()

      if (dishGridRef.value) {
        dishGridRef.value.addEventListener('wheel', handleScroll(dishGridRef.value), { passive: false })
      }
      if (cartDetailsRef.value) {
        cartDetailsRef.value.addEventListener('wheel', handleScroll(cartDetailsRef.value), { passive: false })
      }
    })

    // 组件卸载前移除滚动事件监听器
    onUnmounted(() => {
      if (dishGridRef.value) {
        dishGridRef.value.removeEventListener('wheel', handleScroll(dishGridRef.value))
      }
      if (cartDetailsRef.value) {
        cartDetailsRef.value.removeEventListener('wheel', handleScroll(cartDetailsRef.value))
      }
    })

    // 打开下单弹窗
    const openOrderDialog = () => {
      if (cart.value.length === 0) {
        emit('notify', '购物车为空，无法下单', 'error')
        return
      }
      // 重置表单
      orderForm.value = {
        dineType: '1', // 默认堂食
        remark: ''
      }
      showOrderDialog.value = true
    }

    // 确认下单
    const confirmOrder = async () => {
      try {
        const requestData = {
          dineType: parseInt(orderForm.value.dineType),
          remark: orderForm.value.remark || ''
        }
        const data = await createOrder(requestData)
        // 成功
        showOrderDialog.value = false
        // 提示下单成功
        emit('notify', `下单成功，订单号：${data.data.orderNumber}`, 'success')
        // 清空购物车
        await fetchCart()
      } catch (error) {
        console.error('下单失败:', error)
        emit('notify', '网络错误，请稍后重试', 'error')
      }
    }

    return {
      categories,
      activeCategory,
      filteredDishes,
      cart,
      isCartExpanded,
      showFlavorDialog,
      showOrderDialog,
      orderForm,
      currentDish,
      currentFlavor,
      dishGridRef,
      cartDetailsRef,
      cartCount,
      cartTotal,
      loading,
      addDishToCart,
      toggleCartExpand,
      clearCartItems,
      decreaseQuantity,
      increaseQuantity,
      removeItem,
      openFlavorDialog,
      addToCartWithFlavor,
      getFlavorText,
      openOrderDialog,
      confirmOrder,
      Delete
    }
  }
}
</script>

<style lang="scss" scoped>
.workbench {
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
  padding: 0 20px;

  .category-tabs {
    display: flex;
    gap: 10px;
    padding: 10px 0;
    overflow-x: auto;
    margin-bottom: 20px;

    .category-tab {
      padding: 8px 16px;
      background-color: white;
      border-radius: 20px;
      cursor: pointer;
      transition: all 0.3s;
      white-space: nowrap;

      &.active {
        background-color: #ff9800;
        color: white;
      }

      &.hot {
        background-color: #fff0f0;
        color: #ff4d4f;
        font-weight: bold;

        &:hover {
          background-color: #ffeeee;
        }

        &.active {
          background-color: #ff4d4f;
          color: white;
        }
      }

      &:hover {
        background-color: #fff3e0;
      }
    }
  }

  .dish-grid-container {
    flex: 1;
    overflow: hidden;
    margin-bottom: 80px;
    /* 为购物车留出空间 */

    .dish-grid {
      height: 100%;
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;
      overflow-y: auto;
      padding: 20px 10px 70px 0;
      /* 增大底部缓冲空间 */
      /* 滑动缓存和回弹效果 */
      -webkit-overflow-scrolling: touch;
      scroll-behavior: smooth;

      &::-webkit-scrollbar {
        width: 6px;
      }

      &::-webkit-scrollbar-track {
        background: #f1f1f1;
        border-radius: 3px;
      }

      &::-webkit-scrollbar-thumb {
        background: #ffcc80;
        border-radius: 3px;
      }

      &::-webkit-scrollbar-thumb:hover {
        background: #ff9800;
      }

      .dish-card {
        background-color: white;
        border-radius: 8px;
        overflow: hidden;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        cursor: pointer;
        transition: all 0.3s;
        height: 220px;

        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
        }

        .dish-image {
          height: 150px;
          overflow: hidden;
          position: relative;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }

          .sales-tag {
            position: absolute;
            top: 8px;
            right: 8px;
            background-color: rgba(255, 77, 79, 0.9);
            color: white;
            font-size: 12px;
            padding: 4px 8px;
            border-radius: 12px;
            font-weight: bold;
          }
        }

        .dish-info {
          padding: 10px;

          .dish-name {
            font-size: 14px;
            font-weight: 500;
            margin: 0 0 5px 0;
            cursor: pointer;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }

          .dish-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .dish-price {
              font-size: 16px;
              font-weight: bold;
              color: #ff9800;
              margin: 0;
              cursor: pointer;
            }
          }
        }
      }
    }
  }

  .flavor-options {
    padding: 20px 0;

    .flavor-item {
      margin-bottom: 20px;

      .flavor-label {
        display: inline-block;
        width: 60px;
        font-weight: 500;
      }

      .flavor-buttons {
        display: inline-block;
        margin-left: 20px;

        .el-button {
          margin-right: 10px;
        }
      }

      .spicy-buttons {
        .el-button {
          margin-right: 8px;
        }
      }
    }
  }

  .cart-container {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    z-index: 100;

    .cart-bar {
      height: 60px;
      background-color: white;
      border-top: 1px solid #eaeaea;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 20px;
      box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);

      .cart-info {
        display: flex;
        gap: 20px;

        .cart-count {
          font-size: 14px;
        }

        .cart-total {
          font-size: 18px;
          font-weight: bold;
          color: #ff9800;
        }
      }

      .cart-actions {
        display: flex;
        gap: 10px;

        .checkout-btn {
          min-width: 100px;
        }
      }
    }

    .cart-details {
      background-color: white;
      border-top: 1px solid #eaeaea;
      padding: 20px 20px 70px 20px;
      /* 增大底部缓冲空间 */
      max-height: 25vh;
      overflow-y: auto;
      box-shadow: 0 -4px 15px rgba(0, 0, 0, 0.1);
      /* 滑动缓存和回弹效果 */
      -webkit-overflow-scrolling: touch;
      scroll-behavior: smooth;

      &::-webkit-scrollbar {
        width: 6px;
      }

      &::-webkit-scrollbar-track {
        background: #f1f1f1;
        border-radius: 3px;
      }

      &::-webkit-scrollbar-thumb {
        background: #ffcc80;
        border-radius: 3px;
      }

      &::-webkit-scrollbar-thumb:hover {
        background: #ff9800;
      }

      .empty-cart {
        text-align: center;
        padding: 20px;
        color: #999;
      }

      .cart-items {
        .cart-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 10px 0;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          .cart-item-info {
            display: flex;
            gap: 20px;
            flex: 1;
            min-width: 0;

            .cart-item-details {
              flex: 1;
              min-width: 0;
              display: flex;
              flex-wrap: wrap;
              align-items: center;
              gap: 8px;

              .cart-item-name {
                font-size: 14px;
                color: #333;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                flex-shrink: 0;
              }

              .cart-item-flavor {
                font-size: 12px;
                color: #999;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                flex-shrink: 1;
              }
            }

            .cart-item-price {
              font-size: 14px;
              font-weight: 500;
              color: #ff9800;
              white-space: nowrap;
            }
          }

          .cart-item-quantity {
            display: flex;
            align-items: center;
            gap: 10px;
            margin: 0 20px;

            .quantity {
              min-width: 30px;
              text-align: center;
            }
          }
        }
      }
    }
  }

  // 口味选择弹窗样式
  .flavor-options {
    .flavor-item {
      margin-bottom: 16px;

      .flavor-label {
        display: inline-block;
        width: 60px;
        font-size: 14px;
      }

      .flavor-buttons {
        display: inline-block;

        .el-button {
          margin-right: 8px;
        }
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .dish-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .dish-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .dish-grid {
    grid-template-columns: 1fr;
  }
}

/* 下单确认弹窗样式 */
.order-form {
  .form-item {
    margin-bottom: 20px;

    .form-label {
      display: inline-block;
      width: 80px;
      font-weight: bold;
      margin-right: 10px;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>