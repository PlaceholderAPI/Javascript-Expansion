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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public class JavascriptPlaceholder {

    private final String DIRECTORY = PlaceholderAPIPlugin.getInstance().getDataFolder() + "/javascripts/javascript_data";
    private ScriptEngine engine;
    private String identifier;
    private String script;
    private ScriptData data;
    private File dataFile;
    private FileConfiguration config;

    public JavascriptPlaceholder(ScriptEngine engine, String identifier, String script) {
        Validate.notNull(engine, "ScriptEngine can not be null");
        Validate.notNull(identifier, "Identifier can not be null");
        Validate.notNull(script, "Script can not be null");

        this.engine = engine;
        this.identifier = identifier;
        this.script = script;
        final File directory = new File(DIRECTORY);

        if (directory.exists()) {
            directory.mkdirs();
        }

        data = new ScriptData();
        dataFile = new File(DIRECTORY, identifier + "_data.yml");
        engine.put("Data", data);
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
            engine.put("BukkitPlayer", player != null && player.isOnline() ? player.getPlayer() : null);
            engine.put("OfflinePlayer", player);
            Object result = engine.eval(exp);
            return result != null ? PlaceholderAPI.setBracketPlaceholders(player, result.toString()) : "";
        } catch (ScriptException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        return "Script error";
    }

    public ScriptData getData() {
        // this should never be null but just in case setData(null) is called
        if (data == null) {
            data = new ScriptData();
        }
        return data;
    }

    public void setData(ScriptData data) {
        this.data = data;
    }

    public boolean loadData() {
        config = new YamlConfiguration();

        if (!dataFile.exists()) {
            return false;
        }

        try {
            config.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return false;
        }

        final Set<String> keys = config.getKeys(true);

        if (keys.size() == 0) {
            return false;
        }

        if (data == null) {
            data = new ScriptData();
        } else {
            data.clear();
        }

        keys.forEach(key -> data.set(key, config.get(key)));

        if (!data.isEmpty()) {
            this.setData(data);
            return true;
        }

        return false;
    }

    public boolean saveData() {
        if (data == null || data.isEmpty()) {
            return false;
        }

        if (config == null) {
            return false;
        }

        data.getData().forEach((key, value) -> config.set(key, value));

        try {
            config.save(dataFile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void cleanup() {
        if (this.data != null) {
            this.data.clear();
            this.data = null;
        }

        this.config = null;
    }
}
