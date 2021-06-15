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



import com.extendedclip.papi.expansion.javascript.evaluator.ScriptEvaluator;
import com.extendedclip.papi.expansion.javascript.evaluator.ScriptEvaluatorFactory;
import com.extendedclip.papi.expansion.javascript.script.ScriptData;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavascriptPlaceholder {
    private final String identifier;
    private final String script;
    private ScriptData scriptData;
    private final File dataFile;
    private YamlConfiguration yaml;
    private final Pattern pattern;
    private final ScriptEvaluatorFactory evaluatorFactory;
    private final JavascriptExpansion expansion;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public JavascriptPlaceholder(String identifier, String script, ScriptEvaluatorFactory evaluatorFactory, final JavascriptExpansion expansion) {
        Validate.notNull(identifier, "Identifier can not be null");
        Validate.notNull(script, "Script can not be null");

        String dir = PlaceholderAPIPlugin.getInstance().getDataFolder() + "/javascripts/javascript_data";
        this.identifier = identifier;
        this.script = script;
        final File directory = new File(dir);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        pattern = Pattern.compile("//.*|/\\*[\\S\\s]*?\\*/|%([^%]+)%");
        scriptData = new ScriptData();
        dataFile = new File(directory, identifier + "_data.yml");
        this.evaluatorFactory = evaluatorFactory;
        this.expansion = expansion;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String evaluate(OfflinePlayer player, String... args) {
        // A checker to deny all placeholders inside comment codes
        Matcher matcher = pattern.matcher(script);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String matched = matcher.group(0);
            if (!matched.startsWith("%") || matched.startsWith("/*") || matched.startsWith("//")) continue;

            matcher.appendReplacement(buffer, PlaceholderAPI.setPlaceholders(player, matched));
        }

        matcher.appendTail(buffer);
        try {
            final int length;
            if (args != null) {
                length = args.length;
            } else {
                length = 0;
            }
            final String[] arguments = new String[length];

            for (int i = 0; i < length; i++) {
                if (args[i] == null || args[i].isEmpty()) {
                    continue;
                }
                arguments[i] = PlaceholderAPI.setBracketPlaceholders(player, args[i]);
            }
            final Map<String, Object> defaultBindings = prepareDefaultBindings();

            final ScriptEvaluator evaluator = evaluatorFactory.create(defaultBindings);

            final Map<String, Object> additionalBindings = new HashMap<>();
            additionalBindings.put("args", arguments);
            if (player != null && player.isOnline()) {
                additionalBindings.put("BukkitPlayer", player.getPlayer());
                additionalBindings.put("Player", player.getPlayer());
            }
            additionalBindings.put("OfflinePlayer", player);
            try {
                Object result = evaluator.execute(additionalBindings, script);
                return result != null ? PlaceholderAPI.setBracketPlaceholders(player, result.toString()) : "";
            } catch (RuntimeException exception) { // todo:: prepare specific exception and catch that instead of all runtime exceptions
                ExpansionUtils.errorLog("An error occurred while executing the script '" + identifier + "':\n\t" + exception.getMessage(), null);
                exception.printStackTrace();
                return "Script error (check console)";
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            ExpansionUtils.errorLog("Argument out of bound while executing script '" + identifier + "':\n\t" + ex.getMessage(), null);
        }
        return "Script error (check console)";
    }

    private Map<String, Object> prepareDefaultBindings() {
        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("Data", scriptData);
        bindings.put("DataVar", scriptData.getData());
        bindings.put("BukkitServer", Bukkit.getServer());
        bindings.put("Expansion", expansion);
        bindings.put("Placeholder", this);
        bindings.put("PlaceholderAPI", PlaceholderAPI.class);
        return bindings;
    }

    public String getScript() {
        return script;
    }

    public ScriptData getData() {
        if (scriptData == null) {
            scriptData = new ScriptData();
        }
        return scriptData;
    }

    public void setData(ScriptData data) {
        this.scriptData = data;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean loadData() {
        yaml = new YamlConfiguration();
        dataFile.getParentFile().mkdirs();

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                ExpansionUtils.errorLog("An error occurred while creating data file for " + getIdentifier(), e);
                return false;
            }
        }

        try {
            yaml.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            ExpansionUtils.errorLog("An error occurred while loading for " + getIdentifier(), e);
            return false;
        }

        final Set<String> keys = yaml.getKeys(true);

        if (keys.size() == 0) {
            return false;
        }

        if (scriptData == null)
            scriptData = new ScriptData();
        else scriptData.clear();

        keys.forEach(key -> scriptData.set(key, ExpansionUtils.ymlToJavaObj(yaml.get(key))));

        if (!scriptData.isEmpty()) {
            setData(scriptData);
            return true;
        }
        return false;
    }

    public void saveData() {
        if (scriptData == null || scriptData.isEmpty() || yaml == null) {
            return;
        }

        try {
            yaml.save(dataFile);
        } catch (IOException e) {
            ExpansionUtils.errorLog(ExpansionUtils.PREFIX + "An error occurred while saving data for " + getIdentifier(), e);
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
