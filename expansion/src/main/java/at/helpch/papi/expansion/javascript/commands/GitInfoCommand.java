package at.helpch.papi.expansion.javascript.commands;

import at.helpch.papi.expansion.javascript.ExpansionUtils;
import at.helpch.papi.expansion.javascript.cloud.GitScript;
import at.helpch.papi.expansion.javascript.cloud.GitScriptIndexProvider;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommand;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommandRouter;
import at.helpch.papi.expansion.javascript.commands.util.ColorUtil;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class GitInfoCommand extends ExpansionCommand {
    private final GitScriptIndexProvider indexProvider;

    public GitInfoCommand(final GitScriptIndexProvider indexProvider) {
        super(ExpansionCommandRouter.COMMAND_NAME + " git", "info");
        this.indexProvider = indexProvider;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ColorUtil.colorize("&cIncorrect usage! &f/" + getParentCommandName() + " git info [name]"));
            return;
        }

        final GitScript script = indexProvider.getScriptIndex().flatMap(index -> index.getScript(args[0])).orElse(null);

        if (script == null) {
            sender.sendMessage(ColorUtil.colorize("&cThe script &f" + args[1] + " &cdoes not exist!"));
            return;
        }

        sender.sendMessage(ColorUtil.colorize(
                "&eName: &f" + script.getName() + '\n' +
                "&eVersion: &f" + script.getVersion() + '\n' +
                "&eDescription: &f" + script.getDescription() + '\n' +
                "&eAuthor: &f" + script.getAuthor() + '\n' +
                "&eSource URL: &f" + script.getUrl()
        ));
    }

    @Override
    protected @NotNull String getCommandFormat() {
        return "info [name]";
    }

    @Override
    protected @NotNull String getDescription() {
        return "Fetches info about a git-script";
    }
}
