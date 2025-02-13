package org.adonix.postrise.servers;

import org.testcontainers.containers.PostgreSQLContainer;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;

public class StaticPortServer extends PostgresDocker {

    private static final Integer HOST_PORT = 5801;

    public StaticPortServer() {
        super();
        container.withExposedPorts(PostgreSQLContainer.POSTGRESQL_PORT)
                .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                        new HostConfig().withPortBindings(
                                new PortBinding(Ports.Binding.bindPort(HOST_PORT),
                                        new ExposedPort(PostgreSQLContainer.POSTGRESQL_PORT)))));
    }
}
