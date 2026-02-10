package at.helpch.papi.expansion.javascript.commands;

import at.helpch.papi.expansion.javascript.JavascriptPlaceholder;
import at.helpch.papi.expansion.javascript.commands.util.ColorUtil;
import at.helpch.papi.expansion.javascript.script.ScriptRegistry;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommand;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class DebugCommand extends ExpansionCommand {
    private static final String ARG_LOAD = "loaddata";
    private static final String ARG_SAVE = "savedata";
    private static final String NAME = "debug";

    private final ScriptRegistry registry;

    public DebugCommand(final String parentCommandName, final ScriptRegistry registry) {
        super(parentCommandName, NAME);
        this.registry = registry;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorUtil.colorize("&cIncorrect usage! Type '&f/" + getParentCommandName() + "&c' for more help."));
            return;
        }

        JavascriptPlaceholder jsp = registry.getPlaceholder(getIdentifier(args));
        if (jsp == null) {
            sender.sendMessage(ColorUtil.colorize("&cInvalid javascript identifier! Please re-check your typo"));
            return;
        }

        if (args[0].equals(ARG_SAVE)) {
            jsp.saveData();
            sender.sendMessage(ColorUtil.colorize("&aJavascript data '" + args[1] + "' successfully saved"));
        } else if (args[0].equals(ARG_LOAD)) {
            jsp.getPersistableData().reload();
            sender.sendMessage(ColorUtil.colorize("&aJavascript data '" + args[1] + "' successfully loaded"));
        }
    }

    @Override
    protected @NotNull String getCommandFormat() {
        return "debug [savedata/loaddata] [identifier]";
    }

    @Override
    protected @NotNull String getDescription() {
        return "Test JavaScript code in chat";
    }

    public String getIdentifier(final String[] args) {
        return Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
    }
}
