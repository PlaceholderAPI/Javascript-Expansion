package com.extendedclip.papi.expansion.javascript.evaluator.binding.javet;

import com.extendedclip.papi.expansion.javascript.evaluator.binding.Bindable;
import com.extendedclip.papi.expansion.javascript.evaluator.binding.Binder;
import com.extendedclip.papi.expansion.javascript.evaluator.binding.BindingException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ObjectMethodBinder extends Binder {
    @Override
    public void bind(final Object object, final Bindable valueObject) throws BindingException {
        if (object instanceof Class<?>) {
            throw new IllegalArgumentException("Cannot bind with class instance");
        }
        final Class<?> clazz = object.getClass();
        for (final Method method: clazz.getMethods()) {
            final String name = method.getName();
            if (EXCLUDED_METHODS.contains(name)) {
                continue;
            }
            valueObject.bindMethod(object, name, method);
        }

        for (final Field field: clazz.getFields()) {
            final String name = field.getName();
            if (EXCLUDED_METHODS.contains(name)) {
                continue;
            }
            valueObject.bindProperty(object, name, FIELD_GET, FIELD_SET);
        }
    }
}
