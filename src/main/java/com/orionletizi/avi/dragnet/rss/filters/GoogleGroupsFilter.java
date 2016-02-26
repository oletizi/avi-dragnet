package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.orionletizi.web.Phantom;
import com.rometools.rome.feed.synd.SyndEntry;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class GoogleGroupsFilter implements FeedFilter {

  private final Phantom phantom;
  private FeedFilter filter;

  public GoogleGroupsFilter(final FeedFilter filter) {
    phantom = new Phantom();
    this.filter = filter;
  }

  @Override
  public SyndEntry filter(final SyndEntry entry) {
    if (youngEnough(entry)) {
      return filter.filter(entry);
    } else {
      return null;
    }
  }

  private boolean youngEnough(final SyndEntry entry) {

    try {
      final String template = IOUtils.toString(ClassLoader.getSystemResource("js/ggroups-date.js").openStream());
      final String urlToken = "${entry.url}";
      System.out.println("URL TOKEN: " + urlToken);
      System.out.println("url    : " + entry.getLink());
      final String js = template.replace(urlToken, entry.getLink());
      //System.out.println("Executing: " + js);
      final int status = phantom.execute(js, System.out, System.err);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

    return true;
  }
}
