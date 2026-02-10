package at.helpch.papi.expansion.javascript.commands;

import at.helpch.papi.expansion.javascript.ExpansionUtils;
import at.helpch.papi.expansion.javascript.JavascriptExpansion;
import at.helpch.papi.expansion.javascript.JavascriptPlaceholder;
import at.helpch.papi.expansion.javascript.JavascriptPlaceholderFactory;
import at.helpch.papi.expansion.javascript.commands.router.ExpansionCommand;
import at.helpch.papi.expansion.javascript.commands.util.ColorUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class ParseCommand extends ExpansionCommand {
    private static final String ARG_ME = "me";
    private static final String ARG_PLAYER = "player";

    private final JavascriptPlaceholderFactory placeholderFactory;

    private final JavascriptExpansion expansion;

    public ParseCommand(final String parentCommand, final JavascriptPlaceholderFactory placeholderFactory, JavascriptExpansion expansion) {
        super(parentCommand, "parse");
        this.placeholderFactory = placeholderFactory;
        this.expansion = expansion;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (!expansion.getExpansionConfig(JavascriptExpansion.class).enableParseCommand()) {
            sender.sendMessage(ColorUtil.colorize("&cThis command is disabled in config."));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(ColorUtil.colorize("&cIncorrect usage! &f/" + getParentCommandName() + " parse [me/player] [code]"));
            return;
        }

        final PlayerRef player;

        if ("me".equalsIgnoreCase(args[0])) {
            if (!(sender instanceof Player) && !(sender instanceof PlayerRef)) {
                sender.sendMessage(ColorUtil.colorize("&cOnly players can run this command!"));
                return;
            }

            player = (sender instanceof Player) ? ((Player) sender).getPlayerRef() : (PlayerRef) sender;
        } else {
            player = Universe.get().getPlayerByUsername(args[0], NameMatching.EXACT);
        }

        final String script = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        final JavascriptPlaceholder placeholder = placeholderFactory.create( "parse-command", String.join(" ", script));

        if (player == null) {
            sender.sendMessage(ColorUtil.colorize("&cUnknown player " + args[0]));
            return;
        }

        sender.sendMessage(Message.raw(String.valueOf(placeholder.evaluate(player))));
    }

    @Override
    @NotNull
    protected String getCommandFormat() {
        return "parse [me/player] [code]";
    }

    @Override
    @NotNull
    protected String getDescription() {
        return "Test JavaScript code in chat";
    }

}
