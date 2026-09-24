# Abstract

The project "Azure App Service Deployment Slots with Swap Validation" focuses on achieving zero-downtime deployment for web applications using Microsoft Azure App Service.

The project uses the Spring Boot Student Release Hub application with two deployment environments: Production and Staging. A new application version is first deployed to the Staging slot, where it can be tested and validated before being released to users.

After successful validation, Azure App Service Deployment Slots are used to swap the Staging and Production environments. Application warm-up and health validation help ensure that the new version is ready before the swap.

The project also demonstrates the use of slot-specific application settings to prevent configuration problems during deployment. This approach provides a safer deployment process, reduces downtime, and allows quick rollback when required.

The main objective is to demonstrate a reliable cloud deployment strategy using Azure App Service, deployment slots, validation, warm-up, and slot swapping.
