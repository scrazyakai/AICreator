<template>
  <div class="dashboard-container">
    <a-row :gutter="24">
      <!-- 左侧：积分概览和签到 -->
      <a-col :span="8">
        <a-card title="积分概览" class="dashboard-card">
          <div class="points-overview">
            <div class="current-points">
              <a-statistic
                title="当前积分"
                :value="userPoints"
                :loading="pointsLoading"
                prefix="💎"
              />
            </div>
            <div class="login-stats">
              <a-statistic
                title="连续登录天数"
                :value="consecutiveDays"
                :loading="loginStatsLoading"
                prefix="🔥"
              />
            </div>
            <a-button
              type="primary"
              size="large"
              :loading="checkinLoading"
              :disabled="checkinDisabled"
              @click="handleDailyCheckin"
              class="checkin-btn"
            >
              {{ checkinDisabled ? '今日已签到' : '每日签到' }}
            </a-button>
          </div>
        </a-card>

        <a-card title="邀请码管理" class="dashboard-card" style="margin-top: 16px;">
          <div class="invite-section">
            <div class="invite-code-display">
              <a-input
                v-model:value="inviteCode"
                :placeholder="hasInviteCode ? '邀请码' : '请先生成邀请码'"
                readonly
                class="invite-input"
              >
                <template #addonAfter>
                  <a-button
                    v-if="!hasInviteCode"
                    type="primary"
                    :loading="inviteLoading"
                    @click="handleCreateInviteCode"
                  >
                    生成
                  </a-button>
                  <a-button
                    v-else
                    type="default"
                    disabled
                  >
                    已生成
                  </a-button>
                </template>
              </a-input>
            </div>
            <a-button
              v-if="hasInviteCode && inviteCode"
              type="link"
              @click="copyInviteCode"
              class="copy-btn"
            >
              复制邀请码
            </a-button>
          </div>
        </a-card>
      </a-col>

      <!-- 右侧：积分记录 -->
      <a-col :span="16">
        <a-card title="积分记录" class="dashboard-card">
          <div class="records-section">
            <a-spin :spinning="recordsLoading">
              <a-list
                :data-source="pointsRecords"
                :pagination="paginationConfig"
                @change="handlePageChange"
              >
                <template #renderItem="{ item }">
                  <a-list-item>
                    <a-list-item-meta>
                      <template #title>
                        <div class="record-title">
                          <span class="record-type">{{ getRecordTypeText(item.description) }}</span>
                          <span
                            class="record-points"
                            :class="{ 'positive': item.points > 0, 'negative': item.points < 0 }"
                          >
                            {{ item.points > 0 ? '+' : '' }}{{ item.points }}
                          </span>
                        </div>
                      </template>
                      <template #description>
                        <div class="record-description">
                          <span>{{ item.description || '无描述' }}</span>
                          <span class="record-time">{{ formatTime(item.createTime) }}</span>
                        </div>
                      </template>
                    </a-list-item-meta>
                  </a-list-item>
                </template>
              </a-list>
            </a-spin>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { 
  getUserPoints, 
  dailyLoginReward, 
  getPointsRecords, 
  createInviteCode,
  getInviteCode,
  checkUserLoginToday,
  getUserLoginStatistics
} from '@/api/userController'
import { formatTime } from '@/utils/time'

// 响应式数据
const userPoints = ref<number>(0)
const pointsLoading = ref(false)
const checkinLoading = ref(false)
const checkinDisabled = ref(false)
const inviteCode = ref<string>('')
const inviteLoading = ref(false)
const hasInviteCode = ref<boolean>(false)
const pointsRecords = ref<API.PointsRecord[]>([])
const recordsLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalRecords = ref(0)
const consecutiveDays = ref<number>(0)
const loginStatsLoading = ref(false)

// 分页配置
const paginationConfig = computed(() => ({
  current: currentPage.value,
  pageSize: pageSize.value,
  total: totalRecords.value,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`,
}))

// 获取用户积分
const fetchUserPoints = async () => {
  pointsLoading.value = true
  try {
    const res = await getUserPoints()
    if (res.data.code === 0 && res.data.data) {
      userPoints.value = res.data.data.points || 0
    }
  } catch (error) {
    message.error('获取积分失败')
  } finally {
    pointsLoading.value = false
  }
}

// 获取登录统计信息
const fetchLoginStatistics = async () => {
  loginStatsLoading.value = true
  try {
    const res = await getUserLoginStatistics()
    if (res.data.code === 0 && res.data.data) {
      consecutiveDays.value = res.data.data.curLongestLoginDays || 0
    }
  } catch (error) {
    message.error('获取登录统计失败')
  } finally {
    loginStatsLoading.value = false
  }
}

// 检查今日是否已登录
const checkTodayLoginStatus = async () => {
  try {
    const res = await checkUserLoginToday()
    if (res.data.code === 0) {
      checkinDisabled.value = res.data.data || false
    }
  } catch (error) {
    // 静默处理错误
  }
}

// 每日签到
const handleDailyCheckin = async () => {
  checkinLoading.value = true
  try {
    const res = await dailyLoginReward()
    if (res.data.code === 0) {
      if (res.data.data) {
        message.success('签到成功！获得积分奖励')
        checkinDisabled.value = true
        // 刷新积分和记录
        await Promise.all([fetchUserPoints(), fetchPointsRecords()])
      } else {
        message.warning('今日已签到，明天再来吧！')
        checkinDisabled.value = true
      }
    } else {
      message.error('签到失败：' + res.data.message)
    }
  } catch (error) {
    message.error('签到失败')
  } finally {
    checkinLoading.value = false
  }
}

// 获取邀请码
const fetchInviteCode = async () => {
  try {
    const res = await getInviteCode()
    if (res.data.code === 0) {
      if (res.data.data && res.data.data !== 'null') {
        inviteCode.value = res.data.data
        hasInviteCode.value = true
      } else {
        inviteCode.value = ''
        hasInviteCode.value = false
      }
    }
  } catch (error) {
    hasInviteCode.value = false
  }
}

// 创建邀请码
const handleCreateInviteCode = async () => {
  inviteLoading.value = true
  try {
    const res = await createInviteCode()
    if (res.data.code === 0 && res.data.data) {
      inviteCode.value = res.data.data
      hasInviteCode.value = true
      message.success('邀请码生成成功')
    } else {
      message.error('生成邀请码失败：' + res.data.message)
    }
  } catch (error) {
    message.error('生成邀请码失败')
  } finally {
    inviteLoading.value = false
  }
}

// 复制邀请码
const copyInviteCode = async () => {
  try {
    await navigator.clipboard.writeText(inviteCode.value)
    message.success('邀请码已复制到剪贴板')
  } catch (error) {
    message.error('复制失败')
  }
}

// 获取积分记录
const fetchPointsRecords = async (page = 1) => {
  recordsLoading.value = true
  try {
    const res = await getPointsRecords({
      current: page,
      pageSize: pageSize.value
    })
    if (res.data.code === 0 && res.data.data) {
      pointsRecords.value = res.data.data.records || []
      totalRecords.value = res.data.data.totalRow || 0
      currentPage.value = page
    }
  } catch (error) {
    message.error('获取积分记录失败')
  } finally {
    recordsLoading.value = false
  }
}

// 分页变化
const handlePageChange = (page: number, size: number) => {
  currentPage.value = page
  pageSize.value = size
  fetchPointsRecords(page)
}

// 获取记录类型文本（基于description推断）
const getRecordTypeText = (description: string) => {
  if (!description) {
    return '未知'
  }
  
  // 基于描述内容推断类型
  if (description.includes('每日登录') || description.includes('签到')) {
    return '每日签到'
  } else if (description.includes('邀请') || description.includes('好友')) {
    return '邀请奖励'
  } else if (description.includes('消费') || description.includes('使用')) {
    return '消费'
  } else if (description.includes('管理员') && description.includes('添加')) {
    return '管理员添加'
  } else if (description.includes('管理员') && description.includes('扣除')) {
    return '管理员扣除'
  } else {
    // 如果无法推断，返回描述的前10个字符
    return description.length > 10 ? description.substring(0, 10) + '...' : description
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchUserPoints()
  fetchPointsRecords()
  fetchLoginStatistics()
  checkTodayLoginStatus()
  fetchInviteCode()
})
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;
}

.dashboard-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.points-overview {
  text-align: center;
}

.current-points {
  margin-bottom: 16px;
}

.login-stats {
  margin-bottom: 24px;
}

.checkin-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: 6px;
}

.invite-section {
  text-align: center;
}

.invite-input {
  margin-bottom: 12px;
}

.copy-btn {
  padding: 0;
}

.records-section {
  max-height: 600px;
  overflow-y: auto;
}

.record-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.record-type {
  font-weight: 500;
  color: #333;
}

.record-points {
  font-weight: 600;
  font-size: 16px;
}

.record-points.positive {
  color: #52c41a;
}

.record-points.negative {
  color: #ff4d4f;
}

.record-description {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #666;
  font-size: 14px;
}

.record-time {
  color: #999;
}

:deep(.ant-list-item) {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.ant-list-item:last-child) {
  border-bottom: none;
}

:deep(.ant-statistic-title) {
  color: #666;
  font-size: 14px;
}

:deep(.ant-statistic-content) {
  color: #1890ff;
  font-size: 32px;
  font-weight: 600;
}
</style>
