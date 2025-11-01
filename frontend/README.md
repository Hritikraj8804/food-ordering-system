# 🍕 Food Ordering System - Frontend

A modern **Vue.js 3** frontend with responsive design for the Spring Boot food ordering system. Features role-based dashboards, real-time order tracking, and comprehensive restaurant management.

## 🚀 Quick Start

### Prerequisites
- Node.js 16+
- npm 8+
- Backend running on http://localhost:8080

### Installation & Run
```bash
cd frontend
npm install
npm run dev

# Or use the provided script
./start-frontend.bat
```

### Access Points
- **Frontend UI**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **Swagger Docs**: http://localhost:8080/swagger-ui.html

## 🎯 Features

### 👤 User (Customer) Features
- ✅ **Registration/Login** with USER role
- ✅ **Restaurant Discovery** - Browse all available restaurants
- ✅ **Menu Browsing** - View restaurant menus with images
- ✅ **Order Placement** - Add multiple items to cart and place orders
- ✅ **Order Tracking** - Real-time order status updates
- ✅ **Order History** - Complete order history with details
- ✅ **Review System** - Rate and review menu items
- ✅ **Responsive Design** - Works on desktop and mobile

### 🏨 Hotel (Restaurant Owner) Features
- ✅ **Registration/Login** with HOTEL role
- ✅ **Restaurant Management** - Create and manage restaurants
- ✅ **Menu Management** - Add menu items with image uploads
- ✅ **Order Management** - View and manage incoming orders
- ✅ **Status Updates** - Update order status (PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED)
- ✅ **Order History** - Complete order tracking and history
- ✅ **Review Management** - View customer reviews and ratings
- ✅ **Dashboard Analytics** - Order statistics and insights

## 📱 Usage Flow

### 🔐 Getting Started
1. **Registration**: Choose role (USER/HOTEL) and register
2. **Login**: Access role-specific dashboard

### 👤 Customer Journey (USER)
1. **Browse Restaurants** - View all available restaurants
2. **Select Restaurant** - Choose restaurant and view menu
3. **Add to Cart** - Select menu items and quantities
4. **Place Order** - Review cart and place order
5. **Track Order** - Monitor order status in real-time
6. **Rate & Review** - Provide feedback after delivery

### 🏨 Restaurant Owner Journey (HOTEL)
1. **Create Restaurant** - Set up restaurant profile
2. **Add Menu Items** - Upload menu with images and prices
3. **Manage Orders** - View incoming orders and update status
4. **Track Performance** - Monitor reviews and order statistics
5. **Order History** - Access complete order management history

## 🔧 API Integration

The frontend integrates with these Spring Boot endpoints:

### **User Management**
- `POST /api/users/register` - User registration
- `GET /api/users/{id}` - Get user details

### **Restaurant Management**
- `GET /api/restaurants` - List all restaurants
- `POST /api/restaurants/{hotelOwnerId}` - Create restaurant (HOTEL only)
- `GET /api/restaurants/{id}` - Get restaurant details

### **Menu Management**
- `POST /api/menu-items/{restaurantId}` - Add menu item (HOTEL only)
- `GET /api/menu-items/restaurant/{restaurantId}` - Get restaurant menu
- `POST /api/menu-items/{itemId}/upload-image` - Upload menu item image

### **Order Management**
- `POST /api/orders/{userId}/{restaurantId}` - Place order (USER only)
- `GET /api/orders/user/{userId}` - Get user orders
- `GET /api/orders/restaurant/{restaurantId}` - Get restaurant orders (HOTEL only)
- `PUT /api/orders/{orderId}/status` - Update order status (HOTEL only)

### **Review System**
- `POST /api/menu-items/{itemId}/reviews` - Add review (USER only)
- `GET /api/menu-items/reviews/restaurant/{restaurantId}` - Get restaurant reviews

## 🏗️ Architecture

### **Project Structure**
```
frontend/
├── src/
│   ├── views/
│   │   ├── Login.vue              # User registration/login
│   │   ├── UserDashboard.vue      # Customer main interface
│   │   ├── HotelDashboard.vue     # Restaurant owner interface
│   │   ├── UserOrderHistory.vue   # Customer order history
│   │   ├── OrderHistory.vue       # Hotel order management
│   │   └── ReviewsPage.vue        # Review management system
│   ├── App.vue                    # Main application component
│   └── main.js                    # Vue app initialization
├── public/                        # Static assets
├── package.json                   # Dependencies and scripts
├── vite.config.js                 # Development server config
├── start-frontend.bat             # Windows startup script
└── README.md                      # This file
```

### **Component Architecture**
```
App.vue
├── Login.vue (Authentication)
├── UserDashboard.vue (Customer Interface)
│   ├── Restaurant List
│   ├── Menu Display
│   ├── Cart Management
│   └── Order Placement
├── HotelDashboard.vue (Restaurant Owner Interface)
│   ├── Restaurant Creation
│   ├── Menu Management
│   ├── Order Management
│   └── Status Updates
├── UserOrderHistory.vue (Customer Orders)
├── OrderHistory.vue (Hotel Orders)
└── ReviewsPage.vue (Review System)
```

## 🔒 Role-Based Access Control

### **USER Role Permissions**
- ✅ Browse restaurants and menus
- ✅ Place orders with multiple items
- ✅ Track order status in real-time
- ✅ View complete order history
- ✅ Rate and review menu items
- ❌ Cannot create restaurants
- ❌ Cannot manage other users' orders

### **HOTEL Role Permissions**
- ✅ Create and manage restaurants
- ✅ Add menu items with images
- ✅ View and manage incoming orders
- ✅ Update order status
- ✅ View customer reviews
- ✅ Access order analytics
- ❌ Cannot place orders
- ❌ Cannot access other hotels' data

### **Security Implementation**
- Role validation handled by Spring Boot backend
- 403 Forbidden responses for unauthorized actions
- Frontend UI adapts based on user role
- Protected routes and conditional rendering

## 🛠️ Tech Stack

- **Framework**: Vue.js 3 with Composition API
- **Build Tool**: Vite (fast development server)
- **HTTP Client**: Axios for API communication
- **Routing**: Vue Router 4
- **Styling**: Modern CSS3 with Flexbox/Grid
- **Icons**: Font Awesome
- **Development**: Hot Module Replacement (HMR)

## 📦 Dependencies

### **Production Dependencies**
```json
{
  "vue": "^3.4.0",
  "vue-router": "^4.2.0",
  "axios": "^1.6.0"
}
```

### **Development Dependencies**
```json
{
  "@vitejs/plugin-vue": "^4.5.0",
  "vite": "^5.0.0"
}
```

## 🎨 UI/UX Features

- **Responsive Design** - Works seamlessly on desktop and mobile
- **Modern Interface** - Clean, intuitive user experience
- **Real-time Updates** - Dynamic order status tracking
- **Interactive Elements** - Smooth animations and transitions
- **Role-based Navigation** - Different interfaces for different user types
- **Error Handling** - User-friendly error messages
- **Loading States** - Visual feedback during API calls

## 🚀 Development Scripts

```bash
# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run serve
```

## 🔧 Configuration

### **Vite Configuration (vite.config.js)**
```javascript
export default {
  server: {
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
}
```

### **Environment Setup**
- Backend must be running on port 8080
- Frontend runs on port 3000
- CORS configured in Spring Boot for cross-origin requests

---

**Built with ❤️ using Vue.js 3, Vite & Modern Web Technologies**