package com.extendedclip.papi.expansion.javascript.command;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListCommand extends ICommand {

    private final JavascriptExpansion expansion;

    public ListCommand(JavascriptExpansion expansion) {
        this.expansion = expansion;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        final List<String> loaded = expansion.getLoadedIdentifiers();
        ExpansionUtils.sendMsg(sender,loaded.size() + " &7script" + ExpansionUtils.plural(loaded.size()) + " loaded.",
                String.join(", ", loaded));
    }

    @Override
    @NotNull
    public String getAlias() {
        return "list";
    }
}
