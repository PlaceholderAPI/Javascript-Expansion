package at.helpch.papi.expansion.javascript.commands.util;

// stolen from - https://github.com/nhulston/Essentials/blob/main/src/main/java/com/nhulston/essentials/util/ColorUtil.java

import com.hypixel.hytale.protocol.MaybeBool;
import com.hypixel.hytale.server.core.Message;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtil {
    // Matches all formatting tokens: &#RRGGBB, &0-&f, &l, &r
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "&#([0-9a-fA-F]{6})|&([0-9a-fA-FlLrR])"
    );

    // Standard Minecraft color codes mapped to hex
    private static final String[] COLOR_MAP = {
            "#000000", // &0 - Black
            "#0000AA", // &1 - Dark Blue
            "#00AA00", // &2 - Dark Green
            "#00AAAA", // &3 - Dark Aqua
            "#AA0000", // &4 - Dark Red
            "#AA00AA", // &5 - Dark Purple
            "#FFAA00", // &6 - Gold
            "#AAAAAA", // &7 - Gray
            "#555555", // &8 - Dark Gray
            "#5555FF", // &9 - Blue
            "#55FF55", // &a - Green
            "#55FFFF", // &b - Aqua
            "#FF5555", // &c - Red
            "#FF55FF", // &d - Light Purple
            "#FFFF55", // &e - Yellow
            "#FFFFFF"  // &f - White
    };

    private static final String DEFAULT_COLOR = "#FFFFFF";

    private ColorUtil() {}

    /**
     * Tracks the current text style state during parsing.
     */
    private static class TextStyle {
        String color = DEFAULT_COLOR;
        boolean bold = false;

        void reset() {
            color = DEFAULT_COLOR;
            bold = false;
        }
    }

    /**
     * Parses color and formatting codes and returns a styled Message.
     * Supports: &0-&f (colors), &#RRGGBB (hex colors), &l (bold), &r (reset)
     */
    @Nonnull
    public static Message colorize(@Nonnull String text) {
        List<Message> parts = new ArrayList<>();
        TextStyle currentStyle = new TextStyle();
        Matcher matcher = TOKEN_PATTERN.matcher(text);
        int lastEnd = 0;

        while (matcher.find()) {
            // Add text before this token with current style
            if (matcher.start() > lastEnd) {
                String segment = text.substring(lastEnd, matcher.start());
                if (!segment.isEmpty()) {
                    parts.add(createStyledMessage(segment, currentStyle));
                }
            }

            // Process the token
            String hexColor = matcher.group(1);  // &#RRGGBB capture
            String code = matcher.group(2);       // &X capture

            if (hexColor != null) {
                // Hex color code: &#RRGGBB
                currentStyle.color = "#" + hexColor.toUpperCase();
            } else if (code != null) {
                char c = code.toLowerCase().charAt(0);
                if (c == 'l') {
                    // Bold
                    currentStyle.bold = true;
                } else if (c == 'r') {
                    // Reset all formatting
                    currentStyle.reset();
                } else {
                    // Standard color code: &0-&f
                    int index = Character.digit(c, 16);
                    currentStyle.color = COLOR_MAP[index];
                }
            }

            lastEnd = matcher.end();
        }

        // Add remaining text
        if (lastEnd < text.length()) {
            String segment = text.substring(lastEnd);
            if (!segment.isEmpty()) {
                parts.add(createStyledMessage(segment, currentStyle));
            }
        }

        if (parts.isEmpty()) {
            return Message.raw(text);
        } else if (parts.size() == 1) {
            return parts.getFirst();
        } else {
            return Message.join(parts.toArray(new Message[0]));
        }
    }

    /**
     * Creates a Message with the specified style applied.
     */
    private static Message createStyledMessage(String text, TextStyle style) {
        Message message = Message.raw(text).color(style.color);

        if (style.bold) {
            message.getFormattedMessage().bold = MaybeBool.True;
        }

        return message;
    }
}