package com.orionletizi.avi.dragnet.rss;

import com.rometools.rome.feed.synd.SyndFeed;
import org.junit.Test;

import java.net.URL;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class SSLFeedFetcherTest {

  @Test
  public void testBasics() throws Exception {
    final SSLFeedFetcher sslFeedFetcher = new SSLFeedFetcher(new URL("https://ask.openstack.org/en/feeds/rss/"));
    final SyndFeed feed = sslFeedFetcher.fetch();
    assertNotNull(feed);

    assertTrue(feed.getEntries().size() > 0);

  }

}