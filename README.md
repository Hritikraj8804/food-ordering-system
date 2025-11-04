# 🍕 Food Ordering System - Enterprise Spring Boot Application




A comprehensive **Spring Boot 3 + Vue.js 3** food ordering platform demonstrating enterprise-grade development with **JWT authentication**, **role-based access control**, **AOP logging**, **transaction management**, and modern web architecture.

## 🚀 Features

### **Core Functionality**
- ✅ **JWT Authentication** with secure login/logout and password encryption
- ✅ **Role-Based Access Control** (USER/HOTEL) with resource ownership validation
- ✅ **Restaurant Management** - HOTEL users can create and manage restaurants
- ✅ **Menu Item Management** with image uploads and availability tracking
- ✅ **Order Processing** with atomic transactions and status tracking
- ✅ **Review & Rating System** for restaurants and menu items
- ✅ **Address Management** with multiple saved addresses per user
- ✅ **Real-time Logging** with structured enterprise-grade logging
- ✅ **RESTful API** with proper HTTP status codes and error handling

### **Advanced Spring Features**
- ✅ **Spring Security** with JWT token-based authentication
- ✅ **Aspect-Oriented Programming (AOP)** for cross-cutting concerns
- ✅ **Transaction Management** with @Transactional annotations
- ✅ **Global Exception Handling** with custom error responses
- ✅ **Bean Validation** with @Valid annotations
- ✅ **File Upload Handling** for menu item images
- ✅ **CORS Configuration** for cross-origin requests
- ✅ **Data Transfer Objects (DTOs)** to prevent lazy loading issues

### **Security Implementation**
- ✅ **BCrypt Password Encryption** for secure password storage
- ✅ **JWT Token Management** with configurable expiration
- ✅ **Role-based Route Protection** with 403 Forbidden responses
- ✅ **Resource Ownership Validation** - users can only access their own data
- ✅ **CORS Security** with origin whitelisting

## 🛠️ Tech Stack

### **Backend**
- **Framework**: Java 17, Spring Boot 3.3.5
- **Database**: MySQL 8.0 with JPA/Hibernate
- **Security**: Spring Security + JWT (jjwt 0.12.3)
- **Documentation**: SpringDoc OpenAPI 2.6.0 (Swagger)
- **Logging**: Logback with HTML output support
- **Build Tool**: Maven 3.6+
- **Architecture**: Layered (Controller → Service → Repository → Entity)

### **Frontend**
- **Framework**: Vue.js 3 with Composition API
- **Build Tool**: Vite for fast development
- **HTTP Client**: Axios for API communication
- **Routing**: Vue Router 4 with route guards
- **Styling**: Modern CSS3 with responsive design

### **Development Tools**
- **IDE**: Spring Tools Suite / IntelliJ IDEA / VS Code
- **API Testing**: Postman collection included
- **Database**: MySQL Workbench
- **Version Control**: Git

## 📋 Prerequisites

### **System Requirements**
- **Java**: JDK 17 or higher
- **Database**: MySQL 8.0+
- **Build Tool**: Maven 3.6+
- **Node.js**: 16+ (for frontend)
- **npm**: 8+ (for frontend)

### **Development Environment**
- **IDE**: Spring Tools Suite, IntelliJ IDEA, or VS Code
- **Database Client**: MySQL Workbench or similar
- **API Client**: Postman or similar

## ⚡ Quick Start Guide

### 1. Database Setup
```sql
-- Create database
CREATE DATABASE food_ordering_db;

-- Create user (optional)
CREATE USER 'foodapp'@'localhost' IDENTIFIED BY 'user123';
GRANT ALL PRIVILEGES ON food_ordering_db.* TO 'foodapp'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Backend Configuration
Update `src/main/resources/application.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/food_ordering_db
spring.datasource.username=root
spring.datasource.password=your_password

# JWT Configuration
jwt.secret=mySecretKey123456789012345678901234567890
jwt.expiration=86400000
```

### 3. Start Backend Application
```bash
# Clone the repository
git clone <repository-url>
cd food-ordering-system

# Install dependencies and run
mvn clean install
mvn spring-boot:run

# Alternative: Use IDE
# Import as Maven project and run FoodOrderingSystemApplication.java
```

### 4. Start Frontend (Optional)
```bash
cd frontend
npm install
npm run dev
```

### 5. Access Points
- **Backend API**: `http://localhost:8080/api`
- **Swagger Documentation**: `http://localhost:8080/swagger-ui/index.html`
- **Frontend UI**: `http://localhost:3000` (if running)
- **API Docs JSON**: `http://localhost:8080/v3/api-docs`

<img width="1547" height="841" alt="image" src="https://github.com/user-attachments/assets/b4cd00df-e3d7-415e-beb4-8526805e66e9" />

<img width="1362" height="898" alt="image" src="https://github.com/user-attachments/assets/36d74ac6-04ec-4432-ba52-d438bd6e8797" />

<img width="1192" height="858" alt="image" src="https://github.com/user-attachments/assets/4b9af2cf-1055-45ce-9389-4bc983923ac9" />

<img width="1367" height="796" alt="image" src="https://github.com/user-attachments/assets/7eeff789-61dc-4cf0-8c0c-4ad1876beb99" />


## 🧪 Testing & API Documentation

### **Create Test Users**
```bash
# Create test USER account
curl -X POST http://localhost:8080/api/auth/test-user

# Create test HOTEL account  
curl -X POST http://localhost:8080/api/auth/test-hotel

# Encrypt existing plain text passwords
curl -X POST http://localhost:8080/api/auth/encrypt-passwords
```

### **Test Credentials**
- **Customer**: `test@test.com` / `user123`
- **Hotel Owner**: `hotel@test.com` / `hotel123`
- **Existing Users**: Use original passwords (auto-encrypted on first run)

### **Sample API Calls**

#### Authentication
```bash
# Register new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","name":"John Doe","password":"user123","role":"USER"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"user123"}'
```

#### Restaurant Management
```bash
# Get all restaurants
curl -X GET http://localhost:8080/api/restaurants

# Create restaurant (HOTEL only)
curl -X POST http://localhost:8080/api/restaurants/2 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"name":"Pizza Palace","address":"123 Main St","cuisineType":"Italian"}'
```

#### Menu Items
```bash
# Get restaurant menu
curl -X GET http://localhost:8080/api/menu-items/restaurant/1

# Add menu item (HOTEL only)
curl -X POST http://localhost:8080/api/menu-items/restaurant/1/owner/2 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "name=Margherita Pizza" \
  -F "description=Classic tomato and mozzarella" \
  -F "price=12.99" \
  -F "type=VEG" \
  -F "image=@pizza.jpg"
```

## 📚 Complete API Reference

### **Authentication Endpoints**
| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/login` | User login | Public |
| POST | `/api/auth/test-user` | Create test USER | Public |
| POST | `/api/auth/test-hotel` | Create test HOTEL | Public |
| POST | `/api/auth/encrypt-passwords` | Encrypt existing passwords | Public |

### **Restaurant Management**
| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| GET | `/api/restaurants` | Get all restaurants | Public |
| POST | `/api/restaurants/{hotelOwnerId}` | Create restaurant | HOTEL only |
| GET | `/api/restaurants/{id}` | Get restaurant details | Public |

### **Menu Item Management**
| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| GET | `/api/menu-items/restaurant/{restaurantId}` | Get available menu items | Public |
| GET | `/api/menu-items/restaurant/{restaurantId}/all` | Get all menu items | Public |
| POST | `/api/menu-items/restaurant/{restaurantId}/owner/{ownerId}` | Add menu item | HOTEL only |
| PUT | `/api/menu-items/{itemId}/owner/{ownerId}` | Update menu item | HOTEL only |
| DELETE | `/api/menu-items/{itemId}/owner/{ownerId}` | Delete menu item | HOTEL only |
| GET | `/api/menu-items/image/{itemId}` | Get menu item image | Public |

### **Order Management**
| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| POST | `/api/orders/{userId}/{restaurantId}` | Place order | USER only |
| GET | `/api/orders/user/{userId}` | Get user orders | Protected |
| GET | `/api/orders/restaurant/{restaurantId}` | Get restaurant orders | HOTEL only |
| PUT | `/api/orders/{orderId}/status` | Update order status | HOTEL only |

### **Review System**
| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| POST | `/api/menu-items/review/{orderId}/user/{userId}` | Add review | USER only |
| GET | `/api/menu-items/reviews/restaurant/{restaurantId}` | Get restaurant reviews | Public |
| GET | `/api/menu-items/reviews/item/{itemId}` | Get item reviews | Public |

### **Address Management**
| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| GET | `/api/addresses/user/{userId}` | Get user addresses | Protected |
| POST | `/api/addresses/user/{userId}` | Save new address | Protected |
| DELETE | `/api/addresses/{addressId}` | Delete address | Protected |

## 🏗️ Architecture & Design Patterns

### **Application Architecture**
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │───▶│   Spring Boot   │───▶│     MySQL       │
│   Vue.js 3      │    │   Backend API   │    │   Database      │
│  (Port 3000)    │    │  (Port 8080)    │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### **Backend Layer Architecture**
```
┌─────────────────┐
│   Controllers   │ ← REST endpoints, request validation
│   (@RestController)
└─────────┬───────┘
          │
┌─────────▼───────┐
│    Services     │ ← Business logic, transaction management
│   (@Service)    │
└─────────┬───────┘
          │
┌─────────▼───────┐
│  Repositories   │ ← Data access, JPA queries
│  (@Repository)  │
└─────────┬───────┘
          │
┌─────────▼───────┐
│    Entities     │ ← JPA entities, database mapping
│   (@Entity)     │
└─────────────────┘
```

### **Security Flow**
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Client    │───▶│    JWT      │───▶│   Spring    │───▶│  Protected  │
│   Request   │    │   Filter    │    │  Security   │    │  Resource   │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

### **Project Structure**
```
food-ordering-system/
├── src/main/java/
│   ├── aspect/                    # AOP logging aspects
│   ├── config/                    # Configuration classes
│   ├── controller/                # REST controllers
│   ├── dto/                       # Data transfer objects
│   ├── entity/                    # JPA entities
│   ├── exception/                 # Custom exceptions & handlers
│   ├── repository/                # Data access repositories
│   ├── security/                  # Security configuration & JWT
│   └── service/                   # Business logic services
├── src/main/resources/
│   ├── application.properties     # Application configuration
│   └── logback-spring.xml         # Logging configuration
├── frontend/                      # Vue.js frontend (optional)
├── logs/                          # Application logs
├── uploads/menu-images/           # Uploaded menu images
├── pom.xml                        # Maven dependencies
└── README.md                      # This documentation
```

## 🔒 Security Implementation

### **Role-Based Access Control**
| Role  | Restaurant Creation | Order Placement | Menu Management | Order Management |
|-------|-------------------|-----------------|-----------------|------------------|
| USER  | ❌ 403 Forbidden   | ✅ Allowed      | ❌ View Only    | ✅ Own Orders    |
| HOTEL | ✅ Allowed        | ❌ 403 Forbidden | ✅ Own Items    | ✅ Own Restaurant|

### **JWT Token Structure**
```json
{
  "sub": "user@example.com",
  "role": "USER",
  "userId": 1,
  "iat": 1635724800,
  "exp": 1635811200
}
```

### **Password Security**
- **Encryption**: BCrypt with salt rounds
- **Migration**: Automatic encryption of existing plain text passwords
- **Validation**: Strong password requirements (configurable)

## 📊 Logging & Monitoring

### **Logging Features**
- **Console Logging**: Structured format with timestamps
- **File Logging**: Daily rotation with size limits
- **HTML Logging**: Web-friendly log viewing
- **AOP Logging**: Automatic method execution tracking
- **Request Tracing**: Unique trace IDs for request tracking

### **Log Locations**
- **Console**: Real-time application logs
- **File**: `logs/food-ordering-system.log`
- **HTML**: `logs/food-ordering-system.html`

### **Sample Log Output**
```
2024-01-15 10:30:45.123 [http-nio-8080-exec-1] INFO  [LoggingAspect] [a1b2c3d4] - REQUEST_START - method=UserController.registerUser traceId=a1b2c3d4
2024-01-15 10:30:45.156 [http-nio-8080-exec-1] INFO  [LoggingAspect] [a1b2c3d4] - ORDER_PLACEMENT_START - userId=123 transactionActive=true
2024-01-15 10:30:45.234 [http-nio-8080-exec-1] INFO  [LoggingAspect] [a1b2c3d4] - REQUEST_SUCCESS - method=UserController.registerUser duration=111ms traceId=a1b2c3d4
```

## 🚀 Deployment

### **Development Environment**
```bash
# Start MySQL
sudo systemctl start mysql

# Run application
mvn spring-boot:run

# Access Swagger
open http://localhost:8080/swagger-ui/index.html
```

### **Production Considerations**
- **Database**: Use connection pooling and proper indexing
- **Security**: Use environment variables for sensitive configuration
- **Logging**: Configure log levels and retention policies
- **Monitoring**: Implement health checks and metrics
- **SSL**: Enable HTTPS in production

## 🎯 Learning Outcomes

This project demonstrates enterprise-level skills in:

### **Backend Development**
- **Spring Boot 3** ecosystem and best practices
- **RESTful API** design with proper HTTP semantics
- **Database design** with JPA/Hibernate relationships
- **Security implementation** with JWT and role-based access
- **Transaction management** and data consistency
- **Exception handling** and error response design
- **File upload** and static resource management
- **Logging** and monitoring implementation

### **Software Architecture**
- **Layered architecture** with separation of concerns
- **Dependency injection** and inversion of control
- **Aspect-oriented programming** for cross-cutting concerns
- **Data transfer objects** for API design
- **Repository pattern** for data access abstraction

### **Enterprise Practices**
- **Security-first** development approach
- **Comprehensive logging** and monitoring
- **API documentation** with Swagger/OpenAPI
- **Error handling** and user experience
- **Code organization** and maintainability

## 📄 License

This project is developed for educational and demonstration purposes, showcasing modern Spring Boot development practices and enterprise application architecture.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Support

For questions or support:
- **Documentation**: Check this README and Swagger UI
- **Issues**: Create GitHub issues for bugs or feature requests
- **API Testing**: Use provided Postman collection

---

**Built with ❤️ using Spring Boot 3, Spring Security, JWT, MySQL, and modern Java development practices**
