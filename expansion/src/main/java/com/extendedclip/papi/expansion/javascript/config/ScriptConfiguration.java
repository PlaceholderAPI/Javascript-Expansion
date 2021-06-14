package com.extendedclip.papi.expansion.javascript.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

public interface ScriptConfiguration {
    @Nullable
    Path getPath(@NotNull String scriptName);

    void setPath(@NotNull String scriptName, @Nullable final String name);

    @NotNull
    Collection<String> getScripts();

    @NotNull
    Map<String, Path> getEntries();

    void reload();

    void save();
}
