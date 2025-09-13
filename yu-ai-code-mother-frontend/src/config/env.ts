/**
 * 环境变量配置
 */
import {CodeGenTypeEnum} from "@/utils/codeGenTypes.ts";

// 应用部署域名
export const DEPLOY_DOMAIN = import.meta.env.VITE_DEPLOY_DOMAIN || 'http://localhost:81'

// API 基础地址
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// 静态资源地址 - 开发环境使用代理，生产环境使用完整URL
export const STATIC_BASE_URL = import.meta.env.DEV 
  ? '/static'  // 开发环境使用代理
  : `${API_BASE_URL}/static`  // 生产环境使用完整URL

// 获取部署应用的完整URL
export const getDeployUrl = (deployKey: string) => {
  return `${DEPLOY_DOMAIN}/${deployKey}`
}

// 获取静态资源预览URL
export const getStaticPreviewUrl = (codeGenType: string, appId: string) => {
  //这里需要改为`${STATIC_BASE_URL}/${codeGenType}_${appId}/`
  const baseUrl = `${STATIC_BASE_URL}/${codeGenType}_${appId}/`
  // 如果是 Vue 项目，浏览地址需要添加 dist 后缀
  if (codeGenType === CodeGenTypeEnum.VUE_PROJECT.toString()) {
    return `${baseUrl}dist/index.html`
  }
  // 对于HTML项目，直接返回index.html
  if (codeGenType === CodeGenTypeEnum.HTML.toString()) {
    return `${baseUrl}index.html`
  }
  return baseUrl
}
