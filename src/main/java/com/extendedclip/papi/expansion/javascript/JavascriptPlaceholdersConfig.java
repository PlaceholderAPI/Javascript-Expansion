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
		
		config.options().header("javascript_placeholders.yml"
				+ "\nYou can create custom placeholders which utilize javascript to determine the result of the custom placeholder you create."
				+ "\nYou can specify if the result is based on a boolean or the actual javascript."
				+ "\n"
				+ "\nIf you do not specify a type: the placeholder will default to a boolean type"
				+ "\nA boolean type must contain a true_result: and false_result:"
				+ "\n"
				+ "\nA string type only requires the expression: entry"
				+ "\n"
				+ "\nJavascript placeholders can contain normal placeholders in the expression, true_result, or false_result"
				+ "\nThese placeholders will be parsed to the correct values before the expression is evaluated."
				+ "\n"
				+ "\nYour javascript placeholders will be identified by: %javascript_<identifier>%"
				+ "\n"
				+ "\nJavascript placeholder format:"
				+ "\n"
				+ "\n    BOOLEAN TYPE"
				+ "\n<identifier>:"
				+ "\n  expression: <expression>"
				+ "\n  type: 'boolean'"
				+ "\n  true_result: <result if expression is true>"
				+ "\n  false_result: <result if expression is false>"
				+ "\n"
				+ "\n    STRING TYPE"
				+ "\n<identifier>:"
				+ "\n  expression: <expression>"
				+ "\n  type: 'string'"
				+ "\n"
				+ "\n"
				+ "\n ==== ADVANCED VARIABLES ===="
				+ "\nDO NOT USE THESE VARIABLES UNLESS YOU KNOW WHAT YOU ARE DOING!"
				+ "\n"
				+ "\nYou can access a few Bukkit API classes and methods using certain keywords:"
				+ "\n"
				+ "\nUsing \"BukkitServer\" in your javascript will return Bukkit.getServer()"
				+ "\nYou can use any methods inside of the Server class:"
				+ "\n"
				+ "\nExample: BukkitServer.getBannedPlayers().size().toFixed()"
				+ "\nWill return how many players are banned"
				+ "\n"
				+ "\nThis variable is handy if you want to iterate through all online players.'"
				+ "\n"
				+ "\nUsing \"BukkitPlayer\" in your javascript will return the Player object you are setting placeholders for."
				+ "\nYou can use any methods inside of the Player class:"
				+ "\n"
				+ "\nExample: BukkitPlayer.hasPermission(\"some.permission\")"
				+ "\nWill return if the player has a specific permission"
				+ "\nThis variable is handy if you want to check a players permission node, or access other methods inside of"
				+ "\nthe player class for the specified player."
				+ "\n"
				+ "\nMore advanced variables are coming soon! Only use these variables if you know what you are doing!"
				+ "\n"
				+ "\n =================="
				+ "\n"
				+ "\n"
				+ "\nJavascript placeholder examples:"
				+ "\n"
				+ "\nmillionaire:"
				+ "\n  expression: '%vaulteco_balance% >= 1000000'"
				+ "\n  type: 'boolean'"
				+ "\n  true_result: '&aMillionaire'"
				+ "\n  false_result: '&cbroke'"
				+ "\nis_staff:"
				+ "\n  expression: '\"%vault_group%\" == \"Moderator\" || \"%vault_group%\" == \"Admin\" || \"%vault_group%\" == \"Owner\"'"
				+ "\n  type: 'boolean'"
				+ "\n  true_result: '&bStaff'"
				+ "\n  false_result: '&ePlayer'"
				+ "\nhealth_rounded:"
				+ "\n  expression: 'Math.round(%player_health%)'"
				+ "\n  type: 'string'"
				+ "\nstaff_online:"
				+ "\n  expression: 'var i = 0; for (var p in BukkitServer.getOnlinePlayers()) { if (BukkitServer.getOnlinePlayers()[p].hasPermission(\"staff.online\")) {i = i+1;};} i.toFixed();'"
				+ "\n  type: 'string'"
				+ "\n"
				+ "\n"
				+ "\nYou can optionally specify a file that the javascript expression will be loaded from if your expression"
				+ "\nis bigger than 1 line. To specify javascript be loaded from a file, follow this format:"
				+ "\n"
				+ "\nis_op:"
				+ "\n  expression: 'file: is_op.js'"
				+ "\n  type: 'string'"
				+ "\n"
				+ "\nThe following placeholder will attempt to load javascript from the /plugins/PlaceholderAPI/javascripts/is_op.js file"
				+ "\nif the folder/file exists. If the folder/file does not exist it will be created."
				+ "\nYou must specify the file extension with the file name. Any file extension is accepted."
				+ "\n");
		

		if (config.getKeys(false) == null || config.getKeys(false).isEmpty()) {
			config.set("millionaire.expression", "%vaulteco_balance% >= 1000000");
			config.set("millionaire.type", "boolean");
			config.set("millionaire.true_result", "&aMillionaire");
			config.set("millionaire.false_result", "&cbroke");
			config.set("is_staff.expression", "\"%vault_group%\" == \"Moderator\" || \"%vault_group%\" == \"Admin\" || \"%vault_group%\" == \"Owner\"");
			config.set("is_staff.type", "boolean");
			config.set("is_staff.true_result", "&bStaff");
			config.set("is_staff.false_result", "&ePlayer");
			config.set("health_rounded.expression", "Math.round(%player_health%)");
			config.set("health_rounded.type", "string");
			config.set("staff_online", "var i = 0; for (var p in BukkitServer.getOnlinePlayers()) { if (BukkitServer.getOnlinePlayers()[p].hasPermission(\"staff.online\")) {i = i+1;};} i.toFixed();");
			config.set("staff_online.type", "string");
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
			
			JavascriptReturnType type = JavascriptReturnType.BOOLEAN;
			
			if (config.contains(identifier + ".type")) {
				
				String t = config.getString(identifier + ".type");
				
				if (JavascriptReturnType.getType(t) != null) {
					type = JavascriptReturnType.getType(t);
				}
			}
			
			if (!isValid(identifier, type)) {
				plugin.getLogger().warning("Javascript " + type.getType() + " placeholder " + identifier + " is invalid!");
				continue;
			}
			
			JavascriptPlaceholder pl = null;
			
			String expression = config.getString(identifier + ".expression");

			if (expression.startsWith("file: ")) {
				
				expression = expression.replace("file: ", "");
				
				File f = new File(plugin.getDataFolder() + File.separator + "javascripts", expression);
				
				expression = loadFileExpression(f);
				
				if (expression == null || expression.isEmpty()) {
					plugin.getLogger().info("javascript expression from file: " + f.getName() + " is empty!");
					continue;
				} else {
				
					plugin.getLogger().info("javascript expression loaded from file: " + f.getName());
				}
			}
			
			if (type == JavascriptReturnType.BOOLEAN) {
				
				String trueResult = config.getString(identifier + ".true_result");
				
				String falseResult = config.getString(identifier + ".false_result");
				
				pl = new JavascriptPlaceholder(identifier, type, expression, trueResult, falseResult);
			} else {
				
				pl = new JavascriptPlaceholder(identifier, type, expression, null, null);
			}
			
			boolean added = ex.addJavascriptPlaceholder(pl);
			
			if (added) {
				plugin.getLogger().info("Javascript " + type.getType() + " placeholder %javascript_" + identifier + "% has been loaded!");
				if (pl.loadData()) {
					plugin.getLogger().info("Loaded data for: %javascript_" + identifier + "%");
				}
			} else {
				plugin.getLogger().warning("Javascript " + type.getType() + " placeholder %javascript_" + identifier + "% is a duplicate!");
			}
		}
		return ex.getJavascriptPlaceholdersAmount();
	}
	
	private String loadFileExpression(File f) {

		StringBuilder sb = new StringBuilder();
		
		try {

			if (!f.exists()) {
				
				plugin.getLogger().warning(f.getName() + " does not exist!");
				
				try {
					
					f.createNewFile();
					
					plugin.getLogger().info(f.getName() + " created! Add your javascript expression to this file and use /placeholderapi reload to load it!");
					
				} catch(IOException e) {
					
					e.printStackTrace();
				}
				
				return null;
			}
			
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
	
	private boolean isValid(String identifier, JavascriptReturnType type) {
		if (type == JavascriptReturnType.BOOLEAN) {
			return config.isString(identifier + ".expression") 
				&& config.isString(identifier + ".true_result") 
				&& config.isString(identifier + ".false_result");
		} else {
			return config.isString(identifier + ".expression");
		}
	}
}
