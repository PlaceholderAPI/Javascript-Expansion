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

import com.extendedclip.papi.expansion.javascript.cloud.GithubScriptManager;
import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

public class JavascriptExpansion extends PlaceholderExpansion implements Cacheable, Configurable {

    private ScriptEngine globalEngine = null;
    public ScriptEngineManager engineManager = null;

    private JavascriptPlaceholdersConfig config;
    private final Set<JavascriptPlaceholder> scripts;
    private final String VERSION;
    private static JavascriptExpansion instance;
    private boolean debug;
    private GithubScriptManager githubManager;
    private JavascriptExpansionCommands commands;
    private CommandMap commandMap;
    private String argument_split;

    public JavascriptExpansion() {
        instance = this;
        this.VERSION = getClass().getPackage().getImplementationVersion();
        this.scripts = new HashSet<>();

        try {
            final Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap) field.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            ExpansionUtils.errorLog("An error occurred while accessing CommandMap.", e, true);
        }

        // Register shaded nashorn
        engineManager = new ScriptEngineManager(null);
        engineManager.registerEngineName("nashorn", new NashornScriptEngineFactory());
    }

    @Override
    public String getAuthor() {
        return "clip";
    }

    @Override
    public String getIdentifier() {
        return "javascript";
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public boolean register() {
        String defaultEngine = ExpansionUtils.DEFAULT_ENGINE;

        if (globalEngine == null) {
            try {
                globalEngine = engineManager.getEngineByName(getString("engine", defaultEngine));
            } catch (NullPointerException ex) {
                ExpansionUtils.warnLog("Javascript engine type was invalid! Defaulting to '" + defaultEngine + "'", null);
                globalEngine = engineManager.getEngineByName(defaultEngine);
            }
        }

        argument_split = getString("argument_split", ",");
        if (argument_split.equals("_")) {
            argument_split = ",";
            ExpansionUtils.warnLog("Underscore character will not be allowed for splitting. Defaulting to ',' for this", null);
        }

        debug = (boolean) get("debug", false);
        config = new JavascriptPlaceholdersConfig(this);

        int amountLoaded = config.loadPlaceholders();
        ExpansionUtils.infoLog(amountLoaded + " script" + ExpansionUtils.plural(amountLoaded) + " loaded!");


        if (debug) {
            ExpansionUtils.infoLog("Java version: " + System.getProperty("java.version"));

            final List<ScriptEngineFactory> factories = engineManager.getEngineFactories();
            ExpansionUtils.infoLog("Displaying all script engine factories.", false);

            for (ScriptEngineFactory factory : factories) {
                System.out.println(factory.getEngineName());
                System.out.println("  Version: " + factory.getEngineVersion());
                System.out.println("  Lang name: " + factory.getLanguageName());
                System.out.println("  Lang version: " + factory.getLanguageVersion());
                System.out.println("  Extensions: ." + String.join(", .", factory.getExtensions()));
                System.out.println("  Mime types: " + String.join(", ", factory.getMimeTypes()));
                System.out.println("  Names: " + String.join(", ", factory.getNames()));
            }
        }

        if ((boolean) get("github_script_downloads", false)) {
            githubManager = new GithubScriptManager(this);
            githubManager.fetch();
        }

        registerCommand();
        return super.register();
    }

    @Override
    public void clear() {
        unregisterCommand();

        scripts.forEach(script -> {
            script.saveData();
            script.cleanup();
        });

        if (githubManager != null) {
            githubManager.clear();
            githubManager = null;
        }

        scripts.clear();
        globalEngine = null;
        instance = null;
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

    public ScriptEngine getGlobalEngine() {
        return globalEngine;
    }

    public JavascriptPlaceholdersConfig getConfig() {
        return config;
    }

    @Override
    public Map<String, Object> getDefaults() {
        final Map<String, Object> defaults = new HashMap<>();
        defaults.put("engine", "javascript");
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

    public GithubScriptManager getGithubScriptManager() {
        return githubManager;
    }

    public void setGithubScriptManager(GithubScriptManager manager) {
        this.githubManager = manager;
    }

    private void unregisterCommand() {
        if (commandMap != null && commands != null) {

            try {
                Class<? extends CommandMap> cmdMapClass = commandMap.getClass();
                final Field f;

                //Check if the server's in 1.13+
                if (cmdMapClass.getSimpleName().equals("CraftCommandMap")) {
                    f = cmdMapClass.getSuperclass().getDeclaredField("knownCommands");
                } else {
                    f = cmdMapClass.getDeclaredField("knownCommands");
                }

                f.setAccessible(true);
                Map<String, Command> knownCmds = (Map<String, Command>) f.get(commandMap);
                knownCmds.remove(commands.getName());
                for (String alias : commands.getAliases()) {
                    if (knownCmds.containsKey(alias) && knownCmds.get(alias).toString().contains(commands.getName())) {
                        knownCmds.remove(alias);
                    }
                }

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            commands.unregister(commandMap);
        }
    }

    private void registerCommand() {
        if (commandMap == null) {
            return;
        }

        commands = new JavascriptExpansionCommands(this);
        commandMap.register("papi" + commands.getName(), commands);
        commands.isRegistered();
    }
}
