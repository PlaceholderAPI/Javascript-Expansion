package com.extendedclip.papi.expansion.javascript.cloud;

import java.nio.file.Path;

public interface FileDownloader {
    void download(Path from, Path to);
}
