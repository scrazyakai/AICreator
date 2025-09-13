# AICreator 接口文档

## 项目介绍
AI-Creator 是一个 AI 代码生成平台，提供了用户管理和代码生成相关的 API 接口。

## 接口列表

### 1. 健康检查接口
### 2. 用户注册接口
### 3. 用户登录接口
### 4. 用户登出接口
### 5. 获取用户信息接口
### 6. 禁言用户接口
### 7. 解封用户接口
### 8. 更新用户信息接口
### 9. 更新用户密码接口

## 对话历史相关接口
### 10. 创建对话历史接口
### 11. 获取最新对话历史接口
### 12. 管理员分页查询对话历史接口
### 13. 管理员删除对话历史接口

## 接口详细信息

### 1. 健康检查接口
- **路径**: `/health`
- **方法**: `GET`
- **描述**: 检查服务是否健康
- **参数**: 无
- **返回值**: 
  ```json
  {
    "code": 0,
    "data": "ok",
    "message": "",
    "description": ""
  }
  ```

### 2. 用户注册接口
- **路径**: `/user/register`
- **方法**: `POST`
- **描述**: 用户注册
- **参数**: 
  ```json
  {
    "userAccount": "string", // 账号
    "password": "string", // 密码
    "checkPassword": "string" // 确认密码
  }
  ```
- **返回值**: 
  ```json
  {
    "code": 0,
    "data": 1, // 用户ID
    "message": "",
    "description": ""
  }
  ```

### 3. 用户登录接口
- **路径**: `/user/login`
- **方法**: `POST`
- **描述**: 用户登录
- **参数**: 
  ```json
  {
    "userAccount": "string", // 账号
    "password": "string" // 密码
  }
  ```
- **返回值**: 
  ```json
  {
    "code": 0,
    "data": {
      "userAccount": "string",
      "userName": "string",
      "userAvatar": "string",
      "userProfile": "string",
      "userRole": "string",
      "createTime": "2025-08-25T12:00:00",
      "updateTime": "2025-08-25T12:00:00"
    },
    "message": "",
    "description": ""
  }
  ```

### 4. 用户登出接口
- **路径**: `/user/logout`
- **方法**: `POST`
- **描述**: 用户登出
- **参数**: 无
- **返回值**: 
  ```json
  {
    "code": 0,
    "data": true,
    "message": "",
    "description": ""
  }
  ```

### 5. 获取用户信息接口
- **路径**: `/user/getvo`
- **方法**: `POST`
- **描述**: 获取当前登录用户信息
- **参数**: 无
- **返回值**: 
  ```json
  {
    "code": 0,
    "data": {
      "userAccount": "string",
      "userName": "string",
      "userAvatar": "string",
      "userProfile": "string",
      "userRole": "string",
      "createTime": "2025-08-25T12:00:00",
      "updateTime": "2025-08-25T12:00:00"
    },
    "message": "",
    "description": ""
  }
  ```

### 6. 禁言用户接口
- **路径**: `/user/ban`
- **方法**: `POST`
- **描述**: 禁言用户（管理员权限）
- **参数**: 
  ```json
  {
    "userId": 1, // 用户ID
    "time": 3600 // 禁言时间(秒)
  }
  ```
- **返回值**: 
  ```json
  {
    "code": 0,
    "data": true,
    "message": "",
    "description": ""
  }
  ```

### 7. 解封用户接口
- **路径**: `/user/unban`
- **方法**: `POST`
- **描述**: 解封用户（管理员权限）
- **参数**: 
  ```json
  {
    "userId": 1 // 用户ID
  }
  ```
- **返回值**: 
  ```json
  {
    "code": 0,
    "data": true,
    "message": "",
    "description": ""
  }
  ```

### 8. 更新用户信息接口
- **路径**: `/user/updateInfo`
- **方法**: `POST`
- **描述**: 更新用户信息
- **参数**: 
  ```json
  {
    "password": "string", // 验证密码
    "userName": "string", // 用户昵称
    "userAvatar": "string", // 用户头像
    "userProfile": "string" // 用户简介
  }
  ```
- **返回值**: 
  ```json
  {
    "code": 0,
    "data": true,
    "message": "",
    "description": ""
  }
  ```

### 9. 更新用户密码接口
- **路径**: `/user/updatePassword`
- **方法**: `POST`
- **描述**: 更新用户密码
- **参数**: 
  ```json
  {
    "password": "string", // 原密码
    "newPassword": "string" // 新密码
  }
  ```
- **返回值**: 
  ```json
  {
    "code": 0,
    "data": true,
    "message": "",
    "description": ""
  }
  ```

## 通用响应格式
所有接口返回的通用格式如下：
```json
{
  "code": 0, // 状态码，0表示成功，其他表示失败
  "data": {}, // 数据
  "message": "", // 消息
  "description": "" // 描述
}
```

## 对话历史相关接口详细信息

### 10. 创建对话历史接口
- **路径**: `/chat-history/create`
- **方法**: `POST`
- **描述**: 创建新的对话历史记录
- **权限**: 需要登录
- **参数**: 
  ```json
  {
    "message": "string", // 消息内容
    "messageType": "string", // 消息类型: user/ai
    "appId": 1 // 应用ID
  }
  ```
- **返回值**: 
  ```json
  {
    "code": 0,
    "data": 123456, // 对话历史ID
    "message": "",
    "description": ""
  }
  ```

### 11. 获取最新对话历史接口
- **路径**: `/chat-history/app/{appId}`
- **方法**: `GET`
- **描述**: 获取指定应用的最新对话历史
- **权限**: 需要登录
- **参数**: 
  - `appId`: 应用ID（路径参数）
  - `pageSize`: 每页大小（查询参数，默认10）
  - `lastCreateTime`: 最后创建时间（查询参数，可选）
- **返回值**: 
  ```json
  {
    "code": 0,
    "data": {
      "records": [
        {
          "id": 123456,
          "message": "你好",
          "messageType": "user",
          "appId": 1,
          "userId": 789,
          "createTime": "2025-08-25T12:00:00",
          "updateTime": "2025-08-25T12:00:00",
          "isDelete": 0
        }
      ],
      "total": 1,
      "size": 10,
      "current": 1,
      "pages": 1
    },
    "message": "",
    "description": ""
  }
  ```

### 12. 管理员分页查询对话历史接口
- **路径**: `/chat-history/admin/page`
- **方法**: `POST`
- **描述**: 管理员分页查询所有对话历史
- **权限**: 需要管理员权限
- **参数**: 
  ```json
  {
    "cur": 1, // 当前页
    "size": 10, // 每页大小
    "appId": 1, // 应用ID（可选）
    "messageType": "user", // 消息类型（可选）
    "userId": 789, // 用户ID（可选）
    "message": "关键词", // 消息内容模糊查询（可选）
    "lastCreateTime": "2025-08-25T12:00:00", // 创建时间（可选）
    "sortField": "createTime", // 排序字段（可选）
    "sortOrder": "desc" // 排序方式（可选）
  }
  ```
- **返回值**: 
  ```json
  {
    "code": 0,
    "data": {
      "records": [
        {
          "id": 123456,
          "message": "你好",
          "messageType": "user",
          "appId": 1,
          "userId": 789,
          "createTime": "2025-08-25T12:00:00",
          "updateTime": "2025-08-25T12:00:00",
          "isDelete": 0
        }
      ],
      "total": 1,
      "size": 10,
      "current": 1,
      "pages": 1
    },
    "message": "",
    "description": ""
  }
  ```

### 13. 管理员删除对话历史接口
- **路径**: `/chat-history/admin/delete`
- **方法**: `POST`
- **描述**: 管理员删除指定的对话历史记录
- **权限**: 需要管理员权限
- **参数**: 
  - `chatHistoryId`: 对话历史ID（查询参数）
- **返回值**: 
  ```json
  {
    "code": 0,
    "data": true,
    "message": "",
    "description": ""
  }
  ```

## 数据模型说明

### ChatHistory对话历史实体
```json
{
  "id": "number", // 主键ID
  "message": "string", // 消息内容
  "messageType": "string", // 消息类型枚举：user(用户消息)/ai(AI消息)
  "appId": "number", // 关联应用ID
  "userId": "number", // 创建用户ID
  "createTime": "LocalDateTime", // 创建时间
  "updateTime": "LocalDateTime", // 更新时间
  "isDelete": "number" // 逻辑删除标识：0-未删除，1-已删除
}
```

### MessageType消息类型枚举
- `user`: 用户消息
- `ai`: AI消息

### User用户实体
```json
{
  "id": "number", // 主键ID
  "userAccount": "string", // 登录账号
  "userName": "string", // 用户昵称
  "userAvatar": "string", // 用户头像
  "userProfile": "string", // 用户简介
  "userRole": "string", // 用户角色：user/admin/ban
  "createTime": "LocalDateTime", // 创建时间
  "updateTime": "LocalDateTime", // 更新时间
  "isDelete": "number" // 逻辑删除标识：0-未删除，1-已删除
}
```

### UserRole用户角色枚举
- `user`: 普通用户
- `admin`: 管理员
- `ban`: 被禁言用户

## 错误码说明
- `PARAMS_ERROR` (40000): 参数错误
- `SYSTEM_ERROR` (50000): 系统错误
- `NO_AUTH` (40100): 无权限