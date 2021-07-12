package com.extendedclip.papi.expansion.javascript.cloud.download;

import com.extendedclip.papi.expansion.javascript.cloud.GitScript;

import java.io.IOException;
import java.nio.file.Path;

public interface ScriptDownloader {
    Path download(final GitScript script) throws IOException;
}
