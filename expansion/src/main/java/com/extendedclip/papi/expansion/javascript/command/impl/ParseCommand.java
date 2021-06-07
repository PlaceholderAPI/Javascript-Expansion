package com.extendedclip.papi.expansion.javascript.command.impl;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptPlaceholder;
import com.extendedclip.papi.expansion.javascript.command.ExpansionCommand;
import com.extendedclip.papi.expansion.javascript.evaluator.ScriptEvaluatorFactory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ParseCommand extends ExpansionCommand {
    private static final String ARG_ME = "me";
    private static final String ARG_PLAYER = "player";

    private final ScriptEvaluatorFactory evaluatorFactory;

    public ParseCommand(final String parentCommand, final ScriptEvaluatorFactory evaluatorFactory) {
        super(parentCommand, "parse");
        this.evaluatorFactory = evaluatorFactory;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            ExpansionUtils.sendMsg(sender, "&cIncorrect usage! &f/" + getParentCommandName() + " parse [me/player] [code]");
            return;
        }

        final String script = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        final JavascriptPlaceholder placeholder = new JavascriptPlaceholder( "parse-command", String.join(" ", script),  evaluatorFactory);

        if ("me".equalsIgnoreCase(args[0])) {
            if (!(sender instanceof Player)) {
                ExpansionUtils.sendMsg(sender, "&cOnly players can run this command!");
                return;
            }

            sender.sendMessage(placeholder.evaluate((Player) sender));
            return;
        }

        final OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

        if (!player.hasPlayedBefore() || player.getName() == null) {
            ExpansionUtils.sendMsg(sender, "&cUnknown player " + args[1]);
            return;
        }

        sender.sendMessage(placeholder.evaluate(player));
    }

    @Override
    @NotNull
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList(ARG_ME, ARG_PLAYER), new ArrayList<>());
        }
        return Collections.emptyList();
    }

    @Override
    @NotNull
    protected String getCommandFormat() {
        return "parse [me/player] [code]";
    }

    @Override
    @NotNull
    protected String getDescription() {
        return "Test JavaScript code in chat";
    }

}
