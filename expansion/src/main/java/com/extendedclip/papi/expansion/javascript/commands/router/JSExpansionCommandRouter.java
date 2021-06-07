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
package com.extendedclip.papi.expansion.javascript.commands.router;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import com.extendedclip.papi.expansion.javascript.cloud.GithubScript;
import com.extendedclip.papi.expansion.javascript.commands.*;
import com.extendedclip.papi.expansion.javascript.evaluator.ScriptEvaluatorFactory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class JSExpansionCommandRouter extends Command {
    public static final String COMMAND_NAME = "jsexpansion";
    private static final String PERMISSION = "placeholderapi.js.admin";
    private static final String DESCRIPTION = "JavaScript Expansion Commands";
    private static final List<String> ALIASES = Arrays.asList("javascriptexpansion", "jsexp");

    private final JavascriptExpansion expansion;
    private final ScriptEvaluatorFactory evaluatorFactory;
    private List<ExpansionCommand> subCommands;

    public JSExpansionCommandRouter(JavascriptExpansion expansion, ScriptEvaluatorFactory evaluatorFactory) {
        super(COMMAND_NAME);
        this.evaluatorFactory = evaluatorFactory;
        this.expansion = expansion;
        this.setDescription(DESCRIPTION);
        this.setUsage(String.format("/%s <args>", COMMAND_NAME));
        this.setAliases(ALIASES);
        this.setPermission(PERMISSION);
        initCommands();
    }

    public void initCommands() {
        if (subCommands != null) {
            subCommands.clear();
        }
        subCommands = new ArrayList<>(Arrays.asList(
                new GitCommand("jsexpansion", expansion),
                new ListCommand("jsexpansion", expansion),
                new ParseCommand("jsexpansion", evaluatorFactory),
                new ReloadCommand("jsexpansion", expansion),
                new DebugCommand("jsexpansion", expansion))
        );
    }

    @Override
    public boolean execute(CommandSender sender, @NotNull String label, String[] args) {

        if (!sender.hasPermission(PERMISSION)) {
            ExpansionUtils.sendMsg(sender, "&cYou don't have permission to do that!");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        ExpansionCommand command = null;
        for (ExpansionCommand icmd : subCommands) {
            if (icmd.getName().equalsIgnoreCase(args[0])) {
                command = icmd;
                break;
            }
        }

        if (command == null) {
            ExpansionUtils.sendMsg(sender, "&cInvalid expansion sub-command! Type&f /" + getName() + " &cfor help");
            return true;
        }

        command.execute(sender, sliceFirstArr(args));

        return true;
    }

    //TODO: This thing here has to be organized thoroughly later...
    @Override
    public List<String> tabComplete(CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
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

    private void sendHelp(CommandSender sender) {ExpansionUtils.sendMsg(sender,
                "&eJavascript expansion &7v: &f" + expansion.getVersion(),
                "&eCreated by: &f" + expansion.getAuthor(),
                "&eWiki: &fhttps://github.com/PlaceholderAPI/Javascript-Expansion/wiki",
                "&r",
                "&e/" + COMMAND_NAME + " reload &7- &fReload your javascripts without reloading PlaceholderAPI.",
                "&e/" + COMMAND_NAME + " list &7- &fList loaded script identifiers.",
                "&e/" + COMMAND_NAME + " parse [me/player] [code] &7- &fTest JavaScript code in chat.",
                "&e/" + COMMAND_NAME + " debug [savedata/loaddata] [identifier] &7- &fTest JavaScript code in chat."
        );

        if (expansion.getGithubScriptManager() != null) {
            ExpansionUtils.sendMsg(sender,
                    "&e/" + COMMAND_NAME + " gi\n" +
                            "        t refresh &7- &fRefresh available Github scripts",
                    "&e/" + COMMAND_NAME + " git download [name] &7- &fDownload a script from the js expansion github.",
                    "&e/" + COMMAND_NAME + " git list &7- &fList available scripts in the js expansion github.",
                    "&e/" + COMMAND_NAME + " git info [name] &7- &fGet the description and url of a specific script."
            );
        } else {
            ExpansionUtils.sendMsg(sender,
                    "&e/" + COMMAND_NAME + " git &7- &fGithub command &7(please enable in config)"
            );
        }
    }

    public String[] sliceFirstArr(String[] args) {
        return Arrays.stream(args).skip(1).toArray(String[]::new);
    }

}
