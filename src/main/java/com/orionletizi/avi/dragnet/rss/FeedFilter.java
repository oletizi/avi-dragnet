package com.orionletizi.avi.dragnet.rss;

import com.rometools.rome.feed.synd.SyndEntry;

public interface FeedFilter {
  SyndEntry filter(final SyndEntry entry);
}
