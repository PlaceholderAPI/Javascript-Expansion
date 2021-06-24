package com.extendedclip.papi.expansion.javascript.evaluator.interop.provider;

import java.lang.reflect.Method;
import java.util.Collection;

public interface MethodProvider {
    Collection<Method> fetchMethods(final Object object);
    Collection<Method> fetchStaticMethods(final Class<?> clazz);
}
