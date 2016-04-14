package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.orionletizi.util.logging.Logger;
import com.orionletizi.util.logging.LoggerImpl;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.Date;

public class After implements FeedFilter {

  private static final Logger logger = LoggerImpl.forClass(After.class);

  private DateSource dateSource;

  public After(final DateSource dateSource) {
    this.dateSource = dateSource;
  }

  @Override
  public SyndEntry filter(final SyndEntry entry) {
    final Date updatedDate = entry.getUpdatedDate();
    final boolean isAfter = isAfter(entry.getPublishedDate()) || isAfter(entry.getUpdatedDate());
    SyndEntry rv = isAfter ? entry : null;
    info("entry is after: " + isAfter + "; published: " + entry.getPublishedDate() + ", updated: " + entry.getUpdatedDate() + ", returning null: " + (rv == null));
    return rv;
  }

  private boolean isAfter(final Date entryDate) {
    return entryDate != null && entryDate.after(dateSource.getDate());
  }

  public static interface DateSource {
    Date getDate();
  }

  private void info(final Object o) {
    logger.info(o);
  }
}
