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

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

public class JavascriptPlaceholder {

    private final String DIRECTORY = PlaceholderAPIPlugin.getInstance().getDataFolder() + "/javascripts/javascript_data";
    private final ScriptEngine engine;
    private final String identifier;
    private final String script;
    private final boolean dataPersists;
    private ScriptData scriptData;
    private final File dataFile;
    private YamlConfiguration yaml;

    public JavascriptPlaceholder(ScriptEngine engine, String identifier, String script, boolean dataPersists) {
        Validate.notNull(engine, "ScriptEngine can not be null");
        Validate.notNull(identifier, "Identifier can not be null");
        Validate.notNull(script, "Script can not be null");

        this.engine = engine;
        this.identifier = identifier;
        this.script = script;
        this.dataPersists = dataPersists;
        final File directory = new File(DIRECTORY);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        scriptData = new ScriptData();
        dataFile = new File(directory, identifier + "_data.yml");
        engine.put("Data", scriptData);
        engine.put("BukkitServer", Bukkit.getServer());
        engine.put("Expansion", JavascriptExpansion.getInstance());
        engine.put("Placeholder", this);
        engine.put("PlaceholderAPI", PlaceholderAPI.class);
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getScript() {
        return script;
    }

    public String evaluate(OfflinePlayer player, String... args) {
        String exp = PlaceholderAPI.setPlaceholders(player, script);

        try {
            String[] arguments = null;

            if (args != null && args.length > 0) {
                arguments = new String[args.length];

                for (int i = 0; i < args.length; i++) {
                    if (args[i] == null || args[i].isEmpty()) {
                        continue;
                    }

                    arguments[i] = PlaceholderAPI.setBracketPlaceholders(player, args[i]);
                }
            }

            if (arguments == null) {
                arguments = new String[]{};
            }

            engine.put("args", arguments);

            if (player != null && player.isOnline()) {
                engine.put("BukkitPlayer", player.getPlayer());
                engine.put("Player", player.getPlayer());
            }

            engine.put("OfflinePlayer", player);
            Object result = engine.eval(exp);
            return result != null ? PlaceholderAPI.setBracketPlaceholders(player, result.toString()) : "";
        } catch (ScriptException ex) {
            PlaceholderAPIPlugin.getInstance().getLogger().log(Level.SEVERE, "[JavaScript] An error occurred while executing the script '" + identifier + "'", ex);
        }

        return "Script error! (check console)";
    }

    public ScriptData getData() {
        // this should never be null but just in case setData(null) is called
        if (scriptData == null) {
            scriptData = new ScriptData();
        }
        return scriptData;
    }

    public void setData(ScriptData data) {
        this.scriptData = data;
    }

    public boolean loadData() {
        yaml = new YamlConfiguration();
        dataFile.getParentFile().mkdirs();

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                PlaceholderAPIPlugin.getInstance().getLogger().log(Level.SEVERE, "[JavaScript Expansion] An error occurred while creating data file for " + getIdentifier(), e);
                return false;
            }
        }

        try {
            yaml.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            PlaceholderAPIPlugin.getInstance().getLogger().log(Level.SEVERE, "[JavaScript Expansion] An error occurred while loading for " + getIdentifier(), e);
            return false;
        }

        final Set<String> keys = yaml.getKeys(true);

        if (keys.size() == 0) {
            return false;
        }

        if (scriptData == null) {
            scriptData = new ScriptData();
        } else {
            scriptData.clear();
        }

        keys.forEach(key -> scriptData.set(key, yaml.get(key)));

        if (!scriptData.isEmpty()) {
            this.setData(scriptData);
            return true;
        }

        return false;
    }

    public boolean saveData() {
        if (!dataPersists || scriptData == null || scriptData.isEmpty() || yaml == null) {
            return false;
        }

        scriptData.getData().forEach((key, value) -> yaml.set(key, value));

        try {
            yaml.save(dataFile);
            return true;
        } catch (IOException e) {
            PlaceholderAPIPlugin.getInstance().getLogger().log(Level.SEVERE, "[JavaScript Expansion] An error occurred while saving data for " + getIdentifier(), e);
            return false;
        }
    }

    public void cleanup() {
        if (this.scriptData != null) {
            this.scriptData.clear();
            this.scriptData = null;
        }

        this.yaml = null;
    }
}
