package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.regex.Pattern;

public class RegexFilter implements FeedFilter {

  private final Pattern pattern;

  public RegexFilter(final String regex) {
    this.pattern = Pattern.compile(regex);
  }

  @Override
  public SyndEntry filter(final SyndEntry entry) {
    if (pattern.matcher(entry.getTitle()).matches()) {
      return entry;
    }
    for (SyndContent content : entry.getContents()) {
      if (pattern.matcher(content.getValue()).matches()) {
        return entry;
      }
    }
    return null;
  }
}
