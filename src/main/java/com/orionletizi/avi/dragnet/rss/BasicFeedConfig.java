package com.orionletizi.avi.dragnet.rss;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

public class BasicFeedConfig implements DragnetConfig.FeedConfig {

  @JsonProperty
  private URL feedUrl;

  @JsonProperty
  private String name;

  @JsonProperty
  private boolean shouldWrite;

  @SuppressWarnings("unused")
  public BasicFeedConfig() {
    // for Jackson
  }

  public BasicFeedConfig(final URL feedURL, final String name, final boolean shouldWrite) {
    this.feedUrl = feedURL;
    this.name = name;
    this.shouldWrite = shouldWrite;
  }

  @Override
  public URL getFeedUrl() {
    return feedUrl;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean shouldWrite() {
    return shouldWrite;
  }
}
