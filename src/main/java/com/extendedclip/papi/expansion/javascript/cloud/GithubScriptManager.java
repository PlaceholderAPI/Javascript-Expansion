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
package com.extendedclip.papi.expansion.javascript.cloud;

import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GithubScriptManager {

    private JavascriptExpansion expansion;
    private String javascriptsFolder;
    private List<GithubScript> availableScripts;
    private final String MASTER_LIST_URL = "https://raw.githubusercontent.com/PlaceholderAPI/Javascript-Expansion/master/scripts/master_list.json";
    private final Gson GSON = new Gson();

    public GithubScriptManager(JavascriptExpansion expansion) {
        this.expansion = expansion;
        javascriptsFolder = expansion.getPlaceholderAPI().getDataFolder()
                + File.separator
                + "javascripts"
                + File.separator;
    }

    public void clear() {
        availableScripts = null;
    }

    public void fetch() {
        Bukkit.getScheduler().runTaskAsynchronously(expansion.getPlaceholderAPI(), new Runnable() {
            @Override
            public void run() {
                String json = getContents(MASTER_LIST_URL);
                if (json.isEmpty()) {
                    return;
                }
                availableScripts = GSON.fromJson(json, new TypeToken<ArrayList<GithubScript>>() {
                }.getType());
            }
        });
    }

    public void downloadScript(GithubScript script) {
        Bukkit.getScheduler().runTaskAsynchronously(expansion.getPlaceholderAPI(), new Runnable() {
            @Override
            public void run() {
                List<String> contents = read(script.getUrl());
                if (contents == null || contents.isEmpty()) {
                    return;
                }
                File f = new File(javascriptsFolder, script.getName() + ".js");
                try (PrintStream out = new PrintStream(new FileOutputStream(f))) {
                    contents.forEach(l -> out.println(l));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
                Bukkit.getScheduler().runTask(expansion.getPlaceholderAPI(), new Runnable() {
                    @Override
                    public void run() {
                        expansion.getConfig().load().set(script.getName() + ".file", script.getName() + ".js");
                        expansion.getConfig().save();
                    }
                });
            }
        });
    }

    private String getContents(String url) {
        return String.join("", read(url));
    }

    private List<String> read(String url) {

        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new URL(url).openStream()))) {

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                lines.add(inputLine);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lines;
    }

    public List<GithubScript> getAvailableScripts() {
        return availableScripts;
    }

    public GithubScript getScript(String name) {
        if (availableScripts == null) return null;
        return availableScripts.stream().filter(s -> {
            return s.getName().equalsIgnoreCase(name);
        }).findFirst().orElse(null);
    }
}
