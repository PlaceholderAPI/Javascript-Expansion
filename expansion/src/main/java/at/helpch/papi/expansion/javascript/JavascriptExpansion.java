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
package at.helpch.papi.expansion.javascript;

import at.helpch.papi.expansion.javascript.cloud.GitScriptManager;
import at.helpch.papi.expansion.javascript.config.ExpansionConfig;
import at.helpch.papi.expansion.javascript.config.HeaderWriter;
import at.helpch.papi.expansion.javascript.config.ScriptConfiguration;
import at.helpch.papi.expansion.javascript.config.YamlScriptConfiguration;
import at.helpch.papi.expansion.javascript.evaluator.NashornScriptEvaluatorFactory;
import at.helpch.papi.expansion.javascript.evaluator.QuickJsScriptEvaluatorFactory;
import at.helpch.papi.expansion.javascript.evaluator.ScriptEvaluatorFactory;
import at.helpch.papi.expansion.javascript.commands.router.CommandRegistrar;
import at.helpch.papi.expansion.javascript.script.ConfigurationScriptLoader;
import at.helpch.papi.expansion.javascript.script.ScriptLoader;
import at.helpch.papi.expansion.javascript.script.ScriptRegistry;
import at.helpch.placeholderapi.PlaceholderAPIPlugin;
import at.helpch.placeholderapi.expansion.Cacheable;
import at.helpch.placeholderapi.expansion.Configurable;
import at.helpch.placeholderapi.expansion.PlaceholderExpansion;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;

public class JavascriptExpansion extends PlaceholderExpansion implements Cacheable, Configurable<ExpansionConfig> {
    public static final String AUTHOR = "clip";
    public static final String IDENTIFIER = "javascript";
    public static final String VERSION = JavascriptExpansion.class.getPackage().getImplementationVersion();

    private static final URL SELF_JAR_URL = JavascriptExpansion.class.getProtectionDomain()
            .getCodeSource().getLocation();

    private final ScriptRegistry registry = new ScriptRegistry();
    private final GitScriptManager scriptManager = GitScriptManager.createDefault(PlaceholderAPIPlugin.instance());

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
        final ExpansionConfig config = getExpansionConfig(JavascriptExpansion.class);

        argumentSeparator = config.argumentSplit();
        if (argumentSeparator.equals("_")) {
            argumentSeparator = ",";
            ExpansionUtils.warnLog("Underscore character will not be allowed for splitting. Defaulting to ',' for this", null);
        }

        useQuickJS = config.useQuickJs();

        if (useQuickJS) {
            this.scriptEvaluatorFactory = QuickJsScriptEvaluatorFactory.createWithFallback(i -> {
                getPlaceholderAPI().getLogger().atWarning().log("Failed to use QuickJS Engine. Falling back to Nashorn");
                return createNashornEvaluatorFactory();
            });
        } else {
            this.scriptEvaluatorFactory =  createNashornEvaluatorFactory();
        }


        final HeaderWriter headerWriter = HeaderWriter.fromJar(SELF_JAR_URL);

        final File dataFolder = getPlaceholderAPI().getDataDirectory().toFile();
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
            this.commandRegistrar = new CommandRegistrar(scriptManager, placeholderFactory, scriptConfiguration, registry, loader, this);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        try {
            final int amountLoaded = loader.reload();
            ExpansionUtils.infoLog(amountLoaded + " script" + ExpansionUtils.plural(amountLoaded) + " loaded!");
        } catch (final IOException exception) {
            ExpansionUtils.errorLog("Failed to load scripts", exception);
        }
        if (config.githubScriptDownloads()) {
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
    public String onPlaceholderRequest(PlayerRef player, @NotNull String identifier) {
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

    private static ScriptEvaluatorFactory createNashornEvaluatorFactory() {
        try {
            return NashornScriptEvaluatorFactory.create();
        } catch (URISyntaxException | ReflectiveOperationException | NoSuchAlgorithmException | IOException exception) {
            throw new RuntimeException("Failed to create fallback evaluator: Nashorn" ,exception); // Unrecoverable
        }
    }

    @Override
    public @NotNull Class<ExpansionConfig> provideConfigType() {
        return ExpansionConfig.class;
    }

    @Override
    public @NotNull ExpansionConfig provideDefault() {
        return new ExpansionConfig(false, ",", false, false, false);
    }
}
