# Faculty Demonstration Guide – 24CC3046-P041

**Project:** Student Release Hub - Azure App Service Deployment Slots with Swap Validation
**Target:** Live Examination / Hackathon Jury Demonstration  

---

## 🎯 Demonstration Objective
Visually and technically prove zero-downtime application updates on Microsoft Azure App Service using deployment slots, health check validation, application warm-up, slot-specific (sticky) settings, atomic traffic swap, and instant rollback.

---

## 📋 Faculty Demo Sequence (Step-by-Step)

### Step 1: Show Azure Portal Resources
1. Open [Azure Portal](https://portal.azure.com).
2. Navigate to **Resource Groups** -> Select `rg-studentportal-demo` (or your resource group).
3. Open the App Service: `student-release-hub-2026`.
4. Navigate to **Deployment slots** in the left sidebar.
5. Point out to faculty:
   - **Production slot:** `student-release-hub-2026` (Traffic: 100%)
   - **Staging slot:** `student-release-hub-2026-staging` (Traffic: 0%)

---

### Step 2: Show Live Applications Side-by-Side

Open two browser tabs side-by-side:

| Parameter | Tab 1: Production URL | Tab 2: Staging URL |
| :--- | :--- | :--- |
| **URL** | `https://student-release-hub-2026.azurewebsites.net` | `https://student-release-hub-2026-staging.azurewebsites.net` |
| **Visual Theme** | Classic Blue Card Theme | Sleek Dark Mode with Emerald Accents |
| **Version Badge** | `VERSION 1` (Production Baseline) | `⚡ v2.0.0 - NEW RELEASE` |
| **Slot Badge** | `Slot: Production` | `Slot: Staging` |
| **Environment** | `Env: production` | `Env: staging` |
| **Directory** | Minimal baseline view | 5 Active Student Profiles with GPA & Department |

**Explanation to Faculty:**
> *"Here we see our baseline portal Version 1 actively serving live students in the Production slot. Meanwhile, our DevOps engineering team has deployed our new Version 2 into the isolated Staging slot. No live traffic is touching Staging yet."*

---

### Step 3: Demonstrate Health Validation & Warm-Up
1. Open the Actuator Health probe in Staging:
   `https://student-release-hub-2026-staging.azurewebsites.net/actuator/health`
2. Show the JSON payload:
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
           "environment": "staging",
           "warmupStatus": "READY",
           "swapValidation": "PASSED"
         }
       }
     }
   }
   ```
**Explanation to Faculty:**
> *"Before Azure allows any slot swap, it validates the health endpoint. The JVM JIT compiler pre-warms class bytecode, initial caches load, and the health probe returns HTTP 200 with swapValidation: PASSED. This prevents cold-start latency or deploying a broken build to production."*

---

### Step 4: Show Slot-Specific (Sticky) Setting
1. In Azure Portal, navigate to **Configuration** on the App Service.
2. Highlight the setting:
   - `APP_ENVIRONMENT = production` with **Deployment Slot Setting checked (Sticky)**.
3. Switch to the `staging` slot configuration:
   - `APP_ENVIRONMENT = staging` with **Deployment Slot Setting checked (Sticky)**.
**Explanation to Faculty:**
> *"Sticky settings remain anchored to the physical slot environment. When code is swapped, configuration tied to the environment doesn't accidentally change production behavior."*

---

### Step 5: Perform the Zero-Downtime Slot Swap
1. In the **Deployment slots** blade, click **Swap**.
2. Select:
   - **Source:** `staging`
   - **Target:** `production`
3. Click **Swap**.
4. While the swap executes, explain:
   > *"Azure is now performing an atomic Virtual IP routing switch at the Azure Load Balancer level. Existing TCP sockets finish cleanly, and new requests immediately hit the pre-warmed Version 2 container. There is exactly 0 seconds of downtime."*

---

### Step 6: Verify Production After Swap
1. Refresh the **Production URL** (`https://student-release-hub-2026.azurewebsites.net`).
2. **Immediate Result:** The Production URL now displays **Version 2.0**:
   - `⚡ v2.0.0 - NEW RELEASE`
   - `Slot: Production`
   - `Env: production` (Sticky setting preserved!)
   - Full student directory and metrics active in production!
3. Refresh the **Staging URL**: It now holds the previous **Version 1**!

---

### Step 7: Demonstrate Instant Rollback
1. Explain to Faculty:
   > *"Suppose an issue is detected in production post-deployment. We do not need to push an emergency code fix or wait 20 minutes for a rebuild."*
2. Click **Swap** again:
   - **Source:** `staging`
   - **Target:** `production`
3. Refresh the Production URL: It immediately reverts to **Version 1** with zero downtime!
