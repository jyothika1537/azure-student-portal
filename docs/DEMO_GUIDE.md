# Student Release Hub Demo

1. Show Production V1 (`Version 1.0`, `Production Release`).
2. Show Staging V2 (`Version 2.0`, `Staging Release`).
3. Show `/actuator/health` returns HTTP 200 and `status: UP`.
4. Show Azure Health Check is configured for `/actuator/health`.
5. Show warm-up configuration: `WEBSITE_SWAP_WARMUP_PING_PATH=/actuator/health` and `WEBSITE_SWAP_WARMUP_PING_STATUSES=200`.
6. Show slot-specific settings: `DEPLOYMENT_SLOT=production` on Production and `DEPLOYMENT_SLOT=staging` on Staging; mark each as a deployment slot setting.
7. In Azure Portal, manually swap Staging to Production and confirm Azure validation succeeds.
8. Refresh Production and show V2.
9. Explain rollback using the reverse swap.