package com.extendedclip.papi.expansion.javascript.script;

import java.io.IOException;

public interface ScriptLoader {
    int reload() throws IOException;
    void clear();
}
