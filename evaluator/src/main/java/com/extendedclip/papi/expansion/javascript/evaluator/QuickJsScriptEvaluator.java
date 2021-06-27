package com.extendedclip.papi.expansion.javascript.evaluator;

import com.koushikdutta.quack.*;

import java.util.Map;

public final class QuickJsScriptEvaluator implements ScriptEvaluator {
    private final Map<String, Object> bindings;

    public QuickJsScriptEvaluator(final Map<String, Object> bindings) {
        this.bindings = bindings;
    }

    @Override
    public Object execute(final Map<String, Object> additionalBindings, final String script) throws EvaluatorException {
        try (final QuackContext context = QuackContext.create(true)) {
            for (Map.Entry<String, Object> entry : bindings.entrySet()) {
                bind(context, entry.getKey(), entry.getValue());
            }
            for (Map.Entry<String, Object> entry : additionalBindings.entrySet()) {
                bind(context, entry.getKey(), entry.getValue());
            }
            return context.evaluate(script);
        } catch (final Exception exception) {
            throw new EvaluatorException("Failed to evaluate requested script.", exception);
        }
    }

    private void bind(final QuackContext ctx, final String key, final Object value) {
        ctx.getGlobalObject().set(key, coerce(ctx, value));
    }
    private Object coerce(final QuackContext ctx, final Object value) {
        if (value.getClass().isArray()) {
            final Object[] array = (Object[]) value;
            final JavaScriptObject jsObj = ctx.evaluateForJavaScriptObject("[]");
            for (int i = 0; i < array.length; i++) {
                jsObj.set(i, coerce(ctx, array[i]));
            }
            return jsObj;
        }
        return ctx.coerceJavaToJavaScript(value);
    }
}
