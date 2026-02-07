#!/bin/bash

# Unified setup and run script for Loan Approval Hub

set -e

show_help() {
  echo "Usage: $0 [setup|backend|frontend]"
  echo "  setup    - Install dependencies for frontend and backend"
  echo "  backend  - Run backend Spring Boot server"
  echo "  frontend - Run frontend Vite server"
  echo "  help     - Show this help message"
}

case "$1" in
  setup)
    echo "Setting up frontend..."
    cd loan-approval-hub
    npm install
    cd ..
    echo "Setting up backend..."
    cd backend
    ./mvnw clean install || mvn clean install
    cd ..
    echo "Setup complete!"
    ;;
  backend)
    echo "Starting backend..."
    cd backend
    ./mvnw spring-boot:run || mvn spring-boot:run
    ;;
  frontend)
    echo "Starting frontend..."
    cd loan-approval-hub
    npm run dev
    ;;
  help|*)
    show_help
    ;;
esac
