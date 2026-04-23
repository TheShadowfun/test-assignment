# Software Engineer Test Assignment implementation

## Overview
This repository contains an implementation of the Software Engineer Test Assignment that enables managing customer accounts, multi-currency balances, and financial transactions. The application publishes all state mutations to RabbitMQ and has redundancy to protect against RabbitMQ or network downtime (assumes database connection is still available).

## Setup and Execution

### Prerequisites
* Docker and Docker Compose

### Running the Application
The application is fully containerized and requires no host machine configuration.

Execute the following command in the project root:
```bash
docker-compose up --build
```

**Exposed Services:**
* Application API: `http://localhost:8080`
* PostgreSQL Database: `localhost:5432`
* RabbitMQ Management UI: `http://localhost:15672` (user&pw `guest` / `guest`)

### Running the Test Suite
The project uses Testcontainers for full integration testing against real PostgreSQL and RabbitMQ instances. If docker is running you can execute:

```bash
./gradlew clean test
```

## API Usage

### 1. Create Account
Creates a fresh account.
```bash
curl -X POST http://localhost:8080/accounts \
-H "Content-Type: application/json" \
-d '{
  "customerId": 100,
  "country": "EE",
  "currencies": ["EUR", "USD"]
}'
```

### 2. Get Account
Retrieves account details and current balances.
```bash
curl http://localhost:8080/accounts/1
```

### 3. Create Transaction
Executes a transaction (`IN` / `OUT` marks direction of money flow).
```bash
curl -X POST http://localhost:8080/transactions \
-H "Content-Type: application/json" \
-d '{
  "accountId": 1,
  "amount": 500.00,
  "currency": "EUR",
  "direction": "IN",
  "description": "Initial Deposit"
}'
```

### 4. Get Transactions
Retrieves the transaction history for a specific account.
```bash
curl http://localhost:8080/accounts/1/transactions
```

## Architectural Choices

* Utilisation of `BigDecimal` in Java and `NUMERIC(20,2)` in PostgreSQL to eliminate floating-point rounding errors.
*  Row-level locking is implemented using `SELECT ... FOR UPDATE` on the `balances` table. This eliminates the possibility of race-condition bugs happening during database writes.
* Direct publishing to RabbitMQ from the business logic is avoided to prevent data loss in the case of RabbitMQ crashing / network outages occurring. I implemented the Transactional Outbox Pattern. Changes are committed to an `outbox` table within the same database transaction. A scheduled background worker polls the outbox and asynchronously sends info to RabbitMQ when it is available.

## Performance Estimations

**Throughput Estimate:** ~1,500 - 3,000 Transactions Per Second (based on measuring speed of 10000 mock transactions and also accounting for more demanding circumstances that I didn't simulate).
* The main bottleneck is the database disk I/O and the transaction locking mechanism. If a large amount of requests is made to a single account simultaneously, throughput drops significantly due to sequential lock queuing for that specific accounts records.

## Horizontal Scaling Considerations

To scale this application horizontally across multiple nodes:
- The Spring Boot API is stateless and can be scaled seamlessly behind a load balancer.
- As nodes increase, database connections multiply. Implementation of a connection pooler is required to prevent bottlenecks. Database sharding should also be considered to distribute lock contentions and subsequent delays
- Multiple application instances running the `@Scheduled` Outbox worker will attempt to read and publish the same pending messages, leading to duplicate event delivery. This requires further isolation of instances data or merging this into a single worker run separately with it's own redundancy mechanisms

## AI Usage Disclosure

AI was used during the development of this project for the following purposes:
* Rapidly learning Spring Boot 3 configurations and MyBatis XML mapping syntax
* Generating boilerplate DTOs, domain models, and other miscellaneous boilerplate code
* Resolving issues and debugging within Gradle configuration, MyBatis XML syntax, Springboot syntax, containerised tests
* Discussing alternative architectural decisions and getting to know industry standard techniques
