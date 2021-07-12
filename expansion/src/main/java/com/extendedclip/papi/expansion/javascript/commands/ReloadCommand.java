package com.extendedclip.papi.expansion.javascript.commands;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommand;
import com.extendedclip.papi.expansion.javascript.script.ScriptLoader;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public final class ReloadCommand extends ExpansionCommand {

    private final ScriptLoader loader;

    public ReloadCommand(final String parentCommandName, final ScriptLoader loader) {
        super(parentCommandName, "reload");
        this.loader = loader;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {

        ExpansionUtils.sendMsg(sender, "&aJavascriptExpansion reloading...");
        try {
            final int scripts = loader.reload();
            ExpansionUtils.sendMsg(sender, scripts + " &7script" + ExpansionUtils.plural(scripts) + " loaded");
        } catch (final IOException exception) {
            ExpansionUtils.errorLog("&7Failed to reload scripts.", exception);
            ExpansionUtils.sendMsg(sender, "&7Failed to reload scripts.");
            exception.printStackTrace();
        }
    }

    @Override
    @NotNull
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return Collections.emptyList();
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
