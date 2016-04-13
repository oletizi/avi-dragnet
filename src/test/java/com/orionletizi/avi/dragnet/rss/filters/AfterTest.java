package com.orionletizi.avi.dragnet.rss.filters;

import com.rometools.rome.feed.synd.SyndEntry;
import org.junit.Test;

import java.util.Date;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AfterTest {

  @Test
  public void testBasics() throws Exception {
    SyndEntry entry = mock(SyndEntry.class);
    final Date before = new Date();
    Thread.sleep(100);
    final Date date = new Date();
    Thread.sleep(100);
    final Date after = new Date();

    assertTrue(date.after(before));
    assertTrue(after.after(date));

    After filter = new After(() -> date);

    when(entry.getUpdatedDate()).thenReturn(after);
    assertEquals(entry, filter.filter(entry));

    when(entry.getUpdatedDate()).thenReturn(before);
    assertNull(filter.filter(entry));

    when(entry.getUpdatedDate()).thenReturn(before);
    assertNull(filter.filter(entry));

    when(entry.getUpdatedDate()).thenReturn(after);
    assertEquals(entry, filter.filter(entry));
  }

}