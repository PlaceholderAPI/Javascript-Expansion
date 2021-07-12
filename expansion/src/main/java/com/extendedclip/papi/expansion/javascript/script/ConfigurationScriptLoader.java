package com.extendedclip.papi.expansion.javascript.script;

import com.extendedclip.papi.expansion.javascript.JavascriptPlaceholder;
import com.extendedclip.papi.expansion.javascript.JavascriptPlaceholderFactory;
import com.extendedclip.papi.expansion.javascript.config.ScriptConfiguration;
import com.extendedclip.papi.expansion.javascript.evaluator.ScriptEvaluatorFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigurationScriptLoader implements ScriptLoader {
    private final ScriptRegistry registry;
    private final ScriptConfiguration configuration;
    private final JavascriptPlaceholderFactory placeholderFactory;

    public ConfigurationScriptLoader(ScriptRegistry registry, ScriptConfiguration configuration, JavascriptPlaceholderFactory placeholderFactory) {
        this.registry = registry;
        this.configuration = configuration;
        this.placeholderFactory = placeholderFactory;
    }

    @Override
    public int reload() throws IOException {
        registry.getAllPlaceholders().forEach(JavascriptPlaceholder::saveData);
        registry.clearRegistry();
        configuration.reload();
        int loaded = 0;
        for (final String scriptIdentifier: configuration.getScripts()) {
            final Path path = configuration.getPath(scriptIdentifier);
            if (path == null) continue;
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }
            final String script = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            final JavascriptPlaceholder placeholder = placeholderFactory.create(scriptIdentifier, script);
            registry.register(placeholder);
            loaded++;
        }
        return loaded;
    }

    @Override
    public void clear() {
        registry.getAllPlaceholders().forEach(JavascriptPlaceholder::saveData);
        registry.clearRegistry();
    }
}
