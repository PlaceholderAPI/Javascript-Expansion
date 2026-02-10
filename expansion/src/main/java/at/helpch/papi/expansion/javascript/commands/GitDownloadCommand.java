package at.helpch.papi.expansion.javascript.commands;

import at.helpch.papi.expansion.javascript.ExpansionUtils;
import at.helpch.papi.expansion.javascript.cloud.GitScript;
import at.helpch.papi.expansion.javascript.cloud.GitScriptManager;
import at.helpch.papi.expansion.javascript.cloud.ScriptIndex;
import at.helpch.papi.expansion.javascript.cloud.ScriptIndexProvider;
import at.helpch.papi.expansion.javascript.cloud.download.PathSelector;
import at.helpch.papi.expansion.javascript.cloud.download.ScriptDownloader;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommand;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommandRouter;
import at.helpch.papi.expansion.javascript.commands.util.ColorUtil;
import at.helpch.papi.expansion.javascript.config.ConfigManager;
import at.helpch.papi.expansion.javascript.config.ScriptConfiguration;
import com.hypixel.hytale.server.core.command.system.CommandSender;
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
    private final ConfigManager configManager;

    public GitDownloadCommand(final GitScriptManager scriptManager, final ScriptConfiguration configuration,
                              final ConfigManager configManager) {
        super(ExpansionCommandRouter.COMMAND_NAME + " git", "download");
        this.scriptManager = scriptManager;
        this.configuration = configuration;
        this.configManager = configManager;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ColorUtil.colorize("&cIncorrect usage! &f/" + getParentCommandName() + " git download [name]"));
            return;
        }
        final ScriptIndexProvider indexProvider = scriptManager.getIndexProvider();
        final GitScript script = indexProvider.getScriptIndex().flatMap(index -> index.getScript(args[0])).orElse(null);

        if (script == null) {
            sender.sendMessage(ColorUtil.colorize("&cThe script &f" + args[0] + " &cdoes not exist!"));
            return;
        }
        final PathSelector selector = scriptManager.getDownloadPathSelector();
        final Path path = selector.select(script.getName());
        if (Files.exists(path)) {
            sender.sendMessage(ColorUtil.colorize("&cCould not download " + script.getName() + " because a file with the same name already exist in the javascripts folder."));
            return;
        }
        final ScriptDownloader downloader = scriptManager.getScriptDownloader();
        CompletableFuture.supplyAsync(() -> {
            sender.sendMessage(ColorUtil.colorize("&aDownload started. &eCheck the scripts folder in a moment..."));
            try {
                return downloader.download(script);
            } catch (IOException exception) {
                ExpansionUtils.errorLog("Failed to download expansion!", exception);
                return null;
            }
        }).thenAccept(downloadedPath -> {
            if (downloadedPath == null) return;
            sender.sendMessage(ColorUtil.colorize("&aDownload complete! " + script.getName()));
            configuration.setPath(script.getName(), downloadedPath.getFileName().toString());
            configManager.save();
        });
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

