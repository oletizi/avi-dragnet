package com.orionletizi.avi.dragnet.rss;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class DedupeTest {

  @Test
  public void test() throws Exception {
    final SyndFeedInput input = new SyndFeedInput();
    final SyndFeed feed = input.build(new XmlReader(ClassLoader.getSystemResource("rss/sample-feed.xml")));

    final List<SyndEntry> entries = new ArrayList<>(feed.getEntries());
    entries.addAll(feed.getEntries());

    assertEquals(feed.getEntries().size() * 2, entries.size());

    final Dedupe dedupe = new Dedupe();

    final List<SyndEntry> deduped = dedupe.dedupe(entries);

    assertEquals(feed.getEntries().size(), deduped.size());

  }

}