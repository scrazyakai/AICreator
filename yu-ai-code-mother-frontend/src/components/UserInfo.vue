<template>
  <div class="user-info">
    <a-avatar :src="user?.userAvatar" :size="size">
      {{ getUserInitial() }}
    </a-avatar>
    <span v-if="showName" class="user-name">{{ getUserName() }}</span>
  </div>
</template>

<script setup lang="ts">
interface Props {
  user?: {
    userName?: string
    userAvatar?: string
  }
  userName?: string  // 添加 userName 属性
  size?: number | 'small' | 'default' | 'large'
  showName?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  size: 'default',
  showName: true,
})

// 获取用户名称
const getUserName = () => {
  const userName = props.user?.userName || props.userName || '未知用户'
  
  // 调试信息
  console.log('=== UserInfo 调试信息 ===')
  console.log('props.user:', props.user)
  console.log('props.userName:', props.userName)
  console.log('最终用户名:', userName)
  
  return userName
}

// 获取用户首字母
const getUserInitial = () => {
  const name = getUserName()
  return name.charAt(0) || 'U'
}
</script>

<style scoped>
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 14px;
  color: #1a1a1a;
}
</style>
