package com.extendedclip.papi.expansion.javascript.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class ICommand {

    public String command;

    public abstract void execute(CommandSender sender, String[] args);

    public abstract @NotNull String getAlias();

}
