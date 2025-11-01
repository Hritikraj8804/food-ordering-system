<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1><i class="fas fa-user"></i> Customer Dashboard</h1>
      <p>Discover and order from amazing restaurants</p>
      
      <!-- Navigation Tabs -->
      <div class="nav-tabs">
        <router-link :to="`/user/${id}`" class="nav-tab" active-class="active">
          <i class="fas fa-home"></i>
          Dashboard
        </router-link>
        <router-link :to="`/user/${id}/orders`" class="nav-tab" active-class="active">
          <i class="fas fa-history"></i>
          My Orders
        </router-link>
      </div>
    </div>
    
    <!-- Restaurant List -->
    <div class="section">
      <h2><i class="fas fa-store"></i> Available Restaurants</h2>
      <div class="restaurants-grid">
        <div v-for="restaurant in restaurants" :key="restaurant.id" class="restaurant-card">
          <div class="restaurant-header">
            <h3>{{ restaurant.name }}</h3>
            <span class="cuisine-badge">{{ restaurant.cuisineType }}</span>
          </div>
          <p class="restaurant-address">
            <i class="fas fa-map-marker-alt"></i>
            {{ restaurant.address }}
          </p>
          <div class="restaurant-rating" v-if="restaurant.averageRating">
            <div class="stars">
              <i v-for="n in 5" :key="n" 
                 :class="['fas fa-star', { active: n <= Math.round(restaurant.averageRating) }]"></i>
            </div>
            <span class="rating-text">{{ restaurant.averageRating.toFixed(1) }} ({{ restaurant.reviewCount }} reviews)</span>
          </div>
          <button class="order-btn" @click="viewMenu(restaurant)">
            <i class="fas fa-eye"></i>
            View Menu
          </button>
        </div>
      </div>
    </div>
    
    <!-- Menu Items -->
    <div v-if="selectedRestaurant" class="card">
      <h3>Menu - {{ selectedRestaurant.name }}</h3>
      <div class="menu-grid">
        <div v-for="item in menuItems" :key="item.id" class="food-card">
          <div class="food-image">
            <img :src="`/api/menu-items/image/${item.id}`" :alt="item.name" @error="handleImageError">
            <div class="food-type" :class="item.type.toLowerCase()">
              <i :class="item.type === 'VEG' ? 'fas fa-circle' : 'fas fa-stop'"></i>
            </div>
          </div>
          <div class="food-content">
            <h5>{{ item.name }}</h5>
            <p class="food-description">{{ item.description }}</p>
            <div class="item-rating" v-if="item.averageRating">
              <div class="stars">
                <i v-for="n in 5" :key="n" 
                   :class="['fas fa-star', { active: n <= Math.round(item.averageRating) }]"></i>
              </div>
              <span class="rating-text">{{ item.averageRating.toFixed(1) }}</span>
            </div>
            <div class="food-footer">
              <span class="food-price">₹{{ item.price }}</span>
              <div class="food-actions">
                <div v-if="getCartItem(item.id)" class="quantity-controls">
                  <button class="btn-qty" @click="decreaseQuantity(item.id)">-</button>
                  <span class="quantity">{{ getCartItem(item.id).quantity }}</span>
                  <button class="btn-qty" @click="increaseQuantity(item.id)">+</button>
                </div>
                <button v-else class="btn-add" @click="addToCart(item)">
                  <i class="fas fa-plus"></i>
                  Add
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Cart -->
      <div v-if="cart.length > 0" class="cart">
        <h4>Your Cart</h4>
        <div v-for="cartItem in cart" :key="cartItem.menuItemId" class="cart-item">
          <span>{{ cartItem.name }} x{{ cartItem.quantity }}</span>
          <span>${{ (cartItem.price * cartItem.quantity).toFixed(2) }}</span>
          <button class="btn btn-danger" @click="removeFromCart(cartItem.menuItemId)">Remove</button>
        </div>
        <div class="cart-total">
          <strong>Total: ${{ cartTotal.toFixed(2) }}</strong>
        </div>
        
        <!-- Delivery Address -->
        <div class="address-section">
          <label for="deliveryAddress">Delivery Address:</label>
          <textarea 
            id="deliveryAddress"
            v-model="deliveryAddress" 
            placeholder="Enter your complete delivery address..."
            required
          ></textarea>
        </div>
        
        <button class="btn btn-primary" @click="placeOrder" :disabled="!deliveryAddress.trim()">
          Place Order
        </button>
      </div>
    </div>
    
    <!-- Active Orders -->
    <div class="card">
      <h3>Active Orders</h3>
      <div v-if="currentOrders.length === 0" class="no-orders">
        <i class="fas fa-shopping-cart"></i>
        <h4>No Active Orders</h4>
        <p>Your active orders will appear here</p>
      </div>
      <div class="orders-list">
        <div v-for="order in currentOrders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="order-info">
              <h4>Order #{{ order.id }}</h4>
              <span class="order-date">{{ formatDate(order.createdAt) }}</span>
            </div>
            <div class="order-details">
              <div class="restaurant-info">
                <p><i class="fas fa-store"></i> {{ order.restaurant?.name || 'Unknown' }}</p>
                <span :class="['status-badge', order.status.toLowerCase()]">{{ getStatusText(order.status) }}</span>
              </div>
              <div class="order-total">₹{{ order.totalAmount }}</div>
            </div>
          </div>
          
          <!-- Order Status Pipeline -->
          <div class="status-pipeline">
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
                <h6><i class="fas fa-map-marker-alt"></i> Delivery Information</h6>
                <p><strong>Restaurant:</strong> {{ order.restaurant?.name || 'Unknown' }}</p>
                <p><strong>Order Date:</strong> {{ formatDate(order.createdAt) }}</p>
                <p v-if="order.deliveryAddress"><strong>Address:</strong> {{ order.deliveryAddress }}</p>
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
            <button v-if="order.status === 'PLACED'" class="cancel-btn" @click="cancelOrder(order.id)">
              <i class="fas fa-times"></i>
              Cancel Order
            </button>
            <button v-if="order.status === 'OUT_FOR_DELIVERY'" class="delivered-btn" @click="markAsDelivered(order.id)">
              <i class="fas fa-check"></i>
              Order Received
            </button>
            <button v-if="order.status === 'DELIVERED'" class="invoice-btn" @click="generateInvoice(order)">
              <i class="fas fa-file-pdf"></i>
              Download Invoice
            </button>
            <div v-if="order.status === 'PREPARING'" class="status-info">
              <i class="fas fa-clock"></i>
              Your order is being prepared...
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Review Modal -->
    <div v-if="showReviewModal" class="modal-overlay" @click="closeReviewModal">
      <div class="review-modal" @click.stop>
        <h3><i class="fas fa-star"></i> Rate Your Order</h3>
        <div class="stars">
          <i v-for="n in 5" :key="n" 
             :class="['fas fa-star', { active: n <= rating }]" 
             @click="rating = n"></i>
        </div>
        <textarea v-model="review" placeholder="Write your review..."></textarea>
        <div class="modal-actions">
          <button class="btn btn-primary" @click="submitReview">
            <i class="fas fa-paper-plane"></i>
            Submit Review
          </button>
          <button class="btn btn-secondary" @click="closeReviewModal">
            Skip
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
      restaurants: [],
      selectedRestaurant: null,
      menuItems: [],
      cart: [],
      orders: [],
      deliveryAddress: '',
      showReviewModal: false,
      currentOrderId: null,
      rating: 0,
      review: '',
      error: '',
      success: ''
    }
  },
  computed: {
    cartTotal() {
      return this.cart.reduce((total, item) => total + (item.price * item.quantity), 0)
    },
    currentOrders() {
      return this.orders.filter(order => 
        order.status !== 'DELIVERED' && order.status !== 'CANCELLED'
      )
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
        
        // Load reviews for each restaurant
        for (let restaurant of this.restaurants) {
          await this.loadRestaurantReviews(restaurant)
        }
      } catch (error) {
        this.error = 'Failed to load restaurants'
      }
    },
    
    async loadRestaurantReviews(restaurant) {
      try {
        const response = await axios.get(`/api/menu-items/reviews/restaurant/${restaurant.id}`)
        const reviews = response.data
        if (reviews.length > 0) {
          const totalRating = reviews.reduce((sum, review) => sum + review.rating, 0)
          restaurant.averageRating = totalRating / reviews.length
          restaurant.reviewCount = reviews.length
        }
      } catch (error) {
        // Ignore review loading errors
      }
    },
    
    async viewMenu(restaurant) {
      try {
        this.selectedRestaurant = restaurant
        this.cart = []
        const response = await axios.get(`/api/menu-items/restaurant/${restaurant.id}`)
        this.menuItems = response.data
        
        // Load ratings for each menu item
        for (let item of this.menuItems) {
          await this.loadItemReviews(item)
        }
      } catch (error) {
        this.error = 'Failed to load menu'
      }
    },
    
    async loadItemReviews(item) {
      try {
        const response = await axios.get(`/api/menu-items/reviews/item/${item.id}`)
        const reviews = response.data
        if (reviews.length > 0) {
          const totalRating = reviews.reduce((sum, review) => sum + review.rating, 0)
          item.averageRating = totalRating / reviews.length
          item.reviewCount = reviews.length
        }
      } catch (error) {
        // Ignore review loading errors
      }
    },
    
    addToCart(menuItem) {
      const existingItem = this.cart.find(item => item.menuItemId === menuItem.id)
      if (existingItem) {
        existingItem.quantity++
      } else {
        this.cart.push({
          menuItemId: menuItem.id,
          name: menuItem.name,
          price: menuItem.price,
          quantity: 1
        })
      }
    },
    
    removeFromCart(menuItemId) {
      this.cart = this.cart.filter(item => item.menuItemId !== menuItemId)
    },
    
    getCartItem(menuItemId) {
      return this.cart.find(item => item.menuItemId === menuItemId)
    },
    
    increaseQuantity(menuItemId) {
      const item = this.getCartItem(menuItemId)
      if (item) item.quantity++
    },
    
    decreaseQuantity(menuItemId) {
      const item = this.getCartItem(menuItemId)
      if (item && item.quantity > 1) {
        item.quantity--
      } else if (item && item.quantity === 1) {
        this.removeFromCart(menuItemId)
      }
    },
    
    async placeOrder() {
      try {
        this.error = ''
        this.success = ''
        
        if (this.cart.length === 0) {
          this.error = 'Cart is empty'
          return
        }
        
        if (!this.deliveryAddress.trim()) {
          this.error = 'Please enter delivery address'
          return
        }
        
        const orderData = {
          items: this.cart.map(item => ({
            menuItemId: item.menuItemId,
            quantity: item.quantity
          })),
          deliveryAddress: this.deliveryAddress.trim()
        }
        
        await axios.post(`/api/orders/${this.id}/${this.selectedRestaurant.id}`, orderData)
        
        this.success = `Order placed successfully! Total: $${this.cartTotal.toFixed(2)}`
        this.cart = []
        this.deliveryAddress = ''
        this.selectedRestaurant = null
        this.menuItems = []
        await this.loadOrders()
        
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to place order'
      }
    },
    
    async loadOrders() {
      try {
        console.log('Loading orders for user ID:', this.id)
        const response = await axios.get(`/api/orders/user/${this.id}`)
        this.orders = response.data
        console.log('Loaded orders:', this.orders)
      } catch (error) {
        this.error = 'Failed to load orders'
      }
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
    
    async cancelOrder(orderId) {
      try {
        const userId = parseInt(this.id)
        console.log('Cancelling order:', { orderId, userId })
        await axios.put(`/api/orders/${orderId}/status?status=CANCELLED&updaterId=${userId}`)
        this.success = 'Order cancelled successfully'
        await this.loadOrders()
      } catch (error) {
        console.error('Error cancelling order:', error.response?.data)
        this.error = error.response?.data?.message || 'Failed to cancel order'
      }
    },
    
    async markAsDelivered(orderId) {
      try {
        const userId = parseInt(this.id)
        console.log('Marking order as delivered:', { orderId, userId })
        await axios.put(`/api/orders/${orderId}/status?status=DELIVERED&updaterId=${userId}`)
        this.currentOrderId = orderId
        this.showReviewModal = true
        await this.loadOrders()
      } catch (error) {
        console.error('Error marking as delivered:', error.response?.data)
        this.error = error.response?.data?.message || 'Failed to mark as delivered'
      }
    },
    
    async submitReview() {
      try {
        if (!this.rating) {
          this.error = 'Please select a rating'
          return
        }
        
        const formData = new FormData()
        formData.append('rating', this.rating)
        formData.append('comment', this.review || '')
        
        await axios.post(`/api/menu-items/review/${this.currentOrderId}/user/${this.id}`, formData)
        
        this.success = 'Thank you for your review!'
        this.closeReviewModal()
      } catch (error) {
        this.error = error.response?.data?.message || 'Failed to submit review'
      }
    },
    
    closeReviewModal() {
      this.showReviewModal = false
      this.currentOrderId = null
      this.rating = 0
      this.review = ''
    },
    
    generateInvoice(order) {
      // Simple invoice generation - in real app would be more sophisticated
      const invoiceContent = `
INVOICE
=======
Order #${order.id}
Date: ${this.formatDate(order.createdAt)}
Restaurant: ${order.restaurant?.name || 'Unknown'}

ITEMS:
${order.items.map(item => 
  `${item.itemName || item.menuItem?.name} x${item.quantity} - ₹${(item.price || item.priceAtOrder) * item.quantity}`
).join('\n')}

TOTAL: ₹${order.totalAmount}

Thank you for your order!
      `
      
      const blob = new Blob([invoiceContent], { type: 'text/plain' })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `invoice-${order.id}.txt`
      a.click()
      window.URL.revokeObjectURL(url)
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
    
    handleImageError(event) {
      event.target.src = 'https://via.placeholder.com/300x200/f0f0f0/666?text=No+Image'
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

.section h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.restaurants-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.restaurant-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid #f0f0f0;
}

.restaurant-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0,0,0,0.12);
}

.restaurant-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.restaurant-header h3 {
  font-size: 20px;
  color: #333;
  margin: 0;
}

.cuisine-badge {
  background: linear-gradient(135deg, #ff6b35, #f7931e);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.restaurant-address {
  color: #666;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.order-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #ff6b35, #f7931e);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.order-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 107, 53, 0.3);
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.food-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid #f0f0f0;
}

.food-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0,0,0,0.15);
}

.food-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.food-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.food-card:hover .food-image img {
  transform: scale(1.05);
}

.food-type {
  position: absolute;
  top: 12px;
  left: 12px;
  width: 20px;
  height: 20px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 8px;
  font-weight: bold;
}

.food-type.veg {
  background: white;
  color: #4CAF50;
  border: 2px solid #4CAF50;
}

.food-type.non_veg {
  background: white;
  color: #f44336;
  border: 2px solid #f44336;
}

.food-content {
  padding: 16px;
}

.food-content h5 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  line-height: 1.3;
}

.food-description {
  color: #666;
  font-size: 14px;
  line-height: 1.4;
  margin: 0 0 16px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.food-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.food-price {
  font-size: 20px;
  font-weight: 700;
  color: #ff6b35;
}

.food-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-add {
  background: linear-gradient(135deg, #ff6b35, #f7931e);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.btn-add:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 107, 53, 0.3);
}

.restaurant-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.restaurant-rating .stars {
  display: flex;
  gap: 2px;
}

.restaurant-rating .stars i {
  font-size: 14px;
  color: #ddd;
}

.restaurant-rating .stars i.active {
  color: #ffc107;
}

.rating-text {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.item-rating {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}

.item-rating .stars {
  display: flex;
  gap: 1px;
}

.item-rating .stars i {
  font-size: 12px;
  color: #ddd;
}

.item-rating .stars i.active {
  color: #ffc107;
}

.item-rating .rating-text {
  font-size: 12px;
  color: #666;
  font-weight: 600;
}

.cart {
  margin-top: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #fff5f0, #fff8f5);
  border-radius: 16px;
  border: 2px solid #ff6b35;
}

.cart h4 {
  color: #ff6b35;
  margin-bottom: 16px;
  font-size: 20px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.cart-total {
  margin: 20px 0;
  font-size: 20px;
  color: #ff6b35;
  text-align: center;
}

.order-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin: 16px 0;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  border: 1px solid #f0f0f0;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-header h4 {
  color: #333;
  margin: 0;
}

.order-total {
  font-weight: 600;
  color: #ff6b35;
  font-size: 16px;
}

.order-item {
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
  color: #666;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f8f9fa;
  border-radius: 20px;
  padding: 4px;
}

.btn-qty {
  width: 28px;
  height: 28px;
  border: none;
  background: #ff6b35;
  color: white;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-qty:hover {
  background: #e55a2b;
  transform: scale(1.1);
}

.quantity {
  font-weight: 600;
  min-width: 20px;
  text-align: center;
  color: #333;
  font-size: 14px;
  padding: 0 8px;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-id {
  font-weight: 600;
  color: #333;
  font-size: 18px;
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

.order-items-list {
  margin: 12px 0;
}

.order-item-detail {
  padding: 4px 0;
  color: #666;
  font-size: 14px;
}

.order-actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
}

.cancel-btn {
  padding: 8px 16px;
  background: linear-gradient(135deg, #f44336, #d32f2f);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.delivered-btn {
  padding: 8px 16px;
  background: linear-gradient(135deg, #4CAF50, #45a049);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.cancel-btn:hover, .delivered-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.review-modal {
  background: white;
  border-radius: 16px;
  padding: 32px;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}

.review-modal h3 {
  text-align: center;
  margin-bottom: 24px;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.stars {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 20px;
}

.stars i {
  font-size: 24px;
  color: #ddd;
  cursor: pointer;
  transition: color 0.3s ease;
}

.stars i.active {
  color: #ffc107;
}

.review-modal textarea {
  width: 100%;
  min-height: 80px;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-family: 'Poppins', sans-serif;
  resize: vertical;
  margin-bottom: 20px;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.btn-secondary {
  background: #6c757d;
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #fff3e0, #ffe0b2);
  color: #f57c00;
  border-radius: 8px;
  font-weight: 500;
  border: 2px solid #ffb74d;
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

.bill-container {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  margin-top: 16px;
  overflow: hidden;
}

.bill-header {
  background: linear-gradient(135deg, #ff6b35, #f7931e);
  color: white;
  padding: 12px 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.bill-items {
  padding: 16px;
}

.bill-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.bill-item:last-child {
  border-bottom: none;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-name {
  font-weight: 500;
  color: #333;
}

.item-qty {
  color: #666;
  font-size: 14px;
}

.item-price {
  font-weight: 600;
  color: #ff6b35;
}

.bill-total {
  background: #f8f9fa;
  padding: 16px;
  border-top: 2px solid #ff6b35;
}

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
}

.total-amount {
  color: #ff6b35;
  font-size: 20px;
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

.order-count {
  background: #ff6b35;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  margin-left: 4px;
  min-width: 20px;
  text-align: center;
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
  gap: 8px;
  font-weight: 600;
}

.order-items-table {
  margin: 0;
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
  padding: 16px 20px;
  background: #f8f9fa;
  border-top: 2px solid #2196F3;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #333;
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

.order-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-details p {
  margin: 4px 0;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;
}

.restaurant-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.restaurant-info p {
  margin: 0;
  color: #333;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.order-total {
  color: #ff6b35;
  font-weight: 700;
  font-size: 18px;
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
  background: linear-gradient(135deg, #ff6b35, #f7931e);
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

.invoice-btn {
  padding: 8px 16px;
  background: linear-gradient(135deg, #2196F3, #1976D2);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.invoice-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.3);
}

.address-section {
  margin: 20px 0;
}

.address-section label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
}

.address-section textarea {
  width: 100%;
  min-height: 80px;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-family: 'Poppins', sans-serif;
  resize: vertical;
  transition: border-color 0.3s ease;
}

.address-section textarea:focus {
  outline: none;
  border-color: #ff6b35;
  box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}
</style>