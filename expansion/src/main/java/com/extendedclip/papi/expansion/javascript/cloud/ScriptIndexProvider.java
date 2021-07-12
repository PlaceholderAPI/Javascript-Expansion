package com.extendedclip.papi.expansion.javascript.cloud;

import java.util.Optional;
import java.util.function.Consumer;

public interface ScriptIndexProvider {
    Optional<ScriptIndex> getScriptIndex();
    void refreshIndex(final Consumer<ScriptIndex> indexConsumer);
}
