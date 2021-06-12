package com.extendedclip.papi.expansion.javascript.cloud;

import java.nio.file.Path;

public interface PathSelector {
    Path select(final String name);
}
