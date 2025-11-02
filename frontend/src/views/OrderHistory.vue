<template>
  <div class="order-history">
    <div class="page-header">
      <button class="back-btn" @click="$router.go(-1)">
        <i class="fas fa-arrow-left"></i>
        Back
      </button>
      <h1><i class="fas fa-history"></i> Order History</h1>
      <p>View all your restaurant orders</p>
    </div>

    <!-- Search Section -->
    <div class="search-section">
      <div class="search-box">
        <i class="fas fa-search"></i>
        <input 
          v-model="searchQuery" 
          type="text" 
          placeholder="Search by Order ID (e.g., 123)"
          @input="filterOrders"
        >
        <button v-if="searchQuery" class="clear-btn" @click="clearSearch">
          <i class="fas fa-times"></i>
        </button>
      </div>
    </div>

    <div class="orders-container">
      <div v-if="filteredOrders.length === 0 && !searchQuery" class="no-orders">
        <i class="fas fa-clipboard-list"></i>
        <h3>No Orders Yet</h3>
        <p>Orders will appear here once customers start placing orders</p>
      </div>
      
      <div v-if="filteredOrders.length === 0 && searchQuery" class="no-orders">
        <i class="fas fa-search"></i>
        <h3>No Orders Found</h3>
        <p>No orders match your search criteria</p>
      </div>
      
      <div v-for="order in paginatedFilteredOrders" :key="order.id" class="order-card">
        <div class="order-header">
          <div class="order-info">
            <h4>Order #{{ order.id }}</h4>
            <span class="order-date">{{ formatDate(order.createdAt) }}</span>
          </div>
          <div class="order-details">
            <p><i class="fas fa-user"></i> {{ order.userName || 'Unknown' }}</p>
            <p class="order-total">₹{{ order.totalAmount }}</p>
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
        </div>
      </div>
      
      <!-- Pagination -->
      <div v-if="totalPages > 1" class="pagination">
        <button 
          class="page-btn" 
          :disabled="currentPage === 1" 
          @click="currentPage = 1"
        >
          First
        </button>
        <button 
          class="page-btn" 
          :disabled="currentPage === 1" 
          @click="currentPage--"
        >
          <i class="fas fa-chevron-left"></i>
        </button>
        
        <span v-for="page in visiblePages" :key="page">
          <button 
            v-if="page !== '...'"
            class="page-btn" 
            :class="{ active: page === currentPage }"
            @click="currentPage = page"
          >
            {{ page }}
          </button>
          <span v-else class="page-dots">...</span>
        </span>
        
        <button 
          class="page-btn" 
          :disabled="currentPage === totalPages" 
          @click="currentPage++"
        >
          <i class="fas fa-chevron-right"></i>
        </button>
        <button 
          class="page-btn" 
          :disabled="currentPage === totalPages" 
          @click="currentPage = totalPages"
        >
          Last
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  props: ['id'],
  data() {
    return {
      orders: [],
      filteredOrders: [],
      searchQuery: '',
      currentPage: 1,
      ordersPerPage: 5,
      error: '',
      success: ''
    }
  },

  async mounted() {
    await this.loadOrders()
  },
  methods: {
    async loadOrders() {
      try {
        const response = await axios.get('/api/restaurants')
        const myRestaurants = response.data.filter(r => r.hotelOwnerId == this.id)
        
        this.orders = []
        for (const restaurant of myRestaurants) {
          const orderResponse = await axios.get(`/api/orders/restaurant/${restaurant.id}`)
          this.orders.push(...orderResponse.data)
        }
        
        // Sort by date, newest first
        this.orders.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        this.filteredOrders = [...this.orders]
      } catch (error) {
        this.error = 'Failed to load orders'
      }
    },
    
    filterOrders() {
      if (!this.searchQuery.trim()) {
        this.filteredOrders = [...this.orders]
      } else {
        const query = this.searchQuery.trim()
        this.filteredOrders = this.orders.filter(order => 
          order.id.toString().includes(query)
        )
      }
      this.currentPage = 1
    },
    
    clearSearch() {
      this.searchQuery = ''
      this.filteredOrders = [...this.orders]
      this.currentPage = 1
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
    
  },
  computed: {
    totalPages() {
      return Math.ceil(this.filteredOrders.length / this.ordersPerPage)
    },
    paginatedFilteredOrders() {
      const start = (this.currentPage - 1) * this.ordersPerPage
      const end = start + this.ordersPerPage
      return this.filteredOrders.slice(start, end)
    },
    visiblePages() {
      const pages = []
      const total = this.totalPages
      const current = this.currentPage
      
      if (total <= 7) {
        for (let i = 1; i <= total; i++) {
          pages.push(i)
        }
      } else {
        if (current <= 4) {
          for (let i = 1; i <= 5; i++) pages.push(i)
          pages.push('...')
          pages.push(total)
        } else if (current >= total - 3) {
          pages.push(1)
          pages.push('...')
          for (let i = total - 4; i <= total; i++) pages.push(i)
        } else {
          pages.push(1)
          pages.push('...')
          for (let i = current - 1; i <= current + 1; i++) pages.push(i)
          pages.push('...')
          pages.push(total)
        }
      }
      return pages
    }
  }
}
</script>

<style scoped>
.order-history {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
}

.back-btn {
  position: absolute;
  left: 0;
  top: 0;
  padding: 12px 20px;
  background: linear-gradient(135deg, #ff6b35, #f7931e);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.back-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
}

.page-header h1 {
  font-size: 32px;
  color: #333;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.page-header p {
  color: #666;
  font-size: 16px;
}

.orders-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.no-orders {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.no-orders i {
  font-size: 48px;
  margin-bottom: 16px;
  color: #ddd;
}

.order-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  border: 1px solid #f0f0f0;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.order-info h4 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.order-date {
  color: #666;
  font-size: 14px;
}

.order-details p {
  margin: 4px 0;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;
}

.order-total {
  color: #ff6b35;
  font-weight: 600;
  font-size: 16px;
}

/* Pipeline styles */
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

/* Color-coded steps */
.pipeline-step.placed.active { color: #2196F3; }
.pipeline-step.placed.active .step-icon { background: #2196F3; color: white; animation: pulse 2s infinite; }
.pipeline-step.placed.completed .step-icon { background: #2196F3; color: white; }

.pipeline-step.preparing.active { color: #4CAF50; }
.pipeline-step.preparing.active .step-icon { background: #4CAF50; color: white; animation: pulse 2s infinite; }
.pipeline-step.preparing.completed .step-icon { background: #4CAF50; color: white; }

.pipeline-step.delivery.active { color: #FFC107; }
.pipeline-step.delivery.active .step-icon { background: #FFC107; color: white; animation: pulse 2s infinite; }
.pipeline-step.delivery.completed .step-icon { background: #FFC107; color: white; }

.pipeline-step.delivered.active { color: #FF9800; }
.pipeline-step.delivered.active .step-icon { background: #FF9800; color: white; animation: pulse 2s infinite; }
.pipeline-step.delivered.completed .step-icon { background: #FF9800; color: white; }

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

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

/* Table styles */
.business-bill {
  margin-top: 16px;
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

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 32px;
  padding: 20px;
}

.page-btn {
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  background: white;
  color: #666;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  min-width: 40px;
}

.page-btn:hover:not(:disabled) {
  background: #f8f9fa;
  border-color: #ff6b35;
  color: #ff6b35;
}

.page-btn.active {
  background: linear-gradient(135deg, #ff6b35, #f7931e);
  color: white;
  border-color: #ff6b35;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-dots {
  padding: 8px 4px;
  color: #666;
}

.search-section {
  margin-bottom: 32px;
}

.search-box {
  position: relative;
  max-width: 400px;
  margin: 0 auto;
}

.search-box i {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
  font-size: 16px;
}

.search-box input {
  width: 100%;
  padding: 16px 16px 16px 48px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 16px;
  font-family: 'Poppins', sans-serif;
  transition: border-color 0.3s ease;
}

.search-box input:focus {
  outline: none;
  border-color: #ff6b35;
  box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
}

.clear-btn {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: #f0f0f0;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #666;
  transition: all 0.3s ease;
}

.clear-btn:hover {
  background: #ff6b35;
  color: white;
}
</style>