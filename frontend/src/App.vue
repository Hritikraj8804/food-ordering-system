<template>
  <div class="page-bg">
    <nav class="navbar" v-if="currentUser">
      <div class="container">
        <h1>
          <i class="fas fa-utensils"></i>
          Swiggy
        </h1>
        <div class="user-info">
          <i class="fas fa-user-circle"></i>
          <span>{{ currentUser.name }}</span>
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
export default {
  data() {
    return {
      currentUser: JSON.parse(localStorage.getItem('currentUser'))
    }
  },
  methods: {
    handleUserLogin(user) {
      this.currentUser = user
      localStorage.setItem('currentUser', JSON.stringify(user))
    },
    logout() {
      this.currentUser = null
      localStorage.removeItem('currentUser')
      this.$router.push('/login')
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
</style>