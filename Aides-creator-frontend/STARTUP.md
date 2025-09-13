# 启动说明

## 配置完成

我已经完成了跨域问题的配置，现在可以正常使用可视化编辑器了。

### 主要修改：

1. **Vite代理配置** (`vite.config.ts`)：
   - 添加了 `/static` 路径的代理
   - 将静态资源请求代理到后端服务器

2. **环境配置** (`src/config/env.ts`)：
   - 开发环境使用相对路径 `/static`
   - 生产环境使用完整URL

### 启动步骤：

1. **启动后端服务器**：
   ```bash
   cd AI-Creator
   mvn spring-boot:run
   ```
   后端将在 `http://localhost:8080` 启动

2. **启动前端服务器**：
   ```bash
   cd yu-ai-code-mother-frontend
   npm run dev
   ```
   前端将在 `http://localhost:5173` 启动

3. **测试可视化编辑器**：
   - 打开 `http://localhost:5173`
   - 进入应用聊天页面
   - 点击"编辑模式"按钮
   - 现在应该可以正常选中iframe内的元素了

### 工作原理：

- 前端通过Vite代理访问静态资源
- iframe的src现在指向 `http://localhost:5173/static/html_xxx/index.html`
- 这样就没有跨域问题了，可以直接注入脚本

### 如果还有问题：

1. 检查控制台是否有错误信息
2. 确认后端服务器正在运行
3. 确认前端服务器正在运行
4. 清除浏览器缓存后重试

现在可以测试可视化编辑器功能了！
