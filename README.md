# 🍕 Swiggy Clone - Food Ordering System

A full-stack **Spring Boot 3 + Vue.js 3** application demonstrating enterprise-grade development with **JWT authentication**, **role-based access control**, **AOP logging**, **transaction management**, and modern web UI.

## 🚀 Features

### **Core Functionality**
- ✅ **JWT Authentication** with secure login/logout
- ✅ **Password Encryption** using BCrypt
- ✅ **User Management** with role-based access (USER/HOTEL)
- ✅ **Restaurant Management** (HOTEL users only)
- ✅ **Menu Item Management** with image uploads
- ✅ **Order Processing** with atomic transactions
- ✅ **Order Status Tracking** (PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED)
- ✅ **Review & Rating System** for menu items
- ✅ **Profile & Address Management** with saved addresses
- ✅ **Order Pagination** (5 orders per page)
- ✅ **RESTful API** with proper HTTP status codes
- ✅ **Modern Vue.js Frontend** with responsive design

### **Advanced Spring Features**
- ✅ **JWT Security** with Spring Security integration
- ✅ **Aspect-Oriented Programming (AOP)** - Logs order placement
- ✅ **Transaction Management** - @Transactional for data consistency
- ✅ **Global Exception Handling** - Clean error responses
- ✅ **Bean Validation** - Input validation with @Valid
- ✅ **File Upload Handling** - Menu item images
- ✅ **CORS Configuration** - Cross-origin request handling

### **Security & Access Control**
- ✅ **JWT Token Authentication** - Stateless authentication
- ✅ **Password Encryption** - BCrypt hashing
- ✅ **Role-based restrictions** - USER can order, HOTEL can create restaurants
- ✅ **Route Protection** - Frontend guards for authenticated routes
- ✅ **403 Forbidden** responses for unauthorized actions
- ✅ **Resource ownership validation**

### **Frontend Features**
- ✅ **Responsive Design** - Works on desktop and mobile
- ✅ **Real-time Order Updates** - Dynamic status tracking
- ✅ **Interactive UI** - Modern Vue.js components
- ✅ **Role-based Navigation** - Different dashboards for USER/HOTEL
- ✅ **Order History** - Complete order tracking
- ✅ **Review Management** - Rate and review system

## 🛠️ Tech Stack

### **Backend**
- **Framework**: Java 17, Spring Boot 3.5.7
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven
- **Architecture**: Layered (Controller → Service → Repository)

### **Frontend**
- **Framework**: Vue.js 3 with Composition API
- **Build Tool**: Vite
- **HTTP Client**: Axios
- **Routing**: Vue Router 4
- **Styling**: CSS3 with modern features
- **Icons**: Font Awesome

### **Development Tools**
- **IDE**: Spring Tools Suite / VS Code
- **API Testing**: Postman Collection included
- **Version Control**: Git

## 📋 Prerequisites

### **Backend Requirements**
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### **Frontend Requirements**
- Node.js 16+
- npm 8+

## ⚡ Quick Start

### 1. Database Setup
```sql
CREATE DATABASE food_ordering_db;
```

### 2. Configure Database
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/food_ordering_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Start Backend
```bash
# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Using provided script
./start-application.bat
```

### 4. Start Frontend
```bash
cd frontend
npm install
npm run dev

# Or use the provided script
./frontend/start-frontend.bat
```

### 5. Access Application
- **Frontend UI**: `http://localhost:3000`
- **API Base URL**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

## 🧪 Testing

### **Postman Collection**
Import `Swiggy_Clone_API_Tests.postman_collection.json` for comprehensive testing.

### **Key Test Scenarios**
1. **User Registration** (USER & HOTEL roles)
2. **Restaurant Creation** (HOTEL only - 403 for USER)
3. **Order Placement** (USER only - 403 for HOTEL)
4. **AOP Logging Verification** (Console logs during order placement)

### **Expected AOP Logs**
```
==================================================
⚡ AOP INTERCEPTED: Order placement initiated.
⚡ User ID 1 is attempting to place an order.
⚡ Spring @Transactional has been activated.
==================================================
```

## 📚 API Endpoints

### **Authentication**
- `POST /api/auth/register` - Register new user with encrypted password
- `POST /api/auth/login` - Login with JWT token response
- `POST /api/auth/logout` - Logout endpoint

### **User Management**
- `GET /api/users/{id}` - Get user by ID (Protected)
- `PUT /api/users/{id}/profile` - Update user profile (Protected)

### **Address Management**
- `GET /api/addresses/user/{userId}` - Get user addresses (Protected)
- `POST /api/addresses/user/{userId}` - Save new address (Protected)
- `DELETE /api/addresses/{addressId}` - Delete address (Protected)

### **Restaurant Management**
- `POST /api/restaurants/{hotelOwnerId}` - Create restaurant (HOTEL only)
- `GET /api/restaurants` - List all restaurants
- `GET /api/restaurants/{id}` - Get restaurant details

### **Menu Item Management**
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

### **Full-Stack Architecture**
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Vue.js 3      │───▶│  Spring Boot 3  │───▶│    MySQL 8.0    │
│   Frontend      │    │    Backend      │    │    Database     │
│  (Port 3000)    │    │  (Port 8080)    │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### **Backend Layers**
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controllers   │───▶│    Services     │───▶│  Repositories   │
│  (REST Layer)   │    │ (Business Logic)│    │  (Data Access)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   DTOs/Models   │    │   AOP Aspects   │    │   JPA Entities  │
│                 │    │  (Logging)      │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### **Frontend Structure**
```
frontend/
├── src/
│   ├── views/
│   │   ├── Login.vue              # User registration/login
│   │   ├── UserDashboard.vue      # Customer interface
│   │   ├── HotelDashboard.vue     # Restaurant owner interface
│   │   ├── UserOrderHistory.vue   # Customer order history
│   │   ├── OrderHistory.vue       # Hotel order management
│   │   └── ReviewsPage.vue        # Review management
│   ├── App.vue                    # Main application
│   └── main.js                    # Vue app initialization
├── package.json
└── vite.config.js                 # Development server config
```

## 🔒 Security Model

| Role  | Can Create Restaurant | Can Place Order | Access Level |
|-------|----------------------|------------------|--------------|
| USER  | ❌ (403 Forbidden)    | ✅ Allowed       | Customer     |
| HOTEL | ✅ Allowed           | ❌ (403 Forbidden)| Restaurant Owner |

## 🎯 Key Learning Outcomes

This project demonstrates mastery of:

### **Backend Development**
- **Spring Framework** core concepts and advanced features
- **Enterprise architecture** patterns and best practices
- **RESTful API** design with proper HTTP semantics
- **Database integration** with JPA/Hibernate
- **Aspect-Oriented Programming** for cross-cutting concerns
- **Transaction management** for data consistency
- **Security implementation** through role-based access control
- **File upload handling** and static resource serving

### **Frontend Development**
- **Vue.js 3** with Composition API and modern JavaScript
- **Component-based architecture** and reusable UI components
- **State management** and reactive data binding
- **HTTP client integration** with Axios
- **Responsive design** and modern CSS techniques
- **Single Page Application (SPA)** development

### **Full-Stack Integration**
- **API consumption** from frontend to backend
- **CORS configuration** for cross-origin requests
- **Role-based UI rendering** based on user permissions
- **Real-time data updates** and user experience optimization

## 📄 License

This project is for educational purposes and demonstrates Spring Boot development best practices.

## 📁 Project Structure

```
food-ordering-system/
├── src/                           # Spring Boot backend
│   ├── main/java/com/foodapp/
│   │   ├── controller/            # REST controllers
│   │   ├── service/               # Business logic
│   │   ├── repository/            # Data access layer
│   │   ├── entity/                # JPA entities
│   │   ├── dto/                   # Data transfer objects
│   │   ├── aspect/                # AOP aspects
│   │   └── exception/             # Exception handling
│   └── main/resources/
│       └── application.properties # Database configuration
├── frontend/                      # Vue.js frontend
│   ├── src/
│   │   ├── views/                 # Vue components/pages
│   │   ├── App.vue                # Main app component
│   │   └── main.js                # Vue app setup
│   ├── package.json               # Frontend dependencies
│   └── vite.config.js             # Build configuration
├── uploads/menu-images/           # Uploaded menu item images
├── pom.xml                        # Maven dependencies
├── start-application.bat          # Backend startup script
└── Swiggy_Clone_API_Tests.postman_collection.json
```

---

**Built with ❤️ using Spring Boot 3, Vue.js 3 & Java 17**