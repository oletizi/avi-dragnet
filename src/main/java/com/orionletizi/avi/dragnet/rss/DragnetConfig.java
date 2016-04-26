package com.orionletizi.avi.dragnet.rss;

import java.io.File;
import java.net.URL;

public interface DragnetConfig {


  FeedConfig[] getFeeds();

  File getOutputFile();

  public interface FeedConfig {

    URL getFeedUrl();

    String getName();

    FeedFilter getFilter();

    long getRefreshPeriodMinutes();
  }

}
