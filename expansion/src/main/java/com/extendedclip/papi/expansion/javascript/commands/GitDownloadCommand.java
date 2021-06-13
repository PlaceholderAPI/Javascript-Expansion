package com.extendedclip.papi.expansion.javascript.commands;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.cloud.*;
import com.extendedclip.papi.expansion.javascript.cloud.download.PathSelector;
import com.extendedclip.papi.expansion.javascript.cloud.download.ScriptDownloader;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommand;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommandRouter;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class GitDownloadCommand extends ExpansionCommand {
    private final GitScriptManager scriptManager;

    public GitDownloadCommand(final GitScriptManager scriptManager) {
        super(ExpansionCommandRouter.COMMAND_NAME + " git", "info");
        this.scriptManager = scriptManager;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length < 1) {
            ExpansionUtils.sendMsg(sender, "&cIncorrect usage! &f/" + getParentCommandName() + " git info [name]");
            return;
        }
        final ScriptIndexProvider indexProvider = scriptManager.getIndexProvider();
        final GitScript script = indexProvider.getScriptIndex().flatMap(index -> index.getScript(args[1])).orElse(null);

        if (script == null) {
            ExpansionUtils.sendMsg(sender, "&cThe script &f" + args[1] + " &cdoes not exist!");
            return;
        }
        final PathSelector selector = scriptManager.getDownloadPathSelector();
        final Path path = selector.select(script.getName());
        if (Files.exists(path)) {
            ExpansionUtils.sendMsg(sender, "&cCould not download " + script.getName() + " because a file with the same name already exist in the javascripts folder.");
            return;
        }
        final ScriptDownloader downloader = scriptManager.getScriptDownloader();
        try {
            downloader.download(script);
            ExpansionUtils.sendMsg(sender, "&aDownload started. &eCheck the scripts folder in a moment...");
        } catch (final IOException exception) {
            ExpansionUtils.errorLog("Failed to download expansion!", exception);
        }
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
        return "git download [name]";
    }

    @Override
    protected @NotNull String getDescription() {
        return "Downloads specified git-script";
    }
}

