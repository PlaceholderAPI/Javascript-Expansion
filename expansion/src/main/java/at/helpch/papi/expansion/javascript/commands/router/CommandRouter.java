package at.helpch.papi.expansion.javascript.commands.router;

import at.helpch.papi.expansion.javascript.commands.util.ColorUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class CommandRouter extends AbstractCommand {
    private final Map<String, ExpansionCommand> subCommandMap;
    private final Set<String> permissions = new HashSet<>();

    protected CommandRouter(
            @NotNull final String name,
            @NotNull final String description,
//            @NotNull final String usageMessage,
            @NotNull final List<String> aliases,
            @NotNull final Map<String, ExpansionCommand> commandMap,
            @NotNull final String @NotNull ... permissions
    ) {
        super(name, description);
        addAliases(aliases.toArray(new String[0]));
        setAllowsExtraArguments(true);
//        setAllowsExtraArguments(true);
        this.permissions.addAll(Arrays.asList(permissions));
        this.subCommandMap = commandMap;
    }

    public abstract List<String> getHelpHeader();

    public abstract String getSubCommandHelpFormat();

    public abstract String getInvalidCommandMessage();

    public void execute(@NotNull final CommandSender sender, @NotNull final String[] args) {
        if (!permissions.isEmpty() && permissions.stream().noneMatch(sender::hasPermission)) {
            sender.sendMessage(ColorUtil.colorize("&cYou don't have permission to do that!"));
        }
        if (args.length == 0 || args[0].isEmpty()) {
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
                    .toList();
            header.stream().map(ColorUtil::colorize).forEach(sender::sendMessage);
            subCommandHelp.stream().map(ColorUtil::colorize).forEach(sender::sendMessage);
            return;
        }

        final String subCommand = args[0].toLowerCase();
        final ExpansionCommand matchedCommand = subCommandMap.get(subCommand);

        if (matchedCommand == null) {
            final String invalidMatchMessage = getInvalidCommandMessage();
            sender.sendMessage(ColorUtil.colorize(String.format(invalidMatchMessage, getName())));
            return;
        }

        final String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, args.length - 1);
        matchedCommand.execute(sender, subArgs);
    }

    @Override
    protected @Nullable CompletableFuture<Void> execute(@NotNull CommandContext commandContext) {
        final CommandSender sender = commandContext.sender();
        String input = commandContext.getInputString().replace("jsexpansion", "");

        for (String alias : getAliases()) {
            input = input.replace(alias, "");
        }

        final String[] args = input.trim().split(" ");

        execute(sender, args);
        return CompletableFuture.completedFuture(null);
    }
}
