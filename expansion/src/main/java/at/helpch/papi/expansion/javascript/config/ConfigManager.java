package at.helpch.papi.expansion.javascript.config;

import at.helpch.papi.expansion.javascript.ExpansionUtils;
import at.helpch.papi.expansion.javascript.JavascriptExpansion;
import at.helpch.papi.expansion.javascript.config.model.ScriptConfigModel;
import at.helpch.placeholderapi.PlaceholderAPIPlugin;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ConfigManager {
    private static final URL SELF_URL_JAR = JavascriptExpansion.class.getProtectionDomain().getCodeSource().getLocation();
    private static final Yaml YAML;
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    //            .registerTypeAdapter()
    private static final Pattern LINE_DELIMITER = Pattern.compile("\n");

    static {
        final DumperOptions dump = new DumperOptions();
        dump.setPrettyFlow(true);
        dump.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        final LoaderOptions load = new LoaderOptions();
//        load.comm
        YAML = new Yaml(dump);
    }

    private final JavaPlugin main;
    private final HytaleLogger logger;
    private ScriptConfiguration config;

    public ConfigManager(@NotNull final JavaPlugin main) {
        this.main = main;
        this.logger = main.getLogger();
    }

    public void setup() {
        final String content;

        try {
            final Path file = createFile(main.getDataDirectory().toString() + "/javascript_placeholders.yml");

            if (file != null) {
                content = Files.readString(file);
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        final Path scriptDirectoryPath = PlaceholderAPIPlugin.instance().getDataDirectory().resolve("javascripts");

        try {
            Files.createDirectories(scriptDirectoryPath);
        } catch (IOException exception) {
            logger.atSevere().log("Failed to create script folder.", exception);
        }

        final Map<String, Object> data;

        try {
            data = YAML.load(content);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Map<String, ScriptConfigModel> scripts = GSON.fromJson(GSON.toJsonTree(data), new TypeToken<Map<String, ScriptConfigModel>>(){});
        scripts = scripts == null ? new HashMap<>() : scripts;

        if (config == null) {
            config = new YamlScriptConfiguration(scriptDirectoryPath, scripts);
        } else {
            config.setScripts(scripts);
        }
    }

    public ScriptConfiguration config() {
        return config;
    }

    public void save() {
        String headerString = "";

        try (final InputStream stream = new JarResourceProvider(SELF_URL_JAR).apply("header.txt")) {
            headerString = new BufferedReader(new InputStreamReader(stream)).lines()
                    .collect(Collectors.joining("\n"));
        } catch (final IOException exception) {
            ExpansionUtils.errorLog("Failed to read header file", exception);
        }

        try {
            final Map<String, Object> map = GSON.fromJson(GSON.toJsonTree(config.getScripts()), new TypeToken<Map<String, Object>>(){}.getType());
            final String yaml = map.isEmpty() ? "" : YAML.dump(map);
            final Path path = Paths.get(main.getDataDirectory().toString() + "/javascript_placeholders.yml");
            Files.write(path, Arrays.asList(LINE_DELIMITER.split(headerString + '\n' + yaml)), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            logger.atSevere().log("Something went wrong when saving javascript_placeholders.yml: ", e);
        }
    }

    @Nullable
    private Path createFile(@NotNull final String externalPath) {
        final Path file = Paths.get(externalPath);

        if (Files.exists(file)) {
            return file;
        }

        final Optional<Path> parent = Optional.ofNullable(file.getParent());

        try {
            if (parent.isPresent()) {
                Files.createDirectories(parent.get());
            }

            Files.createFile(file);
        } catch (IOException e) {
            logger.atSevere().log("Something went wrong when trying to create ", file);

            return null;
        }

        return file;
    }
}