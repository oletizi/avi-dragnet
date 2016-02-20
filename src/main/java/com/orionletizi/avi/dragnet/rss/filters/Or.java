package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.List;

public class Or extends PredicateFilter {

  public Or(final List<FeedFilter> predicates) {
    super(predicates);
  }

  public Or() {
    super();
  }

  @Override
  public SyndEntry filter(final SyndEntry entry) {
    SyndEntry rv = null;
    for (FeedFilter predicate : getPredicates()) {
      rv = predicate.filter(entry);
      if (rv != null) {
        break;
      }
    }
    return rv;
  }
}
