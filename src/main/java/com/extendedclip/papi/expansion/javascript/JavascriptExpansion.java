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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class JavascriptExpansion extends PlaceholderExpansion implements Cacheable, Configurable {

	private ScriptEngine engine = null;
	private String engineType = "javascript";
	private JavascriptPlaceholdersConfig config;
	private final Set<JavascriptPlaceholder> scripts = new HashSet<JavascriptPlaceholder>();
	private final String VERSION = getClass().getPackage().getImplementationVersion();
	
	@Override
	public boolean register() {
		engineType = getString("engine", "javascript");
		
		if (engine == null) {
			try {
				engine = new ScriptEngineManager().getEngineByName(engineType);
			} catch (NullPointerException ex) {
				PlaceholderAPIPlugin.getInstance().getLogger().warning("Javascript engine type was invalid! Defaulting to 'javascript'");
				engine = new ScriptEngineManager().getEngineByName("javascript");
			}
			
        	engine.put("BukkitServer", Bukkit.getServer());
		}
		
		config = new JavascriptPlaceholdersConfig(this);
		config.loadPlaceholders();
		return super.register();
	}
	
	@Override
	public void clear() {
		if (!scripts.isEmpty()) {
			scripts.stream().forEach(s -> {
				s.saveData();
				s.cleanup();
			});
		}
		scripts.clear();
		engine = null;
	}

	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		if (p == null) {
			return "";
		}
		
		if (scripts.isEmpty() || engine == null) {
			return null;
		}

		for (JavascriptPlaceholder script : scripts) {
			if (identifier.startsWith(script.getIdentifier() + "_")) {
				
				identifier = identifier.replace(script.getIdentifier() + "_", "");
				
				if (identifier.indexOf(",") == -1) {
					return script.evaluate(engine, p, identifier);
				} else {
					return script.evaluate(engine, p, identifier.split(","));
				}
			} else if (identifier.equalsIgnoreCase(script.getIdentifier())) {
				return script.evaluate(engine, p);
			}
		}
		return null;
	}

	@Override
	public boolean canRegister() {
		return true;
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
	
	public boolean addJavascriptPlaceholder(JavascriptPlaceholder p) {
		if (p == null) {
			return false;
		}
		
		if (scripts.isEmpty()) {
			scripts.add(p);
			return true;
		}
		
		for (JavascriptPlaceholder pl : scripts) {
			if (pl.getIdentifier().equalsIgnoreCase(p.getIdentifier())) {
				return false;
			}
		}
		scripts.add(p);
		return true;
	}
	
	public int getJavascriptPlaceholdersAmount() {
		return scripts == null ? 0 : scripts.size();
	}

	@Override
	public Map<String, Object> getDefaults() {
		Map<String, Object> def = new HashMap<String, Object>();
		def.put("engine", "javascript");
		return def;
	}
}
