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
        final long start = System.currentTimeMillis();
        final long startNano = System.nanoTime();
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
        } finally {
            final long end = System.currentTimeMillis();
            final long endNano = System.nanoTime();

            final long timeTaken = end - start;
            System.out.printf("Evaluated in %d %s\n", timeTaken == 0 ? (endNano - startNano) : timeTaken, timeTaken == 0 ? "ns" : "ms");
        }
    }

    private void bind(final QuackContext ctx, final String key, final Object value) {
        ctx.getGlobalObject().set(key, value);
    }
}
