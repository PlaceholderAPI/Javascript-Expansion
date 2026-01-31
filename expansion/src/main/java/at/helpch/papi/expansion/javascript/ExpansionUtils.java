package at.helpch.papi.expansion.javascript;

import at.helpch.placeholderapi.PlaceholderAPIPlugin;
import com.hypixel.hytale.logger.HytaleLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;

import java.util.*;
import java.util.logging.Level;

public class ExpansionUtils {

    public static final String DEFAULT_ENGINE = "nashorn";
    public static final String PREFIX = "[PAPI] [Javascript-Expansion] ";

    private static final HytaleLogger LOGGER = PlaceholderAPIPlugin.instance().getLogger();

    public static String plural(final int amount) {
        return amount > 1 ? "s" : "";
    }

    public static void warnLog(String log, Throwable throwable) {
        warnLog(log, throwable, true);
    }

    public static void infoLog(final String log) {
        infoLog(log, true);
    }

    public static void infoLog(String log, boolean canPrefix) {
        String prefix = "";
        if (canPrefix) prefix = PREFIX;
        LOGGER.atInfo().log((prefix + log));
    }

    public static void warnLog(String log, Throwable throwable, boolean canPrefix) {
        String prefix = "";
        if (canPrefix) prefix = PREFIX;
        if (throwable == null) {
            LOGGER.atWarning().log(prefix + log);
        } else LOGGER.atWarning().log(prefix + log, throwable);
    }

    public static void errorLog(String log, Throwable throwable) {
        errorLog(log, throwable, true);
    }

    public static void errorLog(String log, Throwable throwable, boolean canPrefix) {
        String prefix = "";
        if (canPrefix) prefix = PREFIX;
        if (throwable == null) {
            LOGGER.atSevere().log(prefix + log);
        } else {
            LOGGER.atSevere().log(prefix + log, throwable);
        }
    }

    protected static Object ymlToJavaObj(Object obj) {
        if (obj instanceof MemorySection) {
            MemorySection ymlMem = (MemorySection) obj;
            if (ymlMem.isList(ymlMem.getCurrentPath())) {
                ArrayList<Object> list = new ArrayList<>();
                for (String entry : ymlMem.getKeys(true)) {
                    list.add(ymlToJavaObj(ymlMem.get(entry)));
                }
                return list;
            } else {
                Map<String, Object> map = new HashMap<>();
                for (String entry : ymlMem.getKeys(true)) {
                    map.put(entry, ymlToJavaObj(ymlMem.get(entry)));
                }
                return map;
            }
        } else {
            return obj;
        }
    }

}
