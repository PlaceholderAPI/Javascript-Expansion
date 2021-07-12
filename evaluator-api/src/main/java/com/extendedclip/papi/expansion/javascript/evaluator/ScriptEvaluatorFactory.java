package com.extendedclip.papi.expansion.javascript.evaluator;

import java.util.Map;

public interface ScriptEvaluatorFactory {

    ScriptEvaluator create(final Map<String, Object> bindings);

    default void cleanBinaries() {}
}
