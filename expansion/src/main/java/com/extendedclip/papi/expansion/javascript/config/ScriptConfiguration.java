package com.extendedclip.papi.expansion.javascript.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public interface ScriptConfiguration {
    @Nullable
    File getFile(@NotNull String scriptName);

    void setFile(@NotNull String scriptName, @Nullable File file);

    @NotNull
    Collection<String> getScripts();

    @NotNull
    Map<String, File> getEntries();
}
