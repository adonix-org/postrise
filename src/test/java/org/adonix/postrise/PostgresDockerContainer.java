package org.adonix.postrise;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresDockerContainer extends PostgreSQLContainer<PostgresDockerContainer> {

    private static final String POSTGRES_IMAGE_NAME = "postgres:17";

    public PostgresDockerContainer() {
        super(POSTGRES_IMAGE_NAME);
    }
}
