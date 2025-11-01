<template>
  <div>
    <h2>Customer Dashboard</h2>
    
    <!-- Restaurant List -->
    <div class="card">
      <h3>Available Restaurants</h3>
      <div v-for="restaurant in restaurants" :key="restaurant.id" class="card">
        <h4>{{ restaurant.name }}</h4>
        <p>{{ restaurant.address }} - {{ restaurant.cuisineType }}</p>
        <button class="btn btn-primary" @click="selectRestaurant(restaurant)">Order From Here</button>
      </div>
    </div>
    
    <!-- Order Form -->
    <div v-if="selectedRestaurant" class="card">
      <h3>Order from {{ selectedRestaurant.name }}</h3>
      <div v-for="(item, index) in orderItems" :key="index" class="form-group">
        <input v-model="item.itemName" placeholder="Item name">
        <input v-model.number="item.quantity" type="number" placeholder="Quantity" min="1">
        <input v-model.number="item.price" type="number" step="0.01" placeholder="Price">
        <button class="btn btn-danger" @click="removeItem(index)">Remove</button>
      </div>
      <button class="btn btn-success" @click="addItem">Add Item</button>
      <button class="btn btn-primary" @click="placeOrder">Place Order</button>
    </div>
    
    <!-- Order History -->
    <div class="card">
      <h3>Your Orders</h3>
      <div v-for="order in orders" :key="order.id" class="card">
        <p><strong>Order #{{ order.id }}</strong> - {{ order.status }}</p>
        <p>Total: ${{ order.totalAmount }}</p>
        <div v-for="item in order.items" :key="item.id">
          {{ item.itemName }} x{{ item.quantity }} - ${{ item.price }}
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
      restaurants: [],
      selectedRestaurant: null,
      orderItems: [{ itemName: '', quantity: 1, price: 0 }],
      orders: [],
      error: '',
      success: ''
    }
  },
  async mounted() {
    await this.loadRestaurants()
    await this.loadOrders()
  },
  methods: {
    async loadRestaurants() {
      try {
        const response = await axios.get('/api/restaurants')
        this.restaurants = response.data
      } catch (error) {
        this.error = 'Failed to load restaurants'
      }
    },
    
    async loadOrders() {
      try {
        const response = await axios.get(`/api/orders/user/${this.id}`)
        this.orders = response.data
      } catch (error) {
        this.error = 'Failed to load orders'
      }
    },
    
    selectRestaurant(restaurant) {
      this.selectedRestaurant = restaurant
    },
    
    addItem() {
      this.orderItems.push({ itemName: '', quantity: 1, price: 0 })
    },
    
    removeItem(index) {
      this.orderItems.splice(index, 1)
    },
    
    async placeOrder() {
      try {
        this.error = ''
        this.success = ''
        
        const validItems = this.orderItems.filter(item => 
          item.itemName && item.quantity > 0 && item.price > 0
        )
        
        if (validItems.length === 0) {
          this.error = 'Please add valid items to your order'
          return
        }
        
        await axios.post(`/api/orders/${this.id}/${this.selectedRestaurant.id}`, {
          items: validItems
        })
        
        this.success = 'Order placed successfully!'
        this.selectedRestaurant = null
        this.orderItems = [{ itemName: '', quantity: 1, price: 0 }]
        await this.loadOrders()
        
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to place order'
      }
    }
  }
}
</script>