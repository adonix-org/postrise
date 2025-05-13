# :memo: Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed

### Added

## [1.0.2] - 2025-05-12

### Fixed

-   Dispatch the server beforeClose event prior to changing the server state to CLOSING.

## [1.0.1] - 2025-05-05

### Added

-   Completed [Javadoc](https://javadoc.io/doc/org.adonix/postrise).
-   Populated existing [README.md](README.md) with full project documentation.

## [1.0.0] - 2025-03-12

### Added

-   Initial release with [PostgreSQL](https://www.postgresql.org) support.
-   Role and permissions management.
-   Delegation to [HikariCP](https://github.com/brettwooldridge/HikariCP) connection pooling.
-   Datasource and Database events.
-   Thread-safe server state management.
-   [JUnit5](https://junit.org/junit5/) tests.
