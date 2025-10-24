# 🧪 Use the official lightweight JMeter image
FROM alpine/jmeter:5.6.3

# Set working directory
WORKDIR /tests

# Copy all test files into the image (plans, scripts, etc.)
COPY . /tests

# Environment variables (these can also come from .env or docker-compose)
ENV JMETER_PLAN=normal_load.jmx
ENV RESULTS_DIR=/tests/results

# Ensure results directory exists
RUN mkdir -p ${RESULTS_DIR}

# Optional: install curl for debugging / health checks
RUN apk add --no-cache curl

# Default command to run JMeter headless with HTML report
CMD jmeter -Dhttps.default.protocol=TLSv1.2 \
           -Djmeter.ssl.ignore_ssl_errors=true \
           -n -t ${JMETER_PLAN} \
           -l ${RESULTS_DIR}/run.csv \
           -e -o ${RESULTS_DIR}/html
