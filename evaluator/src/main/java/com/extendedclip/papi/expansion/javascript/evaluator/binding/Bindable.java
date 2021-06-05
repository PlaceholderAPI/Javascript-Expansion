package com.extendedclip.papi.expansion.javascript.evaluator.binding;

import java.lang.reflect.Method;

public interface Bindable {
    void bindMethod(final Object object, final String name, final Method method) throws BindingException;
    void bindProperty(final Object object, final String name, final Method getter, final Method setter) throws BindingException;
}
