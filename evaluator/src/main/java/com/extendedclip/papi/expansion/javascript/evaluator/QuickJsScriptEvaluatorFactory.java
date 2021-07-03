package com.extendedclip.papi.expansion.javascript.evaluator;

import com.extendedclip.papi.expansion.javascript.evaluator.util.InjectionUtil;
import io.github.slimjar.injector.loader.Injectable;
import io.github.slimjar.injector.loader.InjectableFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public final class QuickJsScriptEvaluatorFactory implements ScriptEvaluatorFactory {
    public static final Collection<String> LIBRARIES = Collections.singletonList("quickjs-1.0.0.isolated-jar");
    private static final URL SELF_JAR_URL = QuickJsScriptEvaluatorFactory.class.getProtectionDomain()
            .getCodeSource().getLocation();

    private QuickJsScriptEvaluatorFactory() {

    }

    @Override
    public ScriptEvaluator create(final Map<String, Object> bindings) {
        return new QuickJsScriptEvaluator(bindings);
    }

    public static ScriptEvaluatorFactory create() throws URISyntaxException, ReflectiveOperationException, NoSuchAlgorithmException, IOException {
        InjectionUtil.inject(LIBRARIES);
        return new QuickJsScriptEvaluatorFactory();
    }
}
