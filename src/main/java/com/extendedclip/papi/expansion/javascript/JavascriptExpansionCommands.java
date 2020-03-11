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
package com.extendedclip.papi.expansion.javascript;

import com.extendedclip.papi.expansion.javascript.cloud.GithubScript;
import com.extendedclip.papi.expansion.javascript.cloud.GithubScriptManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JavascriptExpansionCommands extends Command {

    private JavascriptExpansion expansion;
    private final String PERMISSION = "placeholderapi.js.admin";
    private String command;

    public JavascriptExpansionCommands(JavascriptExpansion expansion) {
        super("jsexpansion");
        command = getName();
        this.expansion = expansion;
        this.setDescription("Javascript expansion commands");
        this.setUsage("/" + command + " <args>");
        this.setPermission(PERMISSION);
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            msg(sender, "&cYou don't have permission to do that!");
            return true;
        }

        if (args.length == 0) {
            msg(sender,
                    "&eJavascript expansion &7v: &f" + expansion.getVersion(),
                    "&eCreated by: &f" + expansion.getAuthor(),
                    "&eWiki: &fhttps://github.com/PlaceholderAPI-Expansions/Javascript-Expansion/wiki",
                    "&r",
                    "&e/" + command + " reload &7- &fReload your javascripts without reloading PlaceholderAPI",
                    "&e/" + command + " list &7- &fList loaded script identifiers."
            );

            if (expansion.getGithubScriptManager() != null) {
                msg(sender,
                        "&e&e/" + command + " git refresh &7- &fRefresh available Github scripts",
                        "&e/" + command + " git download <name> &7- &fDownload a script from the js expansion github.",
                        "&e/" + command + " git list &7- &fList available scripts in the js expansion github.",
                        "&e/" + command + " git info (name) &7- &fGet the description and url of a specific script."
                );
            }

            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload": {
                msg(sender, "&aJavascriptExpansion reloading...");
                final int scripts = expansion.reloadScripts();
                msg(sender, scripts + " &7script" + plural(scripts) + " loaded");
                return true;
            }

            case "list": {
                final List<String> loaded = expansion.getLoadedIdentifiers();
                msg(sender,
                        loaded.size() + " &7script" + plural(loaded.size()) + " loaded.",
                        String.join(", ", loaded)
                );
                return true;
            }

            case "git": {
                if (expansion.getGithubScriptManager() == null) {
                    msg(sender, "&8This feature is disabled in the PlaceholderAPI config.");
                    return true;
                }

                if (args.length < 2) {
                    msg(sender, "&cIncorrect usage!");
                    return true;
                }

                final GithubScriptManager manager = expansion.getGithubScriptManager();

                switch (args[1].toLowerCase()) {
                    case "refresh": {
                        expansion.getGithubScriptManager().fetch();
                        msg(sender, "&aFetching available scripts... Check back in a sec!");
                        return true;
                    }

                    case "list": {
                        final List<GithubScript> availableScripts = manager.getAvailableScripts();
                        final Set<String> scripts = availableScripts.stream().map(GithubScript::getName).collect(Collectors.toSet());

                        msg(sender, availableScripts.size() + " &escript" + plural(availableScripts.size()) + " available on Github.", String.join(", ", scripts));
                        return true;
                    }

                    case "info": {
                        if (args.length < 3) {
                            msg(sender, "&4Incorrect usage! &f/" + command + " git info <name>");
                            return true;
                        }

                        final GithubScript script = manager.getScript(args[2]);

                        if (script == null) {
                            msg(sender, "&4The script &f" + args[2] + " &4does not exist!");
                            return true;
                        }

                        msg(sender,
                                "&eName: &f" + script.getName(),
                                "&eVersion: &f" + script.getVersion(),
                                "&eDescription: &f" + script.getDescription(),
                                "&eAuthor: &f" + script.getAuthor(),
                                "&eSource URL: &f" + script.getUrl()
                        );
                        return true;
                    }

                    case "download": {
                        if (args.length < 3) {
                            msg(sender, "&4Incorrect usage! &f/" + command + " git download <name>");
                            return true;
                        }

                        final GithubScript script = manager.getScript(args[2]);

                        if (script == null) {
                            msg(sender, "&4The script &f" + args[2] + " &4does not exist!");
                            return true;
                        }

                        manager.downloadScript(script);
                        msg(sender, "&6Download started... &eCheck the scripts folder in a moment...");
                        return true;
                    }

                    default: {
                        msg(sender, "&4Incorrect usage! &f/" + command + " &7for more help.");
                        return true;
                    }
                }
            }

            default: {
                return true;
            }
        }
    }

    private String plural(final int amount) {
        return amount > 1 ? "s" : "";
    }

    public void msg(CommandSender sender, String... text) {
        if (text == null) {
            return;
        }

        Arrays.stream(text).forEach(line -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
    }
}
