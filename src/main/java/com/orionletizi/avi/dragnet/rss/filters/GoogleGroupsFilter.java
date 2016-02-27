package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndEntry;

import java.io.IOException;
import java.time.Instant;
import java.time.Period;
import java.util.Date;
import java.util.List;

public class GoogleGroupsFilter implements FeedFilter {

  private FeedFilter filter;
  private Period maxAge;
  private GoogleGroupsDateScraper scraper;

  public GoogleGroupsFilter(final GoogleGroupsDateScraper scraper, final FeedFilter filter, final Period maxAge) {
    this.scraper = scraper;
    this.filter = filter;
    this.maxAge = maxAge;
  }

  @Override
  public SyndEntry filter(final SyndEntry entry) {
    try {
      if (youngEnough(entry)) {
        return filter.filter(entry);
      } else {
        return null;
      }
    } catch (IOException | InterruptedException e) {
      // TODO: Add real exception handling
      throw new RuntimeException(e);
    }
  }

  private boolean youngEnough(final SyndEntry entry) throws IOException, InterruptedException {

    scraper.scrape(entry.getLink());
    final List<Date> dates = scraper.getDates();
    System.out.println("Dates: " + dates);
    if (!dates.isEmpty()) {
      final Instant youngest = dates.get(dates.size() - 1).toInstant();
      final Instant now = Instant.now();
      final Instant minInstant = now.minus(maxAge);

      System.out.println("YOUNGEST DATE FOUND: " + youngest);
      System.out.println("MIN DATE           : " + minInstant);

      if (youngest.isAfter(minInstant)) {
        System.out.println("IS YOUNG ENOUGH. RETURNING TRUE.");
        return true;
      }
    }

    return false;
  }

}
