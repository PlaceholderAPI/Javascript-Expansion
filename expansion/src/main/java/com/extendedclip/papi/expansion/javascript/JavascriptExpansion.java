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
import com.extendedclip.papi.expansion.javascript.cloud.download.ChanneledScriptDownloader;
import com.extendedclip.papi.expansion.javascript.cloud.download.GitScriptPathSelector;
import com.extendedclip.papi.expansion.javascript.cloud.download.PathSelector;
import com.extendedclip.papi.expansion.javascript.cloud.download.ScriptDownloader;
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
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

public class JavascriptExpansion extends PlaceholderExpansion implements Cacheable, Configurable {
    private static final URL SELF_JAR_URL = JavascriptExpansion.class.getProtectionDomain()
            .getCodeSource().getLocation();
    private final String VERSION;
    private static JavascriptExpansion instance;
    private String argument_split;
    private final ScriptEvaluatorFactory scriptEvaluatorFactory;
    private final CommandRegistrar commandRegistrar;
    private final ScriptConfiguration scriptConfiguration;
    private final Path scriptDirectoryPath;
    private final ScriptRegistry registry;
    private final ScriptLoader loader;
    private final GitScriptManager scriptManager;

    public JavascriptExpansion() throws ReflectiveOperationException {
        instance = this;
        this.VERSION = getClass().getPackage().getImplementationVersion();

        try {
            this.scriptEvaluatorFactory = new ClosableScriptEvaluatorFactory(ScriptEvaluatorFactory.isolated());
        } catch (final ReflectiveOperationException exception) {
            // Unrecoverable - Therefore throw wrapped exception with more information
            throw new EvaluatorException("Unable to create evaluator.", exception);
        }
        final PathSelector pathSelector = new GitScriptPathSelector(new File(getPlaceholderAPI().getDataFolder(), "javascripts"));
        final ScriptDownloader downloader = new ChanneledScriptDownloader(pathSelector);
        final GitScriptIndexProvider indexProvider = new GitScriptIndexProvider(getPlaceholderAPI());
        final ActiveStateSetter activeStateSetter = new GitScriptActiveStateSetter(getPlaceholderAPI());

        this.scriptManager = new GitScriptManager(activeStateSetter, indexProvider, downloader, pathSelector);

        final File dataFolder = getPlaceholderAPI().getDataFolder();

        final File configFile = new File(dataFolder, "javascript_placeholders.yml");
        final HeaderWriter headerWriter = new ResourceHeaderWriter(new JarResourceProvider(SELF_JAR_URL));

        this.scriptDirectoryPath = dataFolder.toPath().resolve("javascripts");

        this.scriptConfiguration = new YamlScriptConfiguration(configFile, headerWriter, scriptDirectoryPath);
        this.registry = new ScriptRegistry();
        final JavascriptPlaceholderFactory placeholderFactory = new SimpleJavascriptPlaceholderFactory(this, scriptEvaluatorFactory);
        this.loader = new ConfigurationScriptLoader(registry, scriptConfiguration, placeholderFactory);
        this.commandRegistrar = new CommandRegistrar(this, scriptManager, placeholderFactory, scriptConfiguration, registry);

    }

    @NotNull
    @Override
    public String getAuthor() {
        return "clip";
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "javascript";
    }

    @NotNull
    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public boolean register() {
        argument_split = getString("argument_split", ",");
        if (argument_split.equals("_")) {
            argument_split = ",";
            ExpansionUtils.warnLog("Underscore character will not be allowed for splitting. Defaulting to ',' for this", null);
        }
        scriptConfiguration.reload();

        int amountLoaded = 0;
        try {
            amountLoaded = loader.reload();
        } catch (final IOException exception) {
            ExpansionUtils.errorLog("Failed to load scripts", exception);
        }

        ExpansionUtils.infoLog(amountLoaded + " script" + ExpansionUtils.plural(amountLoaded) + " loaded!");
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

        instance = null;

        scriptEvaluatorFactory.cleanBinaries();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null || registry.getAllPlaceholders().size() == 0) {
            return "";
        }

        for (JavascriptPlaceholder script : registry.getAllPlaceholders()) {
            if (identifier.startsWith(script.getIdentifier() + "_")) {
                identifier = identifier.replaceFirst(script.getIdentifier() + "_", "");

                return !identifier.contains(argument_split) ? script.evaluate(player, identifier) : script.evaluate(player, identifier.split(argument_split));
            }

            if (identifier.equalsIgnoreCase(script.getIdentifier())) {
                return script.evaluate(player);
            }
        }

        return null;
    }

    @Override
    public Map<String, Object> getDefaults() {
        final Map<String, Object> defaults = new HashMap<>();
        defaults.put("debug", false);
        defaults.put("argument_split", ",");
        defaults.put("github_script_downloads", false);

        return defaults;
    }

    public int reloadScripts() {
        try {
            return loader.reload();
        } catch (final IOException exception) {
            ExpansionUtils.errorLog("Failed to reload scripts.", exception);
        }
        return 0;
    }

    public static JavascriptExpansion getInstance() {
        return instance;
    }

}
