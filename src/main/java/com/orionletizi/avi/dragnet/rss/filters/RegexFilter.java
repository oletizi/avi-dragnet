package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.regex.Pattern;

public class RegexFilter implements FeedFilter {

  private final Pattern pattern;

  public RegexFilter(final String regex) {
    this(Pattern.compile(regex));
  }

  public RegexFilter(final Pattern pattern) {
    this.pattern = pattern;
  }

  @Override
  public SyndEntry filter(final SyndEntry entry) {
    if (entry.getTitle() != null) {
      final boolean matches = pattern.matcher(entry.getTitle()).matches();
      info("CHECK TITLE: matches: " + matches + "; pattern: " + pattern + "; title: " + entry.getTitle());
      if (matches) {
        entry.setTitle(tagValue(entry.getTitle()));
        return entry;
      }
    }
    if (entry.getDescription() != null) {
      final boolean matches = pattern.matcher(entry.getDescription().getValue()).matches();
      info("CHECK DESCRIPTION: matches: " + matches + ", pattern: " + pattern + ", description: " + entry.getDescription().getValue());
      if (matches) {
        final SyndContent description = entry.getDescription();
        description.setValue(tagValue(description.getValue()));
        return entry;
      }
    }
    for (SyndContent content : entry.getContents()) {
      if (content.getValue() != null && pattern.matcher(content.getValue()).matches()) {
        content.setValue(tagValue(content.getValue()));
        return entry;
      }
    }
    return null;
  }

  private String tagValue(final String value) {
    return value + " &lt; DRAGNET MATCH: " + pattern + "&gt";
  }

  private void info(Object msg) {
    //System.out.println(getClass().getSimpleName() + ": " + msg);
  }
}
