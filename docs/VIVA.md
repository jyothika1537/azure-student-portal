# Viva Questions and Answers

**What is the problem?** Releases can cause downtime or expose an unverified build if deployed directly to production.

**Why Azure App Service?** It provides managed web-app hosting and deployment slots for controlled releases.

**What are deployment slots?** Separate live app environments under one App Service that can be swapped.

**What is Production?** The slot serving the live application URL and user traffic.

**What is Staging?** An isolated slot used to deploy and validate a release before promotion.

**Why do we validate before swapping?** To catch an unhealthy release before it receives production traffic.

**What is Health Check?** An Azure probe that requests the configured app path and checks whether the app responds successfully.

**What is Spring Boot Actuator?** Spring Boot's operations endpoints; this project exposes `/actuator/health` for health status.

**What is warm-up?** Azure requests the new slot's configured path and waits for an accepted response before completing a swap.

**What are slot-specific settings?** App settings marked sticky so their values stay with a slot when code is swapped.

**How did you overcome bottleneck 1?** I deploy the candidate release to Staging first, keeping Production unchanged during validation.

**How did you overcome bottleneck 2?** I configure the warm-up health path so Azure can check the app before routing the release.

**How did you implement use case 1?** Deploy V2 to Staging, then verify its page and Actuator health endpoint.

**How did you implement use case 2?** Swap the validated Staging release into Production; use a reverse swap to roll back.

**How does rollback work?** Swap the slots back so the previous Production version returns to Production.

**Did you use a database?** No. This release-slot demonstration does not use a database.