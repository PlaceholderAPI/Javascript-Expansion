package at.helpch.papi.expansion.javascript.commands;

import at.helpch.papi.expansion.javascript.ExpansionUtils;
import at.helpch.papi.expansion.javascript.cloud.GitScript;
import at.helpch.papi.expansion.javascript.cloud.GitScriptIndexProvider;
import at.helpch.papi.expansion.javascript.cloud.ScriptIndex;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommand;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommandRouter;
import at.helpch.papi.expansion.javascript.commands.util.ColorUtil;
import com.hypixel.hytale.server.core.command.system.CommandSender;
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

        sender.sendMessage(ColorUtil.colorize(availableScripts.size() + " &escript" + ExpansionUtils.plural(availableScripts.size()) + " available on Github.\n" + String.join(", ", scripts)));
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