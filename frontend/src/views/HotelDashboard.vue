<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1><i class="fas fa-store"></i> Restaurant Owner Dashboard</h1>
      <p>Manage your restaurants and orders</p>
      
      <!-- Navigation Tabs -->
      <div class="nav-tabs">
        <router-link :to="`/hotel/${id}`" class="nav-tab" active-class="active">
          <i class="fas fa-tachometer-alt"></i>
          Dashboard
        </router-link>
        <router-link :to="`/hotel/${id}/orders`" class="nav-tab" active-class="active">
          <i class="fas fa-history"></i>
          Orders
        </router-link>
        <router-link :to="`/hotel/${id}/reviews`" class="nav-tab" active-class="active">
          <i class="fas fa-star"></i>
          Reviews
        </router-link>
      </div>
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
      <h3>Active Orders</h3>
      <div v-if="orders.length === 0" class="no-orders">
        <i class="fas fa-clipboard-list"></i>
        <h4>No Active Orders</h4>
        <p>New orders will appear here when customers place them</p>
      </div>
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <div class="order-info">
            <h4>Order #{{ order.id }}</h4>
            <span class="order-date">{{ formatDate(order.createdAt) }}</span>
          </div>
          <div class="order-details">
            <p><i class="fas fa-user"></i> <strong>Customer:</strong> {{ order.user?.name || 'Unknown' }}</p>
            <p class="order-total"><i class="fas fa-rupee-sign"></i> ₹{{ order.totalAmount }}</p>
          </div>
        </div>
        
        <!-- Order Status Pipeline -->
        <div class="status-pipeline">
          <div v-if="order.status === 'CANCELLED'" class="cancelled-status">
            <div class="step-icon cancelled">
              <i class="fas fa-times"></i>
            </div>
            <span>Order Cancelled</span>
          </div>
          <template v-else>
            <div class="pipeline-step" :class="['placed', { active: isStepActive('PLACED', order.status), completed: isStepCompleted('PLACED', order.status) }]">
              <div class="step-icon"><i class="fas fa-receipt"></i></div>
              <span>Placed</span>
            </div>
            <div class="pipeline-line" :class="{ active: isStepCompleted('PLACED', order.status) }"></div>
            <div class="pipeline-step" :class="['preparing', { active: isStepActive('PREPARING', order.status), completed: isStepCompleted('PREPARING', order.status) }]">
              <div class="step-icon"><i class="fas fa-utensils"></i></div>
              <span>Preparing</span>
            </div>
            <div class="pipeline-line" :class="{ active: isStepCompleted('PREPARING', order.status) }"></div>
            <div class="pipeline-step" :class="['delivery', { active: isStepActive('OUT_FOR_DELIVERY', order.status), completed: isStepCompleted('OUT_FOR_DELIVERY', order.status) }]">
              <div class="step-icon"><i class="fas fa-truck"></i></div>
              <span>Delivery</span>
            </div>
            <div class="pipeline-line" :class="{ active: isStepCompleted('OUT_FOR_DELIVERY', order.status) }"></div>
            <div class="pipeline-step" :class="['delivered', { active: isStepActive('DELIVERED', order.status), completed: isStepCompleted('DELIVERED', order.status) }]">
              <div class="step-icon"><i class="fas fa-check-circle"></i></div>
              <span>Delivered</span>
            </div>
          </template>
        </div>
        
        <!-- Business Bill Details -->
        <div class="business-bill">
          <div class="bill-header">
            <i class="fas fa-file-invoice-dollar"></i>
            <span>Order Invoice</span>
            <span class="invoice-number">#INV-{{ order.id }}</span>
          </div>
          
          <div class="bill-details">
            <div class="customer-details">
              <h6><i class="fas fa-user"></i> Customer Information</h6>
              <p><strong>Name:</strong> {{ order.user?.name || 'Unknown' }}</p>
              <p><strong>Order Date:</strong> {{ formatDate(order.createdAt) }}</p>
            </div>
            
            <div class="order-items-table">
              <table>
                <thead>
                  <tr>
                    <th>Item</th>
                    <th>Qty</th>
                    <th>Rate</th>
                    <th>Amount</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in order.items" :key="item.id">
                    <td class="item-name">{{ item.itemName || item.menuItem?.name || 'Item' }}</td>
                    <td class="item-qty">×{{ item.quantity }}</td>
                    <td class="item-rate">₹{{ item.price || item.priceAtOrder }}</td>
                    <td class="item-total">₹{{ (item.price || item.priceAtOrder) * item.quantity }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <div class="bill-summary">
              <div class="summary-row total">
                <span>Total Amount:</span>
                <span>₹{{ order.totalAmount }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="order-actions">
          <button v-if="order.status === 'PLACED'" class="btn btn-primary" @click="updateOrderStatus(order.id, 'PREPARING')">
            <i class="fas fa-clock"></i>
            Start Preparing
          </button>
          <button v-if="order.status === 'PREPARING'" class="btn btn-primary" @click="updateOrderStatus(order.id, 'OUT_FOR_DELIVERY')">
            <i class="fas fa-truck"></i>
            Ready for Delivery
          </button>
          <button v-if="order.status === 'PLACED'" class="btn btn-danger" @click="updateOrderStatus(order.id, 'CANCELLED')">
            <i class="fas fa-times"></i>
            Cancel Order
          </button>
          <div v-if="order.status === 'OUT_FOR_DELIVERY'" class="status-info">
            <i class="fas fa-truck"></i>
            Waiting for customer confirmation
          </div>
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
          // Filter only incoming orders (not delivered or cancelled)
          const incomingOrders = response.data.filter(order => 
            order.status !== 'DELIVERED' && order.status !== 'CANCELLED'
          )
          this.orders.push(...incomingOrders)
        }
        // Sort by date, newest first
        this.orders.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
      } catch (error) {
        this.error = 'Failed to load orders'
      }
    },
    
    formatDate(dateString) {
      if (!dateString) return 'N/A'
      return new Date(dateString).toLocaleDateString('en-IN', {
        day: 'numeric',
        month: 'short',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    
    isStepActive(step, currentStatus) {
      return step === currentStatus
    },
    
    isStepCompleted(step, currentStatus) {
      const steps = ['PLACED', 'PREPARING', 'OUT_FOR_DELIVERY', 'DELIVERED']
      const stepIndex = steps.indexOf(step)
      const currentIndex = steps.indexOf(currentStatus)
      return currentIndex > stepIndex
    },
    
    getStatusText(status) {
      const statusMap = {
        'PLACED': 'Order Placed',
        'PREPARING': 'Preparing',
        'OUT_FOR_DELIVERY': 'Out for Delivery',
        'DELIVERED': 'Delivered',
        'CANCELLED': 'Cancelled'
      }
      return statusMap[status] || status
    },
    
    async updateOrderStatus(orderId, status) {
      try {
        this.error = ''
        this.success = ''
        
        const userId = parseInt(this.id)
        console.log('Updating order status:', { orderId, status, userId })
        await axios.put(`/api/orders/${orderId}/status?status=${status}&updaterId=${userId}`)
        
        this.success = `Order status updated to ${this.getStatusText(status)}`
        await this.loadOrders()
        
      } catch (error) {
        console.error('Error updating order status:', error.response?.data)
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

.order-info {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}

.order-info h4 {
  margin: 0;
  color: #333;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.status-badge.placed {
  background: #e3f2fd;
  color: #1976d2;
}

.status-badge.preparing {
  background: #fff3e0;
  color: #f57c00;
}

.status-badge.out_for_delivery {
  background: #f3e5f5;
  color: #7b1fa2;
}

.status-badge.delivered {
  background: #e8f5e8;
  color: #388e3c;
}

.status-badge.cancelled {
  background: #ffebee;
  color: #d32f2f;
}

.order-details p {
  margin: 4px 0;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;
}

.order-details .order-total {
  color: #ff6b35;
  font-weight: 600;
  font-size: 16px;
}

.order-items h5 {
  color: #333;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-tabs {
  display: flex;
  gap: 8px;
  margin-top: 24px;
  justify-content: center;
}

.nav-tab {
  padding: 12px 24px;
  background: rgba(255,255,255,0.1);
  color: #666;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  border: 2px solid transparent;
}

.nav-tab:hover {
  background: rgba(255,255,255,0.2);
  color: #333;
}

.nav-tab.active {
  background: linear-gradient(135deg, #ff6b35, #f7931e);
  color: white;
  border-color: #ff6b35;
}

.order-date {
  color: #666;
  font-size: 14px;
}

.status-pipeline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 24px 0;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.pipeline-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  opacity: 0.4;
  transition: all 0.3s ease;
}

.pipeline-step.active {
  opacity: 1;
}

.pipeline-step.completed {
  opacity: 1;
}

.step-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e0e0e0;
  transition: all 0.3s ease;
  color: #666;
}

/* Placed - Blue */
.pipeline-step.placed.active {
  color: #2196F3;
}
.pipeline-step.placed.active .step-icon {
  background: #2196F3;
  color: white;
  animation: pulse 2s infinite;
}
.pipeline-step.placed.completed .step-icon {
  background: #2196F3;
  color: white;
}

/* Preparing - Green */
.pipeline-step.preparing.active {
  color: #4CAF50;
}
.pipeline-step.preparing.active .step-icon {
  background: #4CAF50;
  color: white;
  animation: pulse 2s infinite;
}
.pipeline-step.preparing.completed .step-icon {
  background: #4CAF50;
  color: white;
}

/* Delivery - Yellow */
.pipeline-step.delivery.active {
  color: #FFC107;
}
.pipeline-step.delivery.active .step-icon {
  background: #FFC107;
  color: white;
  animation: pulse 2s infinite;
}
.pipeline-step.delivery.completed .step-icon {
  background: #FFC107;
  color: white;
}

/* Delivered - Orange */
.pipeline-step.delivered.active {
  color: #FF9800;
}
.pipeline-step.delivered.active .step-icon {
  background: #FF9800;
  color: white;
  animation: pulse 2s infinite;
}
.pipeline-step.delivered.completed .step-icon {
  background: #FF9800;
  color: white;
}

/* Cancelled - Red */
.cancelled-status {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #f44336;
  font-weight: 600;
}

.step-icon.cancelled {
  background: #f44336;
  color: white;
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

.pipeline-line {
  flex: 1;
  height: 2px;
  background: #e0e0e0;
  margin: 0 16px;
  transition: all 0.3s ease;
}

.pipeline-line.active {
  background: linear-gradient(90deg, #2196F3, #4CAF50);
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

.business-bill {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  margin-top: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.business-bill .bill-header {
  background: linear-gradient(135deg, #2196F3, #1976D2);
  color: white;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
}

.invoice-number {
  background: rgba(255,255,255,0.2);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
}

.bill-details {
  padding: 20px;
}

.customer-details {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.customer-details h6 {
  margin: 0 0 12px 0;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.customer-details p {
  margin: 4px 0;
  color: #666;
  font-size: 14px;
}

.order-items-table {
  margin-bottom: 20px;
}

.order-items-table table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

.order-items-table th {
  background: #f8f9fa;
  padding: 12px 8px;
  font-weight: 600;
  color: #333;
  border-bottom: 2px solid #e0e0e0;
  font-size: 14px;
}

.order-items-table th:first-child {
  text-align: left;
}

.order-items-table th:not(:first-child) {
  text-align: center;
}

.order-items-table td {
  padding: 12px 8px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
}

.item-name {
  font-weight: 500;
  color: #333;
}

.item-qty {
  text-align: center;
  color: #666;
}

.item-rate, .item-total {
  text-align: center;
  font-weight: 500;
  color: #333;
}

.bill-summary {
  border-top: 2px solid #e0e0e0;
  padding-top: 16px;
  margin-bottom: 16px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  font-size: 14px;
}

.summary-row.total {
  border-top: 1px solid #e0e0e0;
  margin-top: 8px;
  padding-top: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.payment-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #e8f5e8;
  border-radius: 8px;
  border-left: 4px solid #4CAF50;
}

.status-label {
  font-weight: 500;
  color: #333;
}

.payment-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.payment-badge.paid {
  background: #4CAF50;
  color: white;
}

.no-orders {
  text-align: center;
  padding: 40px 20px;
  color: #666;
}

.no-orders i {
  font-size: 48px;
  margin-bottom: 16px;
  color: #ddd;
}

.no-orders h4 {
  margin-bottom: 8px;
  color: #333;
}

.no-orders p {
  color: #666;
  font-size: 14px;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #f3e5f5, #e1bee7);
  color: #7b1fa2;
  border-radius: 8px;
  font-weight: 500;
  border: 2px solid #ba68c8;
  margin-top: 12px;
}

.status-success {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #e8f5e8, #c8e6c9);
  color: #388e3c;
  border-radius: 8px;
  font-weight: 500;
  border: 2px solid #81c784;
  margin-top: 12px;
}
</style>