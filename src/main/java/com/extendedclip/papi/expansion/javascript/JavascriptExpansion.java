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

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.entity.Player;

public class JavascriptExpansion extends PlaceholderExpansion implements Cacheable, Configurable {

	private ScriptEngine globalEngine = null;
	
	private JavascriptPlaceholdersConfig config;
	
	private final Set<JavascriptPlaceholder> scripts = new HashSet<JavascriptPlaceholder>();
	
	private final String VERSION = getClass().getPackage().getImplementationVersion();

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
	public boolean canRegister() {
		return true;
	}
	
	@Override
	public boolean register() {
		if (globalEngine == null) {
			try {
				globalEngine = new ScriptEngineManager().getEngineByName(getString("engine", "javascript"));
			} catch (NullPointerException ex) {
				PlaceholderAPIPlugin.getInstance().getLogger().warning("Javascript engine type was invalid! Defaulting to 'javascript'");
				globalEngine = new ScriptEngineManager().getEngineByName("javascript");
			}	
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
		globalEngine = null;
	}

	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		if (p == null) {
			return "";
		}
		
		if (scripts.isEmpty()) {
			return null;
		}

		for (JavascriptPlaceholder script : scripts) {
			if (identifier.startsWith(script.getIdentifier() + "_")) {
				identifier = identifier.replace(script.getIdentifier() + "_", "");
				return identifier.indexOf(",") == -1 ? script.evaluate(p, identifier) : script.evaluate(p, identifier.split(","));
			} else if (identifier.equalsIgnoreCase(script.getIdentifier())) {
				return script.evaluate(p);
			}
		}
		return null;
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
	
	public ScriptEngine getGlobalEngine() {
		return globalEngine;
	}

	@Override
	public Map<String, Object> getDefaults() {
		Map<String, Object> def = new HashMap<String, Object>();
		def.put("engine", "javascript");
		return def;
	}
}
