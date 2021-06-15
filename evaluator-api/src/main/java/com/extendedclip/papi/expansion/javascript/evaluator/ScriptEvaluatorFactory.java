package com.extendedclip.papi.expansion.javascript.evaluator;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Map;

public interface ScriptEvaluatorFactory {
    String IMPLEMENTATION_QUALIFIED_NAME = "com#extendedclip#papi#expansion#javascript#evaluator#JavetScriptEvaluatorFactory";

    ScriptEvaluator create(final Map<String, Object> bindings);

    void cleanBinaries();

    @SuppressWarnings("unchecked")
    static ScriptEvaluatorFactory isolated() throws ReflectiveOperationException {
        final URL selfJar = ScriptEvaluatorFactory.class
                .getProtectionDomain()
                .getCodeSource().getLocation();
        final ExposingClassLoader classLoader = new ExposingClassLoader(new URL[]{selfJar}, ScriptEvaluator.class, ScriptEvaluatorFactory.class);
        final Class<?> clazz = Class.forName(IMPLEMENTATION_QUALIFIED_NAME.replace('#', '.'), true, classLoader);
        final Constructor<?> factoryConstructor = clazz.getConstructor();
        return (ScriptEvaluatorFactory) factoryConstructor.newInstance();
    }
}
