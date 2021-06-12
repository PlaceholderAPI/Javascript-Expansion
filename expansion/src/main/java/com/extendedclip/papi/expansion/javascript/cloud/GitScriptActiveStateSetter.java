package com.extendedclip.papi.expansion.javascript.cloud;

import org.bukkit.plugin.java.JavaPlugin;

public final class GitScriptActiveStateSetter implements ActiveStateSetter {
    private static final String ACTIVE_STATE_KEY = "expansions.jsexpansion.github_script_downloads";
    private final JavaPlugin plugin;

    public GitScriptActiveStateSetter(final JavaPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public void setActive(boolean state) {
        plugin.getConfig().set(ACTIVE_STATE_KEY, state);
        plugin.saveConfig();
        plugin.reloadConfig();
    }

    @Override
    public boolean isActive() {
        return plugin.getConfig().getBoolean(ACTIVE_STATE_KEY, false);
    }
}
