package com.orionletizi.avi.dragnet.rss;

import com.orionletizi.avi.dragnet.rss.filters.After;
import com.orionletizi.avi.dragnet.rss.filters.And;
import com.orionletizi.util.SequenceGenerator;
import com.orionletizi.util.logging.Logger;
import com.orionletizi.util.logging.LoggerImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedPersister {

  public static final long MAX_DAYS = 1000 * 60 * 60 * 24 * 3;


  public static final String PERSISTENCE_PATH = "archive";
  private static final Logger logger = LoggerImpl.forClass(FeedPersister.class);
  private final URL feedUrl;
  private final FeedFilter filter;
  private final SyndFeedOutput feedWriter;
  private final String feedName;
  private final SyndFeedInput feedReader;
  private final Dedupe dedupe;
  private final After after;
  private File workingDir;
  private SequenceGenerator sequenceGenerator;

  public FeedPersister(final File workingDir, final SequenceGenerator sequenceGenerator, final DragnetConfig.FeedConfig config) {
    this.workingDir = workingDir;
    this.sequenceGenerator = sequenceGenerator;
    feedUrl = config.getFeedUrl();
    if (feedUrl == null) {
      throw new IllegalArgumentException("Null feed url.");
    }
    after = new After(() -> new Date(System.currentTimeMillis() - MAX_DAYS));
    filter = new And()
        .add(after)
        .add(config.getFilter() == null ? feed -> feed : config.getFilter());
    feedName = config.getName();
    feedReader = new SyndFeedInput();
    feedWriter = new SyndFeedOutput();
    dedupe = new Dedupe();
  }

  public void fetch() throws IOException {
    try {
      info("Fetching feed: " + feedName);
      List<SyndEntry> filteredEntries = new ArrayList<>();
      filteredEntries.addAll(getArchivedEntries());
      info("starting with " + filteredEntries.size() + " archived entries");
      final SyndFeed feed = feedReader.build(new XmlReader(feedUrl));
      for (SyndEntry entry : feed.getEntries()) {
        info("Filtering with filter: " + filter);
        final SyndEntry filtered = filter.filter(entry);
        if (filtered != null) {
          info("Entry passed filter. Published: " + entry.getPublishedDate() + ", updated: " + entry.getUpdatedDate());
          filteredEntries.add(filtered);
        } else {
          info("Entry did NOT pass filter. Published: " + entry.getPublishedDate() + ", updated: " + entry.getUpdatedDate());
        }
      }
      info("Feed after fetching latest entries: " + filteredEntries.size());
      filteredEntries = dedupe.dedupe(filteredEntries);
      info("Feed after deduplication: " + filteredEntries.size());
      final String extension = FilenameUtils.getExtension(feedName);
      final File archive = new File(workingDir, PERSISTENCE_PATH + "/" + feedName.replace("." + extension, "-" + sequenceGenerator.next() + "." + extension));
      final File publish = new File(workingDir, feedName);
      FileUtils.forceMkdir(archive.getParentFile());
      feed.setEntries(filteredEntries);
      info("Writing feed archive: " + archive);
      feedWriter.output(feed, archive);
      feedWriter.output(feed, publish);

      info("Deleting old archives...");
      final int deleted = deleteArchivesOlderThan(Instant.now().minus(1, ChronoUnit.DAYS));
      info("Deleted " + deleted + " archive files");
    } catch (FeedException e) {
      throw new IOException(e);
    }
  }

  public List<SyndEntry> getArchivedEntries() throws IOException, FeedException {
    final List<SyndEntry> rv = new ArrayList<>();

    final File latestArchive = getLatestArchive();
    info("Latest archive: " + latestArchive);
    if (latestArchive != null) {
      final SyndFeed feed = feedReader.build(new XmlReader(latestArchive));
      final List<SyndEntry> entries = feed.getEntries();
      info("Found " + entries.size() + " archived entries. Filtering for recency with After filter: " + after);
      for (SyndEntry entry : entries) {
        if (after.filter(entry) != null) {
          rv.add(entry);
        }
      }
      info("Archive size after recency filter: " + rv.size());
    }
    return rv;
  }

  public File getLatestArchive() {
    final File[] files = getFeedArchives();
    long lastMod = Long.MIN_VALUE;
    File choice = null;
    if (files != null) {
      for (File file : files) {
        if (file.lastModified() > lastMod) {
          choice = file;
          lastMod = file.lastModified();
        }
      }
    }
    return choice;
  }

  public int deleteArchivesOlderThan(final Instant date) {
    int rv = 0;
    final File[] archives = getFeedArchives();
    if (archives != null) {
      for (File file : archives) {
        if (file.lastModified() < date.toEpochMilli()) {
          final boolean deleted = file.delete();
          if (deleted) {
            rv++;
          } else {
            info("Unable to deleted old archive: " + file);
          }
        }
      }
    }
    return rv;
  }

  private File[] getFeedArchives() {
    final File fl = new File(workingDir, PERSISTENCE_PATH);
    return fl.listFiles(file -> file.isFile() && file.getName().contains(FilenameUtils.getBaseName(feedName)));
  }

  private void info(final Object message) {
    logger.info("<" + feedName + "> " + message);
  }
}
