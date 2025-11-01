<template>
  <div>
    <h2>Restaurant Owner Dashboard</h2>
    
    <!-- Create Restaurant -->
    <div class="card">
      <h3>Create New Restaurant</h3>
      <div class="form-group">
        <label>Restaurant Name:</label>
        <input v-model="newRestaurant.name" placeholder="Enter restaurant name">
      </div>
      <div class="form-group">
        <label>Address:</label>
        <input v-model="newRestaurant.address" placeholder="Enter address">
      </div>
      <div class="form-group">
        <label>Cuisine Type:</label>
        <input v-model="newRestaurant.cuisineType" placeholder="Enter cuisine type">
      </div>
      <button class="btn btn-success" @click="createRestaurant">Create Restaurant</button>
    </div>
    
    <!-- My Restaurants -->
    <div class="card">
      <h3>My Restaurants</h3>
      <div v-for="restaurant in myRestaurants" :key="restaurant.id" class="card">
        <h4>{{ restaurant.name }}</h4>
        <p>{{ restaurant.address }} - {{ restaurant.cuisineType }}</p>
      </div>
    </div>
    
    <!-- Incoming Orders -->
    <div class="card">
      <h3>Incoming Orders</h3>
      <div v-for="order in orders" :key="order.id" class="card">
        <h4>Order #{{ order.id }} - {{ order.status }}</h4>
        <p><strong>Customer:</strong> {{ order.user?.name || 'Unknown' }}</p>
        <p><strong>Total:</strong> ${{ order.totalAmount }}</p>
        
        <div><strong>Items:</strong></div>
        <div v-for="item in order.items" :key="item.id">
          {{ item.itemName }} x{{ item.quantity }} - ${{ item.price }}
        </div>
        
        <div style="margin-top: 10px;">
          <button class="btn btn-primary" @click="updateOrderStatus(order.id, 'PREPARING')">
            Mark as Preparing
          </button>
          <button class="btn btn-primary" @click="updateOrderStatus(order.id, 'OUT_FOR_DELIVERY')">
            Out for Delivery
          </button>
          <button class="btn btn-success" @click="updateOrderStatus(order.id, 'DELIVERED')">
            Mark as Delivered
          </button>
          <button class="btn btn-danger" @click="updateOrderStatus(order.id, 'CANCELLED')">
            Cancel Order
          </button>
        </div>
      </div>
    </div>
    
    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="success" class="success">{{ success }}</div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  props: ['id'],
  data() {
    return {
      newRestaurant: {
        name: '',
        address: '',
        cuisineType: ''
      },
      myRestaurants: [],
      orders: [],
      error: '',
      success: ''
    }
  },
  async mounted() {
    await this.loadMyRestaurants()
    await this.loadOrders()
  },
  methods: {
    async createRestaurant() {
      try {
        this.error = ''
        this.success = ''
        
        await axios.post(`/api/restaurants/${this.id}`, this.newRestaurant)
        
        this.success = 'Restaurant created successfully!'
        this.newRestaurant = { name: '', address: '', cuisineType: '' }
        await this.loadMyRestaurants()
        
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to create restaurant'
      }
    },
    
    async loadMyRestaurants() {
      try {
        const response = await axios.get('/api/restaurants')
        // Filter restaurants owned by current user
        this.myRestaurants = response.data.filter(r => r.hotelOwner?.id == this.id)
      } catch (error) {
        this.error = 'Failed to load restaurants'
      }
    },
    
    async loadOrders() {
      try {
        // Note: This assumes there's an endpoint to get orders by restaurant
        // For now, we'll simulate this by getting all orders and filtering
        // In a real app, you'd have GET /api/orders/restaurant/{restaurantId}
        const response = await axios.get('/api/restaurants')
        const myRestaurantIds = response.data
          .filter(r => r.hotelOwner?.id == this.id)
          .map(r => r.id)
        
        // This is a workaround - in production you'd have a proper endpoint
        this.orders = []
        
      } catch (error) {
        this.error = 'Failed to load orders'
      }
    },
    
    async updateOrderStatus(orderId, status) {
      try {
        this.error = ''
        this.success = ''
        
        await axios.put(`/api/orders/${orderId}/status?status=${status}&updaterId=${this.id}`)
        
        this.success = `Order status updated to ${status}`
        await this.loadOrders()
        
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to update order status'
      }
    }
  }
}
</script>