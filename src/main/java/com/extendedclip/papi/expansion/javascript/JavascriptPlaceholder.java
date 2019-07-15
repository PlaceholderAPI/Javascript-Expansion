/*
 *
 * Javascript-Expansion
 * Copyright (C) 2019 Ryan McCarthy
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

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;

public class JavascriptPlaceholder {
	
	private ScriptEngine engine = null;

	private String identifier;
	
	private String script;
	
	private ScriptData data = null;
	
	private File dataFile;
	
	private FileConfiguration cfg;
	
	private final String FILEDIR = PlaceholderAPIPlugin.getInstance().getDataFolder() + File.separator + "javascripts" + File.separator + "javascript_data";
	
	public JavascriptPlaceholder(ScriptEngine engine, String identifier, String script) {
		Validate.notNull(engine, "ScriptEngine can not be null");
		Validate.notNull(identifier, "Identifier can not be null");
		Validate.notNull(script, "script can not be null");
		this.engine = engine;
		this.identifier = identifier;
		this.script = script;
		File dir = new File(FILEDIR);
		
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		data = new ScriptData();
		dataFile = new File(FILEDIR, identifier + "_data.yml");
    	engine.put("Data", data);
    	engine.put("BukkitServer", Bukkit.getServer());
    	engine.put("Expansion", JavascriptExpansion.getInstance());
    	engine.put("Placeholder", this);
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getScript() {
		return script;
	}
	
	public String evaluate(OfflinePlayer p, String... args) {
		String exp = PlaceholderAPI.setPlaceholders(p, script);

        try {
        	String[] c = null;
        	
        	if (args != null && args.length > 0) {
        		for (int i = 0 ; i < args.length ; i++) {
        			if (args[i] == null || args[i].isEmpty()) {
        				continue;
        			}
        			
        			String s = PlaceholderAPI.setBracketPlaceholders(p, args[i]);
        			
        			if (c == null) {
        				c = new String[args.length];
        			}
        			
        			c[i] = s;
        		}
        	}
        	
        	if (c == null) {
        		c = new String[]{};
        	}

        	engine.put("args", c);
        	engine.put("BukkitPlayer", p);
            Object result = engine.eval(exp);
            return result != null ? PlaceholderAPI.setBracketPlaceholders(p, result.toString()) : "";
                        
        } catch (ScriptException ex) {
        	ex.printStackTrace();
        }
        return "Script error";
	}
	
	public ScriptData getData() {
		// this should never be null but just in case setData(null) is called
		if (data == null) {
			data = new ScriptData();
		}
		return data;
	}

	public void setData(ScriptData data) {
		this.data = data;
	}
	
	public boolean loadData() {
		
		cfg = new YamlConfiguration();
		
		if (!dataFile.exists()) {
			return false;
		}
		try {
			cfg.load(dataFile);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			return false;
		}
		
		final Set<String> keys = cfg.getKeys(true);
		
		if (keys == null || keys.isEmpty()) {
			return false;
		}
		
		if (data == null) {
			data = new ScriptData();
		} else {
			data.clear();
		}
		
		keys.stream().forEach(k -> {
			data.set(k, cfg.get(k));
		});
		
		if (!data.isEmpty()) {
			this.setData(data);
			return true;
		}
		return false;
	}
	
	public boolean saveData() {
		if (data == null || data.isEmpty()) {
			return false;
		}
		
		if (cfg == null) {
			return false;
		}
		
		data.getData().entrySet().forEach(e -> {
			cfg.set(e.getKey(), e.getValue());
		});
		
		try {
			cfg.save(dataFile);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public void cleanup() {
		if (this.data != null) {
			this.data.clear();
			this.data = null;
		}
		this.cfg = null;
	}

	public String setPAPIPlaceholder(OfflinePlayer p, String identifier) {
		return PlaceholderAPI.setPlaceholders(p, identifier);
	}

	public String setPAPIRelPlaceholder(Player p1, Player p2, String identifier) {
		return PlaceholderAPI.setRelationalPlaceholders(p1, p2, identifier);
	}
}
