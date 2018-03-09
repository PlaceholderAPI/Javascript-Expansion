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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import me.clip.placeholderapi.PlaceholderAPIPlugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class JavascriptPlaceholdersConfig {

	private JavascriptExpansion ex;
	
	private PlaceholderAPIPlugin plugin;
	
	private FileConfiguration config;
	
	private File file;

	public JavascriptPlaceholdersConfig(JavascriptExpansion ex) {
		this.ex = ex;
		plugin = ex.getPlaceholderAPI();
		reload();
	}

	public void reload() {
		if (file == null) {
			file = new File(plugin.getDataFolder(), "javascript_placeholders.yml");
		}
		
		config = YamlConfiguration.loadConfiguration(file);
		config.options().header("Javascript Expansion: " + ex.getVersion()
				+ "\nThis is the main configuration file for the Javascript Expansion."
				+ "\n"
				+ "\nYou will define your javascript placeholders in this file."
				+ "\n"
				+ "\nJavascript files must be located in the:"
				+ "\n /plugins/placeholderapi/javascripts/ folder"
				+ "\n"
				+ "\nA detailed guide on how to create your own javascript placeholders"
				+ "\ncan be found here:"
				+ "\nhttps://github.com/PlaceholderAPI-Expansions/Javascript-Expansion/wiki"
				+ "\n"
				+ "\nYour javascript placeholders will be identified by: %javascript_<identifier>%"
				+ "\n"
				+ "\nConfiguration format:"
				+ "\n"
				+ "\n<identifier>:"
				+ "\n  file: <name of file>.<file extension>"
				+ "\n  engine: (name of script engine)"
				+ "\n"
				+ "\n"
				+ "\nExample:"
				+ "\n"
				+ "\n'my_placeholder':"
				+ "\n  file: 'my_placeholder.js'"
				+ "\n  engine: 'nashorn'");
		
		if (config.getKeys(false) == null || config.getKeys(false).isEmpty()) {
			config.set("example.file", "example.js");
			config.set("example.engine", "nashorn");
		}
		save();
	}

	public FileConfiguration load() {
		if (config == null) {
			reload();
		}
		return config;
	}

	public void save() {
		if ((config == null) || (file == null)) {
			return;
		}
		
		try {
			load().save(file);
		} catch (IOException ex) {
			plugin.getLogger().log(Level.SEVERE, "Could not save to " + file, ex);
		}
	}

	public int loadPlaceholders() {
		if (config == null || config.getKeys(false) == null || config.getKeys(false).isEmpty()) {
			return 0;
		}
		
		File dir = new File(plugin.getDataFolder() + File.separator + "javascripts");
		
		try {
			if (!dir.exists()) {
				dir.mkdirs();
				plugin.getLogger().info("Creating directory: plugins" + File.separator + "PlaceholderAPI" + File.separator + "javascripts");
			} else {
			}
		} catch (SecurityException e) {
			plugin.getLogger().severe("Could not create directory: plugins" + File.separator + "PlaceholderAPI" + File.separator + "javascripts");
		}
		
		for (String identifier : config.getKeys(false)) {
			if (!config.contains(identifier + ".file") || config.getString(identifier + ".file", null) == null) {
				plugin.getLogger().warning("Javascript placeholder: " + identifier + " does not have a file specified");
				continue;
			}
			
			File scriptFile = new File(plugin.getDataFolder() + File.separator + "javascripts", config.getString(identifier + ".file"));
			
			if (!scriptFile.exists()) {
				plugin.getLogger().info(scriptFile.getName() + " does not exist. Creating file...");

				try {
					scriptFile.createNewFile();
					plugin.getLogger().info(scriptFile.getName() + " created! Add your javascript to this file and use /placeholderapi reload to load it!");
				} catch (IOException e) {
					e.printStackTrace();
				}
				continue;
			}
			
			String script = getContents(scriptFile);
			
			if (script == null || script.isEmpty()) {
				plugin.getLogger().warning("File: " + scriptFile.getName() + " for javascript placeholder: " + identifier + " is empty");
				continue;
			}
			
			ScriptEngine engine = null;
			
			if (!config.contains(identifier + ".engine")) {
				engine = ex.getGlobalEngine();
			} else {
				try {
					engine = new ScriptEngineManager().getEngineByName(config.getString(identifier + ".engine", "nashorn"));
				} catch (NullPointerException e) {
					plugin.getLogger().warning("ScriptEngine type for javascript placeholder: " + identifier + " is invalid! Defaulting to global");
					engine = ex.getGlobalEngine();
				}	
			}
			
			if (engine == null) {
				plugin.getLogger().warning("Failed to set ScriptEngine for javascript placeholder: " + identifier);
				continue;
			}
			
			JavascriptPlaceholder pl = new JavascriptPlaceholder(engine, identifier, script);

			boolean added = ex.addJavascriptPlaceholder(pl);
			
			if (added) {
				
				if (pl.loadData()) {
					plugin.getLogger().info("Loaded data for javascript placeholder: " + identifier);
				}
				plugin.getLogger().info("%javascript_" + identifier + "% has been loaded!");
			} else {
				plugin.getLogger().warning("Javascript  placeholder %javascript_" + identifier + "% is a duplicate!");
			}
		}
		return ex.getJavascriptPlaceholdersAmount();
	}
	
	private String getContents(File f) {
		StringBuilder sb = new StringBuilder();
		
		try {
			Scanner scanner = new Scanner(f);

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				if (line == null || line.isEmpty()) {
					continue;
				}
				
				line = line.trim();
				
				/* temp fix for single line comments
				 * doesnt solve every case though..
				 * lines that start with code and may have a comment afterward still screw stuff up...
				*/
				if (line.startsWith("//")) {
					continue;
				}
				sb.append(line + " ");
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}
}
