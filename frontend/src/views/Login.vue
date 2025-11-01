<template>
  <div class="card">
    <h2>Login / Register</h2>
    
    <div class="form-group">
      <label>Email:</label>
      <input v-model="email" type="email" placeholder="Enter email">
    </div>
    
    <div class="form-group">
      <label>Name:</label>
      <input v-model="name" placeholder="Enter name">
    </div>
    
    <div class="form-group">
      <label>Password:</label>
      <input v-model="password" type="password" placeholder="Enter password">
    </div>
    
    <div class="form-group">
      <label>Role:</label>
      <select v-model="role">
        <option value="USER">Customer (USER)</option>
        <option value="HOTEL">Restaurant Owner (HOTEL)</option>
      </select>
    </div>
    
    <button class="btn btn-primary" @click="register">Register & Login</button>
    
    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="success" class="success">{{ success }}</div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      email: '',
      name: '',
      password: '',
      role: 'USER',
      error: '',
      success: ''
    }
  },
  methods: {
    async register() {
      try {
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
      }
    }
  }
}
</script>