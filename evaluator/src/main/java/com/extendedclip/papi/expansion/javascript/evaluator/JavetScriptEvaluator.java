package com.extendedclip.papi.expansion.javascript.evaluator;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.util.HashMap;
import java.util.Map;

public class JavetScriptEvaluator implements ScriptEvaluator {
    private final Map<String, Object> bindings;

    public JavetScriptEvaluator(final Map<String, Object> bindings) {
        this.bindings = new HashMap<>(bindings);
    }

    @Override
    public Object execute(final Map<String, Object> additionalBindings, final String script) {
        try (final V8Runtime runtime = prepareRuntime()) {
            applyBindings(runtime, additionalBindings);
            return runtime.getExecutor(script).execute();
        } catch (final JavetException exception) {
            exception.printStackTrace();
            // This exists because JavetException is not accessible to root module
            throw new RuntimeException(exception.getMessage(), exception.getCause());
        }
    }

    private V8Runtime prepareRuntime() throws JavetException {
        final V8Runtime runtime = V8Host.getV8Instance().createV8Runtime();
        applyBindings(runtime, bindings);
        runtime.allowEval(true);
        return runtime;
    }

    private static void applyBindings(final V8Runtime runtime, final Map<String,Object> bindings) throws JavetException {
        for (final Map.Entry<String, Object> binding: bindings.entrySet()) {
            bind(runtime, binding.getKey(), binding.getValue());
        }
    }

    private static void bind(final V8Runtime runtime, final String key, final Object value) throws JavetException {
        final V8ValueObject object = runtime.createV8ValueObject();
        runtime.getGlobalObject().set(key, value);
        object.bind(value);
    }
}
