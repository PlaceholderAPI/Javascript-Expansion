/*
 *
 * Javascript-Expansion
 * Copyright (C) 2018 Ryan McCarthy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package com.extendedclip.papi.expansion.javascript;

import com.extendedclip.papi.expansion.javascript.cloud.GithubScript;
import com.extendedclip.papi.expansion.javascript.cloud.GithubScriptManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class JavascriptExpansion extends PlaceholderExpansion implements Cacheable, Configurable, Listener {
	
	private ScriptEngine globalEngine = null;
	
	private JavascriptPlaceholdersConfig config;
	private final Set<JavascriptPlaceholder> scripts = new HashSet<>();
	private final String VERSION = getClass().getPackage().getImplementationVersion();
	private static JavascriptExpansion instance;
	private boolean debug = false;
	private GithubScriptManager githubScripts = null;

	public JavascriptExpansion() {
		instance = this;
	}

	/*
	 * I am just testing the waters here because there is no command system for expansions...
	 */
	@EventHandler
	public void onCmdExecute(PlayerCommandPreprocessEvent event) {
		
		String msg = event.getMessage();
		
		if (!msg.startsWith("/papijsp")) {
			return;
		}
		
		if (!event.getPlayer().hasPermission("placeholderapi.admin")) {
			return;
		}
		
		event.setCancelled(true);
		
		Player p = event.getPlayer();
		
		// default command
		if (!msg.contains(" ")) {
			msg(p, "&7Javascript expansion v: &f" + getVersion());
			msg(p, "&7Created by: &f" + getAuthor());
			msg(p, "&fWiki: &ahttps://github.com/PlaceholderAPI-Expansions/Javascript-Expansion/wiki");
			msg(p, "&r");
			msg(p, "&7/papijsp reload &7- &fReload your javascripts without reloading PlaceholderAPI");
			msg(p, "&7/papijsp list &7- &fList loaded script identifiers.");
			msg(p, "&7/papijsp git download <name> &7- &fDownload a script from the js expansion github.");
			msg(p, "&7/papijsp git list &7- &fList available scripts in the js expansion github.");
			msg(p, "&7/papijsp git info (name) &7- &fGet the description and url of a specific script.");
			return;
		}
		
		if (msg.equals("/papijsp reload")) {
			msg(p, "&aReloading...");
			int l = this.reloadScripts();
			msg(p, l + " &7script" + (l == 1 ? "" : "s")+ " loaded");
			return;
		}
		
		if (msg.equals("/papijsp list")) {
			List<String> loaded = this.getLoadedIdentifiers();
			msg(p, loaded.size() + " &7script" + (loaded.size() == 1 ? "" : "s")+ " loaded");
			msg(p, String.join(", ", loaded));
			return;
		}

		if (msg.equals("/papijsp git list")) {
			msg(p, GithubScript.values().length + " &7script"
					+ (GithubScript.values().length == 1 ? "" : "s") + " available on Github.");
			msg(p, String.join(", ", GithubScript.getAllScriptNames()));
			return;
		}

		if (msg.startsWith("/papijsp git info ")) {

			if (this.githubScripts == null) {
				msg(p, "This feature is disabled in the PAPI config!");
				return;
			}

			msg = msg.replace("/papijsp git info ", "");

			GithubScript script = GithubScript.getScript(msg);

			if (script == null) {
				msg(p, "&cThe script &7" + msg + " &cdoes not exist!");
				return;
			}

				msg(p, "&7Name: &f" + script.getName(),
						"&7Version: &f" + script.getVersion(),
						"&7Description: &f" + script.getDescription(),
						"&7Url: &f" + script.getUrl());

			return;
		}

		if (msg.startsWith("/papijsp git download ")) {

			if (this.githubScripts == null) {
				msg(p, "This feature is disabled in the PAPI config!");
				return;
			}

			msg = msg.replace("/papijsp git download ", "");

			GithubScript script = GithubScript.getScript(msg);

			if (script == null) {
				msg(p, "&cThe script &7" + msg + " &cdoes not exist!");
				return;
			}

			Bukkit.getScheduler().runTaskAsynchronously(getPlaceholderAPI(), new Runnable() {
				@Override
				public void run() {
					githubScripts.downloadScript(script);
				}
			});
			msg(p, "&aDownload initiated... Check the scripts folder in a moment...");
			return;
		}
		
		msg(p, "&cIncorrect usage &7- &f/papijsp");
	}

	public void msg(Player p, String... text) {
		Arrays.stream(text).forEach(line -> p.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
	}

	@Override
	public String getAuthor() {
		return "clip";
	}

	@Override
	public String getIdentifier() {
		return "javascript";
	}

	@Override
	public String getPlugin() {
		return null;
	}
	
	@Override
	public String getVersion() {
		return VERSION;
	}
	
	@Override
	public boolean register() {
		if (globalEngine == null) {
			try {
				globalEngine = new ScriptEngineManager().getEngineByName(getString("engine", "nashorn"));
			} catch (NullPointerException ex) {
				getPlaceholderAPI().getLogger().warning("Javascript engine type was invalid! Defaulting to 'nashorn'");
				globalEngine = new ScriptEngineManager().getEngineByName("nashorn");
			}
		}
		debug = (boolean) get("debug", false);
		config = new JavascriptPlaceholdersConfig(this);
		config.loadPlaceholders();
		if (debug) {
			System.out.println("Java version: " + System.getProperty("java.version"));

			ScriptEngineManager manager = new ScriptEngineManager();
			List<ScriptEngineFactory> factories = manager.getEngineFactories();
			System.out.println("displaying all script engine factories:");
			for (ScriptEngineFactory factory : factories) {
				System.out.println("Engine name: " + factory.getEngineName());
				System.out.println("version: " + factory.getEngineVersion());
				System.out.println("lang name: " + factory.getLanguageName());
				System.out.println("lang version: " + factory.getLanguageVersion());
				System.out.println("extensions: " + factory.getExtensions());
				System.out.println("mime types: " + factory.getMimeTypes());
				System.out.println("names: " + factory.getNames());
			}
		}
		if ((Boolean) get("github_script_downloads", false)) {
			githubScripts = new GithubScriptManager(this);
		}
		return super.register();
	}
	
	@Override
	public void clear() {
		scripts.forEach(s -> {
			s.saveData();
			s.cleanup();
		});
		scripts.clear();
		globalEngine = null;
		instance = null;
	}

	@Override
	public String onRequest( OfflinePlayer p, String identifier) {
		if (p == null) {
			return "";
		}
		
		if (scripts.isEmpty()) {
			return null;
		}

		for (JavascriptPlaceholder script : scripts) {
			if (identifier.startsWith(script.getIdentifier() + "_")) {
				identifier = identifier.replace(script.getIdentifier() + "_", "");
				return !identifier.contains(",") ? script.evaluate(p, identifier) : script.evaluate(p, identifier.split(","));
			} else if (identifier.equalsIgnoreCase(script.getIdentifier())) {
				return script.evaluate(p);
			}
		}
		return null;
	}
	
	public boolean addJSPlaceholder(JavascriptPlaceholder p) {
		if (p == null) {
			return false;
		}
		
		if (scripts.isEmpty()) {
			scripts.add(p);
			return true;
		}
		
		if (scripts.stream().filter(s -> s.getIdentifier().equalsIgnoreCase(p.getIdentifier())).findFirst().orElse(null) != null) {
			return false;
		}
		
		scripts.add(p);
		return true;
	}
	
	public Set<JavascriptPlaceholder> getJSPlaceholders() {
		return scripts;
	}
	
	public List<String> getLoadedIdentifiers() {
		List<String> l = new ArrayList<>();
		scripts.forEach(s -> {
			l.add(s.getIdentifier());
		});
		return l;
	}
	
	public JavascriptPlaceholder getJSPlaceholder(String identifier) {
		return scripts.stream().filter(s -> s.getIdentifier().equalsIgnoreCase(identifier)).findFirst().orElse(null);
	}
	
	public int getAmountLoaded() {
		return scripts.size();
	}
	
	public ScriptEngine getGlobalEngine() {
		return globalEngine;
	}

	public JavascriptPlaceholdersConfig getConfig() {
		return config;
	}

	@Override
	public Map<String, Object> getDefaults() {
		Map<String, Object> def = new HashMap<String, Object>();
		def.put("engine", "javascript");
		def.put("debug", false);
		def.put("github_script_downloads", false);
		return def;
	}
	
	private int reloadScripts() {
		scripts.forEach(s -> {
			s.saveData();
			s.cleanup();
		});
		scripts.clear();
		config.reload();
		return config.loadPlaceholders();
	}
	
	public static JavascriptExpansion getInstance() {
		return instance;
	}
}
