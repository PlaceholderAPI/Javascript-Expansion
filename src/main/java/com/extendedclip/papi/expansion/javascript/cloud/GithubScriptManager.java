package com.extendedclip.papi.expansion.javascript.cloud;

import com.extendedclip.papi.expansion.javascript.JavascriptExpansion;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GithubScriptManager {

  private JavascriptExpansion expansion;
  private String path;

  public GithubScriptManager(JavascriptExpansion expansion) {
    this.expansion = expansion;
    path = expansion.getPlaceholderAPI().getDataFolder()
        + File.separator
        + "javascripts"
        + File.separator;
  }

  public void downloadScript(GithubScript script) {
    List<String> contents = read(script.getUrl());
    if (contents == null || contents.isEmpty()) {
      return;
    }
    File f = new File(path, script.getName() + ".js");
    try (PrintStream out = new PrintStream(new FileOutputStream(f))) {
      contents.forEach(l -> out.println(l));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return;
    }
    expansion.getConfig().load().set(script.getName() + ".file", script.getName() +".js");
    expansion.getConfig().save();
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
}
