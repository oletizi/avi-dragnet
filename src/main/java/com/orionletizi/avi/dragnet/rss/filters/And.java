package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.List;

public class And implements FeedFilter {

  private List<FeedFilter> predicates;

  public And(final List<FeedFilter> predicates) {

    this.predicates = predicates;
  }

  @Override
  public SyndEntry filter(final SyndEntry entry) {
    SyndEntry rv = entry;
    for (FeedFilter predicate : predicates) {
      rv = predicate.filter(entry);
      if (rv == null) {
        return null;
      }
    }
    return rv;
  }
}
