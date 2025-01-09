package org.adonix.postrise;

public class JsonConfigurationException extends RuntimeException {

    public JsonConfigurationException(final Throwable t) {
        super(t);
    }

    public JsonConfigurationException(final String message, final Throwable t) {
        super(message, t);
    }
}
