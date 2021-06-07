package com.extendedclip.papi.expansion.javascript.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public abstract class ExpansionCommand {

    private final String parentCommandName;
    public final String name;

    public ExpansionCommand(@NotNull final String parentCommandName, @NotNull final String name) {
        this.parentCommandName = parentCommandName;
        this.name = name;
    }

    public abstract void execute(final CommandSender sender, final String[] args);

    @NotNull
    public abstract List<String> tabComplete(final CommandSender sender, final String[] args);

    @NotNull
    public final String getParentCommandName() {
        return parentCommandName;
    }

    @NotNull
    public final String getName() {
        return name;
    }

}
