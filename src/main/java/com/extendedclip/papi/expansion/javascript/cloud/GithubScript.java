package com.extendedclip.papi.expansion.javascript.cloud;


public class GithubScript {

  private String name, version, author, description, url;

  public GithubScript(String name, String version, String author, String description, String url) {
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
}
