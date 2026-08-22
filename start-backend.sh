#!/bin/bash
echo "========================================"
echo "  AI Risk Tracker - Starting Backend"
echo "========================================"
echo
echo "This will build and run the Spring Boot backend on http://localhost:8080"
echo "First run will download dependencies via Maven and may take a few minutes."
echo
cd backend
mvn spring-boot:run
