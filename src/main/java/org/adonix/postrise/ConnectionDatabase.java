package org.adonix.postrise;

interface ConnectionDatabase {
    /**
     * Get the name of the database for the configuration.
     * 
     * @return the database name.
     */
    String getDatabaseName();
}
