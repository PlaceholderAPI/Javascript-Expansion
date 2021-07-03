package com.extendedclip.papi.expansion.javascript.evaluator.util;

import com.extendedclip.papi.expansion.javascript.evaluator.LibraryInjectionException;
import com.extendedclip.papi.expansion.javascript.evaluator.QuickJsScriptEvaluatorFactory;
import io.github.slimjar.injector.loader.Injectable;
import io.github.slimjar.injector.loader.InjectableFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public final class InjectionUtil {
    private static final URL SELF_JAR_URL = QuickJsScriptEvaluatorFactory.class.getProtectionDomain()
            .getCodeSource().getLocation();

    private InjectionUtil() {

    }

    public static boolean tryInject(final Collection<String> libraries) {
        try {
            inject(libraries);
            return true;
        } catch (final Exception exception) {
            // Fail silently
            return false;
        }
    }

    public static void inject(final Collection<String> libraries) throws LibraryInjectionException {
        try {
            final Collection<URL> libraryURLs = extractLibraries(libraries);
            final ClassLoader bukkitClassLoader = InjectionUtil.class.getClassLoader().getParent();
            final Injectable injectable = InjectableFactory.create(bukkitClassLoader);
            for (final URL libraryURL : libraryURLs) {
                injectable.inject(libraryURL);
            }
        } catch (final Exception exception) {
            throw new LibraryInjectionException(exception);
        }
    }

    private static Collection<URL> extractLibraries(final Collection<String> libraries) throws IOException, URISyntaxException, NoSuchAlgorithmException, ReflectiveOperationException {
        final Collection<URL> extracted = new ArrayList<>();
        final File selfFile = new File(SELF_JAR_URL.toURI());
        final JarFile jarFile = new JarFile(selfFile);
        for (final String library : libraries) {
            final File extractedFile = getExtractionFile(library, selfFile);
            if (extractedFile.exists()) {
                extracted.add(extractedFile.toURI().toURL());
                continue;
            }
            final ZipEntry entry = jarFile.getEntry(library);
            if (entry == null) {
                continue;
            }
            //noinspection ResultOfMethodCallIgnored
            extractedFile.getParentFile().mkdirs();
            //noinspection ResultOfMethodCallIgnored
            extractedFile.createNewFile();
            try (final InputStream stream = jarFile.getInputStream(entry);
                final ReadableByteChannel inChannel = Channels.newChannel(stream);
                final FileChannel outChannel = FileChannel.open(extractedFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                outChannel.transferFrom(inChannel, 0, entry.getSize());
            }
            extracted.add(extractedFile.toURI().toURL());
        }
        return extracted;
    }

    private static File getExtractionFile(final String name, final File selfFile) {
        return new File(selfFile.getParentFile(), "libraries/" + name.replace("isolated-jar", "jar"));
    }
}
