package org.adonix.postrise.servers;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;

public class StaticPortServer extends PostgresDocker {

    private static final int FIXED_HOST_PORT = 5801; // Always use this port on the host
    private static final int CONTAINER_PORT = 5432; // Default Postgres port inside the container

    public StaticPortServer() {
        super();
        container.withExposedPorts(CONTAINER_PORT) // Expose the default Postgres port
                .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                        new HostConfig().withPortBindings(
                                new PortBinding(Ports.Binding.bindPort(FIXED_HOST_PORT),
                                        new ExposedPort(CONTAINER_PORT)))));

    }
}
