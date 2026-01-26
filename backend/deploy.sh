#!/bin/bash

set -e

# ============================
# CONFIGURACIÓN
# ============================
APP_NAME="life2food-backend"
PORT=8080
VOLUME_PATH="$(pwd)/data"

echo "🚀 Desplegando Life2Food Backend en local..."

# ============================
# GIT
# ============================
echo "📥 Actualizando código..."
git pull

# ============================
# MAVEN BUILD
# ============================
echo "🛠️ Compilando proyecto..."
mvn clean package -DskipTests

# ============================
# DOCKER
# ============================
echo "🐳 Deteniendo contenedor previo (si existe)..."
docker stop $APP_NAME 2>/dev/null || true
docker rm $APP_NAME 2>/dev/null || true

echo "🏗️ Construyendo imagen Docker..."
docker build -t $APP_NAME .

echo "▶️ Iniciando contenedor..."
docker run -d \
  --name $APP_NAME \
  -p $PORT:8080 \
  -v $VOLUME_PATH:/mnt/data \
  $APP_NAME

echo "✅ Life2Food Backend desplegado en http://localhost:$PORT"
