package com.extendedclip.papi.expansion.javascript.evaluator;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public final class ExposingClassLoader extends URLClassLoader {
    private final Map<String, Class<?>> exposedClassMap;

    public ExposingClassLoader(final URL[] urls, final Class<?>... exposed) {
        super(urls, getSystemClassLoader());
        exposedClassMap = new HashMap<>();
        for (final Class<?> clazz: exposed) {
            exposedClassMap.put(clazz.getName(), clazz);
        }
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        final Class<?> exposed = exposedClassMap.get(name);
        if (exposed != null) {
            return exposed;
        }
        return super.loadClass(name, resolve);
    }
}
