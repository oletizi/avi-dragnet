package com.orionletizi.avi.dragnet.service;

import com.orionletizi.avi.dragnet.rss.BasicFeedConfig;
import com.orionletizi.avi.dragnet.rss.DragnetConfig;
import com.orionletizi.avi.dragnet.rss.filters.DragnetFilter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

class ServiceConfig {
  private final static String DZONE = "http://feeds.dzone.com/home";
  private final static String INFOQ = "http://www.infoq.com/feed?token=s8sWhq8NCl1T2XMizaXG4rD3eZujOkQj";
//  private final static String OREILLEY_RADAR = "http://feeds.feedburner.com/oreilly/radar/atom";
//  private final static String OREILLEY_FORUMS = "http://forums.oreilly.com/rss/forums/10-oreilly-forums/";
  private final static String SERVER_FAULT = "http://serverfault.com/feeds";
  private final static String STACK_OVERFLOW = "http://stackoverflow.com/feeds/";
//  private final static String OPEN_STACK = "https://ask.openstack.org/en/feeds/rss/";
  private final DragnetConfig.FeedConfig[] feedConfigs;
  private final int port;
  private final File webRoot;

  ServiceConfig(final int port, final File webRoot) {
    this.port = port;
    this.webRoot = webRoot;
    try {
      final DragnetFilter dragnetFilter = new DragnetFilter();
      feedConfigs = new DragnetConfig.FeedConfig[]{
          new BasicFeedConfig(new URL(STACK_OVERFLOW), dragnetFilter, "stack-overflow.xml", 1, true),
          new BasicFeedConfig(new URL(SERVER_FAULT), dragnetFilter, "server-fault.xml", 1, true),
          new BasicFeedConfig(new URL(DZONE), dragnetFilter, "dzone.xml", 5, true),
          new BasicFeedConfig(new URL(INFOQ), dragnetFilter, "infoq.xml", 5, true),
          //        new BasicFeedConfig(newURL(OREILLEY_RADAR), dragnetFilter, "oreilley-radar.xml", 5, true),
          //        new BasicFeedConfig(newURL(OREILLEY_FORUMS), dragnetFilter, "oreilley-forums.xml", 5, true)
          //new BasicFeedConfig(new URL(OPEN_STACK), dragnetFilter, "openstack.xml", 1, true)
      };
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  DragnetConfig.FeedConfig[] getFeedConfigs() {
    return Arrays.copyOf(feedConfigs, feedConfigs.length);
  }

  int getPort() {
    return port;
  }

  File getWebRoot() {
    return webRoot;
  }
}
