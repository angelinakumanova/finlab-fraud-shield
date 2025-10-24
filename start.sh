#!/bin/bash
# start.sh

ENV_FILE=".env"

# Create .env if it doesn't exist
if [ ! -f "$ENV_FILE" ]; then
  echo "🔧 Generating .env file with random secrets..."

  # Generate random base64 secrets
  JWT_SIGNING_KEY_B64=$(openssl rand -base64 32)
  INTERNAL_API_KEY=$(openssl rand -base64 32)

  # Internal Postgres credentials
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

# Export INTERNAL_API_KEY for other scripts (like JMeter)
export $(grep INTERNAL_API_KEY $ENV_FILE)

# Run docker compose
echo "🚀 Starting containers..."
docker compose up --build

echo ""
echo "🔑 Your generated API key is: $INTERNAL_API_KEY"
echo "💡 You can use it in requests as the X-API-KEY header."
echo ""
echo "🧪 To run JMeter test after containers start:"
echo "   ./stress_tests/run_jmeter.sh"
