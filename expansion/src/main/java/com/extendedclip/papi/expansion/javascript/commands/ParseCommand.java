package com.extendedclip.papi.expansion.javascript.commands;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptPlaceholder;
import com.extendedclip.papi.expansion.javascript.JavascriptPlaceholderFactory;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommand;
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

    private final JavascriptPlaceholderFactory placeholderFactory;

    public ParseCommand(final String parentCommand, final JavascriptPlaceholderFactory placeholderFactory) {
        super(parentCommand, "parse");
        this.placeholderFactory = placeholderFactory;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length < 3) {
            ExpansionUtils.sendMsg(sender, "&cIncorrect usage! &f/" + getParentCommandName() + " parse [me/player] [code]");
            return;
        }
        final OfflinePlayer player;

        if ("me".equalsIgnoreCase(args[0])) {
            if (!(sender instanceof Player)) {
                ExpansionUtils.sendMsg(sender, "&cOnly players can run this command!");
                return;
            }

            player = (OfflinePlayer) sender;
        } else {
            player = Bukkit.getOfflinePlayer(args[0]);
        }

        final String script = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        final JavascriptPlaceholder placeholder = placeholderFactory.create( "parse-command", String.join(" ", script));


        if (!player.hasPlayedBefore() || player.getName() == null) {
            ExpansionUtils.sendMsg(sender, "&cUnknown player " + args[0]);
            return;
        }

        sender.sendMessage(placeholder.evaluate(player));
    }

    @Override
    @NotNull
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
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
