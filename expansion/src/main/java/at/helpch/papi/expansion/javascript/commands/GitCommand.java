package at.helpch.papi.expansion.javascript.commands;

import at.helpch.papi.expansion.javascript.ExpansionUtils;
import at.helpch.papi.expansion.javascript.cloud.ActiveStateSetter;
import com.extendedclip.papi.expansion.javascript.cloud.*;
import at.helpch.papi.expansion.javascript.commands.router.CommandRouter;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
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
            ExpansionUtils.sendMsg(sender, "&cIncorrect usage! Type '&f/" + getParentCommandName() + "&c' for more help.");
            return;
        }
        if (!activeStateSetter.isActive() && !"enabled".equalsIgnoreCase(args[0])) {
            ExpansionUtils.sendMsg(sender, "&cThis feature is disabled in the PlaceholderAPI config.");
            return;
        }

        subCommandRouter.execute(sender, getParentCommandName() + " git", args);
    }

    @Override
    @NotNull
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList(ARG_REFRESH, ARG_LIST, ARG_DOWNLOAD, ARG_ENABLED, ARG_INFO), new ArrayList<>());
        } else if (args.length > 1) {
            return subCommandRouter.tabComplete(sender, args[0], args);
        }
        return Collections.emptyList();
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
