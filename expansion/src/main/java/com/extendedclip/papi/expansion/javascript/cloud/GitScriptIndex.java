package com.extendedclip.papi.expansion.javascript.cloud;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public final class GitScriptIndex implements ScriptIndex {
    private static final String INDEX_URL =
            "https://raw.githubusercontent.com/PlaceholderAPI/" +
            "Javascript-Expansion/master/scripts/master_list.json";

    @NotNull
    private final Map<String, GitScript> scriptMap;

    public GitScriptIndex(@NotNull final Map<String, GitScript> scriptMap) {
        this.scriptMap = scriptMap;
    }

    @NotNull
    public Optional<GitScript> getScript(final String name) {
        return Optional.ofNullable(scriptMap.get(name));
    }
}
