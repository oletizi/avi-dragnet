package com.orionletizi.avi.dragnet.rss;

import java.io.File;

public class BasicDragnetConfig implements DragnetConfig {

  private final FeedConfig[] feeds;
  private File writeRoot;
  private final File rawOutputFile;
  private final File filteredOutputFile;

  public BasicDragnetConfig(final FeedConfig[] feeds, final File writeRoot, final File rawOutputFile, final File filteredOutputFile) {
    this.feeds = feeds;
    this.writeRoot = writeRoot;
    this.rawOutputFile = rawOutputFile;
    this.filteredOutputFile = filteredOutputFile;
  }

  @Override
  public FeedConfig[] getFeeds() {
    return feeds;
  }

  @Override
  public File getFilteredOutputFile() {
    return filteredOutputFile;
  }

  @Override
  public File getRawOutputFile() {
    return rawOutputFile;
  }

  @Override
  public File getWriteRoot() {
    return writeRoot;
  }
}
