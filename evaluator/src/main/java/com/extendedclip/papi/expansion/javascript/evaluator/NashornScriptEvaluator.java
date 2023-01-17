package com.extendedclip.papi.expansion.javascript.evaluator;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Map;

public final class NashornScriptEvaluator implements ScriptEvaluator {

    private final ScriptEngine scriptEngine;
    private final Map<String, Object> bindings;

    public NashornScriptEvaluator(final ScriptEngine scriptEngine, final Map<String, Object> bindings) {
        this.scriptEngine = scriptEngine;
        this.bindings = bindings;
    }

    @Override
    public Object execute(final Map<String, Object> additionalBindings, final String script) throws EvaluatorException, ScriptException {
        final Bindings globalBindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        globalBindings.putAll(bindings);
        globalBindings.putAll(additionalBindings);
        scriptEngine.setBindings(globalBindings, ScriptContext.ENGINE_SCOPE);
        return scriptEngine.eval(script);
    }

}
