package at.helpch.placeholderapi.expansion.javascript;

import at.helpch.placeholderapi.api.expansion.Expansion;
import at.helpch.placeholderapi.api.expansion.ExpansionDescription;
import at.helpch.placeholderapi.api.expansion.Platform;
import at.helpch.placeholderapi.api.expansion.placeholder.Placeholder;
import com.eclipsesource.v8.V8;

// ------------------------------
// Copyright (c) PiggyPiglet 2021
// https://www.piggypiglet.me
// ------------------------------
@ExpansionDescription(
        name = "Javascript",
        version = "1.0.0",
        identifier = "javascript",
        authors = "HelpChat",
        platforms = {Platform.BUKKIT, Platform.SPONGE, Platform.NUKKIT}
)
public final class JavascriptExpansion extends Expansion {
    private static final String JAVASCRIPT = "5+5";

    @Placeholder("")
    public Object parse() {
        final V8 runtime = V8.createV8Runtime();
        final Object value = runtime.executeScript(JAVASCRIPT);
        runtime.release();
        return value;
    }
}
