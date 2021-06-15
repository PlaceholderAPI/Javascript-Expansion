package com.extendedclip.papi.expansion.javascript;

import java.io.IOException;

public interface ScriptLoader {
    int reload() throws IOException;
    void clear();
}
