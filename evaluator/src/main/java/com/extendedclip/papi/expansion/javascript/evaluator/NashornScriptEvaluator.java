package com.extendedclip.papi.expansion.javascript.evaluator;

import com.koushikdutta.quack.QuackContext;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Map;
import java.util.stream.Stream;

public final class NashornScriptEvaluator implements ScriptEvaluator {
    private final NashornScriptEngineFactory scriptEngineFactory;
    private final Map<String, Object> bindings;

    public NashornScriptEvaluator(final NashornScriptEngineFactory scriptEngineFactory, final Map<String, Object> bindings) {
        this.scriptEngineFactory = scriptEngineFactory;
        this.bindings = bindings;
    }

    @Override
    public Object execute(final Map<String, Object> additionalBindings, final String script) throws EvaluatorException, ScriptException {
        final ScriptEngine engine = scriptEngineFactory.getScriptEngine();
        final Bindings globalBindings = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
        globalBindings.putAll(bindings);
        globalBindings.putAll(additionalBindings);
        engine.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE);
        return engine.eval(script);
    }
}
