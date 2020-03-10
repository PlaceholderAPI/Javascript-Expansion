package com.extendedclip.papi.expansion.javascript.cloud;

import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;

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
    return availableScripts.stream().filter(s -> {return s.getName().equalsIgnoreCase(name);}).findFirst().orElse(null);
  }
}
