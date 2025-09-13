<template>
  <div id="userManagePage">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.userName" placeholder="输入用户名" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-divider />
    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="data"
      :pagination="pagination"
      :loading="loading"
      @change="doTableChange"
      row-key="id"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="120" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="green">管理员</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">普通用户</a-tag>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button danger @click="doDelete(record.id)">删除</a-button>
          <a-button type="primary" style="margin-left: 8px" @click="doBan(record.id)">封禁</a-button>
          <a-button style="margin-left: 8px" @click="doUnban(record.id)">解封</a-button>
        </template>
      </template>

    </a-table>

    <!-- 封禁用户模态框 -->
    <Modal
      v-model:visible="banModalVisible"
      title="封禁用户"
      centered
      :footer="null"
      width="400px"
    >
      <div style="padding: 16px 0;">
        <p style="margin-bottom: 16px;">请设置封禁时长</p>
        <div style="display: flex; justify-content: space-between; gap: 8px; margin-bottom: 16px;">
          <Button
            v-for="option in banTimeOptions"
            :key="option.value"
            :type="banTime === option.value ? 'primary' : 'default'"
            @click="selectBanTime(option.value)"
            style="flex: 1; min-width: 60px; white-space: nowrap;"
          >
            {{ option.label }}
          </Button>
        </div>
        <div style="display: flex; align-items: center; gap: 8px;">
          <Input
            v-model:value="banTime"
            type="number"
            placeholder="请输入封禁时间(秒)"
            :disabled="banTime === -1"
            style="flex: 1;"
          />
          <span v-if="banTime !== -1">秒</span>
          <span v-else style="color: #f5222d;">永久封禁</span>
        </div>
      </div>
      <Divider />
      <div style="display: flex; justify-content: flex-end; gap: 8px;">
        <Button @click="banModalVisible = false">取消</Button>
        <Button type="primary" @click="confirmBan">确定</Button>
      </div>
    </Modal>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUser, listUserVoByPage, banUser, unbanUser } from '@/api/userController.ts'
import { message, Modal, Button, Input, Divider } from 'ant-design-vue'
import dayjs from 'dayjs'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 展示的数据
const data = ref<API.UserVO[]>([])
const total = ref(0)
const loading = ref(false)

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 10,
  userRole: '', // 空字符串表示不按角色过滤，获取所有用户
})

// 获取数据
const fetchData = async () => {
    loading.value = true
    try {
      const res = await listUserVoByPage({
        ...searchParams,
      })

      if (res.data.code === 0) {
        // 检查API返回的数据结构
        
        // 直接检查是否为数组
        if (Array.isArray(res.data.data)) {
          // 如果直接返回了数组，则使用该数组
          data.value = res.data.data
          total.value = res.data.data.length
        } 
        // 检查是否符合PageUserVO结构
        else if (res.data.data && typeof res.data.data === 'object' && 'records' in res.data.data) {
          const pageData = res.data.data as API.PageUserVO;
          
          // 检查records字段
          if (Array.isArray(pageData.records)) {
            data.value = pageData.records;
            total.value = pageData.totalRow || pageData.records.length;
;
          } else {
            data.value = [];
            total.value = 0;
            message.error('数据格式不符合预期');
          }
        } else {
          data.value = []
          total.value = 0
          message.error('获取数据格式错误')
        }
      } else {
        message.error('获取数据失败，' + res.data.message)
        data.value = []
        total.value = 0
      }
    } catch (error) {
      message.error('请求异常: ' + ((error as Error).message || '未知错误'))
      data.value = []
      total.value = 0
    } finally {
      loading.value = false
    }
  }

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 表格分页变化时的操作
const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索数据
const doSearch = () => {
  // 重置页码
  searchParams.pageNum = 1
  fetchData()
}

// 删除数据
const doDelete = async (id: number) => {
    if (!id) {
      return
    }
    const res = await deleteUser({ id })
    if (res.data.code === 0) {
      message.success('删除成功')
      // 刷新数据
      fetchData()
    } else {
      message.error('删除失败，' + res.data.message)
    }
  }

// 封禁用户相关状态
const banModalVisible = ref(false);
const currentBanUserId = ref(0);
const banTime = ref(0);
const banTimeOptions = [
  { label: '一小时', value: 3600 },
  { label: '一天', value: 86400 },
  { label: '七天', value: 604800 },
  { label: '一个月', value: 2592000 },
  { label: '永久', value: -1 },
];

// 打开封禁弹窗
const openBanModal = (id: number) => {
  currentBanUserId.value = id;
  banTime.value = 0;
  banModalVisible.value = true;
};

// 选择封禁时间
const selectBanTime = (value: number) => {
  banTime.value = value;
};

// 确认封禁
const confirmBan = async () => {
  if (currentBanUserId.value === 0) {
    return;
  }
  
  if (banTime.value === 0 && banTime.value !== -1) {
    message.warning('请选择或输入封禁时间');
    return;
  }

  const res = await banUser({
    userId: currentBanUserId.value,
    time: banTime.value
  })
  
  if (res.data.code === 0) {
    message.success('封禁成功')
    banModalVisible.value = false;
    // 刷新数据
    fetchData()
  } else {
    message.error('封禁失败，' + res.data.message)
  }
};

// 封禁用户入口
const doBan = (id: number) => {
  openBanModal(id);
}

// 解封用户
const doUnban = async (id: number) => {
    if (!id) {
      return
    }
    const res = await unbanUser({ userId: id })
    if (res.data.code === 0) {
      message.success('解封成功')
      // 刷新数据
      fetchData()
    } else {
      message.error('解封失败，' + res.data.message)
    }
  }

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
#userManagePage {
  padding: 24px;
  background: white;
  margin-top: 16px;
}
</style>
