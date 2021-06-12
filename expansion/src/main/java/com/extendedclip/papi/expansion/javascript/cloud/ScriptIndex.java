package com.extendedclip.papi.expansion.javascript.cloud;

import java.util.Optional;

public interface ScriptIndex {
    Optional<GitScript> getScript(final String name);
}
