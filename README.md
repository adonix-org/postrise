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

Create and configure your PostgreSQL data source connections.

⚠️ An exception will be thrown by **Postrise** if connecting as a `SUPERUSER`. See [Security](#-security) for details on how to bypass this behavior if required.

If a non-privileged `ROLE` does not exist, create a secure PostgreSQL `LOGIN` role **without** `SUPERUSER` privileges:

```sql
DROP ROLE IF EXISTS my_login_user;

CREATE ROLE my_login_user
            LOGIN
            NOSUPERUSER
            NOCREATEDB
            NOCREATEROLE
            NOINHERIT
            NOBYPASSRLS;
```

Create a Java `class` which extends PostgresServer:

```java

import org.adonix.postrise.DataSourceSettings;
import org.adonix.postrise.PostgresServer;

public class MyPostgresServer extends PostgresServer {

}
```

`@Override` the configuration defaults of any method to connect to your PostgreSQL server.

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

    // Default username is the logged in user.
    settings.setUsername("my_login_user");

    // Either set the password, or use pg_hba.conf to configure
    // secure access for your user.
    settings.setPassword("In1g0M@nt0Ya");

    // For all other settings, it is recommended to begin with
    // the default values.
}
```

💡 See also [pg_hba.conf](https://www.postgresql.org/docs/current/auth-pg-hba-conf.html) and [HikariCP ](https://github.com/brettwooldridge/HikariCP?tab=readme-ov-file#frequently-used)documentation.

## ⚡ Events

## 🔒 Security

## 🛠️ Build

**Postrise** is a simple Java library that can easily be cloned and built locally.

⚠️ A few prerequisites **must** be installed locally before building:

-   [JDK 11+](https://www.oracle.com/java/technologies/downloads/) - the current Long-Term Support (LTS) version is JDK 21.
-   [Maven](https://maven.apache.org/download.cgi) - may already be installed with your IDE.
-   [Docker](https://www.docker.com) - must be installed and running.

##

💡 Before continuing, use this command to verify the expected Maven and Java versions are on your PATH:

```bash
mvn -v
```

The result should look something like this, with variance for OS and versions:

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
