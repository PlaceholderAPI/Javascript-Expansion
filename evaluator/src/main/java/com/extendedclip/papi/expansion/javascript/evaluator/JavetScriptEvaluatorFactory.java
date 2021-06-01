package com.extendedclip.papi.expansion.javascript.evaluator;

import java.util.Map;

public final class JavetScriptEvaluatorFactory implements ScriptEvaluatorFactory {
    @Override
    public ScriptEvaluator create(final Map<String, Object> bindings) {
        return new JavetScriptEvaluator(bindings);
    }
}
