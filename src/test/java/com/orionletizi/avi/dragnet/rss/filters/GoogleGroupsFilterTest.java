package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndEntry;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoogleGroupsFilterTest {

  private FeedFilter mockFilter;
  private GoogleGroupsFilter filter;
  private SyndEntry entry;
  private GoogleGroupsDateScraper scraper;
  private Period maxAge;
  private java.util.List<java.util.Date> dates;

  @Before
  public void before() throws Exception {
    dates = new ArrayList<>();
    entry = mock(SyndEntry.class);
    mockFilter = mock(FeedFilter.class);
    when(mockFilter.filter(entry)).thenReturn(entry);
    scraper = mock(GoogleGroupsDateScraper.class);
    when(scraper.getDates()).thenReturn(dates);
    maxAge = Period.ofDays(1);
    filter = new GoogleGroupsFilter(scraper, mockFilter, maxAge);
  }

  @Test
  public void testNoDates() throws Exception {
    assertNull(filter.filter(entry));
  }

  @Test
  public void testAgeCheck() throws Exception {
    final Date tooOld = Date.from(Instant.now().minus(maxAge).minus(maxAge));
    dates.add(tooOld);
    assertNull(filter.filter(entry));

    final Date youngEnough = new Date();
    dates.add(youngEnough);
    assertEquals(entry, filter.filter(entry));
  }

  @Test
  public void testYoungEnough() throws Exception {
    dates.add(new Date());
    assertNotNull(filter.filter(entry));
  }

}