package com.extendedclip.papi.expansion.javascript.evaluator.binding;

public final class BindingException extends Exception {
    public BindingException(Throwable cause) {
        super("Failed to bind variable.", cause);
    }
}
