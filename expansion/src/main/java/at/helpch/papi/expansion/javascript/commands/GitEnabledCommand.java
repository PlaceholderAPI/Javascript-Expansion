package at.helpch.papi.expansion.javascript.commands;

import at.helpch.papi.expansion.javascript.ExpansionUtils;
import at.helpch.papi.expansion.javascript.cloud.ActiveStateSetter;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommand;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommandRouter;
import at.helpch.papi.expansion.javascript.commands.util.ColorUtil;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            sender.sendMessage(ColorUtil.colorize("&cIncorrect usage! &f/jsexpansion git enabled (true/false)"));
            return;
        }

        final boolean enabled = Boolean.parseBoolean(args[0]);
        activeStateSetter.setActive(enabled);

        sender.sendMessage(ColorUtil.colorize("&6Git script downloads set to: &e" + enabled));
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