package com.orionletizi.avi.dragnet.service;

public class FeedDescriptor {
  private final String name;
  private final String description;
  private final int size;
  private final String link;
  private final String localRawFeedUrl;
  private final String localFilteredFeedUrl;
  private final int filteredSize;
  private final String lastUpdated;

  public FeedDescriptor(final String name, final String description, int size, String link, String localRawFeedUrl, String localFilteredFeedUrl, int filteredSize, String lastUpdated) {

    this.name = name;
    this.description = description;
    this.size = size;
    this.link = link;
    this.localRawFeedUrl = localRawFeedUrl;
    this.localFilteredFeedUrl = localFilteredFeedUrl;
    this.filteredSize = filteredSize;
    this.lastUpdated = lastUpdated;
  }

  public String getName() {
    return name;
  }

  public int getSize() {
    return size;
  }

  public String getLink() {
    return link;
  }

  public String getLocalRawFeedUrl() {
    return localRawFeedUrl;
  }

  public String getLocalFilteredFeedUrl() {
    return localFilteredFeedUrl;
  }

  public int getFilteredSize() {
    return filteredSize;
  }

  public String getLastUpdated() {
    return lastUpdated;
  }

  public String getDescription() {
    return description;
  }
}
