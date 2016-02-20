package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;

import java.util.ArrayList;
import java.util.List;

public abstract class PredicateFilter implements FeedFilter {

  private List<FeedFilter> predicates;

  public PredicateFilter(final List<FeedFilter> predicates) {

    this.predicates = predicates;
  }

  public PredicateFilter() {
    this(new ArrayList<>());
  }

  public PredicateFilter add(final FeedFilter predicate) {
    this.predicates.add(predicate);
    return this;
  }

  protected List<FeedFilter> getPredicates() {
    return predicates;
  }
}
