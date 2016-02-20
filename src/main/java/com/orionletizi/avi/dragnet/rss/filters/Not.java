package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.List;

public class Not extends PredicateFilter {

  public Not() {
    super();
  }

  public Not(final List<FeedFilter> predicates) {
    super(predicates);
  }

  @Override
  public SyndEntry filter(final SyndEntry entry) {
    throw new RuntimeException("Implement Me");
  }
}
