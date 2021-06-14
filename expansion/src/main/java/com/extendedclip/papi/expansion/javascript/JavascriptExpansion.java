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
import com.extendedclip.papi.expansion.javascript.evaluator.*;
import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class JavascriptExpansion extends PlaceholderExpansion implements Cacheable, Configurable {
    private static final String WIKI_LINK = "https://github.com/PlaceholderAPI/Javascript-Expansion/wiki";

    private JavascriptPlaceholdersConfig config;
    private final Set<JavascriptPlaceholder> scripts;
    private final String VERSION;
    private static JavascriptExpansion instance;
    private GithubScriptManager githubManager;
    private String argument_split;
    private final ScriptEvaluatorFactory scriptEvaluatorFactory;
    private final CommandRegistrar commandRegistrar;
    private final GitScriptManager scriptManager;
    public JavascriptExpansion() throws ReflectiveOperationException {
        instance = this;
        this.VERSION = getClass().getPackage().getImplementationVersion();
        this.scripts = new HashSet<>();

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
        this.commandRegistrar = new CommandRegistrar(this, scriptManager, scriptEvaluatorFactory);
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
        config = new JavascriptPlaceholdersConfig(this, scriptEvaluatorFactory);

        int amountLoaded = config.loadPlaceholders();
        ExpansionUtils.infoLog(amountLoaded + " script" + ExpansionUtils.plural(amountLoaded) + " loaded!");
        if ((boolean) get("github_script_downloads", false)) {
            githubManager = new GithubScriptManager(this);
            githubManager.fetch();
        }

        commandRegistrar.register();
        return super.register();
    }

    @Override
    public void clear() {
        commandRegistrar.unregister();

        scripts.forEach(script -> {
            script.saveData();
            script.cleanup();
        });

        if (githubManager != null) {
            githubManager.clear();
            githubManager = null;
        }

        scripts.clear();
        instance = null;

        scriptEvaluatorFactory.cleanBinaries();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null || scripts.size() == 0) {
            return "";
        }

        for (JavascriptPlaceholder script : scripts) {
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

    public boolean addJSPlaceholder(JavascriptPlaceholder placeholder) {
        if (placeholder == null) {
            return false;
        }

        if (scripts.isEmpty()) {
            scripts.add(placeholder);
            return true;
        }

        if (getJSPlaceholder(placeholder.getIdentifier()) != null) {
            return false;
        }

        scripts.add(placeholder);
        return true;
    }

//    public Set<JavascriptPlaceholder> getJSPlaceholders() {
//        return scripts;
//    }

    public List<String> getLoadedIdentifiers() {
        return scripts.stream()
                .map(JavascriptPlaceholder::getIdentifier)
                .collect(Collectors.toList());
    }

    public JavascriptPlaceholder getJSPlaceholder(String identifier) {
        return scripts.stream()
                .filter(s -> s.getIdentifier().equalsIgnoreCase(identifier))
                .findFirst()
                .orElse(null);
    }

    public int getAmountLoaded() {
        return scripts.size();
    }

    public JavascriptPlaceholdersConfig getConfig() {
        return config;
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
        scripts.forEach(script -> {
            script.saveData();
            script.cleanup();
        });

        scripts.clear();
        config.reload();
        return config.loadPlaceholders();
    }

    public static JavascriptExpansion getInstance() {
        return instance;
    }

}
