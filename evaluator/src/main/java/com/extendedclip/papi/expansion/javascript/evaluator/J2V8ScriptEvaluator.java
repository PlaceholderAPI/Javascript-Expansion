package com.extendedclip.papi.expansion.javascript.evaluator;

import com.eclipsesource.v8.V8;

import java.util.Map;

public final class J2V8ScriptEvaluator implements ScriptEvaluator {
    @Override
    public Object execute(Map<String, Object> additionalBindings, String script) {
        final V8 runtime = V8.createV8Runtime();
        final Object result = runtime.executeScript(script);
        runtime.release(true);
        return result;
    }
}
