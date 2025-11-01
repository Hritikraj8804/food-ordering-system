@echo off
echo ========================================
echo    🚀 Starting Swiggy Clone Application
echo ========================================
echo.
echo 📋 Pre-flight Checklist:
echo ✓ MySQL Server running on localhost:3306
echo ✓ Database 'food_ordering_db' exists
echo ✓ Username: root, Password: 8804
echo.
echo 🔄 Starting Spring Boot Application...
echo.

cd /d "C:\Users\hriti\Documents\workspace-spring-tools-for-eclipse-4.31.0.RELEASE\food-ordering-system\food-ordering-system"

mvn spring-boot:run

echo.
echo 🎯 Application should be running on: http://localhost:8080
echo 📖 Swagger UI available at: http://localhost:8080/swagger-ui.html
echo.
pause