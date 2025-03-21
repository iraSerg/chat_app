# Chat App

## Description

Real-time chat application using Spring Boot and WebSocket.

## Technologies Used

*   Spring Boot
*   Spring Data JPA
*   Spring Data MongoDB
*   Spring Security
*   Spring WebSocket
*   Spring Session
*   Redis
*   RabbitMQ
*   PostgreSQL
*   MongoDB
*   JUnit
*   Mockito
*   TestContainers
*   Thymeleaf
*   JQuery
*   Bootstrap
*   Apache Maven

## Setup

1.  **Clone this repository:**
    ```bash
    git clone https://github.com/iraSerg/chat_app.git
    ```

2.  **Enter the project directory:**
    ```bash
    cd chat_app
    ```

3.  **Set up the dependencies (PostgreSQL, MongoDB, Redis, and RabbitMQ):**

    It assumed that you have docker and docker-compose installed.

    ```bash
    docker-compose -f docker-compose/docker-compose.yaml up
    ```
    Make sure you have configured connection settings to these databases in `src/main/resources/application.properties` or `application.yml`.


4.  **Run the application :**

    To run application. You should do clean to clean any old before you do run and install
    ````bash
    ./mvnw clean install
    ````

    Then run as
     ```bash
    ./mvnw spring-boot:run
    ```
## Usage

Open your web browser and navigate to `http://localhost:8080/login`

## Configuration

The main application settings are located in the `application.yml` file inside your project.

Make sure you set email settings if you want to use reset password functionality: `spring.mail.*`