#!/bin/bash

# Stop script on error
set -e

KEY_PATH=~/Downloads/life2food.pem
USER=ec2-user
HOST=3.149.164.235
BACKEND_DIR=/mnt/data/backendLife2Food/backend

# Check if .env file exists locally
if [ ! -f .env ]; then
  echo "Error: .env file not found in current directory."
  echo "Please make sure you're running this script from the backend directory."
  exit 1
fi

echo "Copying .env file to server (temporary location)..."
scp -i "$KEY_PATH" .env "$USER@$HOST:~/backend.env"

echo "Connecting to $HOST..."

ssh -i "$KEY_PATH" "$USER@$HOST" << 'EOF'
  set -e
  
  echo "Connected to EC2. Starting deployment..."
  
  # Navigate to the backend directory
  cd /mnt/data/backendLife2Food/backend || exit 1
  
  # Move .env file from home directory to backend directory
  echo "Moving .env file to backend directory..."
  mv ~/backend.env .env || {
    echo "Warning: Could not move .env file. Trying to copy instead..."
    cp ~/backend.env .env || {
      echo "Error: Could not copy .env file to backend directory"
      exit 1
    }
  }
  
  # 1. Pull latest changes
  echo "Pulling latest changes from git..."
  git pull
  
  # 2. Build the project
  # Using 'mvn' as it appears to be globally installed based on your history
  echo "Building project with Maven..."
  mvn clean package -DskipTests
  
  # 3. Build Docker image
  echo "Building Docker image..."
  docker build -t life2food-backend .
  
  # 4. Stop and remove existing container
  echo "Stopping and removing existing container..."
  docker stop life2food-backend || true
  docker rm life2food-backend || true
  
  # 5. Verify .env file exists
  if [ ! -f .env ]; then
    echo "Error: .env file not found in current directory"
    exit 1
  fi
  
  echo "Verifying .env file contents..."
  echo "Required variables check:"
  grep -q "^MAIL_PASSWORD=" .env && echo "  ✓ MAIL_PASSWORD" || echo "  ✗ MAIL_PASSWORD missing"
  grep -q "^AWS_ACCESS_KEY=" .env && echo "  ✓ AWS_ACCESS_KEY" || echo "  ✗ AWS_ACCESS_KEY missing"
  grep -q "^AWS_SECRET_KEY=" .env && echo "  ✓ AWS_SECRET_KEY" || echo "  ✗ AWS_SECRET_KEY missing"
  grep -q "^MERCADOPAGO_ACCESS_TOKEN=" .env && echo "  ✓ MERCADOPAGO_ACCESS_TOKEN" || echo "  ✗ MERCADOPAGO_ACCESS_TOKEN missing"
  grep -q "^MERCADOPAGO_PUBLIC_KEY=" .env && echo "  ✓ MERCADOPAGO_PUBLIC_KEY" || echo "  ✗ MERCADOPAGO_PUBLIC_KEY missing"
  
  # 6. Run the new container
  echo "Starting new container with environment variables from .env file..."
  docker run -d \
    --name life2food-backend \
    -p 8080:8080 \
    -v /mnt/data:/mnt/data \
    --env-file .env \
    life2food-backend
    
  echo "Remote deployment completed successfully!"
EOF

