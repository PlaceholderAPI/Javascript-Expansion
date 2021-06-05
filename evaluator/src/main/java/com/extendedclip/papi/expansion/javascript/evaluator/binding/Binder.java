package com.extendedclip.papi.expansion.javascript.evaluator.binding;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public abstract class Binder {
    protected static final Set<String> EXCLUDED_METHODS = new HashSet<>();
    protected static final Method FIELD_GET;
    protected static final Method FIELD_SET;
    static {
        for (Method method : Object.class.getMethods()) {
            if (method.getParameterCount() == 0) {
                String methodName = method.getName();
                EXCLUDED_METHODS.add(methodName);
            }
        }
        for (Method method : Class.class.getMethods()) {
            if (method.getParameterCount() == 0) {
                String methodName = method.getName();
                EXCLUDED_METHODS.add(methodName);
            }
        }
        try {
            FIELD_GET = Field.class.getMethod("get", Object.class);
        } catch (final NoSuchMethodException e) {
            throw new AssertionError("Unable to create field get",e);
        }

        try {
            FIELD_SET = Field.class.getMethod("set", Object.class, Object.class);
        } catch (final NoSuchMethodException e) {
            throw new AssertionError("Unable to create field set",e);
        }
    }
    public abstract void bind(final Object object, final Bindable valueObject) throws BindingException;
}
