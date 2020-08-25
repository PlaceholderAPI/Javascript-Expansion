package com.extendedclip.papi.expansion.javascript.command;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends ICommand {

    private final JavascriptExpansion expansion;

    public ReloadCommand(JavascriptExpansion expansion) {
        this.expansion = expansion;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        ExpansionUtils.sendMsg(sender, "&aJavascriptExpansion reloading...");
        final int scripts = expansion.reloadScripts();
        ExpansionUtils.sendMsg(sender, scripts + " &7script" + ExpansionUtils.plural(scripts) + " loaded");
    }

    @Override
    @NotNull
    public String getAlias() {
        return "reload";
    }
}
