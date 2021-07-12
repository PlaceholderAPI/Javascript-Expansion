package com.extendedclip.papi.expansion.javascript.config;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ResourceHeaderWriter implements HeaderWriter {
    @NotNull
    private final Function<String, InputStream> inputStreamFunction;

    public ResourceHeaderWriter(@NotNull final Function<String, InputStream> inputStreamFunction) {
        this.inputStreamFunction = inputStreamFunction;
    }

    @Override
    public void writeTo(@NotNull final FileConfiguration configuration) {
        try (final InputStream stream = inputStreamFunction.apply("header.txt")) {
            final String headerString = new BufferedReader(new InputStreamReader(stream)).lines()
                    .collect(Collectors.joining("\n"));
            configuration.options().header(headerString);
        } catch (final IOException exception) {
            ExpansionUtils.errorLog("Failed to read header file", exception);
        }
    }
}
