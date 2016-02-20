package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndEntry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class OrTest {

  @Test
  public void testBasics() throws Exception {
    final List<FeedFilter> predicates = new ArrayList<>();

    predicates.add(entry -> null);

    final SyndEntry entry = mock(SyndEntry.class);
    Or or = new Or(predicates);
    assertNull(or.filter(entry));

    predicates.add(entry1 -> null);
    or = new Or(predicates);
    assertNull(or.filter(entry));


    predicates.add(entry2 -> entry2);
    or = new Or(predicates);
    assertEquals(entry, or.filter(entry));
  }

}