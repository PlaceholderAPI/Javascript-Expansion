package com.extendedclip.papi.expansion.javascript.evaluator;

import com.extendedclip.papi.expansion.javascript.evaluator.util.InjectionUtil;
import org.mozilla.javascript.engine.RhinoScriptEngineFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class RhinoJSScriptEvaluatorFactory implements ScriptEvaluatorFactory {

    public static final Collection<String> LIBRARIES = Arrays.asList(
            "rhino-1.7.14.isolated-jar",
            "rhino-runtime-1.7.14.isolated-jar",
            "rhino-engine-1.7.14.isolated-jar",
            "asm-commons-9.2.isolated-jar",
            "asm-util-9.2.isolated-jar",
            "asm-9.2.isolated-jar"
    );

    private final RhinoScriptEngineFactory engineFactory;

    private RhinoJSScriptEvaluatorFactory(final RhinoScriptEngineFactory engineFactory) {
        this.engineFactory = engineFactory;
    }

    @Override
    public ScriptEvaluator create(final Map<String, Object> bindings) {
        return new RhinoJSScriptEvaluator(engineFactory, bindings);
    }

    public static ScriptEvaluatorFactory create() throws URISyntaxException, ReflectiveOperationException, NoSuchAlgorithmException, IOException {
        InjectionUtil.inject(LIBRARIES);
        return new RhinoJSScriptEvaluatorFactory(new RhinoScriptEngineFactory());
    }
}
