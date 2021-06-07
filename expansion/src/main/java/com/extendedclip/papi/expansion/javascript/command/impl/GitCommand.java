package com.extendedclip.papi.expansion.javascript.command.impl;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import com.extendedclip.papi.expansion.javascript.cloud.GithubScript;
import com.extendedclip.papi.expansion.javascript.cloud.GithubScriptManager;
import com.extendedclip.papi.expansion.javascript.command.ExpansionCommand;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public final class GitCommand extends ExpansionCommand {
    private static final String ARG_REFRESH = "refresh";
    private static final String ARG_LIST = "list";
    private static final String ARG_INFO = "info";
    private static final String ARG_DOWNLOAD = "download";
    private static final String ARG_ENABLED = "enabled";


    private final JavascriptExpansion expansion;

    public GitCommand(final String parentCommandName, final JavascriptExpansion expansion) {
        super(parentCommandName, "git");
        this.expansion = expansion;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (expansion.getGithubScriptManager() == null) {
            ExpansionUtils.sendMsg(sender, "&cThis feature is disabled in the PlaceholderAPI config.");
            return;
        }

        if (args.length < 1) {
            ExpansionUtils.sendMsg(sender, "&cIncorrect usage! Type '&f/" + getParentCommandName() + "&c' for more help.");
            return;
        }

        final GithubScriptManager manager = expansion.getGithubScriptManager();

        switch (args[0].toLowerCase()) {
            case "refresh": {
                expansion.getGithubScriptManager().fetch();
                ExpansionUtils.sendMsg(sender, "&aFetching available scripts... Check back in a sec!");
                return;
            }

            case "list": {
                final List<GithubScript> availableScripts = manager.getAvailableScripts();
                final Set<String> scripts = availableScripts.stream().map(GithubScript::getName).collect(Collectors.toSet());

                ExpansionUtils.sendMsg(sender, availableScripts.size() + " &escript" + ExpansionUtils.plural(availableScripts.size()) + " available on Github.", String.join(", ", scripts));
                return;
            }

            case "info": {
                if (args.length < 2) {
                    ExpansionUtils.sendMsg(sender, "&cIncorrect usage! &f/" + getParentCommandName() + " git info [name]");
                    return;
                }

                final GithubScript script = manager.getScript(args[1]);

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

                final GithubScript script = manager.getScript(args[1]);

                if (script == null) {
                    ExpansionUtils.sendMsg(sender, "&cThe script &f" + args[1] + " &cdoes not exist!");
                    return;
                }

                if (new File(expansion.getGithubScriptManager().getJavascriptsFolder(), script.getName() + ".js").exists()) {
                    ExpansionUtils.sendMsg(sender, "&cCould not download " + script.getName() + " because a file with the same name already exist in the javascripts folder.");
                    return;
                }

                manager.downloadScript(script);
                ExpansionUtils.sendMsg(sender, "&aDownload started. &eCheck the scripts folder in a moment...");
                return;
            }

            case "enabled":
                if (args.length < 2) {
                    ExpansionUtils.sendMsg(sender, "&cIncorrect usage! &f/jsexpansion git enabled [true/false]");
                    return;
                }

                final boolean enabled = Boolean.parseBoolean(args[1]);
                final PlaceholderAPIPlugin papi = expansion.getPlaceholderAPI();

                papi.getConfig().set("expansions." + getParentCommandName() + ".github_script_downloads", enabled);
                papi.saveConfig();
                papi.reloadConfig();

                if (!enabled) {
                    if (expansion.getGithubScriptManager() != null) {
                        expansion.getGithubScriptManager().clear();
                        expansion.setGithubScriptManager(null);
                    }
                } else {
                    if (expansion.getGithubScriptManager() == null) {
                        expansion.setGithubScriptManager(new GithubScriptManager(expansion));
                    }
                    expansion.getGithubScriptManager().fetch();
                }

                ExpansionUtils.sendMsg(sender, "&6Git script downloads set to: &e" + enabled);
                return;

            default: {
                ExpansionUtils.sendMsg(sender, "&cIncorrect usage! Type '&f/" + getParentCommandName() + "&c' for more help.");
            }
        }
    }

    @Override
    @NotNull
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList(ARG_REFRESH, ARG_LIST, ARG_DOWNLOAD, ARG_ENABLED, ARG_INFO), new ArrayList<>());
        }
        return Collections.emptyList();
    }

    @Override
    @NotNull
    protected String getCommandFormat() {
        final String args = String.join(", ", Arrays.asList(ARG_REFRESH, ARG_LIST, ARG_DOWNLOAD, ARG_ENABLED, ARG_INFO));
        return "git [" + args + "] [params]";
    }

    @Override
    @NotNull
    protected String getDescription() {
        return "Manage github scripts";
    }
}
