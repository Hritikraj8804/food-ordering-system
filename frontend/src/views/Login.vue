<template>
  <div class="card">
    <div class="auth-tabs">
      <button 
        :class="['tab-btn', { active: isLogin }]" 
        @click="switchToLogin"
      >
        Login
      </button>
      <button 
        :class="['tab-btn', { active: !isLogin }]" 
        @click="switchToRegister"
      >
        Register
      </button>
    </div>

    <h2>{{ isLogin ? 'Login' : 'Register' }}</h2>
    
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
      class="btn btn-primary" 
      @click="isLogin ? login() : register()"
      :disabled="loading"
    >
      {{ loading ? 'Please wait...' : (isLogin ? 'Login' : 'Register') }}
    </button>
    
    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="success" class="success">{{ success }}</div>
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
        
        // Since backend doesn't have login endpoint, we'll find user by email
        const response = await axios.get('/api/users')
        const users = response.data
        const user = users.find(u => u.email === this.email)
        
        if (!user) {
          this.error = 'User not found. Please register first.'
          return
        }
        
        // In a real app, you'd verify password on backend
        // For now, we'll just proceed with the found user
        this.success = 'Login successful!'
        this.$emit('user-login', user)
        
        // Redirect based on role
        if (user.role === 'USER') {
          this.$router.push(`/user/${user.id}`)
        } else {
          this.$router.push(`/hotel/${user.id}`)
        }
        
      } catch (error) {
        this.error = 'Login failed. Please check your credentials.'
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
        
        const response = await axios.post('/api/users/register', {
          email: this.email,
          name: this.name,
          password: this.password,
          role: this.role
        })
        
        const user = response.data
        this.success = 'Registration successful!'
        
        this.$emit('user-login', user)
        
        // Redirect based on role
        if (user.role === 'USER') {
          this.$router.push(`/user/${user.id}`)
        } else {
          this.$router.push(`/hotel/${user.id}`)
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
.auth-tabs {
  display: flex;
  margin-bottom: 20px;
  border-bottom: 1px solid #ddd;
}

.tab-btn {
  flex: 1;
  padding: 10px;
  border: none;
  background: #f8f9fa;
  cursor: pointer;
  border-bottom: 2px solid transparent;
}

.tab-btn.active {
  background: white;
  border-bottom-color: #007bff;
  color: #007bff;
}

.tab-btn:hover {
  background: #e9ecef;
}
</style>