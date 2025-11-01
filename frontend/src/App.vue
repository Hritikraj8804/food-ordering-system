<template>
  <div>
    <nav class="navbar">
      <div class="container">
        <h1>🍕 Food Ordering System</h1>
        <div v-if="currentUser">
          <span>{{ currentUser.name }} ({{ currentUser.role }})</span>
          <button class="btn btn-danger" @click="logout">Logout</button>
        </div>
      </div>
    </nav>
    <div class="container">
      <router-view @user-login="handleUserLogin" />
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