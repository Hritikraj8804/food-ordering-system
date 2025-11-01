# Food Ordering System - Frontend

A minimal Vue.js 3 frontend for the Spring Boot food ordering system.

## 🚀 Quick Start

### Prerequisites
- Node.js 16+
- Backend running on http://localhost:8080

### Installation & Run
```bash
cd frontend
npm install
npm run dev
```

Frontend will be available at: http://localhost:3000

## 🎯 Features

### User (Customer) Features
- Register/Login with USER role
- View all restaurants
- Place orders with multiple items
- View order history

### Hotel (Restaurant Owner) Features  
- Register/Login with HOTEL role
- Create new restaurants
- View owned restaurants
- Update order status (PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED)

## 📱 Usage Flow

1. **Registration**: Choose role (USER/HOTEL) and register
2. **USER Flow**: Browse restaurants → Select restaurant → Add items → Place order
3. **HOTEL Flow**: Create restaurant → Manage incoming orders → Update order status

## 🔧 API Integration

The frontend integrates with these Spring Boot endpoints:
- `POST /api/users/register` - User registration
- `GET /api/restaurants` - List restaurants  
- `POST /api/restaurants/{hotelOwnerId}` - Create restaurant (HOTEL only)
- `POST /api/orders/{userId}/{restaurantId}` - Place order (USER only)
- `GET /api/orders/user/{userId}` - Get user orders
- `PUT /api/orders/{orderId}/status` - Update order status (HOTEL only)

## 🏗️ Architecture

```
frontend/
├── src/
│   ├── views/
│   │   ├── Login.vue          # Registration/Login
│   │   ├── UserDashboard.vue  # Customer interface
│   │   └── HotelDashboard.vue # Restaurant owner interface
│   ├── App.vue                # Main app component
│   └── main.js                # Vue app setup
├── package.json
└── vite.config.js             # Proxy to backend
```

## 🔒 Role-Based Access

- **USER**: Can only place orders, view restaurants, see order history
- **HOTEL**: Can only create restaurants, manage order status
- Role validation handled by Spring Boot backend (403 Forbidden for unauthorized actions)

Built with Vue 3 + Vite + Axios