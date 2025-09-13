/* eslint-disable */
import request from '@/request'

// 用户模块接口

/**
 * 分页查询用户列表接口
 * @description 分页查询用户列表（管理员权限）
 * @method POST
 * @path /user/vos
 * @param {Object} body - 请求体
 * @param {number} [body.pageNum=1] - 当前页
 * @param {number} [body.pageSize=10] - 每页大小
 * @param {string} [body.userAccount] - 账号
 * @param {string} [body.userName] - 用户名
 * @returns {BaseResponsePageUserVO} 用户分页数据
 */
export async function listUserVoByPage(
  body: API.UserQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageUserVO>('/user/vos', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/**
 * 用户登录接口
 * @description 用户登录
 * @method POST
 * @path /user/login
 * @param {Object} body - 请求体
 * @param {string} body.userAccount - 账号
 * @param {string} body.password - 密码
 * @returns {BaseResponseLoginUserVO} 登录用户信息
 */
export async function userLogin(body: API.UserLoginRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLoginUserVO>('/user/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/**
 * 用户注册接口
 * @description 用户注册
 * @method POST
 * @path /user/register
 * @param {Object} body - 请求体
 * @param {string} body.userAccount - 账号
 * @param {string} body.password - 密码
 * @param {string} body.checkPassword - 确认密码
 * @returns {BaseResponseLong} 用户ID
 */
export async function userRegister(
  body: API.UserRegisterRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong>('/user/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/**
 * 用户登出接口
 * @description 用户登出
 * @method POST
 * @path /user/logout
 * @returns {BaseResponseBoolean} 操作结果
 */
export async function userLogout(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/user/logout', {
    method: 'POST',
    ...(options || {}),
  })
}

/**
 * 获取当前登录用户信息接口
 * @description 获取当前登录用户信息
 * @method POST
 * @path /user/getvo
 * @returns {BaseResponseLoginUserVO} 当前登录用户信息
 */
export async function getCurrentLoginUser(options?: { [key: string]: any }) {
  const requestOptions = Object.assign({
    method: 'POST'
  }, options || {});
  return request<API.BaseResponseLoginUserVO>('/user/getvo', requestOptions);
}


/**
 * 封禁用户接口
 * @description 封禁用户（管理员权限）
 * @method POST
 * @path /user/ban
 * @param {Object} body - 请求体
 * @param {number} body.userId - 用户ID
 * @param {number} body.time - 封禁时间(s)
 * @returns {BaseResponseBoolean} 操作结果
 */
export const banUser = async (body: API.UserBanRequest, options?: { [key: string]: any }) => {
  return request<API.BaseResponseBoolean>('/user/ban', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    data: body,
    ...(options || {})
  });
};

/**
 * 解封用户接口
 * @description 解封用户（管理员权限）
 * @method POST
 * @path /user/unban
 * @param {Object} body - 请求体
 * @param {number} body.userId - 用户ID
 * @returns {BaseResponseBoolean} 操作结果
 */
export const unbanUser = async (body: API.UserUnbanRequest, options?: { [key: string]: any }) => {
  return request<API.BaseResponseBoolean>('/user/unban', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    data: body,
    ...(options || {})
  });
};



/**
 * 获取用户信息接口
 * @description 获取指定用户信息
 * @method GET
 * @path /user/get
 * @param {Object} params - 查询参数
 * @param {number} params.id - 用户ID
 * @returns {BaseResponseUser} 用户详细信息
 */
export async function getUserById(
  params: API.getUserByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUser>('/user/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}




/**
 * 更新用户信息接口
 * @description 更新用户信息
 * @method POST
 * @path /user/updateInfo
 * @param {Object} body - 请求体
 * @param {string} body.password - 验证密码
 * @param {string} body.userName - 用户昵称
 * @param {string} body.userAvatar - 用户头像
 * @param {string} body.userProfile - 用户简介
 * @returns {BaseResponseBoolean} 操作结果
 */
export async function updateUserInfo(body: API.UserInfoUpdateRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/user/updateInfo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/**
 * 更新用户密码接口
 * @description 更新用户密码
 * @method POST
 * @path /user/updatePassword
 * @param {Object} body - 请求体
 * @param {string} body.password - 原密码
 * @param {string} body.newPassword - 新密码
 * @returns {BaseResponseBoolean} 操作结果
 */
export async function updateUserPassword(body: API.UserPasswordUpdateRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/user/updatePassword', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/**
 * 添加用户接口
 * @description 添加用户（管理员权限）
 * @method POST
 * @path /user/add
 * @param {Object} body - 请求体
 * @param {string} body.userName - 用户昵称
 * @param {string} body.userAccount - 账号
 * @param {string} body.userAvatar - 用户头像
 * @param {string} body.userProfile - 用户简介
 * @param {string} body.userRole - 用户角色
 * @returns {BaseResponseLong} 新创建的用户ID
 */
export async function addUser(body: API.UserAddRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong>('/user/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/**
 * 删除用户接口
 * @description 删除用户（管理员权限）
 * @method POST
 * @path /user/delete
 * @param {Object} body - 请求体
 * @param {number} body.id - 用户ID
 * @returns {BaseResponseBoolean} 操作结果
 */
export async function deleteUser(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/user/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

// 对话历史相关接口

/**
 * 创建对话历史接口
 * @description 创建新的对话历史记录
 * @method POST
 * @path /chat-history/create
 * @param {Object} body - 请求体
 * @param {string} body.message - 消息内容
 * @param {string} body.messageType - 消息类型: user/ai
 * @param {number} body.appId - 应用ID
 * @returns {BaseResponseLong} 对话历史ID
 */
export async function createChatHistory(body: API.ChatHistoryCreateRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong>('/chat-history/create', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/**
 * 获取最新对话历史接口
 * @description 获取指定应用的最新对话历史
 * @method GET
 * @path /chat-history/app/{appId}
 * @param {Object} params - 查询参数
 * @param {number} params.appId - 应用ID
 * @param {number} [params.pageSize=10] - 每页大小
 * @param {string} [params.lastCreateTime] - 最后创建时间
 * @returns {BaseResponsePageChatHistory} 对话历史分页数据
 */
export async function getLatestChatHistory(
  params: {
    appId: number
    pageSize?: number
    lastCreateTime?: string
  },
  options?: { [key: string]: any }
) {
  const { appId, ...queryParams } = params;
  return request<API.BaseResponsePageChatHistory>(`/chat-history/app/${appId}`, {
    method: 'GET',
    params: queryParams,
    ...(options || {}),
  })
}

/**
 * 管理员分页查询对话历史接口
 * @description 管理员分页查询所有对话历史
 * @method POST
 * @path /chat-history/admin/page
 * @param {Object} body - 请求体
 * @param {number} [body.cur=1] - 当前页
 * @param {number} [body.size=10] - 每页大小
 * @param {number} [body.appId] - 应用ID
 * @param {string} [body.messageType] - 消息类型
 * @param {number} [body.userId] - 用户ID
 * @param {string} [body.message] - 消息内容模糊查询
 * @param {string} [body.lastCreateTime] - 创建时间
 * @param {string} [body.sortField=createTime] - 排序字段
 * @param {string} [body.sortOrder=desc] - 排序方式
 * @returns {BaseResponsePageChatHistory} 对话历史分页数据
 */
export async function adminPageChatHistory(body: API.ChatHistoryAdminQueryRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponsePageChatHistory>('/chat-history/admin/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/**
 * 管理员删除对话历史接口
 * @description 管理员删除指定的对话历史记录
 * @method POST
 * @path /chat-history/admin/delete
 * @param {Object} params - 查询参数
 * @param {number} params.chatHistoryId - 对话历史ID
 * @returns {BaseResponseBoolean} 操作结果
 */
export async function adminDeleteChatHistory(
  params: {
    chatHistoryId: number
  },
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/chat-history/admin/delete', {
    method: 'POST',
    params: params,
    ...(options || {}),
  })
}

// 健康检查接口

/**
 * 获取用户积分接口
 * @description 获取当前登录用户的积分信息
 * @method GET
 * @path /points/get
 * @returns {BaseResponseUserPointsVO} 用户积分信息
 */
export async function getUserPoints(options?: { [key: string]: any }) {
  return request<API.BaseResponseUserPointsVO>('/points/get', {
    method: 'GET',
    ...(options || {}),
  })
}

/**
 * 每日登录接口
 * @description 用户每日登录获取积分
 * @method POST
 * @path /login/everyday
 * @returns {BaseResponseBoolean} 登录结果
 */
export async function dailyLoginReward(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/login/everyday', {
    method: 'POST',
    ...(options || {}),
  })
}

/**
 * 检查今天是否已经登录接口
 * @description 检查用户今天是否已经登录
 * @method GET
 * @path /login/check
 * @returns {BaseResponseBoolean} 是否已登录
 */
export async function checkUserLoginToday(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/login/check', {
    method: 'GET',
    ...(options || {}),
  })
}

/**
 * 获取登录统计信息接口
 * @description 获取用户连续登录天数等统计信息
 * @method POST
 * @path /login/get/statistics
 * @returns {BaseResponseLoginStatisticsVO} 登录统计信息
 */
export async function getUserLoginStatistics(options?: { [key: string]: any }) {
  return request<API.BaseResponseLoginStatisticsVO>('/login/get/statistics', {
    method: 'POST',
    ...(options || {}),
  })
}

/**
 * 获取积分记录接口
 * @description 获取用户积分历史记录（分页）
 * @method GET
 * @path /points/records
 * @param {Object} params - 查询参数
 * @param {number} [params.current=1] - 当前页
 * @param {number} [params.pageSize=10] - 每页大小
 * @returns {BaseResponsePagePointsRecord} 积分记录分页数据
 */
export async function getPointsRecords(
  params: {
    current?: number
    pageSize?: number
  } = {},
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePagePointsRecord>('/points/records', {
    method: 'GET',
    params: {
      current: 1,
      pageSize: 10,
      ...params,
    },
    ...(options || {}),
  })
}

/**
 * 创建邀请码接口
 * @description 创建用户邀请码
 * @method POST
 * @path /inviteCode/create
 * @returns {BaseResponseString} 邀请码
 */
export async function createInviteCode(options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/inviteCode/create', {
    method: 'POST',
    ...(options || {}),
  })
}

/**
 * 获取邀请码接口
 * @description 获取用户邀请码
 * @method GET
 * @path /inviteCode/get
 * @returns {BaseResponseString} 邀请码
 */
export async function getInviteCode(options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/inviteCode/get', {
    method: 'GET',
    ...(options || {}),
  })
}

/**
 * 健康检查接口
 * @description 检查服务是否健康
 * @method GET
 * @path /health
 * @returns {BaseResponseString} 健康状态
 */
export async function healthCheck(options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/health', {
    method: 'GET',
    ...(options || {}),
  })
}
