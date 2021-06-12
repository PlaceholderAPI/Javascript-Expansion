package com.extendedclip.papi.expansion.javascript.cloud;

import java.util.Map;
import java.util.Optional;

public final class GitScriptIndex implements ScriptIndex {
    private static final String INDEX_URL =
            "https://raw.githubusercontent.com/PlaceholderAPI/" +
            "Javascript-Expansion/master/scripts/master_list.json";


    private final Map<String, GitScript> scriptMap;

    public GitScriptIndex(final Map<String, GitScript> scriptMap) {
        this.scriptMap = scriptMap;
    }

    public Optional<GitScript> getScript(final String name) {
        return Optional.ofNullable(scriptMap.get(name));
    }
}
