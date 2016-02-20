package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndEntry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class AndTest {

  @Test
  public void testBasics() throws Exception {
    final List<FeedFilter> predicates = new ArrayList<>();

    predicates.add(entry -> entry);

    And and = new And(predicates);

    final SyndEntry entry = mock(SyndEntry.class);
    assertEquals(entry, and.filter(entry));

    predicates.add(entry1 -> null);

    and = new And(predicates);
    assertEquals(null, and.filter(entry));
  }


}