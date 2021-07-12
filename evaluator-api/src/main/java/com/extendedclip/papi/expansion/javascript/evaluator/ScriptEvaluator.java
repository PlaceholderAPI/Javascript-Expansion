package com.extendedclip.papi.expansion.javascript.evaluator;

import javax.script.ScriptException;
import java.util.Map;

public interface ScriptEvaluator {
    Object execute(final Map<String, Object> additionalBindings, final String script) throws EvaluatorException, ScriptException;
}
