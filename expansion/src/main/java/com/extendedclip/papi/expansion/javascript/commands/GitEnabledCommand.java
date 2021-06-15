package com.extendedclip.papi.expansion.javascript.commands;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.cloud.*;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommand;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommandRouter;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class GitEnabledCommand extends ExpansionCommand {
    private static final List<String> boolCompletion = Arrays.asList("true", "false");
    private final ActiveStateSetter activeStateSetter;

    public GitEnabledCommand(final ActiveStateSetter activeStateSetter) {
        super(ExpansionCommandRouter.COMMAND_NAME + " git", "enabled");
        this.activeStateSetter = activeStateSetter;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length < 1) {
            ExpansionUtils.sendMsg(sender, "&cIncorrect usage! &f/jsexpansion git enabled (true/false)");
            return;
        }

        final boolean enabled = Boolean.parseBoolean(args[0]);
        activeStateSetter.setActive(enabled);

        ExpansionUtils.sendMsg(sender, "&6Git script downloads set to: &e" + enabled);
    }

    @Override
    public @NotNull List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length > 0) {
            return StringUtil.copyPartialMatches(args[0], boolCompletion, new ArrayList<>());
        }
        return Collections.emptyList();
    }

    @Override
    protected @NotNull String getCommandFormat() {
        return "enabled (true/false)";
    }

    @Override
    protected @NotNull String getDescription() {
        return "Enables/Disables usage of git-script management";
    }
}