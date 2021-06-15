package com.extendedclip.papi.expansion.javascript.script;

import com.extendedclip.papi.expansion.javascript.JavascriptPlaceholder;
import com.extendedclip.papi.expansion.javascript.config.ScriptConfiguration;
import com.extendedclip.papi.expansion.javascript.evaluator.ScriptEvaluatorFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigurationScriptLoader implements ScriptLoader {
    private final ScriptRegistry registry;
    private final ScriptConfiguration configuration;
    private final ScriptEvaluatorFactory evaluatorFactory;

    public ConfigurationScriptLoader(ScriptRegistry registry, ScriptConfiguration configuration, ScriptEvaluatorFactory evaluatorFactory) {
        this.registry = registry;
        this.configuration = configuration;
        this.evaluatorFactory = evaluatorFactory;
    }

    @Override
    public int reload() throws IOException {
        registry.getAllPlaceholders().forEach(placeholder -> {
            placeholder.saveData();
            placeholder.cleanup();
        });
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
            final String script = new String(Files.readAllBytes(path));
            final JavascriptPlaceholder placeholder = new JavascriptPlaceholder(scriptIdentifier, script, evaluatorFactory);
            registry.register(placeholder);
            loaded++;
        }
        return loaded;
    }

    @Override
    public void clear() {
        registry.clearRegistry();
    }
}
