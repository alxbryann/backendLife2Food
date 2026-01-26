#!/bin/bash

# Stop script on error
set -e

KEY_PATH=~/Downloads/life2food.pem
USER=ec2-user
HOST=3.149.164.235

echo "Connecting to $HOST..."

ssh -i "$KEY_PATH" "$USER@$HOST" << 'EOF'
  set -e
  
  echo "Connected to EC2. Starting deployment..."
  
  # Navigate to the backend directory
  cd /mnt/data/backendLife2Food/backend || exit 1
  
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
  
  # 5. Run the new container
  echo "Starting new container..."
  docker run -d \
    --name life2food-backend \
    -p 8080:8080 \
    -v /mnt/data:/mnt/data \
    life2food-backend
    
  echo "Remote deployment completed successfully!"
EOF

