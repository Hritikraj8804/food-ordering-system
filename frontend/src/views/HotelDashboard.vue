<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1><i class="fas fa-store"></i> Restaurant Owner Dashboard</h1>
      <p>Manage your restaurants and orders</p>
    </div>
    
    <!-- Create Restaurant (only if no restaurants) -->
    <div v-if="myRestaurants.length === 0" class="section">
      <div class="create-form">
      <h3>Create Your Restaurant First</h3>
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
        <select v-model="newRestaurant.cuisineType">
          <option value="">Select cuisine type</option>
          <option value="Indian">Indian</option>
          <option value="Chinese">Chinese</option>
          <option value="European">European</option>
          <option value="Other">Other</option>
        </select>
      </div>
        <button class="create-btn" @click="createRestaurant">
          <i class="fas fa-plus"></i>
          Create Restaurant
        </button>
      </div>
    </div>
    
    <!-- Menu Management (main focus) -->
    <div v-if="myRestaurants.length > 0" class="card">
      <h3>Manage Menu Items</h3>
      
      <!-- Restaurant Selector -->
      <div class="form-group">
        <label>Select Restaurant:</label>
        <select v-model="selectedRestaurant" @change="loadMenu(selectedRestaurant?.id)">
          <option :value="null">Choose a restaurant</option>
          <option v-for="restaurant in myRestaurants" :key="restaurant.id" :value="restaurant">
            {{ restaurant.name }}
          </option>
        </select>
      </div>
      
      <!-- Add Menu Item -->
      <div v-if="selectedRestaurant" class="menu-management">
        <h4>Add New Menu Item</h4>
        <div class="form-row">
          <input v-model="newMenuItem.name" placeholder="Item name">
          <input v-model="newMenuItem.description" placeholder="Description">
          <input v-model.number="newMenuItem.price" type="number" step="0.01" placeholder="Price">
          <select v-model="newMenuItem.type">
            <option value="VEG">VEG</option>
            <option value="NON_VEG">NON_VEG</option>
          </select>
          <button class="btn btn-success" @click="addMenuItem">Add Item</button>
        </div>
        
        <!-- Current Menu -->
        <h4>Current Menu Items</h4>
        <div v-if="currentMenu.length === 0" class="no-items">
          No menu items yet. Add your first item above.
        </div>
        <div v-for="item in currentMenu" :key="item.id" class="menu-item-row">
          <div class="item-details">
            <strong>{{ item.name }}</strong>
            <p>{{ item.description }}</p>
          </div>
          <span class="item-price">${{ item.price }}</span>
          <span class="item-type" :class="item.type.toLowerCase()">{{ item.type }}</span>
        </div>
      </div>
    </div>
    
    <!-- Incoming Orders -->
    <div class="card">
      <h3>Incoming Orders</h3>
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <h4>Order #{{ order.id }} - {{ order.status }}</h4>
          <p><strong>Customer:</strong> {{ order.user?.name || 'Unknown' }}</p>
          <p><strong>Total:</strong> ${{ order.totalAmount }}</p>
        </div>
        
        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            {{ item.menuItem?.name || 'Item' }} x{{ item.quantity }} - ${{ item.priceAtOrder }}
          </div>
        </div>
        
        <div class="order-actions">
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
      newMenuItem: {
        name: '',
        description: '',
        price: 0,
        type: 'VEG'
      },
      myRestaurants: [],
      selectedRestaurant: null,
      currentMenu: [],
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
        
        if (!this.newRestaurant.name || !this.newRestaurant.address || !this.newRestaurant.cuisineType) {
          this.error = 'Please fill in all fields'
          return
        }
        
        console.log('Creating restaurant for owner ID:', this.id)
        await axios.post(`/api/restaurants/${this.id}`, this.newRestaurant)
        
        this.success = 'Restaurant created successfully!'
        this.newRestaurant = { name: '', address: '', cuisineType: '' }
        await this.loadMyRestaurants()
        await this.loadOrders()
        
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to create restaurant'
      }
    },
    
    async loadMyRestaurants() {
      try {
        const response = await axios.get('/api/restaurants')
        this.myRestaurants = response.data.filter(r => r.hotelOwner?.id == this.id)
      } catch (error) {
        this.error = 'Failed to load restaurants'
      }
    },
    
    async manageMenu(restaurant) {
      this.selectedRestaurant = restaurant
      await this.loadMenu(restaurant.id)
    },
    
    async loadMenu(restaurantId) {
      try {
        const response = await axios.get(`/api/menu-items/restaurant/${restaurantId}`)
        this.currentMenu = response.data
      } catch (error) {
        this.error = 'Failed to load menu'
      }
    },
    
    async addMenuItem() {
      try {
        this.error = ''
        this.success = ''
        
        if (!this.newMenuItem.name || !this.newMenuItem.price) {
          this.error = 'Please fill in all fields'
          return
        }
        
        await axios.post(`/api/menu-items/restaurant/${this.selectedRestaurant.id}/owner/${this.id}`, this.newMenuItem)
        
        this.success = 'Menu item added successfully!'
        this.newMenuItem = { name: '', description: '', price: 0, type: 'VEG' }
        await this.loadMenu(this.selectedRestaurant.id)
        
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to add menu item'
      }
    },
    
    async loadOrders() {
      try {
        this.orders = []
        for (const restaurant of this.myRestaurants) {
          const response = await axios.get(`/api/orders/restaurant/${restaurant.id}`)
          this.orders.push(...response.data)
        }
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

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 40px;
}

.dashboard-header h1 {
  font-size: 32px;
  color: #333;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.dashboard-header p {
  color: #666;
  font-size: 16px;
}

.section {
  margin-bottom: 40px;
}

.create-form {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  border: 1px solid #f0f0f0;
}

.create-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #4CAF50, #45a049);
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

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(76, 175, 80, 0.3);
}

.restaurant-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  border: 1px solid #f0f0f0;
  margin: 16px 0;
}

.restaurant-header {
  margin-bottom: 16px;
}

.menu-management {
  background: linear-gradient(135deg, #fff5f0, #fff8f5);
  padding: 24px;
  border-radius: 16px;
  margin-top: 24px;
  border: 2px solid #ff6b35;
}

.form-row {
  display: grid;
  grid-template-columns: 2fr 3fr 1fr 1fr auto;
  gap: 12px;
  margin: 16px 0;
  align-items: end;
}

.form-row input, .form-row select {
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-family: 'Poppins', sans-serif;
}

.form-row input:focus, .form-row select:focus {
  outline: none;
  border-color: #ff6b35;
}

.menu-item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 12px;
  margin: 16px 0;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  border: 1px solid #f0f0f0;
}

.item-details {
  flex: 1;
}

.item-details strong {
  color: #333;
  font-size: 18px;
}

.item-details p {
  margin: 8px 0 0 0;
  color: #666;
  font-size: 14px;
}

.no-items {
  text-align: center;
  color: #666;
  padding: 40px;
  font-style: italic;
  background: white;
  border-radius: 12px;
  margin: 16px 0;
}

.item-price {
  font-weight: 600;
  color: #ff6b35;
  font-size: 18px;
  margin: 0 16px;
}

.item-type {
  font-size: 11px;
  padding: 4px 12px;
  border-radius: 12px;
  color: white;
  font-weight: 500;
}

.item-type.veg {
  background: #4CAF50;
}

.item-type.non_veg {
  background: #f44336;
}

.order-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin: 16px 0;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  border: 1px solid #f0f0f0;
}

.order-header {
  margin-bottom: 16px;
}

.order-header h4 {
  color: #333;
  font-size: 20px;
  margin-bottom: 8px;
}

.order-items {
  margin: 16px 0;
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
}

.order-item {
  padding: 8px 0;
  border-bottom: 1px solid #e0e0e0;
  color: #666;
}

.order-actions {
  margin-top: 20px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.order-actions button {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.order-actions .btn-primary {
  background: linear-gradient(135deg, #ff6b35, #f7931e);
  color: white;
}

.order-actions .btn-success {
  background: linear-gradient(135deg, #4CAF50, #45a049);
  color: white;
}

.order-actions .btn-danger {
  background: linear-gradient(135deg, #f44336, #d32f2f);
  color: white;
}

.order-actions button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}
</style>