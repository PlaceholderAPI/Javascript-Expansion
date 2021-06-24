package com.extendedclip.papi.expansion.javascript.evaluator.interop.provider;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

public interface FieldProvider {
    Collection<Field> fetchFields(final Object object);
    Collection<Field> fetchStaticFields(final Class<?> clazz);
}
