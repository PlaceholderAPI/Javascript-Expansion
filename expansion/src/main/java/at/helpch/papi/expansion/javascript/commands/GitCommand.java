package at.helpch.papi.expansion.javascript.commands;

import at.helpch.papi.expansion.javascript.ExpansionUtils;
import at.helpch.papi.expansion.javascript.cloud.ActiveStateSetter;
import at.helpch.papi.expansion.javascript.commands.router.CommandRouter;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommand;
import at.helpch.papi.expansion.javascript.commands.util.ColorUtil;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class GitCommand extends ExpansionCommand {
    private static final String ARG_REFRESH = "refresh";
    private static final String ARG_LIST = "list";
    private static final String ARG_INFO = "info";
    private static final String ARG_DOWNLOAD = "download";
    private static final String ARG_ENABLED = "enabled";

    private final ActiveStateSetter activeStateSetter;
    private final CommandRouter subCommandRouter;

    public GitCommand(final String parentCommandName, final ActiveStateSetter activeStateSetter, final CommandRouter subCommandRouter) {
        super(parentCommandName, "git");
        this.activeStateSetter = activeStateSetter;
        this.subCommandRouter = subCommandRouter;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ColorUtil.colorize("&cIncorrect usage! Type '&f/" + getParentCommandName() + "&c' for more help."));
            return;
        }
        if (!activeStateSetter.isActive() && !"enabled".equalsIgnoreCase(args[0])) {
            sender.sendMessage(ColorUtil.colorize("&cThis feature is disabled in the PlaceholderAPI config."));
            return;
        }

        subCommandRouter.execute(sender, args);
    }

    @Override
    @NotNull
    protected String getCommandFormat() {
        final String args = String.join("/", Arrays.asList(ARG_REFRESH, ARG_LIST, ARG_DOWNLOAD, ARG_ENABLED, ARG_INFO));
        return "git [" + args + "] [params]";
    }

    @Override
    @NotNull
    protected String getDescription() {
        return "Manage github scripts";
    }
}
