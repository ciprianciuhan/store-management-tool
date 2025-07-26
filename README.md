# Store Management Tool

A Spring Boot application that mimics the management of a store's inventory of products.

## Overview

- REST API for CRUD operations on products
- Role based access control with OAuth2/JWT authentication
- Database persistence with Postgres and Flyway migrations
- Event streaming with Kafka for real time inventory updates
- Monitoring capabilities with actuator

## Tech Stack

- **Java**: JDK 24
- **Framework**: Spring Boot 3.5.3
- **Security**: Spring Security with OAuth2 Resource Server
- **Database**: PostgreSQL 14.7 with Flyway migrations
- **Messaging**: Kafka for event streaming
- **Authentication**: Keycloak 22.0.3
- **Monitoring**: Spring Boot Actuator
- **Testing**: JUnit 5, Mockito, Spring Security Test

## Getting Started

### Prerequisites

- JDK 24
- Docker and Docker Compose
- Maven

### Setup

1. Clone the repository
2. Install Docker on your machine
3. Run the Docker Compose file from the project
4. Build with `mvn clean install`
5. Run the app
6. To check Kafka event consumption, go in Docker -> kafka-consumer -> check the logs

### Configuration

The application runs on port 8090 with the context path `/store-api/v1`.

Please use the local profile via `application-local.yaml`.

## API Endpoints

| Method | Endpoint              | Description                | Access        |
|--------|----------------------|----------------------------|---------------|
| GET    | /products            | List all products          | USER, ADMIN   |
| GET    | /products/{barcode}  | Get a specific product     | USER, ADMIN   |
| POST   | /products            | Create a new product       | ADMIN         |
| DELETE | /products/{barcode}  | Delete a product           | ADMIN         |
| PATCH  | /products/{barcode}/price | Update product price  | ADMIN         |

A Postman collection is also included in the repo.

## Monitoring

Actuator endpoints are available at `/store-api/v1/actuator/`

## Security

The application uses OAuth2 with JWT tokens for authentication and role-based authorization. Authentication is handled by Keycloak.

## Testing

Run the tests with:
```
mvn test
```

The test suite includes unit tests and integration tests using an in-memory H2 database.
JWT authentication is also mocked in the integration tests.

To avoid the H2 database and mocked authentication, I would have needed to create test containers for Postgres and Keycloak, but I was running out of time.

