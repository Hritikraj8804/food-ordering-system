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

    <!-- Restaurant Filter -->
    <div class="filter-section">
      <div class="restaurant-selector">
        <select v-model="selectedRestaurantId" @change="loadReviews" class="restaurant-filter">
          <option value="">All Restaurants</option>
          <option v-for="restaurant in myRestaurants" :key="restaurant.id" :value="restaurant.id">
            {{ restaurant.name }}
          </option>
        </select>
      </div>
    </div>

    <!-- Stats Summary -->
    <div v-if="reviews.length > 0" class="stats-section">
      <div class="stat-card">
        <i class="fas fa-star"></i>
        <div>
          <h3>{{ averageRating.toFixed(1) }}</h3>
          <p>Average Rating</p>
        </div>
      </div>
      <div class="stat-card">
        <i class="fas fa-comments"></i>
        <div>
          <h3>{{ reviews.length }}</h3>
          <p>Total Reviews</p>
        </div>
      </div>
      <div class="stat-card">
        <i class="fas fa-thumbs-up"></i>
        <div>
          <h3>{{ positiveReviews }}</h3>
          <p>Positive Reviews</p>
        </div>
      </div>
    </div>

    <!-- Reviews List -->
    <div class="reviews-container">
      <div v-if="reviews.length === 0" class="no-reviews">
        <i class="fas fa-comment-slash"></i>
        <i class="fas fa-star"></i>
        <h3>No Reviews Yet</h3>
        <p>Reviews will appear here once customers start rating your orders</p>
      </div>

      <div v-for="review in paginatedReviews" :key="review.id" class="review-card">
        <div class="review-header">
          <div class="customer-info">
            <i class="fas fa-user-circle"></i>
            <div>
              <h4>{{ review.customerName }}</h4>
              <span class="review-date">{{ formatDate(review.createdAt) }}</span>
            </div>
            <span class="customer-name">{{ review.user?.name || 'Anonymous' }}</span>
          </div>
          <div class="rating">
            <i v-for="n in 5" :key="n" :class="['fas fa-star', { filled: n <= review.rating }]"></i>
            <span class="rating-text">{{ review.rating }}/5</span>
          </div>
        </div>

        <div class="restaurant-info">
          <i class="fas fa-store"></i>
          <span>{{ review.restaurantName }}</span>
          <span class="order-id">Order #{{ review.orderId }}</span>
        </div>

        <div v-if="review.comment" class="review-comment">
          <p>"{{ review.comment }}"</p>
        </div>

        <div class="review-actions">
          <button class="reply-btn" @click="replyToReview(review.id)">
            <i class="fas fa-reply"></i>
            Reply
          </button>
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
import axios from 'axios';

export default {
  props: ['id'],
  data() {
    return {
      reviews: [],
      myRestaurants: [],
      selectedRestaurantId: '',
      currentPage: 1,
      reviewsPerPage: 5,
      error: '',
      success: ''
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.reviews.length / this.reviewsPerPage)
    },
    paginatedReviews() {
      const start = (this.currentPage - 1) * this.reviewsPerPage
      const end = start + this.reviewsPerPage
      return this.reviews.slice(start, end)
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
    },
    averageRating() {
      if (this.reviews.length === 0) return 0;
      const sum = this.reviews.reduce((acc, review) => acc + review.rating, 0);
      return sum / this.reviews.length;
    },
    positiveReviews() {
      return this.reviews.filter(review => review.rating >= 4).length;
    }
  },
  async mounted() {
    await this.loadMyRestaurants();
    await this.loadReviews();
  },
  methods: {
    async loadMyRestaurants() {
      try {
        const response = await axios.get('/api/restaurants');
        this.myRestaurants = response.data.filter(r => r.hotelOwnerId == this.id);
      } catch (error) {
        this.error = 'Failed to load restaurants';
      }
    },

    async loadReviews() {
      try {
        this.reviews = [];
        if (this.selectedRestaurantId) {
          const response = await axios.get(`/api/menu-items/reviews/restaurant/${this.selectedRestaurantId}`);
          this.reviews = response.data;
        } else {
          for (let restaurant of this.myRestaurants) {
            const response = await axios.get(`/api/menu-items/reviews/restaurant/${restaurant.id}`);
            this.reviews.push(...response.data);
          }
        }
        this.reviews.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
        this.currentPage = 1 // Reset to first page when loading reviews
      } catch (error) {
        this.error = 'Failed to load reviews';
      }
    },

    formatDate(dateString) {
      if (!dateString) return 'N/A';
      return new Date(dateString).toLocaleDateString('en-IN', {
        day: 'numeric',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },

    replyToReview(reviewId) {
      this.success = 'Reply feature coming soon!';
    }
  }
};
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

.page-header p {
  color: #666;
  font-size: 16px;
}

.filter-section {
  .restaurant-selector {
    margin-bottom: 32px;
    text-align: center;
  }

  .restaurant-filter {
    padding: 12px 16px;
  }
}

.restaurant-selector select {
  padding: 12px 20px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  min-width: 250px;
  font-family: 'Poppins', sans-serif;
  min-width: 300px;
  background: white;
}

.reviews-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 40px;
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
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
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
  font-size: 32px;
  color: #ff6b35;
  font-size: 24px;
  color: #666;
}

.customer-info h4 {
  margin: 0;
}

.customer-name {
  font-weight: 600;
  color: #333;
  font-size: 18px;
}

.review-date {
  color: #666;
  font-size: 14px;
}

.review-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rating {
  .stars {
    display: flex;
    align-items: center;
    gap: 4px; /* spacing between stars */
  }

  i {
    font-size: 20px; /* star size */
    color: #ddd; /* default (empty) star color */
    transition: color 0.3s ease;
  }

  i.active,
  i.filled {
    color: #ffc107; /* filled (yellow) star color */
  }

  .rating-text {
    margin-left: 8px;
    font-weight: 600;
    color: #333;
    font-size: 14px;
  }
}

.review-date {
      color: #666;
      font-size: 14px;
    }

    .restaurant-info {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 16px;
      color: #666;
      font-size: 14px;
    }

    .order-id {
      margin-left: auto;
      background: #f0f0f0;
      padding: 4px 8px;
      border-radius: 4px;
      font-size: 12px;
    }

    .review-comment {
      background: #f8f9fa;
      padding: 16px;
      border-radius: 8px;
      margin-bottom: 16px;
      border-left: 4px solid #ff6b35;
    }

    .review-comment p {
      margin: 0;
      font-style: italic;
      color: #333;
    }

    .review-actions {
      display: flex;
      justify-content: flex-end;
    }

    .reply-btn {
      padding: 8px 16px;
      background: linear-gradient(135deg, #ff6b35, #f7931e);
      color: white;
      border: none;
      border-radius: 6px;
      cursor: pointer;
      margin-bottom: 12px;
      font-weight: 500;
      display: flex;
      align-items: center;
      gap: 6px;
      transition: all 0.3s ease;
    }

    .reply-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(255, 107, 53, 0.3);
    }

    .stats-section {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 20px;
    }

    .order-info h4 {
      margin: 0 0 8px 0;
      color: #333;
    }

    .stat-card {
      background: white;
      border-radius: 16px;
      padding: 24px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      border: 1px solid #f0f0f0;
    }

    .order-items {
      display: flex;
      align-items: center;
      gap: 16px;
    }

    .stat-card i {
      font-size: 32px;
      color: #ff6b35;
      margin-bottom: 16px;
    }

    .stat-card h3 {
      margin: 0;
      font-size: 28px;
      color: #333;
    }

    .order-item {
      background: #f8f9fa;
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 14px;
      color: #666;
    }

    .stat-card p {
      font-size: 14px;
      color: #666;
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

    .stats-section {
      margin-bottom: 32px;
    }
  </style>