package com.extendedclip.papi.expansion.javascript.commands;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import com.extendedclip.papi.expansion.javascript.JavascriptPlaceholder;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class DebugCommand extends ExpansionCommand {
    private static final String ARG_LOAD = "loaddata";
    private static final String ARG_SAVE = "savedata";
    private static final String NAME = "debug";

    private final JavascriptExpansion expansion;

    public DebugCommand(final String parentCommandName, final JavascriptExpansion expansion) {
        super(parentCommandName, NAME);
        this.expansion = expansion;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length < 2) {
            ExpansionUtils.sendMsg(sender, "&cIncorrect usage! Type '&f/" + getParentCommandName() + "&c' for more help.");
            return;
        }

        JavascriptPlaceholder jsp = expansion.getJSPlaceholder(getIdentifier(args));
        if (jsp == null) {
            ExpansionUtils.sendMsg(sender, "&cInvalid javascript identifier! Please re-check your typo");
            return;
        }

        if (args[0].equals(ARG_SAVE)) {
            jsp.saveData();
            ExpansionUtils.sendMsg(sender, "&aJavascript data '" + args[1] + "' successfully saved");
        } else if (args[0].equals(ARG_LOAD)) {
            jsp.loadData();
            ExpansionUtils.sendMsg(sender, "&aJavascript data '" + args[1] + "' successfully loaded");
        }
    }

    @Override
    public @NotNull List<String> tabComplete(final CommandSender sender, final String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList(ARG_SAVE, ARG_LOAD), new ArrayList<>());
        }
        return Collections.emptyList();
    }

    @Override
    protected @NotNull String getCommandFormat() {
        return "debug [savedata/loaddata] [identifier]";
    }

    @Override
    protected @NotNull String getDescription() {
        return "Test JavaScript code in chat";
    }

    public String getIdentifier(final String[] args) {
        return Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
    }
}
