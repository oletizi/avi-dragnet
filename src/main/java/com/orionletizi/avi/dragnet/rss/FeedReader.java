package com.orionletizi.avi.dragnet.rss;

import com.orionletizi.avi.dragnet.rss.filters.And;
import com.orionletizi.avi.dragnet.rss.filters.DragnetFilter;
import com.orionletizi.avi.dragnet.rss.filters.Or;
import com.orionletizi.avi.dragnet.rss.filters.RegexFilter;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
    System.out.print(reader.read(filter));
  }

  private static FeedFilter openshift() {
    return new And()
        .add(pattern("open\\s*shift"))
        .add(new Or()
            .add(pattern("load\\s*balanc"))
            .add(pattern("f5")));
  }

  private static FeedFilter cloudfoundry() {
    return new And()
        .add(pattern("cloud\\s*foundry"))
        .add(new Or()
            .add(pattern("load\\s*balanc"))
            .add(pattern("f5")));
  }

  private static FeedFilter aws() {
    return new And()
        .add(new Or()
            .add(pattern("aws"))
            .add(pattern("amazon\\s*web\\s*services")))
        .add(new Or()
            .add(pattern("elb"))
            .add(pattern("elastic\\s+load\\s*balanc"))
            .add(pattern("f5")));
  }

  private static FeedFilter apic() {
//        text.match(/apic/gi)
//        && (
//               text.match(/load\s*balanc/gi)
//            || text.match(/f5/gi)
//            || text.match(/citrix/gi)
//            || text.match(/a10/gi)
//           )
    return new And()
        .add(pattern("apic"))
        .add(new Or()
            .add(pattern("load\\s*balanc"))
            .add(pattern("f5"))
            .add(pattern("citrix"))
            .add(pattern("a10")));
  }

  private static FeedFilter mesos() {
//        text.match(/mesos/gi)
//        && (
//               text.match(/load\s*balanc/gi)
//            || text.match(/service\s+discovery/gi)
//           )
    return new And()
        .add(pattern("mesos"))
        .add(new Or()
            .add(pattern("load\\s*balanc"))
            .add(pattern("service\\s+discovery")));
  }

  private static FeedFilter openstack() {
    //       (text.match(/open\s*stack/gi)
//        &&
//          (   text.match(/LBaaS/gi)
//           || text.match(/Octavia/gi)
//           || text.match(/f5/gi)
//           || text.match(/citrix/gi)
//           || text.match(/a10/gi)
//           || text.match(/radware/gi)
//          )
//       )
    final And and = new And(new ArrayList<>());
    and.add(pattern("open\\s*stack"))
        .add(new Or()
            .add(pattern("LBaaS"))
            .add(pattern("Octavia"))
            .add(pattern("f5"))
            .add(pattern("a10"))
            .add(pattern("radware")));
    return and;
  }

  private static FeedFilter pattern(String regex) {
    return new RegexFilter(Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE));
  }

  private static String usage() {
    final StringBuilder out = new StringBuilder();
    out.append("FeedReader -- An RSS feed reader with filters.\n\n");
    out.append("Usage:\n\n");
    out.append("\t java " + FeedReader.class.getName() + " <feed url>\n");
    return out.toString();
  }
}
