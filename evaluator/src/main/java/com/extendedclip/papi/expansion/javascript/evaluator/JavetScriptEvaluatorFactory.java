package com.extendedclip.papi.expansion.javascript.evaluator;

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;

import java.util.Map;

public final class JavetScriptEvaluatorFactory implements ScriptEvaluatorFactory {
    static {
        V8Host.setLibraryReloadable(true);
        V8Host host = V8Host.getInstance(JSRuntimeType.V8);
        host.loadLibrary();
    }
    @Override
    public ScriptEvaluator create(final Map<String, Object> bindings) {
        return new JavetScriptEvaluator(bindings);
    }

    @Override
    public void cleanBinaries() {
        V8Host host = V8Host.getInstance(JSRuntimeType.V8);
        host.unloadLibrary();
        V8Host.setLibraryReloadable(false);
        try {
            host.close();
        } catch (final JavetException exception) {
            exception.printStackTrace(); // Todo throw separate exception
        }
    }
}
