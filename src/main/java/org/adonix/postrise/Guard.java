package org.adonix.postrise;

abstract class Guard {

    protected static final String NULL_OBJECT_ERROR = "Unexpected null Object for ";
    protected static final String NULL_STRING_ERROR = "Unexpected null String for ";
    protected static final String BLANK_STRING_ERROR = "Unexpected blank String for ";

    private Guard() {
    }

    public static final void check(final String parameterName, final Object parameter) throws IllegalArgumentException {
        if (parameter == null) {
            throw new IllegalArgumentException(NULL_OBJECT_ERROR + parameterName);
        }
    }

    public static final void check(final String parameterName, final String parameter) throws IllegalArgumentException {
        if (parameter == null) {
            throw new IllegalArgumentException(NULL_STRING_ERROR + parameterName);
        }
        if (parameter.isBlank()) {
            throw new IllegalArgumentException(BLANK_STRING_ERROR + parameterName);
        }
    }
}
