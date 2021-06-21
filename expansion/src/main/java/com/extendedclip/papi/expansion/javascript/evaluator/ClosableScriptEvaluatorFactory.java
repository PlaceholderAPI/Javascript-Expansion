package com.extendedclip.papi.expansion.javascript.evaluator;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.function.Function;

public final class ClosableScriptEvaluatorFactory implements ScriptEvaluatorFactory{
    private final Function<Void, ScriptEvaluatorFactory> internalFactoryProducer;
    private boolean isActive;
    private ScriptEvaluatorFactory evaluatorFactory;

    public ClosableScriptEvaluatorFactory(final Function<Void, ScriptEvaluatorFactory> internalFactoryProducer) {
        this.internalFactoryProducer = internalFactoryProducer;
        this.isActive = true;
    }

    @Override
    public ScriptEvaluator create(Map<String, Object> bindings) {
        if (!isActive) {
            throw new AssertionError("Evaluator creator requested on closed evaluator factory");
        }
        if (evaluatorFactory == null) {
            evaluatorFactory = internalFactoryProducer.apply(null);
        }
        return evaluatorFactory.create(bindings);
    }

    @Override
    public void cleanBinaries() {
        if (evaluatorFactory != null) {
            evaluatorFactory.cleanBinaries();
            evaluatorFactory = null;
        }
        this.isActive = false;
    }
}
