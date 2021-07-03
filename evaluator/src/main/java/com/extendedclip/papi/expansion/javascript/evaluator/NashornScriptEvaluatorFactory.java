package com.extendedclip.papi.expansion.javascript.evaluator;

import com.extendedclip.papi.expansion.javascript.evaluator.util.InjectionUtil;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class NashornScriptEvaluatorFactory implements ScriptEvaluatorFactory {
    public static final Collection<String> LIBRARIES = Collections.singletonList("nashorn-core-15.1.isolated-jar");
    private final NashornScriptEngineFactory engineFactory;

    private NashornScriptEvaluatorFactory(final NashornScriptEngineFactory engineFactory) {
        this.engineFactory = engineFactory;
    }


    @Override
    public ScriptEvaluator create(final Map<String, Object> bindings) {
        return new NashornScriptEvaluator(engineFactory, bindings);
    }

    public static ScriptEvaluatorFactory create() throws URISyntaxException, ReflectiveOperationException, NoSuchAlgorithmException, IOException {
        InjectionUtil.inject(LIBRARIES);
        return new NashornScriptEvaluatorFactory(new NashornScriptEngineFactory());
    }

}
