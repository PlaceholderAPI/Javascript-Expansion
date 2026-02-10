package at.helpch.papi.expansion.javascript.commands;

import at.helpch.papi.expansion.javascript.ExpansionUtils;
import at.helpch.papi.expansion.javascript.cloud.GitScriptIndexProvider;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommand;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommandRouter;
import at.helpch.papi.expansion.javascript.commands.util.ColorUtil;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class GitRefreshCommand extends ExpansionCommand {
    private final GitScriptIndexProvider indexProvider;

    public GitRefreshCommand(final GitScriptIndexProvider indexProvider) {
        super(ExpansionCommandRouter.COMMAND_NAME + " git", "refresh");
        this.indexProvider = indexProvider;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        sender.sendMessage(ColorUtil.colorize("&aFetching available scripts... Check back in a sec!"));
        indexProvider.refreshIndex(index -> {
            sender.sendMessage(ColorUtil.colorize("&aFetched " + index.getCount() + " scripts to index!"));
        });
    }

    @Override
    protected @NotNull String getCommandFormat() {
        return "refresh";
    }

    @Override
    protected @NotNull String getDescription() {
        return "Re-indexes git-scripts from master list";
    }

}
