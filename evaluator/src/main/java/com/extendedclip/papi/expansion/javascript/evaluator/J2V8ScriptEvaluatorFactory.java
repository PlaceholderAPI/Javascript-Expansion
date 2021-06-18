package com.extendedclip.papi.expansion.javascript.evaluator;

import java.util.Map;

public final class J2V8ScriptEvaluatorFactory implements ScriptEvaluatorFactory {
    @Override
    public ScriptEvaluator create(Map<String, Object> bindings) {
        return new J2V8ScriptEvaluator();
    }

    @Override
    public void cleanBinaries() {
        System.out.println("[DEBUG] Cleaned binaries");
    }
}
