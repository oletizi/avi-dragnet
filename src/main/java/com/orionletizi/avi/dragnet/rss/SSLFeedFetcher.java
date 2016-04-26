package com.orionletizi.avi.dragnet.rss;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class SSLFeedFetcher {
  private final SyndFeedInput feedReader;
  private URL url;

  public SSLFeedFetcher(final URL url) {
    if (! url.getProtocol().startsWith("https")) {
      throw new IllegalArgumentException("Feed url must be https: " + url);
    }
    this.url = url;
    feedReader = new SyndFeedInput();
  }

  public SyndFeed fetch() throws IOException, FeedException {
    final HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    final File feedFile = File.createTempFile("httpsFeed", "xml");
    final OutputStream out = new FileOutputStream(feedFile);
    IOUtils.copy(connection.getInputStream(), out);
    final SyndFeed feed = feedReader.build(feedFile);
    return feed;
  }
}
