# Helix — Digital Banking Platform

A full-stack, multi-module Spring Boot microservices project simulating a digital banking platform. Built as a hands-on learning/portfolio project covering microservice architecture, event-driven design, concurrency-safe money transfers, JWT auth, and a themeable frontend.

## Overview

Helix is composed of five independent Spring Boot microservices behind an API Gateway, each owning its own MySQL schema. Services communicate synchronously via Feign and asynchronously via Kafka (transaction events) and RabbitMQ (fraud alerts). The frontend is a plain HTML/CSS/JS single-page app with a full light/dark theme system.

## Architecture

Frontend SPA (plain HTML/JS) sends requests to the API Gateway on port 8080, which routes to five backend services: Account (8081), Transaction (8082), Card (8083), and Fraud (8084). Each service owns its own MySQL schema. Transaction service calls Account service synchronously via Feign. Transaction events flow through Kafka to the Fraud service, which publishes alerts to RabbitMQ.

## Modules

- helix-common (shared) - JWT security config, DTOs, exception handling
- helix-account-service (port 8081) - Auth (register/login), accounts, profile
- helix-transaction-service (port 8082) - Transfers, deposits, transaction history, Kafka producer
- helix-card-service (port 8083) - Card issuance/management
- helix-fraud-service (port 8084) - Kafka consumer, RabbitMQ producer, fraud alerts
- helix-gateway (port 8080) - Single entry point, routes to all services
- frontend/ - Plain HTML/CSS/JS SPA with light/dark theme

## Features

- JWT-based authentication shared across all services via helix-common
- Money transfers with balance validation and Feign-based inter-service calls
- Deposit ("Add Money") flow
- Automatic fraud detection on high-value transactions (Kafka-driven)
- Real-time-style alert notifications via RabbitMQ
- Card issuance and status management (active/frozen/blocked)
- Full transaction history with search
- Light/dark theme toggle across the entire frontend

## Tech Stack

Backend: Java 21, Spring Boot 3.3.4, Spring Security, Spring Data JPA, Spring Cloud OpenFeign, Spring Cloud Gateway, Spring Kafka, Spring AMQP, MySQL 8, JJWT

Frontend: Vanilla HTML/CSS/JavaScript (no framework), hash-based routing

Infra: Docker Compose (MySQL, Kafka, RabbitMQ), Jenkins CI

## Prerequisites

- Java 21
- Docker and Docker Compose
- Maven not required, the wrapper ./mvnw is included

## Getting Started

Clone the repository:
git clone https://github.com/your-username/Helix-Banking.git
cd Helix-Banking

Configure environment:
cp .env.example .env
Then edit .env with your local secrets.

Start infrastructure:
./run-dev.ps1 infra
docker ps
Wait for all three containers to show as healthy.

Build everything:
./mvnw clean install

Run all services, each opens in its own window:
./run-dev.ps1 all

Serve the frontend:
cd frontend
npx serve .

Then open the served frontend URL, register an account, and explore.

RabbitMQ management UI is available at http://localhost:15672 using credentials from your .env file.

## Troubleshooting

MySQL "Access denied" or "Communications link failure" on Windows: A native MySQL install or WSL relay process can silently steal port 3306. This project maps Docker's MySQL to host port 3307 to avoid that conflict. If issues persist, run netstat -ano and filter for port 3306 to check for unexpected listeners.

Env vars not loading: always use ./run-dev.ps1 followed by the service name instead of running mvnw spring-boot:run directly. The script loads .env into the same process that launches Maven, avoiding cross-terminal environment variable issues.

## Architecture Notes

Each service owns its own MySQL schema: helix_account, helix_transaction, helix_card, and helix_fraud. There is no cross-service direct database access.

Inter-service synchronous calls, such as Transaction calling Account, use Feign and forward the caller's JWT so downstream ownership checks still apply.

Transaction events for both transfers and deposits publish to a Kafka topic called transaction-events, which is consumed by the Fraud-Detection service to evaluate the High Value Transaction rule.

Fraud alerts publish to a RabbitMQ queue called helix.alerts.queue.

## Project Structure

Helix-Banking contains: helix-common, helix-account-service, helix-transaction-service, helix-card-service, helix-fraud-service, helix-gateway, frontend, docker-compose.yml, Jenkinsfile, .env.example, commands.txt, and README.md.

## License

This project is for educational and portfolio purposes.
