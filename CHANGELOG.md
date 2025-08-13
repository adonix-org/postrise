# :memo: Changelog

All notable changes to **Postrise** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and **Postrise** adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

## [1.0.7] - 2025-08-13

### Changed

-   Updated dependencies via Dependabot:
    -   com.zaxxer:HikariCP from 6.3.0 to 7.0.1
    -   io.github.hakky54:logcaptor from 2.11.0 to 2.12.0
    -   org.assertj:assertj-core from 3.27.3 to 3.27.4
    -   org.apache.logging.log4j:log4j-api from 2.24.3 to 2.25.1
    -   org.apache.maven.plugins:maven-gpg-plugin from 3.2.7 to 3.2.8
    -   org.junit.jupiter:junit-jupiter-engine from 5.13.1 to 5.13.4
    -   org.junit.jupiter:junit-jupiter-params from 5.13.1 to 5.13.4
    -   org.sonatype.central:central-publishing-maven-plugin from 0.7.0 to 0.8.0
    -   org.testcontainers:postgresql from 1.21.1 to 1.21.3

## [1.0.6] - 2025-06-12

### Changed

-   org.postgresql:postgresql from 42.7.6 to 42.7.7
-   Updated dependencies via Dependabot:
    -   org.junit.jupiter:junit-jupiter-engine from 5.13.0 to 5.13.1
    -   org.junit.jupiter:junit-jupiter-params from 5.13.0 to 5.13.1

## [1.0.5] - 2025-06-02

### Changed

-   Updated dependencies via Dependabot:
    -   org.junit.jupiter:junit-jupiter-engine from 5.12.2 to 5.13.0
    -   org.junit.jupiter:junit-jupiter-params from 5.12.2 to 5.13.0
    -   org.postgresql:postgresql from 42.7.5 to 42.7.6
    -   org.testcontainers:postgresql from 1.21.0 to 1.21.1

### Fixed

-   Clear DataSource listeners on server close.

## [1.0.4] - 2025-05-20

### Fixed

-   Synchronized sets are not inherently thread-safe during iteration and require explicit synchronization.

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
