@echo off

REM Run the application without tests
echo Running Spring Boot application (skipping tests)...
echo.
mvnw.cmd spring-boot:run -DskipTests

pause
