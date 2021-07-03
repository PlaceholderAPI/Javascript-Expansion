package com.extendedclip.papi.expansion.javascript.evaluator;

import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import java.util.Map;

public final class NashornScriptEvaluatorFactory implements ScriptEvaluatorFactory {
    private final NashornScriptEngineFactory engineFactory;

    private NashornScriptEvaluatorFactory(final NashornScriptEngineFactory engineFactory) {
        this.engineFactory = engineFactory;
    }


    @Override
    public ScriptEvaluator create(final Map<String, Object> bindings) {
        return new NashornScriptEvaluator(engineFactory, bindings);
    }



}
