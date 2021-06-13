package com.extendedclip.papi.expansion.javascript.commands.router;

import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import com.extendedclip.papi.expansion.javascript.cloud.GitScriptManager;
import com.extendedclip.papi.expansion.javascript.commands.*;
import com.extendedclip.papi.expansion.javascript.evaluator.ScriptEvaluatorFactory;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.Map;

import static com.extendedclip.papi.expansion.javascript.commands.router.ExpansionCommandRouter.COMMAND_NAME;

public final class CommandRegistrar {
    private static final String WIKI_LINK = "https://github.com/PlaceholderAPI/Javascript-Expansion/wiki";
    private final CommandRouter router;
    private final CommandMap commandMap;

    public CommandRegistrar(final JavascriptExpansion expansion, final GitScriptManager gitScriptManager, final ScriptEvaluatorFactory evaluatorFactory) throws ReflectiveOperationException {
        final GitRefreshCommand gitRefreshCommand = new GitRefreshCommand(gitScriptManager.getIndexProvider());
        final GitListCommand gitListCommand = new GitListCommand(gitScriptManager.getIndexProvider());
        final GitDownloadCommand gitDownloadCommand = new GitDownloadCommand(gitScriptManager);
        final GitInfoCommand gitInfoCommand = new GitInfoCommand(gitScriptManager.getIndexProvider());
        final GitEnabledCommand gitEnabledCommand = new GitEnabledCommand(gitScriptManager.getActiveStateSetter());


        final Map<String, ExpansionCommand> gitCommandMap = ImmutableMap.<String, ExpansionCommand>builder()
                .put("refresh", gitRefreshCommand)
                .put("list", gitListCommand)
                .put("download", gitDownloadCommand)
                .put("info", gitInfoCommand)
                .put("enabled", gitEnabledCommand)
                .build();

        final CommandRouter gitCommandRouter = new ExpansionCommandRouter(expansion.getVersion(), expansion.getAuthor(), WIKI_LINK, gitCommandMap);

        final GitCommand gitCommand = new GitCommand(COMMAND_NAME, gitScriptManager.getActiveStateSetter(), gitCommandRouter);
        final ListCommand listCommand = new ListCommand(COMMAND_NAME, expansion);
        final DebugCommand debugCommand = new DebugCommand(COMMAND_NAME, expansion);
        final ParseCommand parseCommand = new ParseCommand(COMMAND_NAME, evaluatorFactory);
        final ReloadCommand reloadCommand = new ReloadCommand(COMMAND_NAME, expansion);
        final Map<String, ExpansionCommand> commandMap = ImmutableMap.<String, ExpansionCommand>builder()
                .put("git", gitCommand)
                .put("list", listCommand)
                .put("debug", debugCommand)
                .put("parse", parseCommand)
                .put("reload", reloadCommand)
                .build();

        this.router = new ExpansionCommandRouter(expansion.getVersion(), expansion.getAuthor(), WIKI_LINK, commandMap);
        final Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        field.setAccessible(true);
        this.commandMap = (CommandMap) field.get(Bukkit.getServer());
    }

    public void register() {
        commandMap.register("papi" + router.getName(), router);
    }

    public void unregister() {

        try {
            Class<? extends CommandMap> cmdMapClass = commandMap.getClass();
            final Field knownCommandsField;

            //Check if the server's in 1.13+
            if (cmdMapClass.getSimpleName().equals("CraftCommandMap")) {
                knownCommandsField = cmdMapClass.getSuperclass().getDeclaredField("knownCommands");
            } else {
                knownCommandsField = cmdMapClass.getDeclaredField("knownCommands");
            }

            knownCommandsField.setAccessible(true);

            //noinspection unchecked
            final Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
            knownCommands.remove(router.getName());
            for (String alias : router.getAliases()) {
                if (knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(router.getName())) {
                    knownCommands.remove(alias);
                }
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        router.unregister(commandMap);
    }

}
