package com.extendedclip.papi.expansion.javascript;

import com.extendedclip.papi.expansion.javascript.evaluator.ScriptEvaluator;
import com.extendedclip.papi.expansion.javascript.evaluator.ScriptEvaluatorFactory;

import java.util.Map;

public final class ClosableScriptEvaluatorFactory implements ScriptEvaluatorFactory{
    private ScriptEvaluatorFactory internalFactory;
    private boolean isActive;

    public ClosableScriptEvaluatorFactory(ScriptEvaluatorFactory internalFactory) {
        this.internalFactory = internalFactory;
        this.isActive = true;
    }

    @Override
    public ScriptEvaluator create(Map<String, Object> bindings) {
        if (!isActive) {
            throw new AssertionError("Evaluator creator requested on closed evaluator factory");
        }
        return internalFactory.create(bindings);
    }

    @Override
    public void cleanBinaries() {
        internalFactory.cleanBinaries();
        internalFactory = null;
        this.isActive = false;
        System.gc();
        System.runFinalization();
    }
}
