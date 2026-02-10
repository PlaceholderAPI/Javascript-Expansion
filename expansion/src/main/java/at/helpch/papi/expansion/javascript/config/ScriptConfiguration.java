package at.helpch.papi.expansion.javascript.config;

import at.helpch.papi.expansion.javascript.config.model.ScriptConfigModel;
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
    Collection<String> getScriptNames();

    @NotNull
    Map<String, Path> getEntries();

    @NotNull
    Map<String, ScriptConfigModel> getScripts();

    void setScripts(@NotNull final Map<String, ScriptConfigModel> scripts);
}
