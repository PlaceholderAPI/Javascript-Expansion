package com.extendedclip.papi.expansion.javascript.command.impl;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import com.extendedclip.papi.expansion.javascript.command.ExpansionCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListCommand extends ExpansionCommand {

    private final JavascriptExpansion expansion;

    public ListCommand(final String parentCommandName, final JavascriptExpansion expansion) {
        super(parentCommandName, "list");
        this.expansion = expansion;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        final List<String> loaded = expansion.getLoadedIdentifiers();
        ExpansionUtils.sendMsg(sender,loaded.size() + " &7script" + ExpansionUtils.plural(loaded.size()) + " loaded.",
                String.join(", ", loaded));
    }

}
