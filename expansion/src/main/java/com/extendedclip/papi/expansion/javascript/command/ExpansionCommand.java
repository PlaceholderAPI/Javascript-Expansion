package com.extendedclip.papi.expansion.javascript.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class ExpansionCommand {

    private final String parentCommandName;
    public final String name;

    public ExpansionCommand(final String parentCommandName, final String name) {
        this.parentCommandName = parentCommandName;
        this.name = name;
    }

    public abstract void execute(final CommandSender sender, final String[] args);

    public final String getParentCommandName() {
        return parentCommandName;
    }

    public final String getName() {
        return name;
    }

}
