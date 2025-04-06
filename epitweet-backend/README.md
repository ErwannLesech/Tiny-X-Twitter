# Epitweet - Backend

## Overview
Epitweet Backend is a Twitter-like API developed using **Quarkus** and **Maven**. It is built with a **microservices architecture**, consisting of:
- `user-service`: Handles user authentication and management.
- `repo-post`: Manages post creation, retrieval, and interactions.
- `search-service`: Handles post indexing and searching via Elasticsearch.

## API Documentation
The APIs for Epitweet are documented using **Swagger**.
- [Swagger UI - Epitweet Backend](https://app.swaggerhub.com/apis/LESECHERWANN/Epitweet/1.0.0#/)


## Project Structure
```
Epitweet/
├── docker/                # Docker setup files
│   ├── docker-compose.yml
│   ├── init_mongo.js
│
├── common/                # Common utils 
│   ├── src/               
│   ├── pom.xml
│
├── repo-post/             # Post management microservice
│   ├── src/               # Port 8082
│   ├── pom.xml
│
├── user-service/          # User management microservice
│   ├── src/               # Port 8081
│   ├── pom.xml 
│
├── search-service/        # Search management microservice
│   ├── src/               # Port 8083
│   ├── pom.xml 
│
├── integrationTests.http   # HTTP test file for API calls
├── pom.xml                 # Parent Maven project configuration
├── README.md               # Project documentation
```

## Building the Project
You can build the entire project or individual modules.

### 1. Build All Modules
Run the following command at the root of the project:
```sh
mvn clean install
```

### 2. Build Specific Module
For example, to build only the `repo-post` service:
```sh
cd repo-post
mvn clean install
```
To build the `user-service`:
```sh
cd user-service
mvn clean install
```
To build the `search-service`:
```sh
cd search-service
mvn clean install
```
To build the `common`:
```sh
cd common
mvn clean install
```

## Running the Project

### 1. Start Databases and Redis
Run the following command to start all databases and Redis using Docker Compose:
```sh
cd docker
docker-compose up --build
```
This will start MongoDB, ElasticSearch, Neo4j and Redis.

### 2. Start services
To start all backend services, open separate terminal windows and run the following commands:
```sh
# Start the common module
./mvnw quarkus:dev -pl common/

# Start the user-service
./mvnw quarkus:dev -pl user-service/

# Start the repo-post service
./mvnw quarkus:dev -pl repo-post/

# Start the srvc-search service
./mvnw quarkus:dev -pl search-service/
```

## Testing the Project
The project includes both **module tests** (unit tests) and **integration tests**.

### 1. Running Unit Tests (Per Module)
To run unit tests for a specific service, you must first shut down all the running services. This is because the unit tests will restart the specific service to perform the tests.

Run the tests for a specific service:
```sh
# Run tests for user-service
cd user-service
mvn test
```
```sh
# Run tests for repo-post
cd repo-post
mvn test
```
```sh
# Run tests for srvc-search
cd search-service
mvn test
```

### 2. Running All Unit Tests (All Module)
To run all module tests, run the following command  :

```sh
mvn verify
```

### 3. Running Integration Tests
To run integration tests, you have to start all services as seen above.

you can manually test the endpoints using:
- **IntegrationTests.http** in intellij with predefined requests
- **Swagger UI** (links above)

---