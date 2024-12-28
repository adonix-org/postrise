package org.adonix.postrise;

class PostgresDataSource extends PostriseDataSource {

    private static final String POSTGRES_URL_PREFIX = "jdbc:postgresql://";

    public PostgresDataSource(final Server server, final String database) {
        setJdbcUrl(POSTGRES_URL_PREFIX + server.getHostname() + ":" + server.getPort() + "/" + database);
        addDataSourceProperty("tcpKeepAlive", "true");
    }
}
