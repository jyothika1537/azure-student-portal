# 24CC3046-P041 – Azure App Service Deployment Slots with Swap Validation

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Active-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Azure App Service](https://img.shields.io/badge/Azure-App%20Service%20Slots-0078D4.svg)](https://azure.microsoft.com/en-us/products/app-service/)
[![CI/CD](https://img.shields.io/badge/GitHub%20Actions-Automated%20Deploy-2088FF.svg)](https://github.com/features/actions)

An enterprise-grade, demo-ready **Student Portal** engineered for **Azure App Service Deployment Slots** with zero-downtime traffic cutover and automated **Swap Validation**.

---

## 📌 Project Overview

In mission-critical enterprise applications, deploying new releases directly to production causes cold starts, potential downtime, and user disruption. 

This project demonstrates how **Azure App Service Deployment Slots** resolve this challenge:
1. **Version 1 (Production):** The baseline portal serving users.
2. **Version 2 (Staging):** The modern, feature-rich release deployed into an isolated `staging` slot.
3. **Automated Health Validation & Warm-up:** Azure and CI/CD probe `/actuator/health` to confirm the JVM is completely warmed up, JIT-compiled, and returning `HTTP 200 (UP)` with swap validation status `PASSED`.
4. **Zero-Downtime Swap:** Virtual IP routing shifts live traffic from Staging to Production instantaneously without dropping a single HTTP request.
5. **Instant Rollback:** If any issue arises, another swap instantly restores the previous version.

---

## 🛠️ Technology Stack

- **Runtime & Language:** Java 21 (LTS)
- **Framework:** Spring Boot (MVC + Thymeleaf)
- **Observability:** Spring Boot Actuator (`/actuator/health`, `/actuator/info`, `/actuator/metrics`)
- **Build Tool:** Apache Maven 3.9+ (with Maven Wrapper `mvnw` / `mvnw.cmd`)
- **Hosting Platform:** Azure App Service (Linux, Java 21, Java SE embedded web server)
- **CI/CD:** GitHub Actions (`.github/workflows/azure-deploy.yml`)
- **Data Layer:** Lightweight in-memory records (no external database overhead)

---

## 🚀 Quick Local Run & Verification

You can build, test, and run the project locally with the included Maven Wrapper:

### 1. Build and Run Tests
```powershell
# Windows (PowerShell)
.\mvnw.cmd clean test

# Linux / macOS
./mvnw clean test
```

### 2. Package Executable JAR
```powershell
.\mvnw.cmd clean package
```

### 3. Run Locally
```powershell
# Run with Spring Boot plugin
.\mvnw.cmd spring-boot:run

# Or run the packaged JAR directly
java -jar target/studentportal-0.0.1-SNAPSHOT.jar
```
The application will start on **http://localhost:8080**.

---

## 🌐 Endpoints Reference

| Endpoint | Type | Description |
| :--- | :--- | :--- |
| **`/`** | HTML Webpage | Default Landing Page (renders active version based on `app.version` or query parameter `?v=1` / `?v=2`) |
| **`/v1`** | HTML Webpage | Explicit Version 1 Baseline Portal (Classic blue UI, Version 1 badge) |
| **`/v2`** | HTML Webpage | Explicit Version 2 Enhanced Portal (Modern dark-mode UI, metrics, student directory) |
| **`/actuator/health`** | JSON API | Spring Boot Actuator Health Check (Custom `SlotSwapHealthIndicator` with slot name, version, and swap validation status) |
| **`/api/status`** | JSON API | Lightweight system diagnostic payload (`version`, `slot`, `timestamp`, `status`) |

---

## ☁️ Azure App Service Setup & Deployment Slots Guide

### Step 1: Create Resource Group & App Service Plan
> [!NOTE]
> Deployment slots require an App Service Plan of tier **Standard (S1)** or higher (Premium, Isolated).

```bash
# Set your variables
RESOURCE_GROUP="rg-studentportal-demo"
LOCATION="eastus"
APP_PLAN="plan-studentportal-s1"
APP_NAME="azure-student-portal-$RANDOM"

# 1. Create Resource Group
az group create --name $RESOURCE_GROUP --location $LOCATION

# 2. Create Standard S1 App Service Plan
az appservice plan create \
  --name $APP_PLAN \
  --resource-group $RESOURCE_GROUP \
  --sku S1 \
  --is-linux
```

### Step 2: Create Azure Web App (Java 21 Java SE)
```bash
# Create the Web App with Java 21 on Linux
az webapp create \
  --resource-group $RESOURCE_GROUP \
  --plan $APP_PLAN \
  --name $APP_NAME \
  --runtime "JAVA:21-java21"
```

### Step 3: Create the "staging" Deployment Slot
```bash
az webapp deployment slot create \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --slot staging
```

### Step 4: Configure Slot-Specific ("Sticky") Settings
Slot-specific settings guarantee that slot metadata stays bound to the physical environment even after a code swap:

```bash
# Configure sticky setting for Production slot
az webapp config appsettings set \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --slot-settings AZURE_SLOT_NAME=Production

# Configure sticky setting for Staging slot
az webapp config appsettings set \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --slot staging \
  --slot-settings AZURE_SLOT_NAME=Staging
```

### Step 5: Configure Health Check Path
Configure Azure to automatically ping `/actuator/health` for swap validation:
```bash
# Configure Health Check on Production
az webapp config set \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --generic-configurations '{"healthCheckPath": "/actuator/health"}'

# Configure Health Check on Staging
az webapp config set \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --slot staging \
  --generic-configurations '{"healthCheckPath": "/actuator/health"}'
```

---

## 🤖 GitHub Actions CI/CD Deployment

The repository includes a ready-to-use GitHub Actions workflow at [`.github/workflows/azure-deploy.yml`](.github/workflows/azure-deploy.yml).

### Configure GitHub Repository Secrets
Navigate to **GitHub Repository -> Settings -> Secrets and variables -> Actions** and add:
1. `AZURE_WEBAPP_NAME`: Your Azure App Service name (e.g., `azure-student-portal`).
2. `AZURE_WEBAPP_PUBLISH_PROFILE`:
   - Obtain from Azure Portal: **Web App -> Overview -> Get publish profile**.
   - Paste the complete XML content into the secret.

### Triggering the Deployment:
1. **Automated:** Pushing commits to the `main` branch builds and deploys to the `staging` slot.
2. **Manual (Workflow Dispatch):** Go to **Actions -> Build and Deploy to Azure App Service -> Run workflow**:
   - Choose the target slot (`staging` or `production`).
   - Click **Run workflow**.
   - The workflow compiles with Java 21, packages the JAR, deploys via `azure/webapps-deploy@v3`, and executes an automated health probe against `/actuator/health`.

---

## 🎬 Step-by-Step Live Demo Execution Instructions

Follow this exact walkthrough to present the project during hackathon judging or evaluation:

### Phase 1: Establish the Baseline State (Production on Version 1)
1. Ensure the **Production slot** URL (`https://<app-name>.azurewebsites.net`) displays **Version 1**:
   - Classic Blue Header: `🎓 Student Portal`
   - Badge: `VERSION 1`
   - Baseline text: `Azure App Service Deployment Project`
2. Point out that students and faculty are actively using Version 1 in production.

### Phase 2: Deploy Version 2 to the Staging Slot
1. Run the GitHub Actions workflow targeting the `staging` slot (or deploy via Azure CLI/Maven).
2. The staging slot URL (`https://<app-name>-staging.azurewebsites.net`) is now live with **Version 2.0**:
   - Modern Dark-mode design with glowing emerald/indigo gradients.
   - Prominent badge: `⚡ v2.0.0 - NEW RELEASE`.
   - Real-time KPI Metric cards (1,420 Enrolled Students, 36 Courses, 3.78 Avg GPA).
   - Live Student Directory Table with student profiles.
   - Slot Badge: `☁️ Slot: Staging`.

### Phase 3: Inspect Both Slots Side-by-Side
Open both browser tabs side-by-side:
- **Tab 1 (Production):** Shows **Version 1** (safe, uninterrupted).
- **Tab 2 (Staging):** Shows **Version 2** (ready for QA and validation).

### Phase 4: Validate Health & Warm-up
1. Open `https://<app-name>-staging.azurewebsites.net/actuator/health` in a browser.
2. Show the evaluator the JSON response:
   ```json
   {
     "status": "UP",
     "components": {
       "diskSpace": { "status": "UP" },
       "ping": { "status": "UP" },
       "slotSwapHealth": {
         "status": "UP",
         "details": {
           "version": "2.0.0",
           "slotName": "Staging",
           "warmupStatus": "READY",
           "swapValidation": "PASSED"
         }
       }
     }
   }
   ```
3. Explain that the JVM JIT compiler has pre-compiled the hot code paths and initialized the application context before any customer traffic arrives.

### Phase 5: Execute the Zero-Downtime Slot Swap
Execute the swap using Azure Portal or Azure CLI:

```bash
az webapp deployment slot swap \
  --resource-group $RESOURCE_GROUP \
  --name $APP_NAME \
  --slot staging \
  --target-slot production
```
*(Or in Azure Portal: Navigate to **Deployment slots** -> Click **Swap** -> Select Source: `staging`, Target: `production` -> Click **Swap**).*

### Phase 6: Observe Zero-Downtime Result
1. Refresh the **Production URL** (`https://<app-name>.azurewebsites.net`).
2. **Instant Result:** Production now immediately serves **Version 2.0**!
3. Explain to the judges:
   - There was **zero downtime**.
   - No HTTP 502/503 errors occurred.
   - Ongoing user sessions were not terminated.
   - The slot badge on production now reads `☁️ Slot: Production` thanks to sticky slot settings.

### Phase 7: Demonstrate Instant Rollback (Zero-Disruption Safety Net)
1. Explain: *"Suppose our QA team discovers an unexpected edge case in production post-swap. We do not need a lengthy emergency redeployment or hotfix."*
2. Trigger the swap command once more:
   ```bash
   az webapp deployment slot swap \
   --resource-group $RESOURCE_GROUP \
   --name $APP_NAME \
   --slot staging \
   --target-slot production
   ```
3. Refresh the production URL: Production immediately reverts to **Version 1**!
4. The faulty release is safely quarantined back in `staging` for debugging without impacting live users.

---

## 📚 Key Concepts Summary

| Concept | Explanation |
| :--- | :--- |
| **Production Slot** | The public-facing slot hosting live user traffic. |
| **Staging Slot** | The isolated parallel environment for testing and warming up new code before live release. |
| **Version 1** | Baseline version with classic blue styling and "VERSION 1" indicator. |
| **Version 2** | Enhanced version with dark theme, version badge, metrics cards, and student directory table. |
| **Health Validation** | `/actuator/health` probe that returns HTTP 200 and component health status before traffic swap is authorized. |
| **Warm-up** | Pre-loading the Spring ApplicationContext and JVM bytecode in staging so users experience zero cold-start latency. |
| **Slot-Specific Settings** | Configuration properties (like `AZURE_SLOT_NAME`) marked as "Deployment Slot Setting" so they stick to the slot rather than swapping with the code. |
| **Slot Swap** | Instantaneous virtual IP routing change at the load balancer level shifting traffic with zero dropped packets. |
| **Rollback** | Swapping the slots back to restore the previous stable production release within seconds. |

---

## 📄 License
This project is licensed under the Apache 2.0 License - Developed for Hackathon Demo **24CC3046-P041**.
