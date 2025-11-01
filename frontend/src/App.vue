<template>
  <div class="page-bg">
    <nav class="navbar" v-if="currentUser">
      <div class="container">
        <h1>
          <i class="fas fa-utensils"></i>
          Swiggy
        </h1>
        <div class="user-info">
          <button class="profile-btn" @click="goToProfile">
            <i class="fas fa-user-circle"></i>
            <span>{{ currentUser.name }}</span>
          </button>
          <span class="role-badge">{{ currentUser.role }}</span>
          <button class="btn btn-danger" @click="logout">
            <i class="fas fa-sign-out-alt"></i>
            Logout
          </button>
        </div>
      </div>
    </nav>
    <div class="container">
      <div class="main-content">
        <router-view @user-login="handleUserLogin" />
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  data() {
    return {
      currentUser: JSON.parse(localStorage.getItem('user'))
    }
  },
  mounted() {
    // Set up axios interceptor for authentication
    const token = localStorage.getItem('token')
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
    }
    
    // Add response interceptor to handle token expiration
    axios.interceptors.response.use(
      response => response,
      error => {
        if (error.response?.status === 401) {
          this.logout()
        }
        return Promise.reject(error)
      }
    )
  },
  methods: {
    handleUserLogin(authData) {
      this.currentUser = {
        id: authData.userId,
        email: authData.email,
        name: authData.name,
        role: authData.role
      }
    },
    logout() {
      this.currentUser = null
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      delete axios.defaults.headers.common['Authorization']
      this.$router.push('/')
    },
    goToProfile() {
      this.$router.push(`/profile/${this.currentUser.id}`)
    }
  }
}
</script>

<style scoped>
.role-badge {
  background: rgba(255,255,255,0.2);
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.profile-btn {
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.2);
  color: white;
  padding: 8px 12px;
  border-radius: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.profile-btn:hover {
  background: rgba(255,255,255,0.2);
}



.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>