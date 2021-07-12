package com.extendedclip.papi.expansion.javascript.cloud;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class GitScriptIndexProvider implements ScriptIndexProvider {
    private static final Gson GSON = new Gson();
    private static final String INDEX_URL =
            "https://raw.githubusercontent.com/PlaceholderAPI/" +
            "Javascript-Expansion/master/scripts/master_list.json";

    @NotNull
    private final JavaPlugin plugin;

    private ScriptIndex index = null;

    public GitScriptIndexProvider(@NotNull final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public synchronized Optional<ScriptIndex> getScriptIndex() {
        return Optional.ofNullable(index);
    }

    @Override
    public void refreshIndex(@Nullable Consumer<ScriptIndex> indexConsumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try(final Reader indexReader = new InputStreamReader(new URL(INDEX_URL).openStream())) {
                final List<GitScript> scripts = GSON.fromJson(indexReader, new TypeToken<ArrayList<GitScript>>() {}.getType());
                final Map<String, GitScript> map = scripts.stream().collect(
                        Collectors.toMap(GitScript::getName, Function.identity())
                );
                final GitScriptIndex localIndex = new GitScriptIndex(map);
                synchronized (this) {
                    index = localIndex;
                }
                if (indexConsumer != null) {
                    indexConsumer.accept(localIndex);
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });
    }
}
