package com.extendedclip.papi.expansion.javascript.commands.router;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class CommandRouter extends Command {
    private final Map<String, ExpansionCommand> subCommandMap;

    protected CommandRouter(
            @NotNull final String name,
            @NotNull final String description,
            @NotNull final String usageMessage,
            @NotNull final List<String> aliases,
            @Nullable final String permission,
            @NotNull final Collection<ExpansionCommand> commands
    ) {
        super(name, description, usageMessage, aliases);
        setPermission(permission);
        this.subCommandMap = new HashMap<>();

        for (final ExpansionCommand command : commands) {
            subCommandMap.put(command.getName().toLowerCase(), command);
        }
    }

    public abstract List<String> getHelpHeader();

    public abstract String getSubCommandHelpFormat();

    public abstract String getInvalidCommandMessage();

    @Override
    public final boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        final String perm = getPermission();
        if (perm != null && !sender.hasPermission(perm)) {
            sender.sendMessage(CommandRouter.translateColors("&cYou don't have permission to do that!"));
            return true;
        }
        if (args.length == 0) {
            final String format = getSubCommandHelpFormat();
            final List<String> header = getHelpHeader();
            final List<String> subCommandHelp = subCommandMap
                    .values()
                    .stream()
                    .map(cmd ->
                            String.format(
                                    format,
                                    cmd.getParentCommandName(),
                                    cmd.getCommandFormat(),
                                    cmd.getDescription()
                            )
                    )
                    .collect(Collectors.toUnmodifiableList());
            header.stream().map(CommandRouter::translateColors).forEach(sender::sendMessage);
            subCommandHelp.stream().map(CommandRouter::translateColors).forEach(sender::sendMessage);
            return true;
        }

        final String subCommand = args[0].toLowerCase();
        final ExpansionCommand matchedCommand = subCommandMap.get(subCommand);
        if (matchedCommand == null) {
            final String invalidMatchMessage = getInvalidCommandMessage();
            sender.sendMessage(translateColors(String.format(invalidMatchMessage, getName())));
            return true;
        }
        final String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, args.length - 1);
        matchedCommand.execute(sender, subArgs);
        return true;
    }

    @Override
    @NotNull
    public final List<String> tabComplete(@NotNull final CommandSender sender, @NotNull final String alias, @NotNull final String[] args) throws IllegalArgumentException {
        final int length = args.length;
        if (length == 1) { // User requires tab completion for subcommand names
            final String partialString = args[length - 1];
            return StringUtil.copyPartialMatches(
                    partialString, subCommandMap.keySet(),
                    new ArrayList<>()
            );
        } else if (length > 1) { // User requires per-command tab completion
            final String selectedCommandName = args[0];
            final ExpansionCommand command = subCommandMap.get(selectedCommandName);
            if (command == null) {
                return Collections.emptyList();
            }
            return command.tabComplete(sender, args);
        }
        return super.tabComplete(sender, alias, args);
    }

    public static String translateColors(final String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
