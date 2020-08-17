package com.extendedclip.papi.expansion.javascript.command;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import com.extendedclip.papi.expansion.javascript.cloud.GithubScript;
import com.extendedclip.papi.expansion.javascript.cloud.GithubScriptManager;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GitCommand extends ICommand {

    private final JavascriptExpansion expansion;

    public GitCommand(JavascriptExpansion expansion) {
        this.expansion = expansion;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (expansion.getGithubScriptManager() == null) {
            ExpansionUtils.sendMsg(sender, "&cThis feature is disabled in the PlaceholderAPI config.");
            return;
        }

        if (args.length < 1) {
            ExpansionUtils.sendMsg(sender, "&cIncorrect usage! Type '&f/" + command + "&c' for more help.");
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

                ExpansionUtils.sendMsg(sender, availableScripts.size() + " &escript" + plural(availableScripts.size()) + " available on Github.", String.join(", ", scripts));
                return;
            }

            case "info": {
                if (args.length < 2) {
                    ExpansionUtils.sendMsg(sender, "&cIncorrect usage! &f/" + command + " git info [name]");
                    return;
                }

                final GithubScript script = manager.getScript(args[2]);

                if (script == null) {
                    ExpansionUtils.sendMsg(sender, "&cThe script &f" + args[2] + " &cdoes not exist!");
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
                    ExpansionUtils.sendMsg(sender, "&cIncorrect usage! &f/" + command + " git download [name]");
                    return;
                }

                final GithubScript script = manager.getScript(args[2]);

                if (script == null) {
                    ExpansionUtils.sendMsg(sender, "&cThe script &f" + args[2] + " &cdoes not exist!");
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

                papi.getConfig().set("expansions." + command + ".github_script_downloads", enabled);
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
                ExpansionUtils.sendMsg(sender, "&cIncorrect usage! Type '&f/" + command + "&c' for more help.");
            }
        }
    }

    @Override
    public @NotNull String getAlias() {
        return "git";
    }
    
    private String plural(final int amount) {
        return amount > 1 ? "s" : "";
    }
}
