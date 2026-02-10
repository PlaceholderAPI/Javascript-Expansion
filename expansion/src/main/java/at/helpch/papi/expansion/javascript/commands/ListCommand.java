package at.helpch.papi.expansion.javascript.commands;

import at.helpch.papi.expansion.javascript.ExpansionUtils;
import at.helpch.papi.expansion.javascript.JavascriptPlaceholder;
import at.helpch.papi.expansion.javascript.commands.util.ColorUtil;
import at.helpch.papi.expansion.javascript.script.ScriptRegistry;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommand;
import com.hypixel.hytale.server.core.command.system.CommandSender;
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
        sender.sendMessage(ColorUtil.colorize(loaded.size() + " &7script" + ExpansionUtils.plural(loaded.size()) + " loaded.\n" +
                String.join(", ", loaded)));
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
