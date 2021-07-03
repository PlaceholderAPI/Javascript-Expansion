/*
 *
 * Javascript-Expansion
 * Copyright (C) 2020 Ryan McCarthy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package com.extendedclip.papi.expansion.javascript;

import com.extendedclip.papi.expansion.javascript.cloud.*;
import com.extendedclip.papi.expansion.javascript.commands.router.CommandRegistrar;
import com.extendedclip.papi.expansion.javascript.config.*;
import com.extendedclip.papi.expansion.javascript.evaluator.*;
import com.extendedclip.papi.expansion.javascript.script.ConfigurationScriptLoader;
import com.extendedclip.papi.expansion.javascript.script.ScriptLoader;
import com.extendedclip.papi.expansion.javascript.script.ScriptRegistry;
import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;

public class JavascriptExpansion extends PlaceholderExpansion implements Cacheable, Configurable {
    public static final String AUTHOR = "clip";
    public static final String IDENTIFIER = "javascript";
    public static final String VERSION = JavascriptExpansion.class.getPackage().getImplementationVersion();

    private static final URL SELF_JAR_URL = JavascriptExpansion.class.getProtectionDomain()
            .getCodeSource().getLocation();

    private final ScriptRegistry registry = new ScriptRegistry();
    private final GitScriptManager scriptManager = GitScriptManager.createDefault(getPlaceholderAPI());

    private String argumentSeparator = "";
    private boolean useQuickJS = false;
    private ScriptLoader loader;
    private ScriptEvaluatorFactory scriptEvaluatorFactory;
    private CommandRegistrar commandRegistrar;

    @NotNull
    @Override
    public String getAuthor() {
        return AUTHOR;
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @NotNull
    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public boolean register() {
        argumentSeparator = getString("argument_split", ",");
        if (argumentSeparator.equals("_")) {
            argumentSeparator = ",";
            ExpansionUtils.warnLog("Underscore character will not be allowed for splitting. Defaulting to ',' for this", null);
        }

        useQuickJS = (boolean) get("use_quick_js", false);

        if (useQuickJS) {
            this.scriptEvaluatorFactory = QuickJsScriptEvaluatorFactory.createWithFallback(i -> {
                getPlaceholderAPI().getLogger().log(Level.WARNING, "Failed to use QuickJS Engine. Falling back to Nashorn");
                return createNashornEvaluatorFactory();
            });
        } else {
            this.scriptEvaluatorFactory =  createNashornEvaluatorFactory();
        }


        final HeaderWriter headerWriter = HeaderWriter.fromJar(SELF_JAR_URL);

        final File dataFolder = getPlaceholderAPI().getDataFolder();
        final Path scriptDirectoryPath = dataFolder.toPath().resolve("javascripts");
        try {
            Files.createDirectories(scriptDirectoryPath);
        } catch (IOException exception) {
            ExpansionUtils.errorLog("Failed to create script folder.", exception);
        }
        final File configFile = new File(dataFolder, "javascript_placeholders.yml");
        final ScriptConfiguration scriptConfiguration = new YamlScriptConfiguration(configFile, headerWriter, scriptDirectoryPath);
        final JavascriptPlaceholderFactory placeholderFactory = new SimpleJavascriptPlaceholderFactory(this, scriptEvaluatorFactory);
        this.loader = new ConfigurationScriptLoader(registry, scriptConfiguration, placeholderFactory);
        try {
            this.commandRegistrar = new CommandRegistrar(scriptManager, placeholderFactory, scriptConfiguration, registry, loader);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        try {
            final int amountLoaded = loader.reload();
            ExpansionUtils.infoLog(amountLoaded + " script" + ExpansionUtils.plural(amountLoaded) + " loaded!");
        } catch (final IOException exception) {
            ExpansionUtils.errorLog("Failed to load scripts", exception);
        }
        if ((boolean) get("github_script_downloads", false)) {
            scriptManager.getIndexProvider().refreshIndex(scriptIndex -> {
                long gitIndexed = scriptIndex.getCount();
                ExpansionUtils.infoLog("Indexed " + gitIndexed + " gitscript" + ExpansionUtils.plural(Math.toIntExact(gitIndexed)));
            });
        }
        commandRegistrar.register();
        return super.register();
    }

    @Override
    public void clear() {
        commandRegistrar.unregister();
        loader.clear();
        scriptEvaluatorFactory.cleanBinaries();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        for (JavascriptPlaceholder script : registry.getAllPlaceholders()) {
            if (identifier.startsWith(script.getIdentifier() + "_")) {
                identifier = identifier.replaceFirst(script.getIdentifier() + "_", "");

                return !identifier.contains(argumentSeparator) ? script.evaluate(player, identifier) : script.evaluate(player, identifier.split(argumentSeparator));
            }

            if (identifier.equalsIgnoreCase(script.getIdentifier())) {
                return script.evaluate(player);
            }
        }

        return "";
    }

    @Override
    public Map<String, Object> getDefaults() {
        final Map<String, Object> defaults = new HashMap<>();
        defaults.put("debug", false);
        defaults.put("argument_split", ",");
        defaults.put("github_script_downloads", false);
        defaults.put("use_quick_js", false);
        return defaults;
    }

    private static ScriptEvaluatorFactory createNashornEvaluatorFactory() {
        try {
            return NashornScriptEvaluatorFactory.create();
        } catch (URISyntaxException | ReflectiveOperationException | NoSuchAlgorithmException | IOException exception) {
            throw new RuntimeException("Failed to create fallback evaluator: Nashorn" ,exception); // Unrecoverable
        }
    }
}
