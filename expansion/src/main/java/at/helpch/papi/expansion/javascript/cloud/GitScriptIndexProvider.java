package at.helpch.papi.expansion.javascript.cloud;

import at.helpch.placeholderapi.PlaceholderAPIPlugin;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class GitScriptIndexProvider implements ScriptIndexProvider {
    private static final Gson GSON = new Gson();
    private static final String INDEX_URL =
            "https://raw.githubusercontent.com/PlaceholderAPI/" +
            "Javascript-Expansion/hytale/scripts/master_list.json";

    @NotNull
    private final PlaceholderAPIPlugin plugin;

    private ScriptIndex index = null;

    public GitScriptIndexProvider(@NotNull final PlaceholderAPIPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public synchronized Optional<ScriptIndex> getScriptIndex() {
        return Optional.ofNullable(index);
    }

    @Override
    public void refreshIndex(@Nullable Consumer<ScriptIndex> indexConsumer) {
        plugin.getTaskRegistry().registerTask(CompletableFuture.runAsync(() -> {
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
        }));
    }
}
