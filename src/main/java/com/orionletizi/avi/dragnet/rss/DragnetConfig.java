package com.orionletizi.avi.dragnet.rss;

import java.io.File;
import java.net.URL;

public interface DragnetConfig {


  FeedConfig[] getFeeds();

  File getFilteredOutputFile();

  File getRawOutputFile();

  File getWriteRoot();

  public interface FeedConfig {

    URL getFeedUrl();

    String getName();

    boolean shouldWrite();
  }

}
