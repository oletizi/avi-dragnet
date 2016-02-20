package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.List;

public class Or implements FeedFilter {
  private List<FeedFilter> predicates;

  public Or(final List<FeedFilter> predicates) {
    this.predicates = predicates;
  }

  @Override
  public SyndEntry filter(final SyndEntry entry) {
    SyndEntry rv = null;
    for (FeedFilter predicate : predicates) {
      rv = predicate.filter(entry);
      if (rv != null) {
        break;
      }
    }
    return rv;
  }
}
