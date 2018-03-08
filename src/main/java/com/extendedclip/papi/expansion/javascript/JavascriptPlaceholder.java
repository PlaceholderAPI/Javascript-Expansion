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

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;

public class JavascriptPlaceholder {

	private String identifier;
	
	private String expression;
	
	private String trueResult;
	
	private String falseResult;
	
	private JavascriptReturnType type;
	
	private PlaceholderData data = null;
	
	private File dataFile;
	
	private FileConfiguration cfg;
	
	private final String FILEDIR = PlaceholderAPIPlugin.getInstance().getDataFolder() + File.separator + "expansions" + File.separator + "javascript_data";
	
	public JavascriptPlaceholder(String identifier, JavascriptReturnType type, String expression, String trueResult, String falseResult) {
		if (type == null) {
			throw new IllegalArgumentException("Javascript placeholder type must set as 'boolean' or 'string'!");
		}
		
		this.type = type;
		
		if (identifier == null) {
			throw new IllegalArgumentException("Javascript placeholder identifier must not be null!");
		} else if (expression == null) {
			throw new IllegalArgumentException("Javascript placeholder expression must not be null!");
		}
		
		this.identifier = identifier;
		this.expression = expression;
		
		if (type == JavascriptReturnType.BOOLEAN) {
			if (trueResult == null) {
				throw new IllegalArgumentException("Javascript boolean placeholder must contain a true_result!");
			} else if (falseResult == null) {
				throw new IllegalArgumentException("Javascript boolean placeholder must contain a false_result!");
			}
			this.trueResult = trueResult;
			this.falseResult = falseResult;
		}

		File dir = new File(FILEDIR);
		
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		dataFile = new File(FILEDIR, identifier + "_data.yml");
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
		
		PlaceholderData data = new PlaceholderData();
		
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
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getExpression() {
		return expression;
	}
	
	public String getTrueResult() {
		return trueResult;
	}
	
	public String getFalseResult() {
		return falseResult;
	}
	
	public JavascriptReturnType getType() {
		return type;
	}
	
	public String evaluate(ScriptEngine engine, Player p, String... args) {
		String exp = PlaceholderAPI.setPlaceholders(p, expression);

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
        	
        	engine.put("Data", getData());
        	
        	engine.put("BukkitPlayer", p);
        	
            Object result = engine.eval(exp);

            if (type == JavascriptReturnType.BOOLEAN) {
            	
            	if (!(result instanceof Boolean)) {
                 	return "invalid javascript";
                 }
                 
                 if ((boolean) result) {
                 	return PlaceholderAPI.setPlaceholders(p, trueResult);
                 } else {
                 	return PlaceholderAPI.setPlaceholders(p, falseResult);
                 }	
            }
            	
            if (result instanceof String) {
            	String res = PlaceholderAPI.setBracketPlaceholders(p, (String)result);
            	return res != null ? res : "";
            } 
            
            return result != null ? PlaceholderAPI.setBracketPlaceholders(p, result.toString()) : "";
                        
        } catch (ScriptException ex) {
        	PlaceholderAPIPlugin.getInstance().getLogger().severe("Error in javascript format for placeholder - " + this.identifier);
        	ex.printStackTrace();
        }
        return "invalid javascript";
	}
	
	public PlaceholderData getData() {
		if (data == null) {
			data = new PlaceholderData();
		}
		return data;
	}

	public void setData(PlaceholderData data) {
		this.data = data;
	}
	
	public void cleanup() {
		if (this.data != null) {
			this.data.clear();
			this.data = null;
		}
		this.cfg = null;
	}
}
