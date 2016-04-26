package com.orionletizi.avi.dragnet.service;

import com.orionletizi.avi.dragnet.rss.DragnetConfig;
import com.orionletizi.util.logging.Logger;
import com.orionletizi.util.logging.LoggerImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class IndexWriter implements Runnable {
  private static final Logger logger = LoggerImpl.forClass(IndexWriter.class);
  private static final SyndFeedInput feedReader = new SyndFeedInput();
  private final File webroot;
  private final DragnetConfig.FeedConfig[] feedConfigs;
  private Service service;
  private IndexRenderer indexRenderer;

  IndexWriter(final Service service, final IndexRenderer indexRenderer) {
    this.service = service;
    this.indexRenderer = indexRenderer;
    this.webroot = service.getWebroot();
    this.feedConfigs = service.getFeedConfigs();
  }

  @Override
  public void run() {
    try {
      info("fetching " + feedConfigs.length + " feeds to write index...");
      final List<FeedDescriptor> feeds = new ArrayList<>();
      for (DragnetConfig.FeedConfig config : feedConfigs) {
        final File feedFile = new File(webroot, config.getName());
        FeedDescriptor descriptor = null;
        if (feedFile.exists()) {
          info("Fetching feed from file: " + feedFile);
          final SyndFeed feed = feedReader.build(new XmlReader(feedFile));
          int filteredSize = 0;
          try {
            filteredSize = feedReader.build(new XmlReader(new File(webroot, service.getFilteredFeedName(config.getName())))).getEntries().size();
          } catch (FeedException | IOException e) {
            service.handleError(e);
          }
          info("Adding feed to index: " + feed.getTitle());
          descriptor = new FeedDescriptor(feed.getTitle(),
              feed.getDescription(),
              feed.getEntries().size(),
              feed.getLink(),
              config.getName(),
              service.getFilteredFeedName(config.getName()),
              filteredSize,
            feed.getPublishedDate() == null ? "Feed doesn't specify" : feed.getPublishedDate().toString());
        } else {
          info("Feed file doesn't exist: " + feedFile);
          descriptor = new FeedDescriptor(config.getName(), "This feed hasn't been successfully read yet.", 0, config.getFeedUrl().toString(), "", "", 0, "Never");
        }
        feeds.add(descriptor);
      }
      final File outfile = new File(webroot, "index.html");
      info("Writing to " + outfile);
      final FileWriter out = new FileWriter(outfile);
      indexRenderer.render(feeds, out);
      out.close();
    } catch (Exception e) {
      service.handleError(e);
    }
  }

  private void info(final Object msg) {
    logger.info(msg);
  }

}
