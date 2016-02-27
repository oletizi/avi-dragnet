package com.orionletizi.avi.dragnet.rss.filters;

import org.junit.Test;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class GoogleGroupsDateScraperTestIT {

  @Test
  public void testBasics() throws Exception {
    final Duration timeout = Duration.ofMinutes(1);
    final GoogleGroupsDateScraper scraper = new GoogleGroupsDateScraper(timeout);
    String url = "https://groups.google.com/forum/#!topic/cloud-computing/I556wc0FB9U";
    scraper.scrape(url);
    final List<Date> dates = scraper.getDates();
    System.out.println("FOUND THESE DATES: " + dates);
    assertEquals(4, dates.size());
  }
}