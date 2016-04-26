package com.orionletizi.avi.dragnet.rss;

import java.io.File;

public class BasicDragnetConfig implements DragnetConfig {

  private final FeedConfig[] feeds;
  private FeedConfig outputConfig;
  private File outputFile;

  public BasicDragnetConfig(final FeedConfig[] feeds, final FeedConfig outputConfig, final File outputFile) {
    this.feeds = feeds;
    this.outputConfig = outputConfig;
    this.outputFile = outputFile;
  }

  @Override
  public FeedConfig[] getFeeds() {
    return feeds;
  }

  @Override
  public File getOutputFile() {
    return outputFile;
  }

}
