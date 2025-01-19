package org.adonix.postrise;

interface DatabaseNameProvider {
    /**
     * Get the name of the database for the configuration.
     * 
     * @return the database name.
     */
    String getDatabaseName();
}
