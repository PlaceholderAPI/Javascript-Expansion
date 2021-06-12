package com.extendedclip.papi.expansion.javascript.cloud;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

public final class GitScriptIndexManager implements ScriptIndexManager {
    @Override
    public Optional<ScriptIndex> getScriptIndex() {
        return Optional.empty();
    }

    @Override
    public void refreshIndex(Consumer<ScriptIndex> indexConsumer) throws IOException {

    }
}
