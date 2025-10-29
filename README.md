# Gauri Cooks - Backend Service

This repository contains the backend service for **Gauri Cooks**, a bespoke food ordering platform. This service is built with Java and Spring Boot, uses MongoDB as its database, Redis for caching and session management, Stripe for payments, and is deployed on AWS. It serves the frontend application available at [gauricooks.com](https://gauricooks.com).

This is the backend repository. The frontend code is in a separate repository.

## Features

*   **Authentication:** Secure user registration with email verification, login with credentials, and Google OAuth2.
*   **Authorization:** JWT-based stateless authentication with Redis-backed token management and role-based access control (USER, ADMIN).
*   **User Management:** Users can manage their profiles, change passwords, and view their order history. Admins can manage all users.
*   **Menu Management:** Admins can perform CRUD operations on food items available for order.
*   **Order Management:** Users can create, view, and update their orders. Admins can view and manage all orders.
*   **Password Reset:** Secure "forgot password" flow via email with Redis-based token storage and rate limiting.
*   **Payment Processing:** Secure payment intent creation for online orders using Stripe.
*   **Session Security:** JWT token blacklisting and automatic cleanup using Redis.

## Technologies Used

*   **Backend:** Java 21, Spring Boot
*   **Database:** MongoDB
*   **Caching:** Redis
*   **Payments:** Stripe
*   **Authentication:** Spring Security, JWT
*   **Build Tool:** Maven
*   **Containerization:** Docker
*   **Deployment:** AWS

## Prerequisites

*   Java JDK 21
*   Apache Maven
*   A running MongoDB instance
*   A running Redis instance
*   Docker (optional)

## Local Setup and Configuration

1.  **Clone the repository:**
    ```sh
    git clone github.com/thenakulmistry/chefv1
    cd V1
    ```

2.  **Set up Redis locally:**
    ```sh
    # Using Docker (Recommended)
    docker run -d --name redis-server -p 6379:6379 redis:7-alpine
    
    # Or install Redis directly on your system
    ```

3.  **Configure Environment Variables:**
    This project uses environment variables for configuration, as defined in [`src/main/resources/application.yml`](src/main/resources/application.yml). For local development, you can create a `.env.dev` file in the root directory or set these variables in your IDE's run configuration.

    **Required Environment Variables:**
    ```
    # Database Configuration
    SPRING_DATA_MONGODB_URI=<your_mongodb_connection_string>
    SPRING_DATA_MONGODB_DATABASE=<your_database_name>
    
    # Redis Configuration
    REDIS_HOST=localhost
    REDIS_PORT=6379
    REDIS_PASSWORD=
    
    # Authentication & Security
    JWT_SECRET=<your_strong_jwt_secret>
    GOOGLE_OAUTH2_CLIENT_ID=<your_google_client_id>
    GOOGLE_OAUTH2_CLIENT_SECRET=<your_google_client_secret>
    
    # Email Configuration
    MAIL_HOST=smtp.gmail.com
    MAIL_PORT=587
    MAIL_USERNAME=<your_gmail_address>
    MAIL_PASSWORD=<your_gmail_app_password>
    
    # Frontend Configuration
    APP_FRONTEND_URL=http://localhost:5173
    APP_FRONTEND_GOOGLE_REDIRECT_URI=http://localhost:5173/oauth2/redirect

    # Stripe Configuration
    STRIPE_SECRET_KEY=<your_stripe_test_secret_key>
    ```

## Running the Application Locally

### Using Maven
You can run the application using the Maven wrapper included in the project:
```sh
./mvnw spring-boot:run
```

### Using Docker
The project includes a [`Dockerfile`](Dockerfile) for easy containerization.

1.  **Build the Docker image:**
    ```sh
    docker build -t gauri-cooks-backend .
    ```

2.  **Run with Docker Compose (includes Redis):**
    ```yaml
    # docker-compose.yml
    version: '3.8'
    services:
      redis:
        image: redis:7-alpine
        ports:
          - "6379:6379"
      
      app:
        image: gauri-cooks-backend
        ports:
          - "8080:8080"
        environment:
          - REDIS_HOST=redis
          - REDIS_PORT=6379
          # Add other environment variables
        depends_on:
          - redis
    ```

3.  **Run the Docker container (with external Redis):**
    Pass the required environment variables when running the container.
    ```sh
    docker run -p 8080:8080 \
      -e SPRING_DATA_MONGODB_URI="<your_mongodb_uri>" \
      -e SPRING_DATA_MONGODB_DATABASE="<your_database_name>" \
      -e REDIS_HOST="localhost" \
      -e REDIS_PORT="6379" \
      -e GOOGLE_OAUTH2_CLIENT_ID="<your_google_id>" \
      -e GOOGLE_OAUTH2_CLIENT_SECRET="<your_google_secret>" \
      -e MAIL_USERNAME="<your_email>" \
      -e MAIL_PASSWORD="<your_password>" \
      -e JWT_SECRET="<your_jwt_secret>" \
      -e STRIPE_SECRET_KEY="<your_stripe_secret_key>" \
      -e APP_FRONTEND_URL="http://localhost:5173" \
      -e APP_FRONTEND_GOOGLE_REDIRECT_URI="http://localhost:5173/oauth2/redirect" \
      gauri-cooks-backend
    ```

## API Endpoints

The application exposes several REST endpoints. For detailed information, please refer to the controller classes:

*   **Public:** [`PublicController.java`](src/main/java/com/chef/V1/controller/PublicController.java)
    - Includes login, registration, password reset, and email verification endpoints
*   **User:** [`UserController.java`](src/main/java/com/chef/V1/controller/UserController.java)
    - User profile management and secure logout functionality
*   **Admin:** [`AdminController.java`](src/main/java/com/chef/V1/controller/AdminController.java)
    - Administrative functions with enhanced security
*   **Google Auth:** [`GoogleAuthController.java`](src/main/java/com/chef/V1/controller/GoogleAuthController.java)
    - OAuth2 authentication handling
*   **Payment:** [`PaymentController.java`](src/main/java/com/chef/V1/controller/PaymentController.java)

## Security Features

- **JWT Token Management:** Secure token handling with Redis-based blacklisting on logout
- **Password Reset Security:** Time-limited reset tokens with rate limiting to prevent abuse
- **Email Verification:** Secure email verification with automatic token expiration
- **Session Management:** Complete session cleanup and cross-device security