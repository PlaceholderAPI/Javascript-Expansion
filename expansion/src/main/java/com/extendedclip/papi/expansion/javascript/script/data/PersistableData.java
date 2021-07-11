package com.extendedclip.papi.expansion.javascript.script.data;

import com.extendedclip.papi.expansion.javascript.script.ScriptData;

public interface PersistableData {
    ScriptData getScriptData();
    void save();
    void reload();
}
