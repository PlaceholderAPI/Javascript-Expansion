/*
 *
 * Javascript-Expansion
 * Copyright (C) 2020 Ryan McCarthy
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

import com.extendedclip.papi.expansion.javascript.cloud.GithubScriptManager;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

public class JavascriptExpansion extends PlaceholderExpansion implements Cacheable, Configurable {
	
	private ScriptEngine globalEngine = null;
	
	private JavascriptPlaceholdersConfig config;
	private final Set<JavascriptPlaceholder> scripts = new HashSet<>();
	private final String VERSION = getClass().getPackage().getImplementationVersion();
	private static JavascriptExpansion instance;
	private boolean debug;
	private GithubScriptManager githubScripts;
	private JavascriptExpansionCommands commands;
	private CommandMap commandMap;


	public JavascriptExpansion() {
		instance = this;
		try {
			final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			f.setAccessible(true);
			commandMap = (CommandMap) f.get(Bukkit.getServer());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			githubScripts.fetch();
		}

		registerCommand();
		return super.register();
	}
	
	@Override
	public void clear() {
		unregisterCommand();
		scripts.forEach(s -> {
			s.saveData();
			s.cleanup();
		});
		if (githubScripts != null) {
		  githubScripts.clear();
		  githubScripts = null;	
		}
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
	
	protected int reloadScripts() {
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

	public GithubScriptManager getGithubScriptManager() {
		return githubScripts;
	}

	private boolean unregisterCommand() {
		if (commandMap == null || commands == null) return false;
		return commands.unregister(commandMap);
	}

	private boolean registerCommand() {
		if (commandMap == null) return false;
		commands = new JavascriptExpansionCommands(this);
		commandMap.register("papi" + commands.getName(), commands);
		return commands.isRegistered();
	}
}
