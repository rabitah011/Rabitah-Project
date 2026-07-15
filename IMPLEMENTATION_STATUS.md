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

- Status: partially implemented
- Working vertical slices now include social feed creation/listing, likes/dislikes, comments, official notices, PDF question-paper upload/search/download, persisted private conversations/messages, scoped community history/announcements, and a six-module JavaFX platform shell.
- Advanced moderation queues, WebSocket live delivery, polls, refresh-token rotation, full CRUD, academic rollover screens, production object storage, and the complete acceptance matrix remain outstanding.

## Verification log

- 2026-07-15: `/tmp/apache-maven-3.9.11/bin/mvn -q -DskipTests package` — passed.
- 2026-07-15: `/tmp/apache-maven-3.9.11/bin/mvn -q test` — passed (backend roster-normalization test and frontend FXML resource tests).
- 2026-07-15: `xvfb-run -a /tmp/apache-maven-3.9.11/bin/mvn -q clean test` — passed after password-input fix.
- 2026-07-15: Login UI test confirmed editable masked input, synchronized visible input, and password visibility toggle.
- 2026-07-15: Live API checks passed for valid login (200), invalid password (401), blank credentials (400), and health (UP).
- 2026-07-15: Live PostgreSQL checks confirmed 720 roster rows, 24 community rooms, and one system administrator.
- 2026-07-15: Replaced dashboard label swapping with native Dashboard and My Profile tabs after a navigation defect.
- 2026-07-15: Clean regression build passed with 1 backend test and 3 frontend tests, including profile-tab selection and authenticated identity rendering.
- 2026-07-15: Replaced profile tab switching with a dedicated profile scene and visible build 1.0.2 marker.
- 2026-07-15: Real-Stage regression fired My Profile, verified the scene transition and identity values, fired Back, and verified the Dashboard return; clean build passed with zero failures.
- 2026-07-15: Flyway V2 migrated platform feature columns and live acceptance tests passed for feed, reaction, comment, notices, courses, PDF upload, private messages/history, and scoped community room access.
- 2026-07-15: Expanded JavaFX client to Social Feed, Notice Board, Question Repository, Private Chat, Community, and My Profile tabs; clean reactor build and tests passed.
- 2026-07-15: Fixed X11 hit-testing offset on 1366x768 Parrot Debian by fitting the stage to visual bounds, retaining one stable Scene, forcing GTK scale 1.0, disabling HiDPI remapping, and removing modal error overlays; clean build passed.
