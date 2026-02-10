package at.helpch.papi.expansion.javascript.config.model;

public class ScriptConfigModel {
    private String file;

    public ScriptConfigModel(String file) {
        this.file = file;
    }

    public String file() {
        return file;
    }

    public void file(String file) {
        this.file = file;
    }
}
