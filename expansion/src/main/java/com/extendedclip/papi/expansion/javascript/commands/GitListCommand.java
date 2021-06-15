package com.extendedclip.papi.expansion.javascript.commands;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.cloud.GitScript;
import com.extendedclip.papi.expansion.javascript.cloud.GitScriptIndexProvider;
import com.extendedclip.papi.expansion.javascript.cloud.ScriptIndex;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommand;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommandRouter;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public final class GitListCommand extends ExpansionCommand {
    private final GitScriptIndexProvider indexProvider;

    public GitListCommand(final GitScriptIndexProvider indexProvider) {
        super(ExpansionCommandRouter.COMMAND_NAME + " git", "list");
        this.indexProvider = indexProvider;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final Collection<GitScript> availableScripts = indexProvider.getScriptIndex().map(ScriptIndex::getAllScripts).orElse(Collections.emptyList());
        final Set<String> scripts = availableScripts.stream().map(GitScript::getName).collect(Collectors.toSet());

        ExpansionUtils.sendMsg(sender, availableScripts.size() + " &escript" + ExpansionUtils.plural(availableScripts.size()) + " available on Github.", String.join(", ", scripts));
    }

    @Override
    public @NotNull List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    protected @NotNull String getCommandFormat() {
        return "list";
    }

    @Override
    protected @NotNull String getDescription() {
        return "Lists loaded git-scripts";
    }
}