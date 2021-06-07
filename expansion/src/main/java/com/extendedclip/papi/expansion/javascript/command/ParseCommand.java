package com.extendedclip.papi.expansion.javascript.command;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import com.extendedclip.papi.expansion.javascript.JavascriptPlaceholder;
import com.extendedclip.papi.expansion.javascript.evaluator.ScriptEvaluatorFactory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ParseCommand extends ExpansionCommand {

    private final JavascriptExpansion expansion;
    private final ScriptEvaluatorFactory evaluatorFactory;

    public ParseCommand(JavascriptExpansion expansion, final ScriptEvaluatorFactory evaluatorFactory) {
        this.expansion = expansion;
        this.evaluatorFactory = evaluatorFactory;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            ExpansionUtils.sendMsg(sender, "&cIncorrect usage! &f/" + command + " parse [me/player] [code]");
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
    public String getAlias() {
        return "parse";
    }
}
