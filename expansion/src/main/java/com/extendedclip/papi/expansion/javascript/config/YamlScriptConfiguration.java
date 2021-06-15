package com.extendedclip.papi.expansion.javascript.config;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class YamlScriptConfiguration implements ScriptConfiguration {
    private final FileConfiguration fileConfiguration;
    private final File configurationFile;
    private final HeaderWriter headerWriter;
    private final Path scriptDirectoryPath;

    public YamlScriptConfiguration(final File configurationFile, final HeaderWriter headerWriter, final Path scriptDirectoryPath) {
        this(
                YamlConfiguration.loadConfiguration(configurationFile),
                configurationFile,
                headerWriter,
                scriptDirectoryPath
        );
    }

    public YamlScriptConfiguration(final FileConfiguration configuration, final File configurationFile, final HeaderWriter headerWriter, final Path scriptDirectoryPath) {
        this.fileConfiguration = configuration;
        this.configurationFile = configurationFile;
        this.headerWriter = headerWriter;
        this.scriptDirectoryPath = scriptDirectoryPath;
        if (!Files.isDirectory(scriptDirectoryPath)) {
            throw new AssertionError("Expected directory for scripts to be saved/loaded from. Found non-directory path.");
        }
        setPath("example", "example.js");
    }

    @Override
    @Nullable
    public Path getPath(@NotNull final String scriptName) {
        final ConfigurationSection scriptSection = fileConfiguration.getConfigurationSection(scriptName);
        if (scriptSection == null) {
            return null;
        }
        String fileName = scriptSection.getString("file");
        if (fileName == null) {
            fileName = scriptName + ".js";
        }
        return scriptDirectoryPath.resolve(fileName);
    }

    @Override
    public void setPath(@NotNull final String scriptName, @Nullable final String name) {
        final String key = scriptName + ".file";
        fileConfiguration.set(key, name);
    }

    @Override
    @NotNull
    public Collection<String> getScripts() {
        return fileConfiguration.getKeys(false);
    }

    @Override
    @NotNull
    public Map<String, Path> getEntries() {
        //noinspection ConstantConditions
        return getScripts().stream().collect(Collectors.toMap(Function.identity(), this::getPath));
    }

    @Override
    public void reload() {
        try {
            if (!configurationFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                configurationFile.getParentFile().mkdirs();
                //noinspection ResultOfMethodCallIgnored
                configurationFile.createNewFile();
            }
            fileConfiguration.load(configurationFile);
            // Ensure presence of header in case user re-wrote the entire file
            headerWriter.writeTo(fileConfiguration);
            save();
        } catch (final IOException | InvalidConfigurationException exception) {
            ExpansionUtils.errorLog("Failed to reload configuration", exception);
        }
    }

    @Override
    public void save() {
        try {
            fileConfiguration.save(configurationFile);
        } catch (final IOException exception) {
            ExpansionUtils.errorLog("Failed to save configuration", exception);
        }
    }
}
