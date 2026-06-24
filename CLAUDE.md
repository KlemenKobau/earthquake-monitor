# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Spring Boot 4.1.0 application (Java 25) that will ingest earthquake data from the USGS GeoJSON feed
(`https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php`, spec at https://geojson.org/) and
persist/serve it. The codebase is currently a skeleton: `client/` and `dto/` packages exist but are
empty, and there are no Flyway migrations yet.

## Build & Run

This project uses the Maven wrapper — always invoke `./mvnw`, not a system-installed `mvn`.

- Build: `./mvnw clean install`
- Run the app: `./mvnw spring-boot:run`
- Run all tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=EarthquakemonitorApplicationTests`
- Run a single test method: `./mvnw test -Dtest=EarthquakemonitorApplicationTests#methodName`

Local Postgres for development is provided via Docker Compose (`compose.yaml`) and is started
automatically by Spring Boot's Docker Compose support when running the app — no manual
`docker compose up` is needed.

## Architecture

- **Database**: PostgreSQL, schema managed via Flyway (`spring-boot-starter-flyway`,
  `flyway-database-postgresql`). Migrations belong in `src/main/resources/db/migration`.
- **Persistence**: Spring Data JPA.
- **Web layer**: Spring MVC (`spring-boot-starter-webmvc`).
- **Tests**: JUnit via `spring-boot-starter-*-test` starters, with Testcontainers
  (`testcontainers-postgresql`) providing a real Postgres instance for integration tests. The
  shared container setup lives in `TestcontainersConfiguration` (test source), wired up via
  `@ServiceConnection` so Spring Boot auto-configures the datasource — don't hand-roll connection
  properties in tests.
  - `TestEarthquakemonitorApplication` / `TestcontainersConfiguration` under
    `src/test/java/com/kkobau/earthquakemonitor/` is the entry point for running the app locally
    with the Testcontainers-backed Postgres instead of Docker Compose.
- **Package layout** (`src/main/java/com/kkobau/earthquakemonitor/`):
  - `client/` — intended for the USGS feed HTTP client.
  - `dto/` — intended for GeoJSON DTOs modeling the USGS feed response (FeatureCollection,
    Feature, Geometry, properties, per the GeoJSON spec).
  - Root package holds `EarthquakemonitorApplication` (the `@SpringBootApplication` entry point).
- Lombok is available (compile-time only, excluded from the runnable jar via the Spring Boot Maven
  plugin's `<excludes>` config) — use it for DTO/entity boilerplate instead of hand-writing
  getters/setters/constructors.
