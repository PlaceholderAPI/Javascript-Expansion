package at.helpch.papi.expansion.javascript.cloud.download;

import java.nio.file.Path;

public interface PathSelector {
    Path select(final String name);
}
