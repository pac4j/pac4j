# AGENTS.md

This file provides guidance when working with code in this repository.

## Build, validation, and test commands

This repository is a Maven multi-module project (`pom.xml` at the root) targeting Java 17.

- Build all modules:
  - `mvn clean install`
- Fast verification pipeline (includes Checkstyle at `validate`, PMD + SpotBugs at `compile`, unit tests via Surefire):
  - `mvn clean verify`
- Run unit tests for all modules:
  - `mvn test`
- Run tests for one module:
  - `mvn -pl pac4j-core test`
- Run one test class in one module:
  - `mvn -pl pac4j-core -Dtest=ConfigTests test`
- Run one test method:
  - `mvn -pl pac4j-core -Dtest=ConfigTests#testMethodName test`
- Run integration tests (`*IT.java`), which are disabled by default:
  - `mvn -PforceIT verify`
- Build one module with required upstream modules:
  - `mvn -pl pac4j-saml -am clean install`

Static analysis configuration files are at the repo root:
- `checkstyle.xml`
- `spotbugs-exclude.xml`

## High-level architecture

### Module layout and dependency direction

- `pac4j-core` is the foundation: shared interfaces and default runtime engine logic.
- Feature modules build on `pac4j-core` and implement protocol/auth mechanisms:
  - `pac4j-oauth`, `pac4j-oidc`, `pac4j-saml`, `pac4j-cas`, `pac4j-http`, `pac4j-ldap`, `pac4j-sql`, `pac4j-jwt`, `pac4j-mongo`, `pac4j-couch`, `pac4j-kerberos`, `pac4j-gae`.
- `pac4j-config` builds `Config` from properties and conditionally wires clients/authenticators based on classpath + properties.
- Environment/framework integration modules:
  - `pac4j-javaee` and `pac4j-jakartaee` adapt servlet APIs to pac4j context abstractions.
  - `pac4j-springboot` autoconfigures a `Config` bean using `PropertiesConfigFactory`.

### Core runtime model (big picture)

The central object is `org.pac4j.core.config.Config`, which aggregates:
- `Clients` (authentication entry points),
- `Authorizers` and `Matchers`,
- logic engines (`SecurityLogic`, `CallbackLogic`, `LogoutLogic`),
- context/session/profile/http-action factories/adapters.

`Config` also pushes itself into each `BaseClient` so clients can access shared runtime config.

`Clients` is the runtime registry:
- validates unique client names,
- applies shared callback URL/resolvers/generators to indirect/base clients,
- resolves clients by name through an initialized map.

### Request flow across engine classes

- **Security flow** (`DefaultSecurityLogic`):
  1. Build `CallContext` from framework parameters.
  2. Resolve active clients.
  3. Apply matchers; if not matched, grant access immediately.
  4. Load profiles through `ProfileManager`.
  5. If no profile, try direct-client authentication.
  6. If profiles exist, run authorizers; grant or forbid.
  7. If still unauthenticated, either start indirect redirect (and save requested URL) or return unauthorized.

- **Callback flow** (`DefaultCallbackLogic`):
  1. Resolve exactly one indirect callback client.
  2. Extract and validate credentials.
  3. Build and save profile, optionally renew session.
  4. Redirect to saved requested URL (or default URL).

- **Logout flow** (`DefaultLogoutLogic`):
  1. Resolve profiles and redirect target (validated by logout URL regex).
  2. Perform local logout/profile removal and optional session destruction.
  3. Optionally perform central logout by delegating to client-specific logout action.

### Context and profile lifecycle

- `CallContext` carries the trio: `WebContext`, `SessionStore`, and `ProfileManagerFactory`.
- `ProfileManager` stores profiles in request and/or session under pac4j constants, supports multi-profile mode, and can renew expired profiles via client `renewUserProfile`.

### Configuration assembly points

- Properties-based assembly is in `pac4j-config` via `PropertiesConfigFactory`.
  - It conditionally creates encoders, authenticators, and clients (OAuth/OIDC/SAML/CAS/HTTP, etc.) from properties.
- Spring Boot integration (`pac4j-springboot`) autowires properties and exposes a default `Config` bean through `ConfigAutoConfiguration`.

## Documentation subtree

- `documentation/README.md` describes local docs preview:
  - `bundle exec jekyll serve`
