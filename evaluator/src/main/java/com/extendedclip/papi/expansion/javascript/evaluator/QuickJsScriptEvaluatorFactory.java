package com.extendedclip.papi.expansion.javascript.evaluator;

import com.extendedclip.papi.expansion.javascript.evaluator.util.InjectionUtil;

import javax.script.ScriptException;
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
import java.util.function.Function;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public final class QuickJsScriptEvaluatorFactory implements ScriptEvaluatorFactory {
    private static final String TEST_EVALUATION_SCRIPT = "10 * 10";
    private static final int TEST_EVALUATION_RESULT = 100;

    public static final Collection<String> LIBRARIES = Collections.singletonList("quickjs-1.0.0.isolated-jar");
    private static final URL SELF_JAR_URL = QuickJsScriptEvaluatorFactory.class.getProtectionDomain()
            .getCodeSource().getLocation();

    private QuickJsScriptEvaluatorFactory() {

    }

    @Override
    public ScriptEvaluator create(final Map<String, Object> bindings) {
        return new QuickJsScriptEvaluator(bindings);
    }

    public static ScriptEvaluatorFactory createWithFallback(final Function<Void, ScriptEvaluatorFactory> evaluatorFactoryProducer) {
        try {
            final ScriptEvaluatorFactory evaluatorFactory = create();
            attemptBasicEvaluation(evaluatorFactory);
            return evaluatorFactory;
        } catch (final Exception exception) {
            return evaluatorFactoryProducer.apply(null);
        }
    }

    private static void attemptBasicEvaluation(final ScriptEvaluatorFactory evaluatorFactory) throws ScriptException {
        final Object result = evaluatorFactory.create(Collections.emptyMap()).execute(Collections.emptyMap(), "10 * 10");
        if (result instanceof Integer && ((Integer) result).intValue() == TEST_EVALUATION_RESULT) {
            return;
        }
        throw new RuntimeException("Failed basic evaluation test");
    }

    public static ScriptEvaluatorFactory create() throws URISyntaxException, ReflectiveOperationException, NoSuchAlgorithmException, IOException {
        InjectionUtil.inject(LIBRARIES);
        return new QuickJsScriptEvaluatorFactory();
    }
}
