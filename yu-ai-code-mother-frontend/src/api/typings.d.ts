declare namespace API {
  type AppAddRequest = {
    appName?: string
    initPrompt?: string
  }

  type AppAdminUpdateRequest = {
    id?: number
    appName?: string
    cover?: string
    priority?: number
  }

  type AppDeployRequest = {
    appId?: number
  }

  type AppQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    priority?: number
    userId?: number
  }

  type AppUpdateRequest = {
    id?: number
    appName?: string
  }

  type AppVO = {
    id?: number
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    deployedTime?: string
    priority?: number
    userId?: number
    userName?: string  // 添加 userName 字段
    createTime?: string
    updateTime?: string
    user?: UserVO
  }

  type BaseResponseAppVO = {
    code?: number
    data?: AppVO
    message?: string
  }

  type BaseResponseBoolean = {
    code?: number
    data?: boolean
    message?: string
  }

  type BaseResponseLoginUserVO = {
    code?: number
    data?: LoginUserVO
    message?: string
  }

  type BaseResponseLong = {
    code?: number
    data?: number
    message?: string
  }

  type BaseResponsePageAppVO = {
    code?: number
    data?: PageAppVO
    message?: string
  }

  type BaseResponsePageChatHistory = {
    code?: number
    data?: PageChatHistory
    message?: string
  }

  type BaseResponsePageUserVO = {
    code?: number
    data?: PageUserVO
    message?: string
  }

  type BaseResponseString = {
    code?: number
    data?: string
    message?: string
  }

  type BaseResponseUser = {
    code?: number
    data?: User
    message?: string
  }

  type BaseResponseUserVO = {
    code?: number
    data?: UserVO
    message?: string
  }

  type ChatHistory = {
    id?: number
    message?: string
    messageType?: string
    appId?: number
    userId?: number
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type ChatHistoryQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    message?: string
    messageType?: string
    appId?: number
    userId?: number
    lastCreateTime?: string
  }

  type chatToGenCodeParams = {
    appId: number
    message: string
  }

  type DeleteRequest = {
    id?: number
  }

  type downloadAppCodeParams = {
    appId: number
  }

  type getAppVOByIdByAdminParams = {
    id: number
  }

  type getAppVOByIdParams = {
    id: number
  }

  type getUserByIdParams = {
    id: number
  }

  type getUserVOByIdParams = {
    id: number
  }

  type listAppChatHistoryParams = {
    appId: number
    pageSize?: number
    lastCreateTime?: string
  }

  type LoginUserVO = {
    id?: number
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
    updateTime?: string
  }

  type PageAppVO = {
    records?: AppVO[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PageChatHistory = {
    records?: ChatHistory[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PageUserVO = {
    records?: UserVO[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type ServerSentEventString = true

  type serveStaticResourceParams = {
    deployKey: string
  }

  type User = {
    id?: number
    userAccount?: string
    userPassword?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    editTime?: string
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type UserBanRequest = {
    userId?: number
    time?: number
  }

  type UserUnbanRequest = {
    userId?: number
  }

  type UserInfoUpdateRequest = {
    password?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
  }

  type UserPasswordUpdateRequest = {
    password?: string
    newPassword?: string
  }

  type ChatHistoryCreateRequest = {
    message?: string
    messageType?: string
    appId?: number
  }

  type ChatHistoryAdminQueryRequest = {
    cur?: number
    size?: number
    appId?: number
    messageType?: string
    userId?: number
    message?: string
    lastCreateTime?: string
    sortField?: string
    sortOrder?: string
  }

  type UserAddRequest = {
    userName?: string
    userAccount?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
  }

  type UserLoginRequest = {
    userAccount?: string
    password?: string
  }

  type UserQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    userName?: string
    userAccount?: string
    userProfile?: string
    userRole?: string
  }

  type UserRegisterRequest = {
    userAccount?: string
    password?: string
    checkPassword?: string
    inviteCode?: string
  }

  type UserUpdateRequest = {
    id?: number
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
  }

  type UserVO = {
    id?: number
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
  }

  type UserPointsVO = {
    id?: number
    userId?: number
    points?: number
    createTime?: string
    updateTime?: string
  }

  type BaseResponseUserPointsVO = {
    code?: number
    data?: UserPointsVO
    message?: string
  }

  type PointsRecord = {
    id?: number
    userId?: number
    points?: number
    description?: string
    createTime?: string
  }

  type PagePointsRecord = {
    records?: PointsRecord[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type BaseResponsePagePointsRecord = {
    code?: number
    data?: PagePointsRecord
    message?: string
  }

  type BaseResponseBoolean = {
    code?: number
    data?: boolean
    message?: string
  }

  type BaseResponseString = {
    code?: number
    data?: string
    message?: string
  }

  type LoginStatisticsVO = {
    curLongestLoginDays?: number
  }

  type BaseResponseLoginStatisticsVO = {
    code?: number
    data?: LoginStatisticsVO
    message?: string
  }
}
