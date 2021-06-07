package com.extendedclip.papi.expansion.javascript.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

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
    protected abstract String getCommandFormat();

    @NotNull
    protected abstract String getDescription();

    @NotNull
    protected final String getParentCommandName() {
        return parentCommandName;
    }

    @NotNull
    protected final String getName() {
        return name;
    }
}
