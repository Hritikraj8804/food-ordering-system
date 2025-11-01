# 🚀 Swiggy Clone - Final Testing Guide

## Pre-Test Setup

### 1. Database Setup
Ensure MySQL is running with the database `food_ordering_db`:
```sql
CREATE DATABASE food_ordering_db;
```

### 2. Start the Application
```bash
cd C:\Users\hriti\Documents\workspace-spring-tools-for-eclipse-4.31.0.RELEASE\food-ordering-system\food-ordering-system
mvn spring-boot:run
```

## 🎯 Key Testing Objectives

### ✅ **Role-Based Access Control**
- **USER** role can place orders ✓
- **HOTEL** role can create restaurants ✓
- **Cross-role restrictions** enforced (403 Forbidden) ✓

### ✅ **AOP Logging Verification**
- LoggingAspect intercepts `OrderService.placeOrder()` ✓
- Console logs show AOP interception ✓

### ✅ **Transaction Management**
- `@Transactional` on `placeOrder` method ✓
- Order atomicity guaranteed ✓

## 📋 Test Execution Steps

### Step 1: Import Postman Collection
1. Open Postman
2. Import `Swiggy_Clone_API_Tests.postman_collection.json`
3. Ensure application is running on `http://localhost:8080`

### Step 2: Execute Tests in Order

#### **Phase 1: User Registration**
1. **Create USER Role Customer** → Should return `201 Created`
2. **Create HOTEL Role Owner** → Should return `201 Created`

#### **Phase 2: Role-Based Restaurant Management**
3. **✅ HOTEL Creates Restaurant** → Should return `201 Created`
4. **❌ USER Tries to Create Restaurant** → Should return `403 Forbidden`

#### **Phase 3: AOP & Transaction Testing**
5. **✅ USER Places Order** → **WATCH CONSOLE LOGS** for AOP interception:
   ```
   ==================================================
   ⚡ AOP INTERCEPTED: Order placement initiated.
   ⚡ User ID 1 is attempting to place an order.
   ⚡ Spring @Transactional has been activated.
   ==================================================
   ```
6. **❌ HOTEL Tries to Place Order** → Should return `403 Forbidden`

#### **Phase 4: Verification**
7. **Get User Orders** → Should show the placed order
8. **Get All Restaurants** → Should show the created restaurant

#### **Phase 5: Error Handling**
9. **Get Non-existent User** → Should return `404 Not Found`
10. **Order from Non-existent Restaurant** → Should return `404 Not Found`

## 🔍 Expected Results Summary

| Test Case | Expected Status | Key Verification |
|-----------|----------------|------------------|
| USER registers | `201 Created` | User created with USER role |
| HOTEL registers | `201 Created` | User created with HOTEL role |
| HOTEL creates restaurant | `201 Created` | Restaurant linked to HOTEL user |
| USER tries restaurant creation | `403 Forbidden` | Role-based access denied |
| USER places order | `201 Created` | **AOP logs appear in console** |
| HOTEL tries to place order | `403 Forbidden` | Role-based access denied |
| Get user orders | `200 OK` | Order with items returned |
| Non-existent user | `404 Not Found` | Clean error response |
| Non-existent restaurant order | `404 Not Found` | Clean error response |

## 🎉 Success Criteria

✅ **All role-based restrictions work correctly**  
✅ **AOP logging appears in console during order placement**  
✅ **Transaction management ensures order atomicity**  
✅ **Clean error responses for all edge cases**  
✅ **No compilation or runtime errors**

## 🚨 Troubleshooting

### If AOP Logs Don't Appear:
- Check that `@EnableAspectJAutoProxy` is enabled (should be automatic with Spring Boot AOP starter)
- Verify LoggingAspect pointcut expression matches the service method signature

### If Role Checks Fail:
- Verify user IDs in the database match the test requests
- Check that roles are correctly set during user registration

### If Database Errors:
- Ensure MySQL is running and `food_ordering_db` exists
- Check `application.properties` database credentials

## 📊 Final Project Status
Upon successful completion of all tests:
- **✅ Code Complete**
- **✅ AOP Implemented**  
- **✅ Role-Based Security**
- **✅ Transaction Management**
- **✅ Error Handling**
- **🎯 READY FOR PRODUCTION**