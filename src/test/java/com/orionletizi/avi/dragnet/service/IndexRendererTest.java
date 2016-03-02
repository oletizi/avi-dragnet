package com.orionletizi.avi.dragnet.service;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.junit.Test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class IndexRendererTest {

  @Test
  public void test() throws Exception {
    final IndexRenderer renderer = new IndexRenderer();

    final SyndFeedInput input = new SyndFeedInput();
    final SyndFeed feed = input.build(new XmlReader(ClassLoader.getSystemResource("rss/sample-feed.xml")));

    final List<SyndFeed> feeds = new ArrayList<>();
    feeds.add(feed);

    StringWriter out = new StringWriter();
    renderer.render(feeds, out);
    System.out.println(out.getBuffer());
  }
}