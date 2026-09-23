# Viva Examination Questions and Answers – 24CC3046-P041

**Project Title:** Azure App Service Deployment Slots with Swap Validation  
**Candidate:** Jyothika Vucha  

---

### Q1: What is an Azure App Service Deployment Slot?
**Answer:**  
A deployment slot is an isolated, live web app environment hosted on the same App Service Plan as the production application. Each slot has its own publicly accessible URL (e.g., `<app-name>-staging.azurewebsites.net`). Deployment slots allow development teams to deploy, validate, and warm up new code releases in isolation before swapping them into production with zero downtime.

---

### Q2: What tier of App Service Plan is required to use Deployment Slots?
**Answer:**  
Deployment slots require a **Standard (S1)**, **Premium (P1v2/P1v3)**, or **Isolated (I1v2)** App Service Plan. Free (F1) and Basic (B1) tiers do not support deployment slots.

---

### Q3: How does Azure App Service achieve Zero-Downtime during a Slot Swap?
**Answer:**  
During a swap, Azure does not restart the instances or stop the server. Instead, it re-points the Virtual IP (VIP) and routing tables at the Azure front-end load balancers:
1. Azure ensures the target slot container is warmed up and answering requests.
2. The network router seamlessly swaps the destination IP mapping between the production and staging slots.
3. In-flight HTTP requests are allowed to complete on their existing TCP connections, while all new incoming HTTP requests are immediately routed to the new version.

---

### Q4: What is Application Warm-Up and why is it essential for Java/Spring Boot apps?
**Answer:**  
In Java applications, the JVM requires time to load bytecode classes into memory, initialize the Spring `ApplicationContext`, populate caches, and execute Just-In-Time (JIT) compilation. If user traffic hits a newly started Java container immediately, users experience high response latency ("cold start") or 504 Gateway Timeouts. Warm-up ensures HTTP probes (such as `/actuator/health`) ping the application until the JVM is warm before traffic is redirected.

---

### Q5: What is the role of Spring Boot Actuator `/actuator/health` in this project?
**Answer:**  
Spring Boot Actuator exposes operational health endpoints. In this project, we implemented a custom `SlotSwapHealthIndicator` that reports the active slot name, application version, warm-up status, and swap readiness (`PASSED`). Azure Health Check monitors this path and aborts any slot swap if the health probe returns anything other than HTTP `200 OK`.

---

### Q6: What is a "Slot-Specific Setting" (or "Sticky Setting")?
**Answer:**  
By default, application settings (environment variables) swap along with the code. However, when an App Setting is marked as a **Deployment Slot Setting** ("Sticky"), it remains anchored to that physical slot.  
In our demo:
- `APP_ENVIRONMENT = production` is sticky to the Production slot.
- `APP_ENVIRONMENT = staging` is sticky to the Staging slot.  
When code swaps, the application code moves, but the environment badge dynamically reflects the host slot environment.

---

### Q7: How does Rollback work with Deployment Slots?
**Answer:**  
When a slot swap is executed, the previous production release is not deleted; it moves into the `staging` slot. If a critical defect or regression is discovered in production post-deployment, an administrator triggers a second swap. The previous version is immediately restored to production within seconds without needing to rebuild or re-deploy code.

---

### Q8: What does the GitHub Actions CI/CD workflow do in this project?
**Answer:**  
The workflow [`.github/workflows/azure-deploy.yml`](../.github/workflows/azure-deploy.yml):
1. Runs automated compilation and unit testing on Java 21.
2. Packages the production-ready executable Spring Boot JAR.
3. Automatically deploys the artifact to the Azure App Service `staging` slot using `azure/webapps-deploy@v3`.
4. Executes an automated post-deployment health probe against `/actuator/health` with retry logic to verify successful warm-up before declaring the deployment complete.
