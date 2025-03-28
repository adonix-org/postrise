[![JDK Compatibility](https://img.shields.io/badge/JDK_-11+-blue.svg)](https://www.oracle.com/java/technologies/downloads/)
[![Apache2 License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://github.com/adonix-org/postrise/blob/main/LICENSE)
[![Build Status](https://github.com/adonix-org/postrise/actions/workflows/build.yml/badge.svg)](https://github.com/adonix-org/postrise/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=org.adonix%3Apostrise&metric=alert_status)](https://sonarcloud.io/summary/overall?id=org.adonix%3Apostrise)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=org.adonix%3Apostrise&metric=security_rating)](https://sonarcloud.io/summary/overall?id=org.adonix%3Apostrise)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.adonix%3Apostrise&metric=coverage)](https://sonarcloud.io/summary/overall?id=org.adonix%3Apostrise)

<a href="https://postrise.adonix.org">
    <picture>
        <source srcset="./img/header-dark.png" media="(prefers-color-scheme: dark)">
        <img src="./img/header-light.png" alt="Postrise" height="auto" width="550px"></img>
    </picture>
</a>

Get connected *FAST* with **Postrise**, a thread-safe Java library for developers to acquire pooled JDBC connections from PostgreSQL. **Postrise** provides a simple, object-oriented solution for configuring data sources while encouraging safe database access. The event-based architecture enables subscriptions to the data source lifecycle. Connection pooling is provided by the exceptional [HikariCP](https://github.com/brettwooldridge/HikariCP) implementation.

![Code](./img/code.png)

## ⚙️ Install

💡 Find the latest **Postrise** version and additional installation snippets in the [Maven Central Repository](https://central.sonatype.com/artifact/org.adonix/postrise).

#### Maven

```xml
<properties>
    <version.postrise>1.0.0</version.postrise>
</properties>

<dependency>
    <groupId>org.adonix</groupId>
    <artifactId>postrise</artifactId>
    <version>${version.postrise}</version>
    <scope>compile</scope>
</dependency>
```

#### Gradle

```gradle
dependencies {
    implementation 'org.adonix:postrise:1.0.0'
}
```

## ⏱️ Quickstart

Create and configure PostgreSQL data sources.

⚠️ An exception will be thrown by **Postrise** if connecting as a `SUPERUSER`. See [Security](#-security) to create a `NOSUPERUSER` role or bypass this behavior if required.

Create a Java `class` which extends PostgresServer:

```java

import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.PostgresServer;

public class MyPostgresServer extends PostgresServer {

}
```

`@Override` superclass methods to connect to your PostgreSQL server as required.

#### Host:

```java
/**
 * Default: "localhost"
 */
@Override
public String getHostName() {
    return "db.mydomain.com";
}
```

#### Port:

```java
/**
 * Default: 5432
 */
@Override
public Integer getPort() {
    return 5433;
}
```

#### Create DataSource Event:

```java
@Override
public void beforeCreate(final DataSourceSettings settings) {

    // Not required, but logs the create event.
    super.beforeCreate(settings);

    // Default username is the current OS user.
    settings.setUsername("my_login_user");

    // Either set the password, or use pg_hba.conf to configure
    // secure access for your user.
    settings.setPassword("In1g0M@nt0Ya");

    // For all other settings, it is recommended to begin with
    // the default values.
}
```

🔗 See also [pg_hba.conf](https://www.postgresql.org/docs/current/auth-pg-hba-conf.html) and [HikariCP ](https://github.com/brettwooldridge/HikariCP?tab=readme-ov-file#frequently-used)documentation.

##

After your server has been configured, it's time to create a new instance. The data source and connection pool will be created on demand when a connection is requested by your application. Your new server implements the `AutoCloseable` interface, and all data sources will be closed when the server is closed. The instantiation and closure details of **Postrise** servers will depend on your application, but here is a simple example:

```java
import java.sql.Connection;
import org.adonix.postrise.Server;

public class MyApp {

    // Your PostgreSQL server. Also could be declared in the
    // try-with-resources scope of the application.
    private static final Server server = new MyPostgresServer();

    public static void main(String[] args) throws Exception {
        // A new data source is created on the first connection
        // request to the database.
        try (final Connection connection = server.getConnection("my_database")) {
            // Do something with this connection.
        } finally {
            // Closes the server and all data sources.
            server.close();
        }
    }
}
```

Or if you delegate to  a `NOLOGIN` role:

```java
try (final Connection connection = server.getConnection("my_database", "my_application_role")) {
    // The current_user for this connection is now "my_application_role".
}
```

## ⚡ Events

## 🔒 Security

If a non-privileged `ROLE` does not exist, create a secure PostgreSQL `LOGIN` role **without** `SUPERUSER` privileges:

```sql
-- Recreate if exists
DROP ROLE IF EXISTS my_login_user;

-- The LOGIN role is NOSUPERUSER.
CREATE ROLE my_login_user
            LOGIN
            NOSUPERUSER
            NOCREATEDB
            NOCREATEROLE
            NOINHERIT
            NOBYPASSRLS;
```

💡 Grant the minimally required database and table permissions to this `ROLE`, or delegate those permissions to a `NOLOGIN` role that the `LOGIN` role can switch to with `SET ROLE` as follows:

```sql
-- Recreate if exists
DROP ROLE IF EXISTS my_application_role;

-- The application role cannot LOGIN.
CREATE ROLE my_application_role
            NOLOGIN
            NOSUPERUSER
            NOCREATEDB
            NOCREATEROLE
            NOINHERIT
            NOBYPASSRLS;

-- Allow the LOGIN user to switch to this ROLE.
GRANT my_application_role TO my_login_user;

-- The application role can only SELECT from my_table.
GRANT SELECT ON my_table TO my_application_role;
```

Session and current users can be queried with this SQL:

```sql
SELECT session_user, current_user;
```

An example result set:
| **session_user** | **current_user** |
|------------------|------------------|
| my_login_user | my_application_role |

🔗 See also [Database Roles](https://www.postgresql.org/docs/current/database-roles.html), [Grant](https://www.postgresql.org/docs/current/sql-grant.html).

## 🛠️ Build

**Postrise** is a simple Java library that can easily be cloned and built locally.

⚠️ A few prerequisites **must** be installed before building:

-   [JDK 11+](https://www.oracle.com/java/technologies/downloads/) - the latest Long-Term Support (LTS) version is JDK 21.
-   [Maven](https://maven.apache.org/download.cgi) - may already be installed with your IDE.
-   [Docker](https://www.docker.com) - must be installed and running.

##

💡 Before continuing, use this command to verify the expected Maven and Java versions are on your PATH:

```bash
mvn -v
```

The result should look similar to this with variances for OS and versions:

```bash
Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Maven home: /Users/Inigo/Programs/apache-maven-3.9.9
Java version: 23, vendor: Oracle Corporation, runtime: /Library/Java/JavaVirtualMachines/jdk-23.jdk/Contents/Home
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "15.3.2", arch: "aarch64", family: "mac"
```

##

Create a working folder where the **Postrise** project will be installed and use the command-line to clone the repository into that folder:

```bash
git clone https://github.com/adonix-org/Postrise.git
```

Next use that same command-line to switch to the **Postrise** folder.

```bash
cd Postrise
```

Finally, run this Maven command to build and test **Postrise**:

```bash
mvn clean verify
```

Or use this Maven command to build, test, and install **Postrise**:

```bash
mvn clean install
```
