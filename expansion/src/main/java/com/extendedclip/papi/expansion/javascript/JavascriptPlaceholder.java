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
import com.extendedclip.papi.expansion.javascript.script.data.PersistableData;
import com.extendedclip.papi.expansion.javascript.script.data.YmlPersistableData;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JavascriptPlaceholder {
    private final String identifier;
    private final String script;
    private final PersistableData persistableData;
    private final Pattern pattern = Pattern.compile("//.*|/\\*[\\S\\s]*?\\*/|%([^%]+)%");
    private final ScriptEvaluatorFactory evaluatorFactory;
    private final JavascriptExpansion expansion;

    public JavascriptPlaceholder(@NotNull final String identifier, @NotNull final String script, @NotNull final ScriptEvaluatorFactory evaluatorFactory, @NotNull final JavascriptExpansion expansion) {
        final Path dataFilePath = expansion.getPlaceholderAPI().getDataFolder()
                .toPath()
                .resolve("javascripts")
                .resolve("javascript_data")
                .resolve(identifier + "_data.yml");

        try {
            this.persistableData = YmlPersistableData.create(identifier, dataFilePath);
        } catch (final IOException exception) {
            ExpansionUtils.errorLog("Unable to create placeholder data file", exception);
            throw new RuntimeException(exception);
        }
        this.identifier = identifier;
        this.script = script;
        this.evaluatorFactory = evaluatorFactory;
        this.expansion = expansion;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String evaluate(final OfflinePlayer player, final String... args) {
        // A checker to deny all placeholders inside comment codes
        final Matcher matcher = pattern.matcher(script);
        final StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            final String matched = matcher.group(0);
            if (!matched.startsWith("%") || matched.startsWith("/*") || matched.startsWith("//")) continue;
            matcher.appendReplacement(buffer, PlaceholderAPI.setPlaceholders(player, matched));
        }
        matcher.appendTail(buffer);
        final String parsedScript = buffer.toString();
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
                Object result = evaluator.execute(additionalBindings, parsedScript);
                return result != null ? PlaceholderAPI.setBracketPlaceholders(player, result.toString()) : "";
            } catch (RuntimeException | ScriptException exception) { // todo:: prepare specific exception and catch that instead of all runtime exceptions
                ExpansionUtils.errorLog("An error occurred while executing the script '" + identifier , exception);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            ExpansionUtils.errorLog("Argument out of bound while executing script '" + identifier + "':\n\t" + ex.getMessage(), null);
        }
        return "Script error (check console)";
    }

    private Map<String, Object> prepareDefaultBindings() {
        final Map<String, Object> bindings = new HashMap<>();
        bindings.put("Data", persistableData.getScriptData());
        bindings.put("DataVar", persistableData.getScriptData().getData());
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
        return persistableData.getScriptData();
    }

    public void saveData() {
        persistableData.save();
    }

    public PersistableData getPersistableData() {
        return persistableData;
    }
}
