@echo off
echo Starting Food Ordering System Frontend...
echo.
echo Make sure the Spring Boot backend is running on http://localhost:8080
echo.

cd /d "%~dp0"

if not exist node_modules (
    echo Installing dependencies...
    npm install
)

echo Starting development server...
npm run dev

pause