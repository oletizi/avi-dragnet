package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.List;

public class And extends PredicateFilter {

  public And() {
    super();
  }

  public And(final List<FeedFilter> predicates) {
    super(predicates);
  }

  @Override
  public SyndEntry filter(final SyndEntry entry) {
    SyndEntry rv = entry;
    for (FeedFilter predicate : getPredicates()) {
      rv = predicate.filter(entry);
      if (rv == null) {
        return null;
      }
    }
    return rv;
  }
}
