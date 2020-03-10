package com.extendedclip.papi.expansion.javascript;

import com.extendedclip.papi.expansion.javascript.cloud.GithubScript;
import com.extendedclip.papi.expansion.javascript.cloud.GithubScriptManager;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class JavascriptExpansionCommands extends Command {

  private JavascriptExpansion expansion;

  public JavascriptExpansionCommands(JavascriptExpansion expansion) {
    super("jsexpansion");
    this.expansion = expansion;
    this.setDescription("Javascript expansion commands");
    this.setUsage("/jsexpansion <arg>");
    this.setPermission("placeholderapi.js.admin");
  }

  @Override
  public boolean execute(CommandSender s, String label, String[] args) {
    if (!s.hasPermission(this.getPermission())) {
      msg(s, "&cYou don't have permission to do that!");
      return true;
    }

    if (args.length == 0) {
      msg(s, "&eJavascript expansion &7v: &f" + expansion.getVersion(),
          "&eCreated by: &f" + expansion.getAuthor(),
          "&eWiki: &ahttps://github.com/PlaceholderAPI-Expansions/Javascript-Expansion/wiki",
          "&r",
          "&e/jsexpansion reload &7- &fReload your javascripts without reloading PlaceholderAPI",
          "&e/jsexpansion list &7- &fList loaded script identifiers.");
      if (expansion.getGithubScriptManager() != null) {
        msg(s, "&e&e/jsexpansion git refresh &7- &fRefresh available Github scripts",
            "&e/jsexpansion git download <name> &7- &fDownload a script from the js expansion github.",
            "&e/jsexpansion git list &7- &fList available scripts in the js expansion github.",
            "&7/jsexpansion git info (name) &7- &fGet the description and url of a specific script.");
      }
      return true;
    }

    if (args[0].equalsIgnoreCase("reload")) {
      msg(s, "&aJavascriptExpansion reloading...");
      int l = expansion.reloadScripts();
      msg(s, l + " &7script" + (l == 1 ? "" : "s") + " loaded");
      return true;
    }

    if (args[0].equalsIgnoreCase("list")) {
      List<String> loaded = expansion.getLoadedIdentifiers();
      msg(s, loaded.size() + " &7script" + (loaded.size() == 1 ? "" : "s") + " loaded");
      msg(s, String.join(", ", loaded));
      return true;
    }

    if (args[0].equalsIgnoreCase("git")) {
      if (expansion.getGithubScriptManager() == null) {
        msg(s, "&8This feature is disabled in the PlaceholderAPI config.");
        return true;
      }

      if (args.length < 2) {
        msg(s, "&cIncorrect usage!");
        return true;
      }

      GithubScriptManager manager = expansion.getGithubScriptManager();

      if (args[1].equalsIgnoreCase("refresh")) {
        expansion.getGithubScriptManager().fetch();
        msg(s, "&aFetching available scripts... Check back in a sec!");
        return true;
      }

      if (args[1].equalsIgnoreCase("list")) {
        msg(s, manager.getAvailableScripts().size() + " &escript"
            + (manager.getAvailableScripts().size() == 1 ? "" : "s") + " available on Github.",
            String.join(", ", manager.getAvailableScripts().stream()
            .map(GithubScript::getName)
            .collect(Collectors.toSet())));
        return true;
      }

      if (args[1].equalsIgnoreCase("info")) {
        if (args.length < 3) {
          msg(s, "&4Incorrect usage! &f" + this.getName() + " git info <name>");
          return true;
        }
        GithubScript script = manager.getScript(args[2]);
        if (script == null) {
          msg(s, "&4The script &f" + args[2] + " &4does not exist!");
          return true;
        }
        msg(s, "&eName: &f" + script.getName(),
            "&eVersion: &f" + script.getVersion(),
            "&eDescription: &f" + script.getDescription(),
            "&eAuthor: &f" + script.getAuthor(),
            "&eSource URL: &f" + script.getUrl());
        return true;
      }
      if (args[1].equalsIgnoreCase("download")) {
        if (args.length < 3) {
          msg(s, "&4Incorrect usage! &f" + this.getName() + " git download <name>");
          return true;
        }
        GithubScript script = manager.getScript(args[2]);

        if (script == null) {
          msg(s, "&4The script &f" + args[2] + " &4does not exist!");
          return true;
        }

        manager.downloadScript(script);
        msg(s, "&aDownload started... &eCheck the scripts folder in a moment...");
        return true;
      }
      msg(s, "&4Incorrect usage! &f" + this.getName() + " &7for more help.");
      return true;
    }

    return true;
  }

  public void msg(CommandSender s, String... text) {
    Arrays.stream(text)
        .forEach(line -> s.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
  }
}
