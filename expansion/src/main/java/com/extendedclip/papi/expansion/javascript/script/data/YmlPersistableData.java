package com.extendedclip.papi.expansion.javascript.script.data;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.script.ScriptData;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


public final class YmlPersistableData implements PersistableData {
    private final String identifier;
    private final ScriptData scriptData;
    private final File dataFile;
    private final YamlConfiguration configuration;

    private YmlPersistableData(final String identifier, final ScriptData scriptData, final File dataFile, final YamlConfiguration configuration) {
        this.identifier = identifier;
        this.scriptData = scriptData;
        this.dataFile = dataFile;
        this.configuration = configuration;
    }

    @Override
    public ScriptData getScriptData() {
        return scriptData;
    }

    @Override
    public void save() {
        try {
            configuration.save(dataFile);
        } catch (IOException e) {
            ExpansionUtils.errorLog(ExpansionUtils.PREFIX + "An error occurred while saving data for " + identifier, e);
        }
    }

    @Override
    public void reload() {
        try {
            configuration.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            ExpansionUtils.errorLog(ExpansionUtils.PREFIX + "An error occurred while saving data for " + identifier, e);
        }
    }

    public static PersistableData create(final String identifier, final Path dataPath) throws IOException {
        if (!Files.exists(dataPath)) {
            Files.createDirectories(dataPath.getParent());
            Files.createFile(dataPath);
        }
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(dataPath.toFile());
        final Map<String, Object> map = new ConfigurationMap(configuration);
        return new YmlPersistableData(identifier, new ScriptData(map), dataPath.toFile(), configuration);
    }
}
