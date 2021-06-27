package com.extendedclip.papi.expansion.javascript.evaluator.util;

import io.github.slimjar.app.builder.ApplicationBuilder;
import io.github.slimjar.app.builder.InjectingApplicationBuilder;
import io.github.slimjar.injector.loader.Injectable;
import io.github.slimjar.injector.loader.InstrumentationInjectable;
import io.github.slimjar.injector.loader.UnsafeInjectable;
import io.github.slimjar.injector.loader.WrappedInjectableClassLoader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.security.NoSuchAlgorithmException;

public final class Injectables {
    private Injectables() {

    }

    public static Injectable createInjectable(final ClassLoader classLoader) throws ReflectiveOperationException, NoSuchAlgorithmException, IOException, URISyntaxException {
        final boolean legacy = isOnLegacyJVM();
        Injectable injectable = null;

        if (legacy && classLoader instanceof URLClassLoader) {
            injectable = new WrappedInjectableClassLoader((URLClassLoader) ApplicationBuilder.class.getClassLoader());
        } else if (isUnsafeAvailable() && classLoader instanceof URLClassLoader) {
            try {
                injectable = UnsafeInjectable.create((URLClassLoader) classLoader);
            } catch (final Exception exception) {
                // ignored
            }
        }

        if (injectable == null) {
            injectable = InstrumentationInjectable.create();
        }

        return injectable;
    }

    private static boolean isOnLegacyJVM() {
        final String version = System.getProperty("java.version");
        final String[] parts = version.split("\\.");

        final int jvmLevel;
        switch (parts.length) {
            case 0:
                jvmLevel = 16; // Assume highest if not found.
                break;
            case 1:
                jvmLevel = Integer.parseInt(parts[0]);
                break;
            default:
                jvmLevel = Integer.parseInt(parts[1]);
                break;
        }
        return jvmLevel < 9;
    }

    private static boolean isUnsafeAvailable() {
        try {
            Class.forName("sun.misc.Unsafe");
        } catch (final ClassNotFoundException e) {
            return false;
        }
        return true;
    }
}
