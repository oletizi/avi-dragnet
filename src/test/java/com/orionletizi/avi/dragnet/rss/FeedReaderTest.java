package com.orionletizi.avi.dragnet.rss;

import com.orionletizi.avi.dragnet.rss.filters.DragnetFilter;
import com.rometools.rome.feed.synd.SyndEntry;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FeedReaderTest {

  private URL sampleFeed;
  private FeedReader reader;

  @Before
  public void before() throws Exception {
    sampleFeed = ClassLoader.getSystemResource("rss/sample-feed.xml");
    reader = new FeedReader(sampleFeed);
  }

  @Test
  public void testBasics() throws Exception {
    // test filtering
    assertEquals(20, reader.read(entry -> entry).size());
    assertEquals(0, reader.read(entry -> null).size());
  }

  @Test
  public void testDragnet() throws Exception {
    final DragnetFilter filter = new DragnetFilter();
    final List<SyndEntry> filtered = reader.read(filter);
    assertEquals(2, filtered.size());
  }

  @Test
  public void testMain() throws Exception {
    FeedReader.main(new String[]{sampleFeed.toString()});
  }
}