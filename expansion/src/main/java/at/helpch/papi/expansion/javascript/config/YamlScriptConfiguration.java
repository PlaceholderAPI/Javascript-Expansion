package at.helpch.papi.expansion.javascript.config;

import at.helpch.papi.expansion.javascript.config.model.ScriptConfigModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class YamlScriptConfiguration implements ScriptConfiguration {
    private final Path scriptDirectoryPath;
    private final Map<String, ScriptConfigModel> scripts;

    public YamlScriptConfiguration(final Path scriptDirectoryPath, @NotNull final Map<String, ScriptConfigModel> scripts) {
        this.scriptDirectoryPath = scriptDirectoryPath;
        this.scripts = scripts;
        if (!Files.isDirectory(scriptDirectoryPath)) {
            throw new AssertionError("Expected directory for scripts to be saved/loaded from. Found non-directory path.");
        }
    }

    @Override
    @Nullable
    public Path getPath(@NotNull final String scriptName) {
        final ScriptConfigModel script = scripts.get(scriptName);
        if (script == null) {
            return null;
        }
        String fileName = script.file();
        if (fileName == null) {
            fileName = scriptName + ".js";
        }
        return scriptDirectoryPath.resolve(fileName);
    }

    @Override
    public void setPath(@NotNull final String scriptName, @Nullable final String name) {
        final String key = scriptName + ".file";
        scripts.computeIfAbsent(scriptName, ScriptConfigModel::new).file(name);
    }

    @Override
    @NotNull
    public Collection<String> getScriptNames() {
        return scripts.keySet();
    }

    @Override
    @NotNull
    public Map<String, ScriptConfigModel> getScripts() {
        return scripts;
    }

    @Override
    public void setScripts(final @NotNull Map<String, ScriptConfigModel> scripts) {
        scripts.clear();
        scripts.putAll(scripts);
    }

    @Override
    @NotNull
    public Map<String, Path> getEntries() {
        //noinspection ConstantConditions
        return getScriptNames().stream().collect(Collectors.toMap(Function.identity(), this::getPath));
    }
}
