# 🧾 FinLab Fraud Shield – Instant Invoice Fraud Detection
🏆 **Second place in the hackathon** 🏆

> **Team:** Andjelina • FinLab Hackathon 2025


---

## 🚀 Overview

**FinLab Fraud Shield** is a fully containerized, cloud-ready microservice application that detects and prevents **invoice payment fraud** in real time.  
It verifies outgoing supplier payments against a **crowdsourced risk graph of IBANs** and flags suspicious or anomalous transactions in **≤ 200 ms**.

---

## 🧩 Features

- 🧠 **Crowdsourced Fraud Graph** – dynamically links reported IBANs to form a self-learning risk network.  
- ⚡ **Low-Latency Checks (< 200 ms)** – risk evaluation optimized via PostgreSQL + Redis caching.  
- 🧱 **Microservice Architecture**
  - `api-gateway` → JWT auth, routing (**Spring WebFlux**)  
  - `accounts-service` → IBAN graph logic (**Spring Boot MVC**)  
  - `nginx` → HTTPS reverse proxy  
  - `postgres` → persistent store  
  - `redis` → cache layer  
- 🔐 **Secure Auth Flow** – JWT tokens + internal API key. 

---

## 🧰 Tech Stack

| Component | Technology |
|------------|-------------|
| Frontend | JavaScript |
| Backend | **Spring Boot 3**, Java 21 |
| API Gateway | **Spring WebFlux (Reactive)** |
| Database | **PostgreSQL 16** |
| Cache | **Redis 7** |
| Proxy | **NGINX (HTTPS 443)** |
| Stress Testing | **Apache JMeter 5.6.3** |
| Containerization | **Docker & Docker Compose** |

> 🌀 **Implementation Note:**  
> I implemented the **API Gateway** with **Spring WebFlux** instead of the traditional Spring MVC to achieve higher scalability under concurrent load.  
> The same REST contract and container structure are preserved; only the underlying server model is reactive.


## To access the app's UI, go to -> https://localhost/app/
## Credentials: username -> admin; password: admin

## ⚙️ Quick Start & Running Instructions

### 🧾 Prerequisites
- **Docker Desktop v24+**
- **Git**
- Optional: **PowerShell 7+** or **bash**

---

### 1️⃣ Clone the Project
```bash
git clone https://github.com/angelinakumanova/finlab-fraud-shield.git
cd finlab-fraud-shield
```

---

### 2️⃣ Start the Stack
(Make sure **Docker Engine** is running)
```bash
./start.sh
```

This script:
- Generates a `.env` with random secrets and API keys.  
- Builds all Docker images.  
- Starts everything with Docker Compose.  


Check container status:
```bash
docker compose ps
```

---

### 3️⃣ Access the API

**Example Request**
```http
POST https://localhost/api/v1/iban/check
Content-Type: application/json
X-API-KEY: <internal_api_key>

{
  "iban": "BG11BANK12341234567890"
}
```

**Example Response**
```json
{
  "iban": "BG11BANK12341234567890",
  "decision": "ALLOW",
  "score": 0.5,
  "reasons": ["reports=0", "neighbors=1"]
}
```

---

### 4️⃣ Authentication

The gateway uses:
- **JWT-based tokens** for external users.  
- **Internal API key** (`X-API-KEY`) for service-to-service communication.  

View keys:
```bash
cat .env
```

Use in headers:
```http
Authorization: Bearer <jwt>
X-API-KEY: <internal_api_key>
```

---

###  Stress Testing (JMeter)

Two JMeter plans are included:
- `normal_load.jmx`
- `extreme_load.jmx`

---

### 5️⃣ Stop the System
```bash
docker compose down
```
To remove volumes:
```bash
docker compose down -v
```

---

## 📈 Performance Targets

| Scenario | Threads | Avg Response (ms) | Error Rate | Result |
|-----------|----------|-------------------|-------------|--------|
| Normal Load | 20 | ≤ 120 | < 1 % | ✅ Passed |
| Extreme Load | 500 | ≤ 200 | < 3 % | ✅ Passed |

---

## 🧮 Database Schema

| Table | Purpose |
|--------|----------|
| `iban_accounts` | Stores all IBANs |
| `iban_edges` | Links related IBANs (risk graph) |
| `iban_reports` | Crowdsourced fraud reports |
| `users`, `auth_tokens` | Gateway authentication |



## 🧾 License
Apache License 2.0
Made with ❤️ for FinLab Hackathon.

