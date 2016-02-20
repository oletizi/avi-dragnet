package com.orionletizi.avi.dragnet.rss;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertEquals;

public class FeedReaderTest {

  @Test
  public void testBasics() throws Exception {
    final URL sampleFeed = ClassLoader.getSystemResource("rss/sample-feed.xml");
    final FeedReader reader = new FeedReader(sampleFeed);

    // test filtering
    assertEquals(20, reader.read(entry -> entry).size());
    assertEquals(0, reader.read(entry -> null).size());
  }


}