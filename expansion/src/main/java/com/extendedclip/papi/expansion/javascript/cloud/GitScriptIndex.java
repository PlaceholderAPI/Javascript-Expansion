package com.extendedclip.papi.expansion.javascript.cloud;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public final class GitScriptIndex implements ScriptIndex {

    @NotNull
    private final Map<String, GitScript> scriptMap;

    public GitScriptIndex(@NotNull final Map<String, GitScript> scriptMap) {
        this.scriptMap = scriptMap;
    }

    @Override
    public Collection<GitScript> getAllScripts() {
        return scriptMap.values();
    }

    @Override
    @NotNull
    public Optional<GitScript> getScript(final String name) {
        return Optional.ofNullable(scriptMap.get(name));
    }

    @Override
    public long getCount() {
        return scriptMap.size();
    }
}
