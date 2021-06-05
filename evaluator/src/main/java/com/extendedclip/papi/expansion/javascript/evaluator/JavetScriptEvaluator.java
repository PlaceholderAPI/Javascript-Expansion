package com.extendedclip.papi.expansion.javascript.evaluator;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.extendedclip.papi.expansion.javascript.evaluator.binding.Binder;
import com.extendedclip.papi.expansion.javascript.evaluator.binding.javet.MediatingMethodBinder;
import com.extendedclip.papi.expansion.javascript.evaluator.binding.javet.ObjectMethodBinder;
import com.extendedclip.papi.expansion.javascript.evaluator.binding.javet.StaticMethodBinder;

import java.util.HashMap;
import java.util.Map;

public class JavetScriptEvaluator implements ScriptEvaluator {
    private final IJavetLogger logger;
    private final Map<String, Object> bindings;
    private final Binder binder;

    public JavetScriptEvaluator(final Map<String, Object> bindings) {
        this(new PassthroughJavetLogger(), bindings, new MediatingMethodBinder(new StaticMethodBinder(), new ObjectMethodBinder()));
    }

    public JavetScriptEvaluator(final IJavetLogger logger, final Map<String, Object> bindings, Binder binder) {
        this.logger = logger;
        this.bindings = new HashMap<>(bindings);
        this.binder = binder;
    }

    @Override
    public Object execute(final Map<String, Object> additionalBindings, final String script) {
        try (final V8Runtime runtime = prepareRuntime()) {
            applyBindings(runtime, additionalBindings);
            final Object result = runtime.getExecutor(script).execute();
            runtime.lowMemoryNotification(); // Schedule bound values for GC
            return result;
        } catch (final JavetException exception) {
            // This exists because JavetException is not accessible to root module
            throw new RuntimeException(exception.getMessage(), exception.getCause());
        }
    }

    private V8Runtime prepareRuntime() throws JavetException {
        final V8Runtime runtime = V8Host.getV8Instance().createV8Runtime();
        runtime.setLogger(logger);
        runtime.setConverter(new JavetReflectiveObjectConverter(binder));
        runtime.allowEval(true);
        applyBindings(runtime, bindings);
        return runtime;
    }

    private static void applyBindings(final V8Runtime runtime, final Map<String,Object> bindings) throws JavetException {
        for (final Map.Entry<String, Object> binding: bindings.entrySet()) {
            bind(runtime, binding.getKey(), binding.getValue());
        }
    }

    private static void bind(final V8Runtime runtime, final String key, final Object value) throws JavetException {
        runtime.getGlobalObject().set(key, value);
    }
}
