<template>
  <div id="userLoginPage">
    <div class="login-container">
      <div class="logo-container">
        <div class="logo">AICreator</div>
        <p class="slogan">不写一行代码，生成完整应用</p>
      </div>
      <a-form
        :model="formState"
        name="basic"
        autocomplete="off"
        @finish="handleSubmit"
        class="login-form"
      >
        <a-form-item
          name="userAccount"
          :rules="[{ required: true, message: '请输入账号' }]"
          class="form-item"
        >
          <a-input
            v-model:value="formState.userAccount"
            placeholder="请输入账号"
            class="custom-input"
            :status="formErrors.userAccount ? 'error' : ''"
          />
          <div v-if="formErrors.userAccount" class="error-message">{{ formErrors.userAccount }}</div>
        </a-form-item>
        <a-form-item
          name="password"
          :rules="[
            { required: true, message: '请输入密码' },
            { min: 8, message: '密码长度不能小于 8 位' },
          ]"
          class="form-item"
        >
          <a-input-password
            v-model:value="formState.password"
            placeholder="请输入密码"
            class="custom-input"
            :status="formErrors.password ? 'error' : ''"
          />
          <div v-if="formErrors.password" class="error-message">{{ formErrors.password }}</div>
        </a-form-item>
        <div class="form-footer">
          <RouterLink to="/user/register" class="register-link">没有账号？去注册</RouterLink>
        </div>
        <a-form-item class="form-item">
          <a-button
            type="primary"
            html-type="submit"
            style="width: 100%"
            class="login-button"
          >
            登录
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { userLogin } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  password: '',
})

const formErrors = reactive<Record<string, string>>({})
const isLoading = ref(false)

const router = useRouter()
const loginUserStore = useLoginUserStore()

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  // 重置错误
  Object.keys(formErrors).forEach(key => delete formErrors[key])

  isLoading.value = true
  try {
    const res = await userLogin(values)
    // 登录成功，把登录态保存到全局状态中
    if (res.data.code === 0 && res.data.data) {
      await loginUserStore.fetchLoginUser()
      message.success('登录成功')
      router.push({
        path: '/',
        replace: true,
      })
    } else {
      message.error('登录失败，' + res.data.message)
    }
  } catch (error) {
    message.error('登录请求失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

// 表单验证
const validateForm = () => {
  if (!formState) {
    return false;
  }
  let isValid = true

  if (!formState.userAccount.trim()) {
    formErrors.userAccount = '请输入账号'
    isValid = false
  } else {
    delete formErrors.userAccount
  }

  if (!formState.password) {
    formErrors.password = '请输入密码'
    isValid = false
  } else if (formState.password.length < 8) {
    formErrors.password = '密码长度不能小于 8 位'
    isValid = false
  } else {
    delete formErrors.password
  }

  return isValid
}
</script>

<style scoped>
#userLoginPage {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4eaf5 100%);
  padding: 24px;
}

.login-container {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.login-container:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
}

.logo-container {
  background: linear-gradient(135deg, #1890ff 0%, #0050b3 100%);
  padding: 40px 24px;
  text-align: center;
  color: white;
}

.logo {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
  letter-spacing: 1px;
}

.slogan {
  font-size: 14px;
  opacity: 0.9;
}

.login-form {
  padding: 32px 24px;
}

.form-item {
  margin-bottom: 20px;
}

.custom-input {
  height: 44px;
  border-radius: 8px;
  font-size: 15px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.custom-input:focus-within {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.error-message {
  color: #f5222d;
  font-size: 12px;
  margin-top: 4px;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 24px;
}

.register-link {
  color: #1890ff;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.3s ease;
}

.register-link:hover {
  color: #096dd9;
  text-decoration: underline;
}

.login-button {
  height: 44px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  background: #1890ff;
  border: none;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.login-button:hover {
  background: #096dd9;
}

.login-button:active {
  transform: scale(0.98);
}

.login-button:disabled {
  background: #8cc5ff;
  cursor: not-allowed;
}
</style>
