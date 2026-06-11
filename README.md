# Spring Boot JWT Spring Security

A Spring Boot application that demonstrates user authentication and authorization using Spring Security and JSON Web Tokens (JWT).

## Overview

This project implements a user management API with login, registration, JWT-based authentication, role-based access control, and basic user administration features.

## Tech Stack

- Java 11
- Spring Boot 2.6.3
- Spring Security
- Spring Data JPA
- MySQL
- JWT
- Maven
- Lombok

## Features

- User registration
- User login
- JWT token generation
- Role-based authorization
- User listing
- Find user by username
- Add new users
- Update user information
- Delete users
- Reset password
- Upload/update profile image

## API Endpoints

| Method | Endpoint | Description |
|----------|----------|----------|
| POST | `/user/login` | Authenticates a user and returns a JWT token in the response header |
| POST | `/user/register` | Registers a new user |
| POST | `/user/add` | Adds a new user |
| POST | `/user/update` | Updates an existing user |
| GET | `/user/find/{username}` | Finds a user by username |
| GET | `/user/list` | Returns all users |
| GET | `/user/resetPassword/{email}` | Resets a user's password |
| DELETE | `/user/delete/{id}` | Deletes a user |
| POST | `/user/updateProfileImage` | Updates a user's profile image |

## Database Configuration

The application uses MySQL.

Default configuration:

```yml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/support
    username: root
    password: mysqldb
```

## Security

The application uses Spring Security with JWT authentication. After a successful login, the JWT token is returned in the response header and should be included in future requests that require authentication.

## Project Structure

```text
src/main/java/com/learn/support
├── configuration
├── constant
├── domain
├── enumeration
├── exception
├── filter
├── listener
├── repository
├── resource
├── service
├── utility
└── SupportApplication.java
```

## Notes

This project was built as a learning project to practice Spring Boot security concepts, including authentication, authorization, JWT generation, user roles, and secured REST endpoints.

## Author

Marlon Braga
