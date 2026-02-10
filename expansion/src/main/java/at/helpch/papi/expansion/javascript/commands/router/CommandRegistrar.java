package at.helpch.papi.expansion.javascript.commands.router;

import at.helpch.papi.expansion.javascript.JavascriptExpansion;
import at.helpch.papi.expansion.javascript.JavascriptPlaceholderFactory;
import at.helpch.papi.expansion.javascript.commands.*;
import at.helpch.papi.expansion.javascript.config.ConfigManager;
import at.helpch.papi.expansion.javascript.script.ScriptLoader;
import at.helpch.papi.expansion.javascript.script.ScriptRegistry;
import at.helpch.papi.expansion.javascript.cloud.GitScriptManager;
import at.helpch.placeholderapi.PlaceholderAPIPlugin;
import at.helpch.papi.expansion.javascript.config.ScriptConfiguration;
import com.hypixel.hytale.server.core.command.system.CommandRegistration;

import java.lang.reflect.Field;
import java.util.Map;

import static at.helpch.papi.expansion.javascript.commands.router.ExpansionCommandRouter.COMMAND_NAME;

public final class CommandRegistrar {
    private static final String WIKI_LINK = "https://github.com/PlaceholderAPI/Javascript-Expansion/wiki";
    private final CommandRouter router;
    private CommandRegistration hytaleCommand;

    public CommandRegistrar(final GitScriptManager gitScriptManager, final JavascriptPlaceholderFactory placeholderFactory,
                            final ScriptConfiguration configuration, final ScriptRegistry registry,
                            final ScriptLoader loader, JavascriptExpansion expansion,
                            final ConfigManager configManager) {
        final GitRefreshCommand gitRefreshCommand = new GitRefreshCommand(gitScriptManager.getIndexProvider());
        final GitListCommand gitListCommand = new GitListCommand(gitScriptManager.getIndexProvider());
        final GitDownloadCommand gitDownloadCommand = new GitDownloadCommand(gitScriptManager, configuration, configManager);
        final GitInfoCommand gitInfoCommand = new GitInfoCommand(gitScriptManager.getIndexProvider());
        final GitEnabledCommand gitEnabledCommand = new GitEnabledCommand(gitScriptManager.getActiveStateSetter());

        final Map<String, ExpansionCommand> gitCommandMap = Map.of(
                "refresh", gitRefreshCommand,
                "list", gitListCommand,
                "download", gitDownloadCommand,
                "info", gitInfoCommand,
                "enabled", gitEnabledCommand
        );

        final CommandRouter gitCommandRouter = new ExpansionCommandRouter(JavascriptExpansion.VERSION, JavascriptExpansion.AUTHOR, WIKI_LINK, gitCommandMap);

        final GitCommand gitCommand = new GitCommand(COMMAND_NAME, gitScriptManager.getActiveStateSetter(), gitCommandRouter);
        final ListCommand listCommand = new ListCommand(COMMAND_NAME, registry);
        final DebugCommand debugCommand = new DebugCommand(COMMAND_NAME, registry);
        final ParseCommand parseCommand = new ParseCommand(COMMAND_NAME, placeholderFactory, expansion);
        final ReloadCommand reloadCommand = new ReloadCommand(COMMAND_NAME, loader);
        final Map<String, ExpansionCommand> commandMap = Map.of(
                "git", gitCommand,
                "list", listCommand,
                "debug", debugCommand,
                "parse", parseCommand,
                "reload", reloadCommand
        );

        this.router = new ExpansionCommandRouter(JavascriptExpansion.VERSION, JavascriptExpansion.AUTHOR, WIKI_LINK, commandMap);
    }

    public void register() {
        hytaleCommand = PlaceholderAPIPlugin.instance().getCommandRegistry().registerCommand(router);
    }

    public void unregister() {
        if (hytaleCommand != null) {
            hytaleCommand.unregister();
        }
    }

}
