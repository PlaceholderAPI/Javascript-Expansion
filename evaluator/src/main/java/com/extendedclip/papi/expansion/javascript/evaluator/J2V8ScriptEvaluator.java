package com.extendedclip.papi.expansion.javascript.evaluator;

import com.eclipsesource.v8.*;
import com.eclipsesource.v8.utils.MemoryManager;
import io.alicorn.v8.V8JavaAdapter;

import java.util.Map;

public final class J2V8ScriptEvaluator implements ScriptEvaluator {
    private final Map<String, Object> bindings;

    public J2V8ScriptEvaluator(final Map<String, Object> bindings) {
        this.bindings = bindings;
    }

    @Override
    public Object execute(final Map<String, Object> additionalBindings, final String script) throws Exception {
        try (final V8 v8 = V8.createV8Runtime()) {
            // Memory manager to handle bound reference management
            final MemoryManager memoryManager = new MemoryManager(v8);

            for (Map.Entry<String, Object> entry : bindings.entrySet()) {
                V8JavaAdapter.injectObject(entry.getKey(), entry.getValue(), v8);
            }
            for (Map.Entry<String, Object> entry : additionalBindings.entrySet()) {
                V8JavaAdapter.injectObject(entry.getKey(), entry.getValue(), v8);
            }
            final Object result = v8.executeScript(script);

            // Release bound items;
            memoryManager.release();
            return result;
        } catch (final Exception exception) {
            throw new EvaluatorException("Failed to evaluate requested script.", exception);
        }
    }
}
