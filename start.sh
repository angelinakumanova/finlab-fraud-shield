#!/bin/bash
# start.sh

ENV_FILE=".env"

# Create .env if it doesn't exist
if [ ! -f "$ENV_FILE" ]; then
  echo "🔧 Generating .env file with random secrets..."

  # Generate random base64 secrets
  JWT_SIGNING_KEY_B64=$(openssl rand -base64 32)
  INTERNAL_API_KEY=$(openssl rand -base64 32)

  # You can keep the DB credentials simple since it's internal
  POSTGRES_USER=dev
  POSTGRES_PASSWORD=dev
  POSTGRES_DB=finlab_db

  cat <<EOF > $ENV_FILE
# ==== AUTO-GENERATED ENV FILE ====
# Generated on $(date)

# --- PostgreSQL ---
POSTGRES_USER=$POSTGRES_USER
POSTGRES_PASSWORD=$POSTGRES_PASSWORD
POSTGRES_DB=$POSTGRES_DB

# --- Redis ---
REDIS_HOST=redis
REDIS_PORT=6379

# --- JWT ---
JWT_SIGNING_KEY_B64=$JWT_SIGNING_KEY_B64
JWT_TTL_SECONDS=900

# --- Internal API Key ---
INTERNAL_API_KEY=$INTERNAL_API_KEY
EOF

  echo "✅ .env file created."
else
  echo "ℹ️ .env file already exists. Skipping generation."
fi

# Start Docker Compose
echo "🚀 Starting containers..."
docker-compose up --build
