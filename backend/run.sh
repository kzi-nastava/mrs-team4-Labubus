#!/bin/bash

set -e

echo "Running Spring Boot application (skipping tests)..."
echo ""
./mvnw spring-boot:run -DskipTests

