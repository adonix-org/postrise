# :memo: Changelog

All notable changes to **Postrise** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and **Postrise** adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Fixed

## [1.0.4] - 2025-05-??

### Fixed

-   The dataSourceListeners synchronized set is not thread-safe during iteration and requires manual synchronization

## [1.0.3] - 2025-05-17

### Fixed

-   Only log the data source creation **after** all the `beforeCreate` events have been dispatched.

## [1.0.2] - 2025-05-12

### Added

-   Changelog.

### Fixed

-   Dispatch the server `beforeClose` event **prior** to updating the server state to `CLOSING`.

## [1.0.1] - 2025-05-05

### Added

-   [Javadoc](https://javadoc.io/doc/org.adonix/postrise) inline comments.
-   Populated [README.md](README.md) with full project documentation.

## [1.0.0] - 2025-03-12

### Added

-   Initial release with [PostgreSQL](https://www.postgresql.org) support.
-   Role and permissions management.
-   Delegation to [HikariCP](https://github.com/brettwooldridge/HikariCP) connection pooling.
-   DataSource and Database events.
-   Thread-safe server state management.
-   [JUnit5](https://junit.org/junit5/) tests.
