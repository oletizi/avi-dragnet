package com.orionletizi.avi.dragnet.rss;

import com.orionletizi.avi.dragnet.rss.filters.DragnetFilter;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FeedReader {
  private URL feedUrl;

  public FeedReader(URL feedUrl) {

    this.feedUrl = feedUrl;
  }

  public List<SyndEntry> read(final FeedFilter filter) throws IOException {
    final SyndFeedInput input = new SyndFeedInput();
    try {
      final SyndFeed feed = input.build(new XmlReader(feedUrl));
      final List<SyndEntry> rv = new ArrayList<>();
      for (SyndEntry entry : feed.getEntries()) {
        final SyndEntry filtered = filter.filter(entry);
        if (filtered != null) {
          rv.add(filtered);
        }
      }
      return rv;
    } catch (FeedException e) {
      throw new IOException(e);
    }
  }


  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println(usage());
      return;
    }
    final URL url = new URL(args[0]);
    final FeedReader reader = new FeedReader(url);

    final FeedFilter filter = new DragnetFilter();
    final List<SyndEntry> filtered = reader.read(filter);

    final SyndFeed outfeed = new SyndFeedImpl();
    outfeed.setTitle("Avi Dragnet");
    outfeed.setDescription("Avi Dragnet");
    outfeed.setFeedType("atom_1.0");
    outfeed.setLink("http://some.website.com/");
    outfeed.setEntries(filtered);

    SyndFeedOutput out = new SyndFeedOutput();
    out.output(outfeed, new OutputStreamWriter(System.out));
  }


  private static String usage() {
    final StringBuilder out = new StringBuilder();
    out.append("FeedReader -- An RSS feed reader with filters.\n\n");
    out.append("Usage:\n\n");
    out.append("\t java " + FeedReader.class.getName() + " <feed url>\n");
    return out.toString();
  }
}
