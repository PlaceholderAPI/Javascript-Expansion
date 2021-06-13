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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class GitInfoCommand extends ExpansionCommand {
    private final GitScriptIndexProvider indexProvider;

    public GitInfoCommand(final GitScriptIndexProvider indexProvider) {
        super(ExpansionCommandRouter.COMMAND_NAME + " git", "info");
        this.indexProvider = indexProvider;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length < 1) {
            ExpansionUtils.sendMsg(sender, "&cIncorrect usage! &f/" + getParentCommandName() + " git info [name]");
            return;
        }

        final GitScript script = indexProvider.getScriptIndex().flatMap(index -> index.getScript(args[0])).orElse(null);

        if (script == null) {
            ExpansionUtils.sendMsg(sender, "&cThe script &f" + args[1] + " &cdoes not exist!");
            return;
        }

        ExpansionUtils.sendMsg(sender,
                "&eName: &f" + script.getName(),
                "&eVersion: &f" + script.getVersion(),
                "&eDescription: &f" + script.getDescription(),
                "&eAuthor: &f" + script.getAuthor(),
                "&eSource URL: &f" + script.getUrl()
        );
    }

    @Override
    public @NotNull List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length > 0) {
            final List<String> scripts = indexProvider.getScriptIndex()
                    .map(ScriptIndex::getAllScripts)
                    .orElse(Collections.emptyList()).stream()
                    .map(GitScript::getName)
                    .collect(Collectors.toList());
            return StringUtil.copyPartialMatches(args[0], scripts, new ArrayList<>());
        }
        return Collections.emptyList();
    }

    @Override
    protected @NotNull String getCommandFormat() {
        return "git info [name]";
    }

    @Override
    protected @NotNull String getDescription() {
        return "Fetches info about a git-script";
    }
}
