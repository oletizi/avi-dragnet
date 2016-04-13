package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.orionletizi.avi.dragnet.rss.filters.vendor.*;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.Date;

public class DragnetFilter implements FeedFilter {

  public static final long FIVE_DAYS = 1000 * 60 * 60 * 24 * 5;

  private final FeedFilter filter;

  public DragnetFilter() {
    filter =
        new And()
            .add(new After(() -> new Date(System.currentTimeMillis() - FIVE_DAYS)))
            .add(
                new Or()
                    .add(new Avi())
                    .add(new AmazonWebServices())
                    .add(new APIC())
                    .add(new CloudFoundry())
                    .add(new Mesos())
                    .add(new OpenShift())
                    .add(new OpenStack())
            );
  }

  @Override
  public SyndEntry filter(final SyndEntry entry) {
    return filter.filter(entry);
  }
}
