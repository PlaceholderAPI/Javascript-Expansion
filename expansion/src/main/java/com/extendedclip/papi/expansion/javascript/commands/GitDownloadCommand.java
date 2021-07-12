package com.extendedclip.papi.expansion.javascript.commands;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.cloud.*;
import com.extendedclip.papi.expansion.javascript.cloud.download.PathSelector;
import com.extendedclip.papi.expansion.javascript.cloud.download.ScriptDownloader;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommand;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommandRouter;
import com.extendedclip.papi.expansion.javascript.config.ScriptConfiguration;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class GitDownloadCommand extends ExpansionCommand {
    private final GitScriptManager scriptManager;
    private final ScriptConfiguration configuration;

    public GitDownloadCommand(final GitScriptManager scriptManager, final ScriptConfiguration configuration) {
        super(ExpansionCommandRouter.COMMAND_NAME + " git", "download");
        this.scriptManager = scriptManager;
        this.configuration = configuration;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length < 1) {
            ExpansionUtils.sendMsg(sender, "&cIncorrect usage! &f/" + getParentCommandName() + " git info [name]");
            return;
        }
        final ScriptIndexProvider indexProvider = scriptManager.getIndexProvider();
        final GitScript script = indexProvider.getScriptIndex().flatMap(index -> index.getScript(args[0])).orElse(null);

        if (script == null) {
            ExpansionUtils.sendMsg(sender, "&cThe script &f" + args[0] + " &cdoes not exist!");
            return;
        }
        final PathSelector selector = scriptManager.getDownloadPathSelector();
        final Path path = selector.select(script.getName());
        if (Files.exists(path)) {
            ExpansionUtils.sendMsg(sender, "&cCould not download " + script.getName() + " because a file with the same name already exist in the javascripts folder.");
            return;
        }
        final ScriptDownloader downloader = scriptManager.getScriptDownloader();
        CompletableFuture.supplyAsync(() -> {
            ExpansionUtils.sendMsg(sender, "&aDownload started. &eCheck the scripts folder in a moment...");
            try {
                return downloader.download(script);
            } catch (IOException exception) {
                ExpansionUtils.errorLog("Failed to download expansion!", exception);
                return null;
            }
        }).thenAccept(downloadedPath -> {
            if (downloadedPath == null) return;
            ExpansionUtils.sendMsg(sender, "&aDownload complete! " + script.getName());
            configuration.setPath(script.getName(), downloadedPath.getFileName().toString());
            configuration.save();
        });
    }

    @Override
    public @NotNull List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length > 0) {
            final ScriptIndexProvider indexProvider = scriptManager.getIndexProvider();
            final List<String> scripts = indexProvider.getScriptIndex()
                    .map(ScriptIndex::getAllScripts)
                    .orElse(Collections.emptyList()).stream()
                    .map(GitScript::getName)
                    .collect(Collectors.toList());
            return StringUtil.copyPartialMatches(args[0], scripts, new ArrayList<>());
        }
        return Collections.emptyList();
    }

    @Override
    protected @NotNull String getCommandFormat() {
        return "download [name]";
    }

    @Override
    protected @NotNull String getDescription() {
        return "Downloads specified git-script";
    }
}

