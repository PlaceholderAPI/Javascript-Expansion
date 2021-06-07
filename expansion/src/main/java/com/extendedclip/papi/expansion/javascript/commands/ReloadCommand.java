package com.extendedclip.papi.expansion.javascript.commands;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class ReloadCommand extends ExpansionCommand {

    private final JavascriptExpansion expansion;

    public ReloadCommand(final String parentCommandName, final JavascriptExpansion expansion) {
        super(parentCommandName, "reload");
        this.expansion = expansion;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {

        ExpansionUtils.sendMsg(sender, "&aJavascriptExpansion reloading...");
        final int scripts = expansion.reloadScripts();
        ExpansionUtils.sendMsg(sender, scripts + " &7script" + ExpansionUtils.plural(scripts) + " loaded");
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
