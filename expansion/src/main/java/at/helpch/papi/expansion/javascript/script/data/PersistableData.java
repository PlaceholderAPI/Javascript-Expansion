package at.helpch.papi.expansion.javascript.script.data;

import at.helpch.papi.expansion.javascript.script.ScriptData;

public interface PersistableData {
    ScriptData getScriptData();
    void save();
    void reload();
}
