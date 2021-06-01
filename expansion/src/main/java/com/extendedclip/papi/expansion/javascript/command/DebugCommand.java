package com.extendedclip.papi.expansion.javascript.command;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import com.extendedclip.papi.expansion.javascript.JavascriptPlaceholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DebugCommand extends ICommand {

    private final JavascriptExpansion expansion;

    public DebugCommand(JavascriptExpansion expansion) {
        this.expansion = expansion;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            ExpansionUtils.sendMsg(sender, "&cIncorrect usage! Type '&f/" + command + "&c' for more help.");
            return;
        }

        JavascriptPlaceholder jsp = expansion.getJSPlaceholder(getIdentifier(args));
        if (jsp == null) {
            ExpansionUtils.sendMsg(sender, "&cInvalid com.extendedclip.papi.expansion.javascript identifier! Please re-check your typo");
            return;
        }

        if (args[0].equals("savedata")) {
            jsp.saveData();
            ExpansionUtils.sendMsg(sender, "&aJavascript data '" + args[1] + "' successfully saved");
        } else if (args[0].equals("loaddata")) {
            jsp.loadData();
            ExpansionUtils.sendMsg(sender, "&aJavascript data '" + args[1] + "' successfully loaded");
        }
    }

    public String getIdentifier(String[] args) {
        return Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
    }

    @Override
    public @NotNull String getAlias() {
        return "debug";
    }
}
