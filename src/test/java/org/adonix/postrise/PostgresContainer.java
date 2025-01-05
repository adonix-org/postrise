package org.adonix.postrise;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainer extends PostgreSQLContainer<PostgresContainer> {

    private static final String POSTGRES_IMAGE_NAME = "postgres:17";

    public String getImageName() {
        return POSTGRES_IMAGE_NAME;
    }

    public PostgresContainer() {
        super(POSTGRES_IMAGE_NAME);
    }

    @Override
    public String toString() {
        return POSTGRES_IMAGE_NAME;
    }
}
