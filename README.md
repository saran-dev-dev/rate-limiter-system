# Rate Limiter System

## Description

A Spring Boot application that limits how frequently a client can access an API endpoint using a rate-limiting mechanism.

The system applies rate limits per client and blocks excessive requests with appropriate HTTP responses.

---

## Functionality

- Rate limiting per client
- Token Bucket algorithm
- Redis used to store rate-limit state
- Returns HTTP 429 when the limit is exceeded
- Thread-safe request handling

---

## How It Works

- Client sends a request with `X-CLIENT-ID` header
- A servlet filter intercepts incoming requests
- Token Bucket logic checks whether the request is allowed
- Redis stores and updates the bucket state
- Request is allowed or blocked based on available tokens

---

## Endpoints

- GET /health – Health check endpoint  
- GET /api/test – Rate-limited test endpoint

---

## Required Header

All rate-limited requests must include:

X-CLIENT-ID

---

## Technology

- Java 17
- Spring Boot
- Redis
- Maven

---

## Running the Application

### Prerequisites

- Java 17 or higher
- Redis running locally

### Start Redis

redis-server

### Run the application

./mvnw spring-boot:run

The backend runs on:

http://localhost:8080

---

## Frontend

https://github.com/saran-dev-dev/rate-limiter-frontend
