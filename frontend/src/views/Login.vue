<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="brand-header">
        <i class="fas fa-utensils brand-icon"></i>
        <h1>Swiggy Clone</h1>
        <p>Order food from your favorite restaurants</p>
      </div>
      
      <div class="auth-tabs">
        <button 
          :class="['tab-btn', { active: isLogin }]" 
          @click="switchToLogin"
        >
          <i class="fas fa-sign-in-alt"></i>
          Login
        </button>
        <button 
          :class="['tab-btn', { active: !isLogin }]" 
          @click="switchToRegister"
        >
          <i class="fas fa-user-plus"></i>
          Register
        </button>
      </div>
    
    <div class="form-group">
      <label>Email:</label>
      <input v-model="email" type="email" placeholder="Enter email">
    </div>
    
    <div v-if="!isLogin" class="form-group">
      <label>Name:</label>
      <input v-model="name" placeholder="Enter name">
    </div>
    
    <div class="form-group">
      <label>Password:</label>
      <input v-model="password" type="password" placeholder="Enter password">
    </div>
    
    <div v-if="!isLogin" class="form-group">
      <label>Role:</label>
      <select v-model="role">
        <option value="USER">Customer (USER)</option>
        <option value="HOTEL">Restaurant Owner (HOTEL)</option>
      </select>
    </div>
    
      <button 
        class="auth-btn" 
        @click="isLogin ? login() : register()"
        :disabled="loading"
      >
        <i v-if="loading" class="fas fa-spinner fa-spin"></i>
        <i v-else-if="isLogin" class="fas fa-sign-in-alt"></i>
        <i v-else class="fas fa-user-plus"></i>
        {{ loading ? 'Please wait...' : (isLogin ? 'Login' : 'Create Account') }}
      </button>
      
      <div v-if="error" class="error">
        <i class="fas fa-exclamation-circle"></i>
        {{ error }}
      </div>
      <div v-if="success" class="success">
        <i class="fas fa-check-circle"></i>
        {{ success }}
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      isLogin: true,
      email: '',
      name: '',
      password: '',
      role: 'USER',
      error: '',
      success: '',
      loading: false
    }
  },
  methods: {
    async login() {
      if (!this.email || !this.password) {
        this.error = 'Please enter both email and password'
        return
      }
      
      try {
        this.loading = true
        this.error = ''
        this.success = ''
        
        const response = await axios.post('/api/auth/login', {
          email: this.email,
          password: this.password
        })
        
        const authData = response.data
        
        // Store token and user data
        localStorage.setItem('token', authData.token)
        localStorage.setItem('user', JSON.stringify({
          id: authData.userId,
          email: authData.email,
          name: authData.name,
          role: authData.role
        }))
        
        // Set default authorization header
        axios.defaults.headers.common['Authorization'] = `Bearer ${authData.token}`
        
        this.success = 'Login successful!'
        this.$emit('user-login', authData)
        
        // Redirect based on role
        if (authData.role === 'USER') {
          this.$router.push(`/user/${authData.userId}`)
        } else {
          this.$router.push(`/hotel/${authData.userId}`)
        }
        
      } catch (error) {
        this.error = error.response?.data?.message || 'Login failed. Please check your credentials.'
      } finally {
        this.loading = false
      }
    },
    
    async register() {
      if (!this.email || !this.name || !this.password) {
        this.error = 'Please fill in all required fields'
        return
      }
      
      try {
        this.loading = true
        this.error = ''
        this.success = ''
        
        const response = await axios.post('/api/auth/register', {
          email: this.email,
          name: this.name,
          password: this.password,
          role: this.role
        })
        
        const authData = response.data
        
        // Store token and user data
        localStorage.setItem('token', authData.token)
        localStorage.setItem('user', JSON.stringify({
          id: authData.userId,
          email: authData.email,
          name: authData.name,
          role: authData.role
        }))
        
        // Set default authorization header
        axios.defaults.headers.common['Authorization'] = `Bearer ${authData.token}`
        
        this.success = 'Registration successful!'
        this.$emit('user-login', authData)
        
        // Redirect based on role
        if (authData.role === 'USER') {
          this.$router.push(`/user/${authData.userId}`)
        } else {
          this.$router.push(`/hotel/${authData.userId}`)
        }
        
      } catch (error) {
        this.error = error.response?.data?.message || 'Registration failed'
      } finally {
        this.loading = false
      }
    },
    
    switchToLogin() {
      this.isLogin = true
      this.clearForm()
    },
    
    switchToRegister() {
      this.isLogin = false
      this.clearForm()
    },
    
    clearForm() {
      this.email = ''
      this.name = ''
      this.password = ''
      this.role = 'USER'
      this.error = ''
      this.success = ''
    }
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.auth-card {
  background: white;
  border-radius: 20px;
  padding: 40px;
  width: 100%;
  max-width: 450px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.1);
  border: 1px solid rgba(255,255,255,0.2);
}

.brand-header {
  text-align: center;
  margin-bottom: 32px;
}

.brand-icon {
  font-size: 48px;
  color: #ff6b35;
  margin-bottom: 16px;
}

.brand-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.brand-header p {
  color: #666;
  font-size: 16px;
}

.auth-tabs {
  display: flex;
  margin-bottom: 32px;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 4px;
}

.tab-btn {
  flex: 1;
  padding: 12px 16px;
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: 8px;
  font-weight: 500;
  color: #666;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.tab-btn.active {
  background: white;
  color: #ff6b35;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.tab-btn:hover:not(.active) {
  background: rgba(255,255,255,0.5);
}

.auth-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #ff6b35, #f7931e);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 24px;
}

.auth-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 107, 53, 0.3);
}

.auth-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 16px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s ease;
  font-family: 'Poppins', sans-serif;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #ff6b35;
  box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
}

.error {
  background: #ffebee;
  color: #c62828;
  padding: 12px 16px;
  border-radius: 8px;
  border-left: 4px solid #f44336;
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.success {
  background: #e8f5e8;
  color: #2e7d32;
  padding: 12px 16px;
  border-radius: 8px;
  border-left: 4px solid #4caf50;
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>