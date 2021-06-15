package com.extendedclip.papi.expansion.javascript.cloud;

import com.extendedclip.papi.expansion.javascript.cloud.download.ChanneledScriptDownloader;
import com.extendedclip.papi.expansion.javascript.cloud.download.GitScriptPathSelector;
import com.extendedclip.papi.expansion.javascript.cloud.download.PathSelector;
import com.extendedclip.papi.expansion.javascript.cloud.download.ScriptDownloader;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class GitScriptManager {
    private final ActiveStateSetter activeStateSetter;
    private final GitScriptIndexProvider indexProvider;
    private final ScriptDownloader scriptDownloader;
    private final PathSelector downloadPathSelector;

    public GitScriptManager(ActiveStateSetter activeStateSetter, GitScriptIndexProvider indexProvider, ScriptDownloader scriptDownloader, PathSelector downloadPathSelector) {
        this.activeStateSetter = activeStateSetter;
        this.indexProvider = indexProvider;
        this.scriptDownloader = scriptDownloader;
        this.downloadPathSelector = downloadPathSelector;
    }

    public ActiveStateSetter getActiveStateSetter() {
        return activeStateSetter;
    }

    public GitScriptIndexProvider getIndexProvider() {
        return indexProvider;
    }

    public ScriptDownloader getScriptDownloader() {
        return scriptDownloader;
    }

    public PathSelector getDownloadPathSelector() {
        return downloadPathSelector;
    }

    public static GitScriptManager createDefault(final JavaPlugin plugin) {
        final PathSelector pathSelector = new GitScriptPathSelector(new File(plugin.getDataFolder(), "javascripts"));
        final ScriptDownloader downloader = new ChanneledScriptDownloader(pathSelector);
        final GitScriptIndexProvider indexProvider = new GitScriptIndexProvider(plugin);
        final ActiveStateSetter activeStateSetter = new GitScriptActiveStateSetter(plugin);
        return new GitScriptManager(activeStateSetter, indexProvider, downloader, pathSelector);
    }
}
