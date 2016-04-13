package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.Date;

public class After implements FeedFilter {
  private DateSource dateSource;

  public After(final DateSource dateSource) {
    this.dateSource = dateSource;
  }

  @Override
  public SyndEntry filter(final SyndEntry entry) {
    final Date updatedDate = entry.getUpdatedDate();
    return (isAfter(entry.getPublishedDate()) || isAfter(entry.getUpdatedDate())) ? entry : null;
  }

  private boolean isAfter(final Date entryDate) {
    return entryDate != null && entryDate.after(dateSource.getDate());
  }

  public static interface DateSource {
    Date getDate();
  }
}
