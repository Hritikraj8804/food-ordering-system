<template>
  <div class="reviews-page">
    <div class="page-header">
      <button class="back-btn" @click="$router.go(-1)">
        <i class="fas fa-arrow-left"></i>
        Back
      </button>
      <h1><i class="fas fa-star"></i> Customer Reviews</h1>
      <p>See what customers are saying about your restaurants</p>
    </div>

    <!-- Restaurant Selector -->
    <div class="restaurant-selector">
      <select v-model="selectedRestaurantId" @change="loadReviews">
        <option value="">All Restaurants</option>
        <option v-for="restaurant in myRestaurants" :key="restaurant.id" :value="restaurant.id">
          {{ restaurant.name }}
        </option>
      </select>
    </div>

    <!-- Reviews List -->
    <div class="reviews-container">
      <div v-if="reviews.length === 0" class="no-reviews">
        <i class="fas fa-star"></i>
        <h3>No Reviews Yet</h3>
        <p>Customer reviews will appear here once orders are delivered</p>
      </div>
      
      <div v-for="review in reviews" :key="review.id" class="review-card">
        <div class="review-header">
          <div class="customer-info">
            <i class="fas fa-user-circle"></i>
            <span class="customer-name">{{ review.user?.name || 'Anonymous' }}</span>
          </div>
          <div class="review-meta">
            <div class="stars">
              <i v-for="n in 5" :key="n" 
                 :class="['fas fa-star', { active: n <= review.rating }]"></i>
            </div>
            <span class="review-date">{{ formatDate(review.createdAt) }}</span>
          </div>
        </div>
        
        <div class="review-content">
          <div class="restaurant-info" v-if="!selectedRestaurantId">
            <i class="fas fa-store"></i>
            <span>{{ review.restaurant?.name }}</span>
          </div>
          
          <div class="order-info">
            <h4>Order #{{ review.order?.id }}</h4>
            <div class="order-items">
              <span v-for="item in review.order?.items" :key="item.id" class="order-item">
                {{ item.itemName || item.menuItem?.name }} (×{{ item.quantity }})
              </span>
            </div>
          </div>
          
          <p class="review-comment" v-if="review.comment">
            "{{ review.comment }}"
          </p>
        </div>
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
      reviews: [],
      myRestaurants: [],
      selectedRestaurantId: '',
      error: '',
      success: ''
    }
  },
  async mounted() {
    await this.loadMyRestaurants()
    await this.loadReviews()
  },
  methods: {
    async loadMyRestaurants() {
      try {
        const response = await axios.get('/api/restaurants')
        this.myRestaurants = response.data.filter(r => r.hotelOwner?.id == this.id)
      } catch (error) {
        this.error = 'Failed to load restaurants'
      }
    },
    
    async loadReviews() {
      try {
        this.reviews = []
        if (this.selectedRestaurantId) {
          const response = await axios.get(`/api/menu-items/reviews/restaurant/${this.selectedRestaurantId}`)
          this.reviews = response.data
        } else {
          // Load reviews for all restaurants
          for (let restaurant of this.myRestaurants) {
            const response = await axios.get(`/api/menu-items/reviews/restaurant/${restaurant.id}`)
            this.reviews.push(...response.data)
          }
        }
        // Sort by date, newest first
        this.reviews.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
      } catch (error) {
        this.error = 'Failed to load reviews'
      }
    },
    
    formatDate(dateString) {
      if (!dateString) return 'N/A'
      return new Date(dateString).toLocaleDateString('en-IN', {
        day: 'numeric',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
.reviews-page {
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

.restaurant-selector {
  margin-bottom: 32px;
  text-align: center;
}

.restaurant-selector select {
  padding: 12px 20px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  min-width: 300px;
  background: white;
}

.reviews-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.no-reviews {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.no-reviews i {
  font-size: 48px;
  margin-bottom: 16px;
  color: #ddd;
}

.review-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  border: 1px solid #f0f0f0;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.customer-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.customer-info i {
  font-size: 24px;
  color: #666;
}

.customer-name {
  font-weight: 600;
  color: #333;
}

.review-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stars {
  display: flex;
  gap: 2px;
}

.stars i {
  font-size: 16px;
  color: #ddd;
}

.stars i.active {
  color: #ffc107;
}

.review-date {
  color: #666;
  font-size: 14px;
}

.restaurant-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  color: #ff6b35;
  font-weight: 500;
}

.order-info h4 {
  margin: 0 0 8px 0;
  color: #333;
}

.order-items {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.order-item {
  background: #f8f9fa;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
  color: #666;
}

.review-comment {
  font-style: italic;
  color: #555;
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  border-left: 4px solid #ff6b35;
  margin: 0;
}
</style>