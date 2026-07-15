# Rabitah

Rabitah is a Java 21 university social and academic desktop application. The repository contains a Spring Boot API and a non-modular JavaFX client. The implementation is being constructed incrementally according to `IMPLEMENTATION_STATUS.md`.

## Prerequisites

- Java 21
- Maven 3.9+
- Docker with Compose, or PostgreSQL 14+

## Local setup

1. Copy `.env.example` to `.env` and replace both sample security values.
2. Start PostgreSQL: `docker compose up -d postgres`.
3. Export the values from `.env` in your shell.
4. Start the API: `mvn -f Rabitah-Backend/pom.xml spring-boot:run`.
5. Confirm `http://localhost:8080/actuator/health` returns `UP`.
6. In another terminal, start JavaFX: `mvn -f Rabitah-Frontend/pom.xml javafx:run`.

The base seed creates `SYSADMIN`, 720 deterministic roster entries, and 24 community rooms. Use the password supplied through `RABITAH_SYSTEM_ADMIN_PASSWORD`. No runtime secret is committed.

## Verification

Run `mvn test` from the repository root. The backend uses Flyway as the only schema owner and Hibernate validates the migrated schema.

## Eclipse

Use **File → Import → Existing Maven Projects**, select this repository, and import both child projects. Ensure Eclipse uses the Java 21 JDK. Run `RabitahBackendApplication` for the API and `RabitahApplication` for the desktop client.

## Current scope

Consult `IMPLEMENTATION_STATUS.md` before demonstration. Features not marked complete there must not be represented as working.
