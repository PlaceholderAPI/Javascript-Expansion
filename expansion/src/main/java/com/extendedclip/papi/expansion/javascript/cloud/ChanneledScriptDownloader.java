package com.extendedclip.papi.expansion.javascript.cloud;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.*;
import java.nio.file.*;

public final class ChanneledScriptDownloader implements ScriptDownloader {
    private final PathSelector pathSelector;

    public ChanneledScriptDownloader(final PathSelector pathSelector) {
        this.pathSelector = pathSelector;
    }


    @Override
    public Path download(final GitScript script) throws IOException {
        final URL url = new URL(script.getUrl());
        final URLConnection urlConnection = url.openConnection();
        final long length = urlConnection.getContentLength();

        final Path to = pathSelector.select(script.getName());
        if (Files.exists(to))
        try (ReadableByteChannel fromChannel = Channels.newChannel(url.openStream())) {
            try (FileChannel toChannel = FileChannel.open(to, StandardOpenOption.CREATE)) {
                toChannel.transferFrom(fromChannel, 0, length);
            }
        }
        return to;
    }
}
