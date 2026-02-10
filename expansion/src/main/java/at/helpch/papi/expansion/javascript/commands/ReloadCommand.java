package at.helpch.papi.expansion.javascript.commands;

import at.helpch.papi.expansion.javascript.ExpansionUtils;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommand;
import at.helpch.papi.expansion.javascript.commands.util.ColorUtil;
import at.helpch.papi.expansion.javascript.script.ScriptLoader;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class ReloadCommand extends ExpansionCommand {

    private final ScriptLoader loader;

    public ReloadCommand(final String parentCommandName, final ScriptLoader loader) {
        super(parentCommandName, "reload");
        this.loader = loader;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {

        sender.sendMessage(ColorUtil.colorize("&aJavascriptExpansion reloading..."));
        try {
            final int scripts = loader.reload();
            sender.sendMessage(ColorUtil.colorize(scripts + " &7script" + ExpansionUtils.plural(scripts) + " loaded"));
        } catch (final IOException exception) {
            ExpansionUtils.errorLog("&7Failed to reload scripts.", exception);
            sender.sendMessage(ColorUtil.colorize("&7Failed to reload scripts."));
            exception.printStackTrace();
        }
    }

    @Override
    @NotNull
    protected String getCommandFormat() {
        return "reload";
    }

    @Override
    @NotNull
    protected String getDescription() {
        return "Reload your javascripts without reloading PlaceholderAPI";
    }
}
