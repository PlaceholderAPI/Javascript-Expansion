package com.extendedclip.papi.expansion.javascript.commands;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import com.extendedclip.papi.expansion.javascript.JavascriptPlaceholder;
import com.extendedclip.papi.expansion.javascript.ScriptRegistry;
import com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ListCommand extends ExpansionCommand {

    private final ScriptRegistry registry;

    public ListCommand(final String parentCommandName, final ScriptRegistry registry) {
        super(parentCommandName, "list");
        this.registry = registry;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {

        final List<String> loaded = registry.getAllPlaceholders().stream().map(JavascriptPlaceholder::getIdentifier).collect(Collectors.toList());
        ExpansionUtils.sendMsg(sender,loaded.size() + " &7script" + ExpansionUtils.plural(loaded.size()) + " loaded.",
                String.join(", ", loaded));
    }

    @Override
    @NotNull
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return Collections.emptyList();
    }

    @Override
    @NotNull
    protected String getCommandFormat() {
        return "list";
    }

    @Override
    @NotNull
    protected String getDescription() {
        return "List loaded script identifiers";
    }

}
