# grep_backend

This repository contains the backend for the Grep Web Application.

## Tech stack

The backend is built using the following technologies:

- [Kotlin](https://kotlinlang.org/) - A modern programming language that is fully interoperable with Java and is used for building server-side applications.
- [Spring Boot](https://spring.io/projects/spring-boot) - A framework for building production-ready applications with Java and Kotlin.
- [Spring Security](https://spring.io/projects/spring-security) - A powerful and customizable authentication and access control framework for Java applications.
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - A part of the Spring Data project that makes it easy to implement JPA-based repositories.
- [MySQL](https://www.mysql.com/) - An open-source relational database management system that is used to store the application's data.
- [Maven](https://maven.apache.org/) - A build automation tool used primarily for Java projects.

## Setup project

First clone the repository:

```bash
git clone https://github.com/borgaar/grep-backend && cd grep-backend
```

### Prerequisites
This is what you will need, depending on how you want to run the project.

- [Java 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) - The Java Development Kit (JDK) is required to run the application.
- [Maven](https://maven.apache.org/) - A build automation tool used primarily for Java projects.
- [Docker](https://www.docker.com/) - A platform for developing, shipping, and running applications in containers.

## Running the project

Follow the instructions below to run the project locally:

```
./db.sh # Starts the MySQL database
mvn clean
mvn spring-boot:run
```

To populate the database with some initial mock-data, you can run the `mockdata.sql` script.

**Note:** The database will be wiped everytime the server starts. To stop this, comment out (add `#` at the start of the line) `spring.sql.init.mode=always` in `src/main/resources/application.properties`. Note that the schema.sql must then be manually run after the database is started.

## Run the test

To run the tests, you can use the following command:

```
./db.sh # Starts the MySQL database
mvn clean test
```

### API Documentation

The API documentation can be found by running the backend server and navigating to `http://localhost:8080/swagger-ui/index.html` in your browser.
