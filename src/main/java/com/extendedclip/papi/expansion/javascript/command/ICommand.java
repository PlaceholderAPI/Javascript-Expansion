package com.extendedclip.papi.expansion.javascript.command;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class ICommand {

    public String command;

    public abstract void execute(CommandSender sender, String[] args);

    public abstract @NotNull String getAlias();

}
