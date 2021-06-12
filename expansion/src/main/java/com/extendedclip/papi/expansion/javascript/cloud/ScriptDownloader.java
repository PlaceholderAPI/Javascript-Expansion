package com.extendedclip.papi.expansion.javascript.cloud;

import java.io.IOException;
import java.nio.file.Path;

public interface ScriptDownloader {
    Path download(final GitScript script) throws IOException;
}
