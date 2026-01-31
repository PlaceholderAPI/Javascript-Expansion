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



import at.helpch.papi.expansion.javascript.evaluator.ScriptEvaluator;
import at.helpch.papi.expansion.javascript.evaluator.ScriptEvaluatorFactory;
import at.helpch.papi.expansion.javascript.script.ScriptData;
import at.helpch.papi.expansion.javascript.script.data.PersistableData;
import at.helpch.papi.expansion.javascript.script.data.YmlPersistableData;
import at.helpch.placeholderapi.PlaceholderAPI;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
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
        final Path dataFilePath = expansion.getPlaceholderAPI().getDataDirectory()
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

    public String evaluate(final PlayerRef playerRef, final String... args) {
        // A checker to deny all placeholders inside comment codes
        final Matcher matcher = pattern.matcher(script);
        final StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            final String matched = matcher.group(0);
            if (!matched.startsWith("%") || matched.startsWith("/*") || matched.startsWith("//")) continue;
            matcher.appendReplacement(buffer, PlaceholderAPI.setPlaceholders(playerRef, matched));
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
                arguments[i] = PlaceholderAPI.setBracketPlaceholders(playerRef, args[i]);
            }

            final Map<String, Object> defaultBindings = prepareDefaultBindings();

            final ScriptEvaluator evaluator = evaluatorFactory.create(defaultBindings);

            final Map<String, Object> additionalBindings = new HashMap<>();
            additionalBindings.put("args", arguments);
            if (playerRef != null) {
                final Ref<EntityStore> ref = playerRef.getReference();

                if (ref == null || !ref.isValid())
                    return null;

                final Store<EntityStore> store = ref.getStore();
                final Player player = store.getComponent(ref, Player.getComponentType());

                additionalBindings.put("Player", player);
            }
            additionalBindings.put("OfflinePlayer", playerRef);
            try {
                Object result = evaluator.execute(additionalBindings, parsedScript);
                return result != null ? PlaceholderAPI.setBracketPlaceholders(playerRef, result.toString()) : "";
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
        bindings.put("HytaleServer", HytaleServer.get());
        bindings.put("Universe", Universe.get());
        bindings.put("Permissions", PermissionsModule.get());
        // todo: any other important things?
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
