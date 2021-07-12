package com.extendedclip.papi.expansion.javascript.commands.router;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ExpansionCommandRouter extends CommandRouter {
    public static final String COMMAND_NAME = "jsexpansion";
    private static final String PERMISSION = "placeholderapi.js.admin";
    private static final String DESCRIPTION = "JavaScript Expansion Commands";
    private static final String USAGE = "/jsexpansion <sub-command> <params>";
    private static final List<String> ALIASES = Arrays.asList("javascriptexpansion", "jsexp");
    private static final Collection<String> HELP_HEADER = Arrays.asList(
            "&eJavascript expansion &7v: &f{version}",
            "&eCreated by: &f{author}",
            "&eWiki: &f{wiki}",
            "&r"
    );
    private final String expansionVersion;
    private final String authorName;
    private final String wikiLink;

    public ExpansionCommandRouter(
            @NotNull final String expansionVersion,
            @NotNull final String authorName,
            @NotNull final String wikiLink,
            @NotNull final Map<String, ExpansionCommand> commandMap
    ) {
        super(COMMAND_NAME, DESCRIPTION, USAGE, ALIASES, PERMISSION, commandMap);
        this.expansionVersion = expansionVersion;
        this.authorName = authorName;
        this.wikiLink = wikiLink;
    }

    @Override
    public List<String> getHelpHeader() {
        return HELP_HEADER.stream().map(this::replacePlaceholders).collect(Collectors.toList());
    }

    @Override
    public String getSubCommandHelpFormat() {
        return "&e/%1$s %2$s &7- &f%3$s";
    }

    @Override
    public String getInvalidCommandMessage() {
        return "&cInvalid expansion sub-command! Type&f /%1$s &cfor help";
    }

    private String replacePlaceholders(final String input) {
        return input
                .replace("{version}", expansionVersion)
                .replace("{author}", authorName)
                .replace("{wiki}", wikiLink);
    }
}
