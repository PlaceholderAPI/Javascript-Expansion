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

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class JavascriptPlaceholdersConfig {

    private final JavascriptExpansion ex;
    private final PlaceholderAPIPlugin plugin;
    private FileConfiguration config;
    private File file;

    public JavascriptPlaceholdersConfig(JavascriptExpansion ex) {
        this.ex = ex;
        plugin = ex.getPlaceholderAPI();
        reload();
    }

    public void reload() {
        if (file == null) {
            file = new File(plugin.getDataFolder(), "javascript_placeholders.yml");
        }

        config = YamlConfiguration.loadConfiguration(file);
        config.options().header("Javascript Expansion: " + ex.getVersion()
                + "\nThis is the main configuration file for the Javascript Expansion."
                + "\n"
                + "\nYou will define your javascript placeholders in this file."
                + "\n"
                + "\nJavascript files must be located in the:"
                + "\n /plugins/placeholderapi/javascripts/ folder"
                + "\n"
                + "\nA detailed guide on how to create your own javascript placeholders"
                + "\ncan be found here:"
                + "\nhttps://github.com/PlaceholderAPI-Expansions/Javascript-Expansion/wiki"
                + "\n"
                + "\nYour javascript placeholders will be identified by: %javascript_<identifier>%"
                + "\n"
                + "\nConfiguration format:"
                + "\n"
                + "\n<identifier>:"
                + "\n  file: <name of file>.<file extension>"
                + "\n  engine: (name of script engine)"
                + "\n"
                + "\n"
                + "\nExample:"
                + "\n"
                + "\n'my_placeholder':"
                + "\n  file: 'my_placeholder.js'"
                + "\n  engine: 'nashorn'");

        if (config.getKeys(false).isEmpty()) {
            config.set("example.file", "example.js");
            config.set("example.engine", ExpansionUtils.DEFAULT_ENGINE);
        }

        save();
    }

    public FileConfiguration load() {
        if (config == null) reload();
        return config;
    }

    public void save() {
        if (config == null || file == null) {
            return;
        }

        try {
            load().save(file);
        } catch (IOException ex) {
            ExpansionUtils.warnLog("Could not save to " + file, ex);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public int loadPlaceholders() {
        if (config == null || config.getKeys(false).isEmpty()) {
            return 0;
        }

        final File directory = new File(plugin.getDataFolder(), "javascripts");

        try {
            if (!directory.exists()) {
                directory.mkdirs();
                ExpansionUtils.infoLog("Creating directory: " + directory.getPath());
            }
        } catch (SecurityException e) {
            ExpansionUtils.errorLog("Could not create directory: " + directory.getPath(), e);
        }

        for (String identifier : config.getKeys(false)) {
            final String fileName = config.getString(identifier + ".file");
            if (!config.contains(identifier + ".file") || fileName == null) {
                ExpansionUtils.warnLog("Javascript placeholder: " + identifier + " does not have a file specified", null);
                continue;
            }

            final File scriptFile = new File(plugin.getDataFolder() + "/javascripts", fileName);

            if (!scriptFile.exists()) {
                ExpansionUtils.infoLog(scriptFile.getName() + " does not exist. Creating one for you...");

                try {
                    scriptFile.createNewFile();
                    ExpansionUtils.infoLog(scriptFile.getName() + " created! Add your javascript to this file and use '/jsexpansion reload' to load it!");
                } catch (IOException e) {
                    ExpansionUtils.errorLog("An error occurred while creating " + scriptFile.getName(), e);
                }

                continue;
            }

            final String script = getContents(scriptFile);

            if (script == null || script.isEmpty()) {
                ExpansionUtils.warnLog("File: " + scriptFile.getName() + " for Javascript placeholder: " + identifier + " is empty", null);
                continue;
            }

            final JavascriptPlaceholder placeholder = new JavascriptPlaceholder(identifier, script, (a, b) -> "");
            final boolean added = ex.addJSPlaceholder(placeholder);

            if (added) {
                if (placeholder.loadData()) {
                    ExpansionUtils.infoLog("Data for placeholder &b" + identifier + "&r has been loaded");
                }

                ExpansionUtils.infoLog("Placeholder &b%javascript_" + identifier + "%&r has been loaded");
            } else {
                ExpansionUtils.warnLog("Javascript placeholder %javascript_" + identifier + "% is duplicated!", null);
            }
        }
        return ex.getAmountLoaded();
    }

    private String getContents(File file) {
        final StringBuilder sb = new StringBuilder();

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            lines.forEach((line) -> sb.append(line).append("\n"));
        } catch (IOException e) {
            return null;
        }

        return sb.toString();
    }
}
