package org.adonix.postrise;

public class JsonConfigurationException extends RuntimeException {

    public JsonConfigurationException(final Exception e) {
        super(e);
    }

    public JsonConfigurationException(final String message, final Exception e) {
        super(message, e);
    }
}
