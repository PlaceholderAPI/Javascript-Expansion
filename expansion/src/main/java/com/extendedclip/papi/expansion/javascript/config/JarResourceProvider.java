package com.extendedclip.papi.expansion.javascript.config;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public final class JarResourceProvider implements Function<String, InputStream> {
    private final URL resourceJar;

    public JarResourceProvider(final URL resourceJar) {
        this.resourceJar = resourceJar;
    }

    @Override
    @Nullable
    public InputStream apply(@Nullable final String fileName) {
        try {
            final JarFile jarFile = new JarFile(new File(resourceJar.toURI()));
            final ZipEntry zipEntry = jarFile.getEntry(fileName);
            if (zipEntry == null) {
                return null;
            }
            return jarFile.getInputStream(zipEntry);
        } catch (final IOException | URISyntaxException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
