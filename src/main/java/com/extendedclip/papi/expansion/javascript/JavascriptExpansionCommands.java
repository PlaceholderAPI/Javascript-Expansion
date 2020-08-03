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
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class JavascriptExpansionCommands extends Command {

    private final JavascriptExpansion expansion;
    private final String PERMISSION = "placeholderapi.js.admin";
    private final String command;
    private final String[] aliases;

    public JavascriptExpansionCommands(JavascriptExpansion expansion) {
        super("jsexpansion");
        command = getName();
        this.expansion = expansion;
        this.setDescription("Javascript expansion commands");
        this.setUsage("/" + command + " <args>");
        this.aliases = new String[]{"jsexpansion", "jsexp"};
        this.setAliases(Arrays.asList(aliases));
        this.setPermission(PERMISSION);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (!sender.hasPermission(PERMISSION)) {
            return Collections.emptyList();
        }

        final List<String> commands = new ArrayList<>(Arrays.asList("list", "parse", "reload"));
        final List<String> completion = new ArrayList<>();

        if (expansion.getGithubScriptManager() != null) {
            commands.add(0, "git");
        }

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], commands, completion);
        }

        if (args[0].equalsIgnoreCase("git")) {
            if (expansion.getGithubScriptManager() == null) {
                return Collections.emptyList();
            }

            if (args.length == 2) {
                return StringUtil.copyPartialMatches(args[1], Arrays.asList("download", "enable", "info", "list", "refresh"), completion);
            }

            if (args.length == 3 && args[1].equalsIgnoreCase("download")) {
                if (expansion.getGithubScriptManager().getAvailableScripts() == null) {
                    return Collections.emptyList();
                }

                return StringUtil.copyPartialMatches(args[2], expansion.getGithubScriptManager().getAvailableScripts().stream().map(GithubScript::getName).collect(Collectors.toList()), completion);
            }
        }

        return Collections.emptyList();
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
                    "&eWiki: &fhttps://github.com/PlaceholderAPI/Javascript-Expansion/wiki",
                    "&r",
                    "&e/" + command + " reload &7- &fReload your javascripts without reloading PlaceholderAPI.",
                    "&e/" + command + " list &7- &fList loaded script identifiers.",
                    "&e/" + command + " parse [me/player] [code] &7- &fTest JavaScript code in chat."
            );

            if (expansion.getGithubScriptManager() != null) {
                msg(sender,
                        "&e/" + command + " git refresh &7- &fRefresh available Github scripts",
                        "&e/" + command + " git download [name] &7- &fDownload a script from the js expansion github.",
                        "&e/" + command + " git list &7- &fList available scripts in the js expansion github.",
                        "&e/" + command + " git info [name] &7- &fGet the description and url of a specific script."
                );
            }

            return true;
        }

        switch (args[0].toLowerCase()) {
            case "git": {
                if (expansion.getGithubScriptManager() == null) {
                    msg(sender, "&cThis feature is disabled in the PlaceholderAPI config.");
                    return true;
                }

                if (args.length < 2) {
                    msg(sender, "&cIncorrect usage! Type '&f/" + command + "&c' for more help.");
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
                            msg(sender, "&cIncorrect usage! &f/" + command + " git info [name]");
                            return true;
                        }

                        final GithubScript script = manager.getScript(args[2]);

                        if (script == null) {
                            msg(sender, "&cThe script &f" + args[2] + " &cdoes not exist!");
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
                            msg(sender, "&cIncorrect usage! &f/" + command + " git download [name]");
                            return true;
                        }

                        final GithubScript script = manager.getScript(args[2]);

                        if (script == null) {
                            msg(sender, "&cThe script &f" + args[2] + " &cdoes not exist!");
                            return true;
                        }

                        if (new File(expansion.getGithubScriptManager().getJavascriptsFolder(), script.getName() + ".js").exists()) {
                            msg(sender, "&cCould not download " + script.getName() + " because a file with the same name already exist in the javascripts folder.");
                            return true;
                        }

                        manager.downloadScript(script);
                        msg(sender, "&aDownload started. &eCheck the scripts folder in a moment...");
                        return true;
                    }

                    case "enabled":
                        if (args.length < 3) {
                            msg(sender, "&cIncorrect usage! &f/" + command + " git enabled [true/false]");
                            return true;
                        }

                        final boolean enabled = Boolean.parseBoolean(args[2]);
                        final PlaceholderAPIPlugin papi = expansion.getPlaceholderAPI();

                        papi.getConfig().set("expansions." + this.getName() + ".github_script_downloads", enabled);
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

                        msg(sender, "&6Git script downloads set to: &e" + enabled);
                        return true;

                    default: {
                        msg(sender, "&cIncorrect usage! Type '&f/" + command + "&c' for more help.");
                        return true;
                    }
                }
            }

            case "list": {
                final List<String> loaded = expansion.getLoadedIdentifiers();
                msg(sender,
                        loaded.size() + " &7script" + plural(loaded.size()) + " loaded.",
                        String.join(", ", loaded)
                );
                return true;
            }

            case "parse": {
                if (args.length < 3) {
                    msg(sender, "&cIncorrect usage! &f/" + command + " parse [me/player] [code]");
                    return true;
                }

                final String script = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                final JavascriptPlaceholder placeholder = new JavascriptPlaceholder(expansion.getGlobalEngine(), "parse-command", String.join(" ", script));

                if ("me".equalsIgnoreCase(args[1])) {
                    if (!(sender instanceof Player)) {
                        msg(sender, "&cOnly players can run this command!");
                        return true;
                    }

                    sender.sendMessage(placeholder.evaluate((Player) sender));
                    return true;
                }

                final OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

                if (!player.hasPlayedBefore() || player.getName() == null) {
                    msg(sender, "&cUnknown player " + args[1]);
                    return true;
                }

                sender.sendMessage(placeholder.evaluate(player));
                return true;
            }

            case "reload": {
                msg(sender, "&aJavascriptExpansion reloading...");
                final int scripts = expansion.reloadScripts();
                msg(sender, scripts + " &7script" + plural(scripts) + " loaded");
                return true;
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
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Arrays.stream(text).filter(Objects::nonNull).collect(Collectors.joining("\n"))));
    }
}
