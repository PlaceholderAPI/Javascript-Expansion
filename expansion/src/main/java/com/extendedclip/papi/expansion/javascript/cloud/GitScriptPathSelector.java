package com.extendedclip.papi.expansion.javascript.cloud;

import java.io.File;
import java.nio.file.Path;

public final class GitScriptPathSelector implements PathSelector {
    private final String expansionPath;

    public GitScriptPathSelector(final File expansionFolder) {
        this.expansionPath = expansionFolder.getAbsolutePath();
    }

    @Override
    public Path select(final String name) {
        return Path.of(expansionPath, name + ".js");
    }
}
