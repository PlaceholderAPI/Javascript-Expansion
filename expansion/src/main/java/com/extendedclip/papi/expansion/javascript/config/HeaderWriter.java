package com.extendedclip.papi.expansion.javascript.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.net.URL;

public interface HeaderWriter {
    void writeTo(final FileConfiguration configuration);

    static HeaderWriter fromJar(final URL jarUrl) {
        return new ResourceHeaderWriter(new JarResourceProvider(jarUrl));
    }
}
