package at.helpch.papi.expansion.javascript.config;

import org.jetbrains.annotations.NotNull;

public class ExpansionConfig {
    private boolean debug;
    private String argumentSplit;
    private boolean githubScriptDownloads;
    private boolean enableParseCommand;
    private boolean useQuickJs;

    public ExpansionConfig(final boolean debug, @NotNull final String argumentSplit,
                           final boolean githubScriptDownloads, final boolean enableParseCommand,
                           final boolean useQuickJs) {
        this.debug = debug;
        this.argumentSplit = argumentSplit;
        this.githubScriptDownloads = githubScriptDownloads;
        this.enableParseCommand = enableParseCommand;
        this.useQuickJs = useQuickJs;
    }

    public boolean debug() {
        return debug;
    }

    @NotNull
    public String argumentSplit() {
        return argumentSplit;
    }

    public boolean githubScriptDownloads() {
        return githubScriptDownloads;
    }

    public void githubScriptDownloads(final boolean githubScriptDownloads) {
        this.githubScriptDownloads = githubScriptDownloads;
    }

    public boolean enableParseCommand() {
        return enableParseCommand;
    }

    public boolean useQuickJs() {
        return useQuickJs;
    }
}
