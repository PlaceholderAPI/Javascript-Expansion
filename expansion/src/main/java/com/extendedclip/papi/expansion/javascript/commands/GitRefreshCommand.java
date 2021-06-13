package com.extendedclip.papi.expansion.javascript.commands;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.cloud.GitScript;
import com.extendedclip.papi.expansion.javascript.cloud.GitScriptIndexProvider;
import com.extendedclip.papi.expansion.javascript.cloud.ScriptIndex;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommand;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommandRouter;
import org.bukkit.command.CommandSender;
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
        ExpansionUtils.sendMsg(sender, "&aFetching available scripts... Check back in a sec!");
        indexProvider.refreshIndex(index -> {
            ExpansionUtils.sendMsg(sender, "&aFetched " + index.getCount() + " scripts to index!");
        });
    }

    @Override
    public @NotNull List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    protected @NotNull String getCommandFormat() {
        return "git refresh";
    }

    @Override
    protected @NotNull String getDescription() {
        return "Re-indexes git-scripts from master list";
    }

}
