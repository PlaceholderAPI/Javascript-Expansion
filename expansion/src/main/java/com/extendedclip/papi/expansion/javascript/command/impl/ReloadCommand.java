package com.extendedclip.papi.expansion.javascript.command.impl;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import com.extendedclip.papi.expansion.javascript.command.ExpansionCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends ExpansionCommand {

    private final JavascriptExpansion expansion;

    public ReloadCommand(final String parentCommandName, final JavascriptExpansion expansion) {
        super(parentCommandName, "reload");
        this.expansion = expansion;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        ExpansionUtils.sendMsg(sender, "&aJavascriptExpansion reloading...");
        final int scripts = expansion.reloadScripts();
        ExpansionUtils.sendMsg(sender, scripts + " &7script" + ExpansionUtils.plural(scripts) + " loaded");
    }
}
