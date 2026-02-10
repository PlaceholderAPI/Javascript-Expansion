package at.helpch.papi.expansion.javascript.script.data;

import at.helpch.papi.expansion.javascript.ExpansionUtils;
import at.helpch.papi.expansion.javascript.config.ConfigManager;
import at.helpch.papi.expansion.javascript.script.ScriptData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;


public final class JsonPersistableData implements PersistableData {
    private static final Gson GSON = new Gson();
    private static final TypeToken<Map<String, Object>> MAP_TYPE = new TypeToken<Map<String, Object>>(){};

    private final String identifier;
    private final ScriptData scriptData;
    private final File dataFile;

    private JsonPersistableData(final String identifier, final ScriptData scriptData, final File dataFile) {
        this.identifier = identifier;
        this.scriptData = scriptData;
        this.dataFile = dataFile;
    }

    @Override
    public ScriptData getScriptData() {
        return scriptData;
    }

    @Override
    public void save() {
        try {
            final String json = GSON.toJson(scriptData.getData());
            Files.write(dataFile.toPath(), json.lines().toList(), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            ExpansionUtils.errorLog(ExpansionUtils.PREFIX + "An error occurred while saving data for " + identifier, e);
        }
    }

    @Override
    public void reload() {
        try {
            final String json = Files.readString(dataFile.toPath());
            final Map<String, Object> data = GSON.fromJson(json, MAP_TYPE);
            scriptData.setData(data);
        } catch (IOException e) {
            ExpansionUtils.errorLog(ExpansionUtils.PREFIX + "An error occurred while saving data for " + identifier, e);
        }
    }

    public static PersistableData create(final String identifier, final Path dataPath) throws IOException {
        if (!Files.exists(dataPath)) {
            Files.createDirectories(dataPath.getParent());
            Files.createFile(dataPath);
        }
//        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(dataPath.toFile());
        final Map<String, Object> map = new HashMap<>();
        return new JsonPersistableData(identifier, new ScriptData(map), dataPath.toFile());
    }
}
