#!/usr/bin/env bash
set -euo pipefail

# --- Paths ---
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/../.env"
RESULTS_DIR="$SCRIPT_DIR/results"
NETWORK_NAME="finlab-net"

# --- Detect host OS for proper volume mapping ---
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" || "$OSTYPE" == "cygwin" ]]; then
  MOUNT_PATH=$(pwd -W)     # Windows (Docker Desktop)
else
  MOUNT_PATH="$SCRIPT_DIR" # Linux/macOS/WSL
fi

# --- Load API key from .env ---
if [[ -f "$ENV_FILE" ]]; then
  export INTERNAL_API_KEY=$(grep -E '^INTERNAL_API_KEY=' "$ENV_FILE" | cut -d '=' -f2-)
else
  echo "⚠️  .env not found. Run ./start.sh first."
  read -rp "Press Enter to close..."
  exit 1
fi

# --- Prepare results folders ---
rm -rf "$RESULTS_DIR/normal" "$RESULTS_DIR/extreme"
mkdir -p "$RESULTS_DIR/normal" "$RESULTS_DIR/extreme"

JMETER_IMAGE="alpine/jmeter:5.6.3"

run_test() {
  local plan=$1
  local outdir="$RESULTS_DIR/$plan"
  echo "🚀 Running ${plan^^} load test..."

  docker run --rm \
    --network "$NETWORK_NAME" \
    -e INTERNAL_API_KEY="$INTERNAL_API_KEY" \
    -v "${MOUNT_PATH}:/tests" \
    "$JMETER_IMAGE" \
    jmeter \
      -Dhttps.default.protocol=TLSv1.2 \
      -Djmeter.ssl.ignore_ssl_errors=true \
      -n \
      -t "/tests/${plan}_load.jmx" \
      -l "/tests/results/${plan}/${plan}.jtl" \
      -e \
      -o "/tests/results/${plan}/html"

  echo "✅ ${plan^} load completed."
  echo "📄 Results: stress_tests/results/${plan}/${plan}.jtl"
}

# --- Run both plans ---
run_test normal
run_test extreme

echo ""
echo "✅ All tests complete!"
echo "   HTML Reports:"
echo "   stress_tests/results/normal/html/index.html"
echo "   stress_tests/results/extreme/html/index.html"

echo ""
read -rp "Press Enter to close..."
