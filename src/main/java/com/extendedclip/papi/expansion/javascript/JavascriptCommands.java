package com.extendedclip.papi.expansion.javascript;

import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import static me.clip.placeholderapi.util.Msg.color;

public class JavascriptCommands extends Command {

    private JavascriptExpansion ex;

    public JavascriptCommands(JavascriptExpansion ex) {
        super("papijsp");
        this.setDescription("JavaScript expansion commands");
        this.usageMessage = color("&cIncorrect usage. &7/papijsp help");
        this.setAliases(Lists.newArrayList("daddyjsp"));

        this.ex = ex;
    }

    public boolean execute(CommandSender sender, String label, String[] args) {

        if (sender.hasPermission("placeholderapi.admin")) {
            if (args.length == 0) {
                sender.sendMessage(color("&eJavascript expansion &fv" + ex.getVersion()));
                sender.sendMessage(color("&eCreated by: &f" + ex.getAuthor()));
                return true;
            }

            if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "help":
                        sender.sendMessage(color("&eJavascript expansion &fv" + ex.getVersion()));
                        sender.sendMessage("");
                        sender.sendMessage(color("&e/papijsp reload &7- &fReload your Javascript placeholders only."));
                        sender.sendMessage(color("&e/papijsp list &7- &fLists loaded Javascript placeholders' identifiers."));
                        sender.sendMessage("");
                        sender.sendMessage(color("&eCreated by: &f" + ex.getAuthor()));
                        TextComponent wiki = new TextComponent(color("&eWiki: "));
                        TextComponent click = new TextComponent(color("&fClick here"));
                        click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color("&6Check the wiki for more info!")).create()));
                        click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/PlaceholderAPI/Javascript-Expansion/wiki"));
                        wiki.addExtra(click);
                        sender.spigot().sendMessage(wiki);
                        return true;
                    case "reload":
                        sender.sendMessage(color("&6Reloading..."));
                        int l = ex.reloadScripts();
                        sender.sendMessage(color("&f" + l + " &escript" + (l == 1 ? "" : "s")+ " loaded."));
                        return true;
                    case "list":
                        sender.sendMessage(color("&f" + ex.getAmountLoaded() + " &6script" + (ex.getAmountLoaded() == 1 ? "" : "s")+ " loaded."));
		                sender.sendMessage(color("&e" + String.join("&f, &e", ex.getLoadedIdentifiers())));
                        return true;
                }
            }
        } else {
            sender.sendMessage(color("You don't have permission to do that!"));
            return true;
        }

        sender.sendMessage(usageMessage);
        return true;
    }
}
