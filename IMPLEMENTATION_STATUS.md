# Implementation Status

## Phase 1 — Repository skeleton and Maven configuration

- Status: complete
- Created the backend, frontend, web, documentation, script, and deployment layout.
- Configured Java 21, Spring Boot 3.5.16, JavaFX 21.0.11, and required dependencies.

## Phase 2 — PostgreSQL, Flyway, and seed data

- Status: foundation complete; database integration gate still pending
- Added the full initial relational schema, indexes, constraints, configurable deterministic roster seed, reference data, 24 community rooms, and environment-secured system administrator.

## Phase 3 — Authentication and JWT

- Status: partial
- Student roster registration, pending status, BCrypt verification, login, JWT issuance, and inactive-account rejection are implemented.
- Refresh rotation, logout revocation, privileged verification-code consumption, and token-version request validation remain outstanding.

## Phase 11 — JavaFX authentication and main shell

- Status: partial
- Added a non-modular JavaFX application, FXML login/shell, background HTTP client, in-memory tokens, session routing, and logout.
- Registration and role-specific navigation remain outstanding.

## Phases 4–10 and 12–14

- Status: not complete
- Database tables and deployment foundations exist, but the required services, endpoints, screens, demo seeding, acceptance coverage, CI, and production storage profiles are not yet implemented.

## Verification log

- 2026-07-15: `/tmp/apache-maven-3.9.11/bin/mvn -q -DskipTests package` — passed.
- 2026-07-15: `/tmp/apache-maven-3.9.11/bin/mvn -q test` — passed (backend roster-normalization test and frontend FXML resource tests).
