package com.orionletizi.avi.dragnet.rss;

import com.orionletizi.util.logging.Logger;
import com.orionletizi.util.logging.LoggerImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FeedPersister {

  //private static final String PERSISTENCE_PATH = "archive";
  private static final Logger logger = LoggerImpl.forClass(FeedPersister.class);
  private final URL feedUrl;
  private final SyndFeedOutput feedWriter;
  private final String feedName;
  private final SyndFeedInput feedReader;
  private final Dedupe dedupe;
  private final FeedFilter archiveFilter;
  private final FeedFilter feedFilter;
  private File workingDir;

  public FeedPersister(final File workingDir,
                       final DragnetConfig.FeedConfig config,
                       final FeedFilter archiveFilter) {
    this.workingDir = workingDir;
    this.archiveFilter = archiveFilter;
    feedUrl = config.getFeedUrl();
    if (feedUrl == null) {
      throw new IllegalArgumentException("Null feed url.");
    }
    feedName = config.getName();
    feedFilter = config.getFilter();
    feedReader = new SyndFeedInput();
    feedWriter = new SyndFeedOutput();
    dedupe = new Dedupe();
  }

  public void fetch() throws IOException {
    try {
      info("Fetching feed: " + feedName + " at " + feedUrl);
      List<SyndEntry> allEntries = new ArrayList<>();
      allEntries.addAll(getPersistedEntries());
      info("starting with " + allEntries.size() + " persisted entries");
      info("fetching live entries from " + feedUrl);
      final SyndFeed feed = feedReader.build(new XmlReader(feedUrl));
      info("found " + feed.getEntries().size() + " persisted entries. Filtering and adding to persisted entries...");
      //allEntries.addAll(feed.getEntries());
      for (final SyndEntry entry : feed.getEntries()) {
        final SyndEntry filtered = feedFilter.filter(entry);
        if (filtered != null) {
          allEntries.add(filtered);
        }
      }
      info("Entries after adding filtered live entries: " + allEntries.size());
      info("Deduping...");
      allEntries = dedupe.dedupe(allEntries);
      info("All entries after dedupe: " + allEntries.size());
      final File publish = getPublishFile();
      feed.setEntries(allEntries);
      info("Writing " + feed.getEntries().size() + " entries to feed file: " + publish);
      feedWriter.output(feed, publish);

    } catch (FeedException e) {
      throw new IOException(e);
    }
  }

  private File getPublishFile() {
    return new File(workingDir, feedName);
  }

  private List<SyndEntry> getPersistedEntries() throws IOException, FeedException {
    final List<SyndEntry> rv = new ArrayList<>();

    final File saved = getPublishFile();
    if (saved.exists()) {
      info("Saved feed: " + saved);
      final SyndFeed feed = feedReader.build(new XmlReader(saved));
      final List<SyndEntry> entries = feed.getEntries();
      info("Found " + entries.size() + " saved entries before filtering with archive filter: " + archiveFilter);
      for (SyndEntry entry : entries) {
        if (archiveFilter.filter(entry) != null) {
          rv.add(entry);
        }
      }
      info("Persisted feed size after archive filter: " + rv.size());
    }
    return rv;
  }

  private void info(final Object message) {
    logger.info("<" + feedName + "> " + message);
  }
}
