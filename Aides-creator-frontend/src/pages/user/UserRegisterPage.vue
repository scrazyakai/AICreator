<template>
  <div id="userRegisterPage">
    <div class="register-container">
      <div class="logo-container">
        <div class="logo">AICreator</div>
        <p class="slogan">不写一行代码，生成完整应用</p>
      </div>
      <a-form
        :model="formState"
        name="basic"
        autocomplete="off"
        @finish="handleSubmit"
        class="register-form"
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
            { min: 8, message: '密码不能小于 8 位' },
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
        <a-form-item
          name="checkPassword"
          :rules="[
            { required: true, message: '请确认密码' },
            { min: 8, message: '密码不能小于 8 位' },
            { validator: validateCheckPassword },
          ]"
          class="form-item"
        >
          <a-input-password
            v-model:value="formState.checkPassword"
            placeholder="请确认密码"
            class="custom-input"
            :status="formErrors.checkPassword ? 'error' : ''"
          />
          <div v-if="formErrors.checkPassword" class="error-message">{{ formErrors.checkPassword }}</div>
        </a-form-item>
        <a-form-item
          name="inviteCode"
          class="form-item"
        >
          <a-input
            v-model:value="formState.inviteCode"
            placeholder="邀请码（选填）"
            class="custom-input"
            :status="formErrors.inviteCode ? 'error' : ''"
          />
          <div v-if="formErrors.inviteCode" class="error-message">{{ formErrors.inviteCode }}</div>
        </a-form-item>
        <div class="form-footer">
          <RouterLink to="/user/login" class="login-link">已有账号？去登录</RouterLink>
        </div>
        <a-form-item class="form-item">
          <a-button
            type="primary"
            html-type="submit"
            style="width: 100%"
            class="register-button"
            :loading="isLoading"
          >
            注册
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { userRegister } from '@/api/userController'
import { message } from 'ant-design-vue'
import { reactive, ref } from 'vue'

const router = useRouter()

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  password: '',
  checkPassword: '',
  inviteCode: '',
})

const formErrors = reactive<Record<string, string>>({})
const isLoading = ref(false)

/**
 * 验证确认密码
 * @param rule
 * @param value
 * @param callback
 */
const validateCheckPassword = (rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value && value !== formState.password) {
    formErrors.checkPassword = '两次输入密码不一致'
    callback(new Error('两次输入密码不一致'))
  } else {
    delete formErrors.checkPassword
    callback()
  }
}

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: API.UserRegisterRequest) => {
  // 重置错误
  Object.keys(formErrors).forEach(key => delete formErrors[key])

  isLoading.value = true
  try {
    const res = await userRegister(values)
    // 注册成功，跳转到登录页面
    if (res.data.code === 0) {
      message.success('注册成功')
      router.push({
        path: '/user/login',
        replace: true,
      })
    } else {
      message.error('注册失败，' + res.data.message)
    }
  } catch (error) {
    message.error('注册请求失败，请稍后重试')
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
    formErrors.password = '密码不能小于 8 位'
    isValid = false
  } else {
    delete formErrors.password
  }

  if (!formState.checkPassword) {
    formErrors.checkPassword = '请确认密码'
    isValid = false
  } else if (formState.checkPassword.length < 8) {
    formErrors.checkPassword = '密码不能小于 8 位'
    isValid = false
  } else if (formState.checkPassword !== formState.password) {
    formErrors.checkPassword = '两次输入密码不一致'
    isValid = false
  } else {
    delete formErrors.checkPassword
  }

  // 邀请码验证（选填，但如果填写了则进行基本格式验证）
  if (formState.inviteCode && formState.inviteCode.trim()) {
    if (formState.inviteCode.length < 4) {
      formErrors.inviteCode = '邀请码格式不正确'
      isValid = false
    } else {
      delete formErrors.inviteCode
    }
  } else {
    delete formErrors.inviteCode
  }

  return isValid
}
</script>

<style scoped>
#userRegisterPage {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4eaf5 100%);
  padding: 24px;
}

.register-container {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.register-container:hover {
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

.register-form {
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

.login-link {
  color: #1890ff;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.3s ease;
}

.login-link:hover {
  color: #096dd9;
  text-decoration: underline;
}

.register-button {
  height: 44px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  background: #1890ff;
  border: none;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.register-button:hover {
  background: #096dd9;
}

.register-button:active {
  transform: scale(0.98);
}

.register-button:disabled,
.register-button:disabled:hover {
  background: #8cc5ff;
  cursor: not-allowed;
  transform: none;
}
</style>
