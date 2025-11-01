# 🍕 Swiggy Clone - Food Ordering System

A comprehensive **Spring Boot 3** application demonstrating enterprise-grade development with **role-based access control**, **AOP logging**, and **transaction management**.

## 🚀 Features

### **Core Functionality**
- ✅ **User Management** with role-based access (USER/HOTEL)
- ✅ **Restaurant Management** (HOTEL users only)
- ✅ **Order Processing** with atomic transactions
- ✅ **RESTful API** with proper HTTP status codes

### **Advanced Spring Features**
- ✅ **Aspect-Oriented Programming (AOP)** - Logs order placement
- ✅ **Transaction Management** - @Transactional for data consistency
- ✅ **Global Exception Handling** - Clean error responses
- ✅ **Bean Validation** - Input validation with @Valid

### **Security & Access Control**
- ✅ **Role-based restrictions** - USER can order, HOTEL can create restaurants
- ✅ **403 Forbidden** responses for unauthorized actions
- ✅ **Resource ownership validation**

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot 3.5.7
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven
- **Architecture**: Layered (Controller → Service → Repository)

## 📋 Prerequisites

- Java 17+
- MySQL 8.0+
- Maven 3.6+

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

### 3. Run Application
```bash
# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Using provided script
./start-application.bat
```

### 4. Access Application
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

### **User Management**
- `POST /api/users/register` - Register new user
- `GET /api/users/{id}` - Get user by ID

### **Restaurant Management**
- `POST /api/restaurants/{hotelOwnerId}` - Create restaurant (HOTEL only)
- `GET /api/restaurants` - List all restaurants

### **Order Management**
- `POST /api/orders/{userId}/{restaurantId}` - Place order (USER only)
- `GET /api/orders/user/{userId}` - Get user orders
- `PUT /api/orders/{orderId}/status` - Update order status (HOTEL only)

## 🏗️ Architecture

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

## 🔒 Security Model

| Role  | Can Create Restaurant | Can Place Order | Access Level |
|-------|----------------------|------------------|--------------|
| USER  | ❌ (403 Forbidden)    | ✅ Allowed       | Customer     |
| HOTEL | ✅ Allowed           | ❌ (403 Forbidden)| Restaurant Owner |

## 🎯 Key Learning Outcomes

This project demonstrates mastery of:
- **Spring Framework** core concepts and advanced features
- **Enterprise architecture** patterns and best practices
- **RESTful API** design with proper HTTP semantics
- **Database integration** with JPA/Hibernate
- **Aspect-Oriented Programming** for cross-cutting concerns
- **Transaction management** for data consistency
- **Security implementation** through role-based access control

## 📄 License

This project is for educational purposes and demonstrates Spring Boot development best practices.

---

**Built with ❤️ using Spring Boot 3 & Java 17**