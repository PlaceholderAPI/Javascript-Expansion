package com.extendedclip.papi.expansion.javascript.evaluator;

import java.util.Map;

public final class J2V8ScriptEvaluatorFactory implements ScriptEvaluatorFactory {
    @Override
    public ScriptEvaluator create(final Map<String, Object> bindings) {
        return new J2V8ScriptEvaluator(bindings);
    }

    @Override
    public void cleanBinaries() {

    }
}
