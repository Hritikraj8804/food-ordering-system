<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1><i class="fas fa-user"></i> Customer Dashboard</h1>
      <p>Discover and order from amazing restaurants</p>
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
      <div v-for="item in menuItems" :key="item.id" class="menu-item">
        <div class="item-info">
          <h4>{{ item.name }} <span class="item-type" :class="item.type.toLowerCase()">{{ item.type }}</span></h4>
          <p>{{ item.description }}</p>
          <p class="price">${{ item.price }}</p>
        </div>
        <div class="item-actions">
          <div v-if="getCartItem(item.id)" class="quantity-controls">
            <button class="btn-qty" @click="decreaseQuantity(item.id)">-</button>
            <span class="quantity">{{ getCartItem(item.id).quantity }}</span>
            <button class="btn-qty" @click="increaseQuantity(item.id)">+</button>
            <button class="btn btn-danger" @click="removeFromCart(item.id)">Remove</button>
          </div>
          <button v-else class="btn btn-success" @click="addToCart(item)">Add to Cart</button>
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
        <button class="btn btn-primary" @click="placeOrder">Place Order</button>
      </div>
    </div>
    
    <!-- Order History -->
    <div class="section">
      <h2><i class="fas fa-history"></i> Your Orders</h2>
      <div class="orders-list">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <span class="order-id">Order #{{ order.id }}</span>
            <span :class="['status-badge', order.status.toLowerCase()]">{{ getStatusText(order.status) }}</span>
          </div>
          <div class="order-total">₹{{ order.totalAmount }}</div>
          <div class="order-items-list">
            <div v-for="item in order.items" :key="item.id" class="order-item-detail">
              {{ item.itemName || item.menuItem?.name || 'Item' }} × {{ item.quantity }} - ₹{{ item.price || item.priceAtOrder }}
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
    
    async viewMenu(restaurant) {
      try {
        this.selectedRestaurant = restaurant
        this.cart = []
        const response = await axios.get(`/api/menu-items/restaurant/${restaurant.id}`)
        this.menuItems = response.data
      } catch (error) {
        this.error = 'Failed to load menu'
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
        
        const orderData = {
          items: this.cart.map(item => ({
            menuItemId: item.menuItemId,
            quantity: item.quantity
          }))
        }
        
        await axios.post(`/api/orders/${this.id}/${this.selectedRestaurant.id}`, orderData)
        
        this.success = `Order placed successfully! Total: $${this.cartTotal.toFixed(2)}`
        this.cart = []
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
        // In a real app, you'd send this to a reviews endpoint
        // await axios.post(`/api/orders/${this.currentOrderId}/review`, {
        //   rating: this.rating,
        //   review: this.review
        // })
        this.success = 'Thank you for your review!'
        this.closeReviewModal()
      } catch (error) {
        this.error = 'Failed to submit review'
      }
    },
    
    closeReviewModal() {
      this.showReviewModal = false
      this.currentOrderId = null
      this.rating = 0
      this.review = ''
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

.menu-item {
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

.item-info h4 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 18px;
}

.item-type {
  font-size: 11px;
  padding: 3px 8px;
  border-radius: 10px;
  color: white;
  font-weight: 500;
  margin-left: 8px;
}

.item-type.veg {
  background: #4CAF50;
}

.item-type.non_veg {
  background: #f44336;
}

.price {
  font-weight: 600;
  color: #ff6b35;
  margin: 8px 0;
  font-size: 16px;
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
  gap: 12px;
}

.btn-qty {
  width: 32px;
  height: 32px;
  border: 2px solid #ff6b35;
  background: white;
  color: #ff6b35;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  transition: all 0.3s ease;
}

.btn-qty:hover {
  background: #ff6b35;
  color: white;
  transform: scale(1.1);
}

.quantity {
  font-weight: 600;
  min-width: 24px;
  text-align: center;
  color: #333;
  font-size: 16px;
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
</style>