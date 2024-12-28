package org.adonix.postrise.exception;

public class CreateDataSourceException extends RuntimeException {

    private static final String ERROR_MESSAGE = "Unable to create data source";

    public CreateDataSourceException(final Throwable t) {
        super(ERROR_MESSAGE, t);
    }
}
