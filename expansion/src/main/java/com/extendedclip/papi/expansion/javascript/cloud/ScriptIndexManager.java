package com.extendedclip.papi.expansion.javascript.cloud;

import com.extendedclip.papi.expansion.javascript.cloud.ScriptIndex;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

public interface ScriptIndexManager {
    Optional<ScriptIndex> getScriptIndex();
    void refreshIndex(final Consumer<ScriptIndex> indexConsumer) throws IOException;
}
