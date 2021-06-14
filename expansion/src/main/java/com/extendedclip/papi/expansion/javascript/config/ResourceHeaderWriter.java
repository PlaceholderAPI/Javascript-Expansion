package com.extendedclip.papi.expansion.javascript.config;

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.function.Function;

public final class ResourceHeaderWriter implements HeaderWriter {
    @NotNull
    private final Function<Void, InputStream> inputStreamFunction;

    public ResourceHeaderWriter(@NotNull final Function<Void, InputStream> inputStreamFunction) {
        this.inputStreamFunction = inputStreamFunction;
    }

    @Override
    public void writeTo(@NotNull final FileConfiguration configuration) {
        try (final InputStream stream = inputStreamFunction.apply(null);
             final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            stream.transferTo(outputStream);
            final String headerString = outputStream.toString(Charset.defaultCharset());
            configuration.options().header(headerString);
        } catch (final IOException exception) {
            ExpansionUtils.errorLog("Failed to read header file", exception);
        }
    }
}
