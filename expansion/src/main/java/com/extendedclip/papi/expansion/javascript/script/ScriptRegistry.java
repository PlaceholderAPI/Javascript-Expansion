package com.extendedclip.papi.expansion.javascript.script;

import com.extendedclip.papi.expansion.javascript.JavascriptPlaceholder;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ScriptRegistry {
    private final Map<String, JavascriptPlaceholder> registeredScripts;

    public ScriptRegistry() {
        this(new HashMap<>());
    }

    public ScriptRegistry(final Map<String, JavascriptPlaceholder> registeredScripts) {
        this.registeredScripts = registeredScripts;
    }

    public boolean register(final JavascriptPlaceholder placeholder) {
        final JavascriptPlaceholder previousPlaceholder = registeredScripts.putIfAbsent(placeholder.getIdentifier(), placeholder);
        return previousPlaceholder == null; // Registered only if there was not prior script with the same name.
    }

    public void unregister(final JavascriptPlaceholder placeholder) {
        registeredScripts.remove(placeholder.getIdentifier());
    }

    public void clearRegistry() {
        registeredScripts.clear();
    }

    @Nullable
    public JavascriptPlaceholder getPlaceholder(final String identifier) {
        return registeredScripts.get(identifier);
    }

    public Collection<JavascriptPlaceholder> getAllPlaceholders() {
        return registeredScripts.values();
    }
}
