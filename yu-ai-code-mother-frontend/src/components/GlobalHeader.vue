<template>
  <a-layout-header class="header">
    <a-row :wrap="false">
      <!-- 左侧：Logo和标题 -->
      <a-col flex="200px">
        <RouterLink to="/">
          <div class="header-left">
            <img class="logo" src="/favicon.jpg" alt="Logo" />
            <h1 class="site-title">AICreator</h1>
          </div>
        </RouterLink>
      </a-col>
      <!-- 中间：导航菜单 -->
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :items="menuItems"
          @click="handleMenuClick"
        />
      </a-col>
      <!-- 右侧：用户操作区域 -->
      <a-col>
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <a-space>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                {{ loginUserStore.loginUser.userName ?? '无名' }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item disabled class="points-item">
                    <span class="points-text">当前积分：{{ userPoints || 0 }}</span>
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item @click="goToDashboard">
                    <DashboardOutlined />
                    积分仪表盘
                  </a-menu-item>
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, h, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { userLogout, getUserPoints } from '@/api/userController'
import { LogoutOutlined, HomeOutlined, DashboardOutlined } from '@ant-design/icons-vue'

const loginUserStore = useLoginUserStore()
const router = useRouter()
// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
// 用户积分
const userPoints = ref<number>(0)
// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, next) => {
  selectedKeys.value = [to.path]
})

// 菜单配置项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/admin/appManage',
    label: '应用管理',
    title: '应用管理',
  }
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))

// 处理菜单点击
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  // 跳转到对应页面
  if (key.startsWith('/')) {
    router.push(key)
  }
}

// 获取用户积分
const fetchUserPoints = async () => {
  // 检查用户是否已登录（有ID且用户名不是"未登录"）
  if (!loginUserStore.loginUser.id || loginUserStore.loginUser.userName === '未登录') {
    return
  }
  
  try {
    const res = await getUserPoints()
    
    if (res.data.code === 0 && res.data.data) {
      userPoints.value = res.data.data.points || 0
    }
  } catch (error) {
    // 静默处理错误
  }
}

// 跳转到仪表盘
const goToDashboard = () => {
  router.push('/user/dashboard')
}

// 退出登录
const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    userPoints.value = 0 // 重置积分
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}

// 组件挂载时获取积分
onMounted(async () => {
  // 先获取登录用户信息
  await loginUserStore.fetchLoginUser()
  
  // 然后获取积分
  fetchUserPoints()
})
</script>

<style scoped>
.header {
  background: #fff;
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  height: 48px;
  width: 48px;
}

.site-title {
  margin: 0;
  font-size: 18px;
  color: #1890ff;
}

.ant-menu-horizontal {
  border-bottom: none !important;
}

/* 积分显示样式 */
.points-item {
  cursor: default !important;
  background: #f0f2f5 !important;
}

.points-item:hover {
  background: #f0f2f5 !important;
}

.points-text {
  color: #1890ff;
  font-weight: 500;
  font-size: 14px;
}
</style>
