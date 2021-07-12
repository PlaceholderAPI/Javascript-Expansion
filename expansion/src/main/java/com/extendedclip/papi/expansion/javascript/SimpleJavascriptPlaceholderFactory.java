package com.extendedclip.papi.expansion.javascript;

import com.extendedclip.papi.expansion.javascript.evaluator.ScriptEvaluatorFactory;

public final class SimpleJavascriptPlaceholderFactory implements JavascriptPlaceholderFactory {
    private final JavascriptExpansion expansion;
    private final ScriptEvaluatorFactory evaluatorFactory;

    public SimpleJavascriptPlaceholderFactory(final JavascriptExpansion expansion, final ScriptEvaluatorFactory evaluatorFactory) {
        this.expansion = expansion;
        this.evaluatorFactory = evaluatorFactory;
    }

    @Override
    public JavascriptPlaceholder create(final String identifier, final String script) {
        return new JavascriptPlaceholder(identifier, script, evaluatorFactory, expansion);
    }
}
