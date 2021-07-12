package com.extendedclip.papi.expansion.javascript.evaluator;

public final class LibraryInjectionException extends RuntimeException {
    public LibraryInjectionException(final Throwable cause) {
        super(String.format("Java Version: %s", System.getProperty("java.version")),cause);
    }
}
