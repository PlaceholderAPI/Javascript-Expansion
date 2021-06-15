package com.extendedclip.papi.expansion.javascript.evaluator;

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
        // Attempt to unload JNI libraries (ClassLoader gc causes native libraries loaded into it to be unloaded)
        System.gc();
        System.runFinalization();
    }
}
