# webapp
This project implements a simple health check API using Spring Boot. The `/healthz` endpoint is used to monitor the health of the web application by checking its connection to the database. If the database is reachable, the endpoint returns a 200 OK status, and if not, it returns a 503 Service Unavailable status. Only GET requests are supported for the `/healthz` endpoint, and the API should neither accept nor require a payload.

## Features
### Health Check Endpoint: /healthz is used to monitor the health of the web application.
200 OK: If the database is connected successfully.
503 Service Unavailable: If the database is unreachable.
Only GET requests are allowed, and no payload is required.
### User Management:
Create User (POST /v1/user): Allows users to create an account by providing email, password, first name, and last name. Validations ensure proper input.
Update User (PUT /v1/user/self): Authenticated users can update their own account information (first name, last name, password).
Get User Info (GET /v1/user/self): Authenticated users can retrieve their own account information. Fields such as id, account_created, and account_updated are read-only and cannot be modified by users.
---

## Prerequisites for Building and Deploying Locally
### Java Development Kit (JDK) 17+:
    Have **JDK 17 or higher** installed.
### Apache Maven:
    Have **Maven** installed, as the project uses Maven for dependency management and building.
### MySQL Database:
    **MySQL** database must be running, and should have a database ready for the application.
    Create a database and user in MySQL for this project:
    Database: csye6225
    User: chenni
### Postman or cURL:
    Have **Postman** installed to test the REST API.
---

## Build and Deploy Instructions
### Clone the Repository
1. Fork the GitHub repository
2. Clone forked repository to local machine

### Configure the Application
1. Open the `application.properties` file located in the `healthcheck/src/main/resources/ directory`.
2. Update the **MySQL** connection details

### Build the Application
1. Open a terminal and navigate to the root directory of the project.
2. Run the following Maven command to compile the project:
    ```bash
    mvn clean install

### Run the Application
1. After building the project, can start the application by running:
   ```bash
   mvn spring-boot:run
2. The application will start on `http://localhost:8080` by default.
---

## API Endpoints
### Health Check
#### GET /healthz:
Checks the health of the application by testing the database connection.
Returns:
    200 OK if the database connection is healthy.
    503 Service Unavailable if the database is unreachable.
### User Management
#### POST /v1/user: Create a new user by providing email, password, first_name, and last_name.
Validations:
    email: Must be a valid email format and unique.
    password: Must not be empty.
    first_name, last_name: Must not be empty.
Returns:
    201 Created on successful creation.
    400 Bad Request if the input is invalid (e.g., email already exists).
#### PUT /v1/user/self: Update an authenticated user's account information (first name, last name, password).
Fields like id, account_created, and account_updated are read-only and cannot be updated.
Returns:
    204 No Content on successful update.
    400 Bad Request for invalid updates (e.g., attempting to update id).
#### GET /v1/user/self: Retrieve the authenticated user's account information.
Returns:
    200 OK with user details.
    401 Unauthorized if the user is not authenticated.

---

## Continuous Integration (CI) with GitHub Actions for Web App
Added a GitHub Actions workflow to run the application tests for each pull request raised. A pull request can only be merged if the workflow executes successfully.
Added Status Checks GitHub branch protection to prevent users from merging a pull request when the GitHub Actions workflow run fails.
