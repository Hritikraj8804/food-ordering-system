# 🍕 Food Ordering System - Frontend

A modern **Vue.js 3** frontend application with **JWT authentication**, **role-based routing**, and **responsive design** for the food ordering system.

## 🚀 Features

### **Authentication & Security**
- ✅ **JWT Token Management** - Secure authentication with automatic token handling
- ✅ **Route Guards** - Protected routes based on authentication status
- ✅ **Role-based Navigation** - Different dashboards for USER and HOTEL roles
- ✅ **Auto-logout** - Automatic logout on token expiration
- ✅ **Persistent Sessions** - Login state maintained across browser sessions

### **User Interface**
- ✅ **Responsive Design** - Works seamlessly on desktop and mobile
- ✅ **Modern UI Components** - Clean, intuitive interface design
- ✅ **Real-time Updates** - Dynamic order status tracking
- ✅ **Interactive Elements** - Smooth animations and transitions
- ✅ **Pagination** - 5 items per page for orders and reviews

### **Core Functionality**
- ✅ **User Registration/Login** - Secure authentication flow
- ✅ **Profile Management** - Update user information and manage addresses
- ✅ **Restaurant Browsing** - View restaurants with ratings and reviews
- ✅ **Menu Exploration** - Browse menu items with images and ratings
- ✅ **Order Placement** - Add items to cart and place orders with saved addresses
- ✅ **Order Tracking** - Real-time order status with visual pipeline
- ✅ **Review System** - Rate and review menu items
- ✅ **Address Management** - Save and manage multiple delivery addresses

## 🛠️ Tech Stack

- **Framework**: Vue.js 3 with Composition API
- **Build Tool**: Vite for fast development and building
- **HTTP Client**: Axios with JWT interceptors
- **Routing**: Vue Router 4 with authentication guards
- **Styling**: Modern CSS3 with responsive design
- **Icons**: Font Awesome for consistent iconography

## 📋 Prerequisites

- Node.js 16+
- npm 8+
- Backend API running on `http://localhost:8080`

## ⚡ Quick Start

### 1. Install Dependencies
```bash
npm install
```

### 2. Start Development Server
```bash
npm run dev
```

### 3. Build for Production
```bash
npm run build
```

### 4. Preview Production Build
```bash
npm run preview
```

## 🏗️ Project Structure

```
frontend/
├── src/
│   ├── views/                     # Page components
│   │   ├── Login.vue              # Authentication (Login/Register)
│   │   ├── UserDashboard.vue      # Customer dashboard with cart
│   │   ├── HotelDashboard.vue     # Restaurant owner dashboard
│   │   ├── UserOrderHistory.vue   # Customer order history (Paginated)
│   │   ├── OrderHistory.vue       # Hotel order management (Paginated)
│   │   ├── ReviewsPage.vue        # Review management (Paginated)
│   │   └── ProfilePage.vue        # Profile & address management
│   ├── App.vue                    # Root component with navigation
│   └── main.js                    # App initialization with router
├── public/                        # Static assets
├── package.json                   # Dependencies and scripts
├── vite.config.js                 # Vite configuration
└── README.md                      # This file
```

## 🔐 Authentication Flow

### **Login Process**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Login     │───▶│   Backend   │───▶│  JWT Token  │───▶│  Dashboard  │
│   Form      │    │  Validates  │    │  Generated  │    │  Redirect   │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

### **Token Management**
- **Storage**: JWT tokens stored in localStorage
- **Headers**: Automatic Authorization header injection
- **Expiration**: Auto-logout on token expiration (401 responses)
- **Cleanup**: Token removal on logout

### **Route Protection**
```javascript
// Protected routes require authentication
{
  path: '/user/:id',
  component: UserDashboard,
  meta: { requiresAuth: true, role: 'USER' }
}
```

## 🎨 UI Components

### **Dashboard Features**
- **User Dashboard**: Restaurant browsing, menu viewing, cart management
- **Hotel Dashboard**: Restaurant management, menu item creation, order tracking
- **Order History**: Paginated order list with status pipeline visualization
- **Profile Page**: User information and address management

### **Interactive Elements**
- **Order Status Pipeline**: Visual representation of order progress
- **Rating System**: Star-based rating with hover effects
- **Cart Management**: Add/remove items with quantity controls
- **Address Selection**: Dropdown for saved addresses during checkout

## 📱 Responsive Design

### **Breakpoints**
- **Desktop**: 1200px+ (Full layout with sidebars)
- **Tablet**: 768px-1199px (Adapted layout)
- **Mobile**: <768px (Stacked layout with mobile navigation)

### **Mobile Features**
- Touch-friendly buttons and controls
- Optimized form layouts
- Collapsible navigation menu
- Swipe-friendly order cards

## 🔄 State Management

### **Authentication State**
```javascript
// User data stored in localStorage
const user = {
  id: 1,
  email: "user@example.com",
  name: "John Doe",
  role: "USER"
}

// JWT token for API requests
const token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### **Route Guards**
```javascript
router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user'))
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && (!user || !token)) {
    next('/')  // Redirect to login
  } else {
    next()     // Allow navigation
  }
})
```

## 🌐 API Integration

### **Axios Configuration**
```javascript
// Automatic JWT token injection
axios.defaults.headers.common['Authorization'] = `Bearer ${token}`

// Response interceptor for token expiration
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Auto-logout on token expiration
      logout()
    }
    return Promise.reject(error)
  }
)
```

### **API Endpoints Used**
- **Authentication**: `/api/auth/login`, `/api/auth/register`
- **User Data**: `/api/users/{id}`, `/api/addresses/user/{userId}`
- **Restaurants**: `/api/restaurants`, `/api/menu-items/restaurant/{id}`
- **Orders**: `/api/orders/user/{userId}`, `/api/orders/{userId}/{restaurantId}`
- **Reviews**: `/api/menu-items/{itemId}/reviews`

## 🎯 Key Features by Role

### **USER Role (Customers)**
- Browse restaurants and menus
- Add items to cart with quantity selection
- Place orders with saved address selection
- Track order status with visual pipeline
- View order history with pagination
- Rate and review menu items
- Manage profile and delivery addresses

### **HOTEL Role (Restaurant Owners)**
- Create and manage restaurants
- Add menu items with image uploads
- View and manage incoming orders
- Update order status (PREPARING → OUT_FOR_DELIVERY → DELIVERED)
- View customer reviews with sentiment analysis
- Access paginated order history and reviews

## 🚀 Development

### **Available Scripts**
```bash
npm run dev      # Start development server
npm run build    # Build for production
npm run preview  # Preview production build
npm run lint     # Run ESLint (if configured)
```

### **Environment Variables**
```bash
VITE_API_BASE_URL=http://localhost:8080/api  # Backend API URL
```

### **Development Server**
- **URL**: `http://localhost:3000`
- **Hot Reload**: Automatic page refresh on file changes
- **Proxy**: API requests proxied to backend server

---

**Built with ❤️ using Vue.js 3, Vite, and modern web technologies**