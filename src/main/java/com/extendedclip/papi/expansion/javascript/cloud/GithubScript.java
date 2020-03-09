package com.extendedclip.papi.expansion.javascript.cloud;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum GithubScript {
  HAS_PERMISSION("has_permission",
      "1.0.0",
      "aBooDyy",
      "Check if player have \"permission.test\" permission and send a custom message.",
      "https://raw.githubusercontent.com/PlaceholderAPI/Javascript-Expansion/master/scripts/has_permission.js"),
  ;

  private String name, version, author, description, url;

  private GithubScript(String name, String version, String author, String description, String url) {
    this.name = name;
    this.version = version;
    this.author = author;
    this.description = description;
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public String getAuthor() {
    return author;
  }

  public String getDescription() {
    return description;
  }

  public String getUrl() {
    return url;
  }

  public static List<String> getAllScriptNames() {
    return Arrays.stream(values()).map(GithubScript::getName).collect(Collectors.toList());
  }

  public static GithubScript getScript(String name) {
    return Arrays.stream(values()).filter(s -> { return s.getName().equalsIgnoreCase(name); } ).findFirst().orElse(null);
  }
}
