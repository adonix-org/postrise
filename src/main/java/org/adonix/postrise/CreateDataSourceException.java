package org.adonix.postrise;

public class CreateDataSourceException extends RuntimeException {

    private static final String ERROR_MESSAGE = "Unable to create data source";

    protected CreateDataSourceException(final Throwable t) {
        super(ERROR_MESSAGE, t);
    }
}
