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

import com.extendedclip.papi.expansion.javascript.ExpansionUtils;
import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import com.extendedclip.papi.expansion.javascript.JavascriptPlaceholdersConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GithubScriptManager {

    private final JavascriptExpansion expansion;
    private final String JAVASCRIPTS_FOLDER;
    private List<GithubScript> availableScripts;
    private final String MASTER_LIST_URL = "https://raw.githubusercontent.com/PlaceholderAPI/Javascript-Expansion/master/scripts/master_list.json";
    private final Gson GSON = new Gson();

    public GithubScriptManager(JavascriptExpansion expansion) {
        this.expansion = expansion;
        JAVASCRIPTS_FOLDER = expansion.getPlaceholderAPI().getDataFolder()
                + File.separator
                + "javascripts"
                + File.separator;
    }

    public void clear() {
        availableScripts = null;
    }

    public void fetch() {
        Bukkit.getScheduler().runTaskAsynchronously(expansion.getPlaceholderAPI(), () -> {
            final String json = getContents(MASTER_LIST_URL);

            if (json.isEmpty()) {
                return;
            }

            availableScripts = GSON.fromJson(json, new TypeToken<ArrayList<GithubScript>>() {}.getType());
        });
    }

    public void downloadScript(GithubScript script) {
        Bukkit.getScheduler().runTaskAsynchronously(expansion.getPlaceholderAPI(), () -> {
            final List<String> contents = read(script.getUrl());

            if (contents.isEmpty()) {
                return;
            }

            try (final PrintStream out = new PrintStream(new FileOutputStream(new File(JAVASCRIPTS_FOLDER, script.getName() + ".js")))) {
                contents.forEach(out::println);
            } catch (FileNotFoundException e) {
                ExpansionUtils.errorLog("An error occurred while downloading " + script.getName(), e);
                return;
            }

            Bukkit.getScheduler().runTask(expansion.getPlaceholderAPI(), () -> {
                JavascriptPlaceholdersConfig config = expansion.getConfig();
                config.load().set(script.getName() + ".file", script.getName() + ".js");
                config.load().set(script.getName() + ".engine", "javascript");

                config.save();
            });
        });
    }

    private String getContents(String url) {
        return String.join("", read(url));
    }

    private List<String> read(final String url) {
        final List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            lines.addAll(reader.lines().filter(Objects::nonNull).collect(Collectors.toList()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lines;
    }

    public List<GithubScript> getAvailableScripts() {
        return availableScripts;
    }

    public GithubScript getScript(final String name) {
        if (availableScripts == null) {
            return null;
        }

        return availableScripts.stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public String getJavascriptsFolder() {
        return JAVASCRIPTS_FOLDER;
    }
}
