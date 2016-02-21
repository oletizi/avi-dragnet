package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.orionletizi.avi.dragnet.rss.filters.vendor.*;
import com.rometools.rome.feed.synd.SyndEntry;

public class DragnetFilter implements FeedFilter {

  private final FeedFilter filter;

  public DragnetFilter() {
    filter = new Or()
        .add(new Avi())
        .add(new AmazonWebServices())
        .add(new APIC())
        .add(new CloudFoundry())
        .add(new Mesos())
        .add(new OpenShift())
        .add(new OpenStack());
  }

  @Override
  public SyndEntry filter(final SyndEntry entry) {
    return filter.filter(entry);
  }
}
