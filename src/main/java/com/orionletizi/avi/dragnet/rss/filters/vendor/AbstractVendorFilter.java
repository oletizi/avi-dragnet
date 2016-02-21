package com.orionletizi.avi.dragnet.rss.filters.vendor;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.orionletizi.avi.dragnet.rss.filters.RegexFilter;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.regex.Pattern;

public abstract class AbstractVendorFilter implements FeedFilter {

  private FeedFilter filter;

  protected void setFilter(final FeedFilter filter) {
    this.filter = filter;
  }

  protected RegexFilter pattern(final String pattern) {
    return new RegexFilter(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
  }

  @Override
  public SyndEntry filter(final SyndEntry entry) {
    return filter.filter(entry);
  }
}
