package com.extendedclip.papi.expansion.javascript.cloud;

import java.util.Collection;
import java.util.Optional;

public interface ScriptIndex {
    Collection<GitScript> getAllScripts();
    Optional<GitScript> getScript(final String name);
    long getCount();
}
