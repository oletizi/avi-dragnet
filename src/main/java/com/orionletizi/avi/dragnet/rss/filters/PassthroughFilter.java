package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndEntry;

public class PassthroughFilter implements FeedFilter {
  @Override
  public SyndEntry filter(final SyndEntry entry) {
    return entry;
  }
}