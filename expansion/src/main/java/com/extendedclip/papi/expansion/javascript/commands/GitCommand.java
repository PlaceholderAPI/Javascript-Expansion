package com.extendedclip.papi.expansion.javascript.commands;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.cloud.*;
import com.extendedclip.papi.expansion.javascript.cloud.download.PathSelector;
import com.extendedclip.papi.expansion.javascript.cloud.download.ScriptDownloader;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommand;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class GitCommand extends ExpansionCommand {
    private static final String ARG_REFRESH = "refresh";
    private static final String ARG_LIST = "list";
    private static final String ARG_INFO = "info";
    private static final String ARG_DOWNLOAD = "download";
    private static final String ARG_ENABLED = "enabled";


    private final GitScriptManager scriptManager;

    public GitCommand(final String parentCommandName, final GitScriptManager scriptManager) {
        super(parentCommandName, "git");
        this.scriptManager = scriptManager;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length < 1) {
            ExpansionUtils.sendMsg(sender, "&cIncorrect usage! Type '&f/" + getParentCommandName() + "&c' for more help.");
            return;
        }
        final ActiveStateSetter activeStateSetter = scriptManager.getActiveStateSetter();
        if (!activeStateSetter.isActive() && !"enabled".equalsIgnoreCase(args[0])) {
            ExpansionUtils.sendMsg(sender, "&cThis feature is disabled in the PlaceholderAPI config.");
            return;
        }

        final ScriptIndexProvider indexManager = scriptManager.getIndexProvider();

        switch (args[0].toLowerCase()) {
            case "refresh": {
                ExpansionUtils.sendMsg(sender, "&aFetching available scripts... Check back in a sec!");
                indexManager.refreshIndex(index -> {
                    ExpansionUtils.sendMsg(sender, "&aFetched " + index.getCount() + " scripts to index!");
                });
                return;
            }

            case "list": {
                final Collection<GitScript> availableScripts = indexManager.getScriptIndex().map(ScriptIndex::getAllScripts).orElse(Collections.emptyList());
                final Set<String> scripts = availableScripts.stream().map(GitScript::getName).collect(Collectors.toSet());

                ExpansionUtils.sendMsg(sender, availableScripts.size() + " &escript" + ExpansionUtils.plural(availableScripts.size()) + " available on Github.", String.join(", ", scripts));
                return;
            }

            case "info": {
                if (args.length < 2) {
                    ExpansionUtils.sendMsg(sender, "&cIncorrect usage! &f/" + getParentCommandName() + " git info [name]");
                    return;
                }

                final GitScript script = indexManager.getScriptIndex().flatMap(index -> index.getScript(args[1])).orElse(null);

                if (script == null) {
                    ExpansionUtils.sendMsg(sender, "&cThe script &f" + args[1] + " &cdoes not exist!");
                    return;
                }

                ExpansionUtils.sendMsg(sender,
                        "&eName: &f" + script.getName(),
                        "&eVersion: &f" + script.getVersion(),
                        "&eDescription: &f" + script.getDescription(),
                        "&eAuthor: &f" + script.getAuthor(),
                        "&eSource URL: &f" + script.getUrl()
                );
                return;
            }

            case "download": {
                if (args.length < 2) {
                    ExpansionUtils.sendMsg(sender, "&cIncorrect usage! &f/" + getParentCommandName() + " git download [name]");
                    return;
                }

                final GitScript script = indexManager.getScriptIndex().flatMap(index -> index.getScript(args[1])).orElse(null);

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
                return;
            }

            case "enabled":
                if (args.length < 2) {
                    ExpansionUtils.sendMsg(sender, "&cIncorrect usage! &f/jsexpansion git enabled [true/false]");
                    return;
                }

                final boolean enabled = Boolean.parseBoolean(args[1]);
                activeStateSetter.setActive(enabled);

                ExpansionUtils.sendMsg(sender, "&6Git script downloads set to: &e" + enabled);
                return;

            default: {
                ExpansionUtils.sendMsg(sender, "&cIncorrect usage! Type '&f/" + getParentCommandName() + "&c' for more help.");
            }
        }
    }

    @Override
    @NotNull
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList(ARG_REFRESH, ARG_LIST, ARG_DOWNLOAD, ARG_ENABLED, ARG_INFO), new ArrayList<>());
        }
        return Collections.emptyList();
    }

    @Override
    @NotNull
    protected String getCommandFormat() {
        final String args = String.join("/", Arrays.asList(ARG_REFRESH, ARG_LIST, ARG_DOWNLOAD, ARG_ENABLED, ARG_INFO));
        return "git [" + args + "] [params]";
    }

    @Override
    @NotNull
    protected String getDescription() {
        return "Manage github scripts";
    }
}
