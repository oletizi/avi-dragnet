package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RegexFilterTest {

  @Test
  public void testBasics() throws Exception {
    SyndEntry entry = mock(SyndEntry.class);
    when(entry.getTitle()).thenReturn("My title!");

    final List<SyndContent> contents = new ArrayList<>();
    final SyndContent content = mock(SyndContent.class);
    when(content.getValue()).thenReturn("A content entry");
    contents.add(content);
    when(entry.getContents()).thenReturn(contents);

    FeedFilter regexFilter = new RegexFilter("");
    assertEquals(null, regexFilter.filter(entry));

    regexFilter = new RegexFilter(".*");
    assertEquals(entry, regexFilter.filter(entry));

    regexFilter = new RegexFilter("don't match anything");
    assertEquals(null, regexFilter.filter(entry));

    regexFilter = new RegexFilter(".+title.+");
    assertEquals(entry, regexFilter.filter(entry));

    regexFilter = new RegexFilter("^A\\s+content.+");
    assertEquals(entry, regexFilter.filter(entry));
  }
}