package at.helpch.papi.expansion.javascript.cloud;


import at.helpch.papi.expansion.javascript.config.ExpansionConfig;
import at.helpch.placeholderapi.PlaceholderAPIPlugin;

public final class GitScriptActiveStateSetter implements ActiveStateSetter {
    private static final String ACTIVE_STATE_KEY = "expansions.javascript.github_script_downloads";
    private final ExpansionConfig config;

    public GitScriptActiveStateSetter(final ExpansionConfig config) {
        this.config = config;
    }


    @Override
    public void setActive(boolean state) {
        config.githubScriptDownloads(state);
        PlaceholderAPIPlugin.instance().configManager().save();
    }

    @Override
    public boolean isActive() {
        return config.githubScriptDownloads();
    }
}
