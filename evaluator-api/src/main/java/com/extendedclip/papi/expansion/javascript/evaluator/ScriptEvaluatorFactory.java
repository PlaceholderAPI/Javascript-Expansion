package com.extendedclip.papi.expansion.javascript.evaluator;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Map;

public interface ScriptEvaluatorFactory {

    ScriptEvaluator create(final Map<String, Object> bindings);

    void cleanBinaries();

    @SuppressWarnings("unchecked")
    static ScriptEvaluatorFactory create() {

        return new J2V8ScriptEvaluatorFactory();
    }
}
