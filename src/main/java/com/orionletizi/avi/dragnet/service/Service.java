package com.orionletizi.avi.dragnet.service;

import com.orionletizi.avi.dragnet.rss.Aggregator;
import com.orionletizi.avi.dragnet.rss.BasicFeedConfig;
import com.orionletizi.avi.dragnet.rss.DragnetConfig;
import com.orionletizi.avi.dragnet.rss.FeedPersister;
import com.orionletizi.avi.dragnet.rss.filters.After;
import com.orionletizi.avi.dragnet.rss.filters.GoogleGroupsDateScraper;
import com.orionletizi.util.logging.LoggerImpl;
import com.rometools.rome.io.FeedException;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.SimpleWebServer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Service {
  private static final long MAX_DAYS = 1000 * 60 * 60 * 24 * 3;

  private static final After MAX_AGE_FILTER = new After(() -> new Date(System.currentTimeMillis() - MAX_DAYS));
  private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private static final long REFRESH_PERIOD_IN_MINUTES = 1;

  private final Aggregator aggregator;
  private final File log;
  private final File errorLog;
  private final ScheduledThreadPoolExecutor executor;
  private ServiceConfig config;

  private Service(final ServiceConfig config) throws IOException, FeedException {
    this.config = config;
    this.log = new File(config.getWebRoot(), "log.txt");
    this.errorLog = new File(config.getWebRoot(), "error-log.txt");

    executor = new ScheduledThreadPoolExecutor(config.getFeedConfigs().length + 3);

    // set up index page renderer
    info("Scheduling the index writer...");
    executor.scheduleWithFixedDelay(new IndexWriter(this, new IndexRenderer()), 0, 10, TimeUnit.SECONDS);

    // Set up a persisting feed fetchers for each feed.
    // This periodically fetches new feed entries and adds them to the local feed file.
    // It also purges older entries to ensure the feed file doesn't grow without bounds.
    // There are two persisters per feed:
    // 1. The raw feed contents
    // 2. The filtered feed contents
    final DragnetConfig.FeedConfig[] dragnetFeeds = new DragnetConfig.FeedConfig[config.getFeedConfigs().length];
    for (int i = 0; i < config.getFeedConfigs().length; i++) {
      final DragnetConfig.FeedConfig feedConfig = config.getFeedConfigs()[i];
      final String feedName = feedConfig.getName();
      final String filteredFeedName = getFilteredFeedName(feedName);

      info("scheduling feed persister for: " + feedName);
      final BasicFeedConfig unfilteredConfig = new BasicFeedConfig(
          feedConfig.getFeedUrl(),
          entry -> entry,
          feedName,
          feedConfig.getRefreshPeriodMinutes(),
          true);
      scheduleFeedPersister(unfilteredConfig);

      info("scheduling a filtering feed persistor for: " + feedName);
      final BasicFeedConfig filteredConfig = new BasicFeedConfig(
          new File(config.getWebRoot(), feedName).toURI().toURL(),
          feedConfig.getFilter(),
          filteredFeedName,
          feedConfig.getRefreshPeriodMinutes(),
          true
      );
      scheduleFeedPersister(filteredConfig);

      // Set up the feed configs for the aggregator (it needs to read from the local persisted feeds
      dragnetFeeds[i] = new BasicFeedConfig(
          new File(config.getWebRoot(), filteredFeedName).toURI().toURL(),
          event -> event,//config.getFilter(),
          filteredFeedName,
          feedConfig.getRefreshPeriodMinutes(),
          false);
    }

    // Set up the aggregator to aggregate the persisted, filtered feeds
    this.aggregator = new Aggregator(new DragnetConfig() {
      @Override
      public DragnetConfig.FeedConfig[] getFeeds() {
        return dragnetFeeds;
      }

      @Override
      public File getOutputFile() {
        return new File(config.getWebRoot(), "filtered.xml");
      }

    });

    // Schedule aggregator to aggregate all of the separate feed files into a single feed file
    executor.scheduleWithFixedDelay(() -> {
      try {
        log("Refreshing aggregator feed...");
        aggregator.aggregate();
        log("Done refreshing aggregator feed.");
      } catch (Throwable e) {
        handleError(e);
      }
    }, 0, REFRESH_PERIOD_IN_MINUTES, TimeUnit.MINUTES);
  }

  private void scheduleFeedPersister(final DragnetConfig.FeedConfig feedConfig) {
    final Runnable task = () -> {
      try {
        info("Fetching and persisting " + feedConfig.getFeedUrl() + "...");
        new FeedPersister(config.getWebRoot(), feedConfig, MAX_AGE_FILTER).fetch();
        info("Done fetching and persisting " + feedConfig.getFeedUrl());
      } catch (IOException e) {
        Service.this.log(errorLog, "Error fetching feed: " + feedConfig.getFeedUrl() + "; exception: " + e);
      }
    };
    task.run();
    executor.scheduleWithFixedDelay(task, 0, feedConfig.getRefreshPeriodMinutes(), TimeUnit.MINUTES);
  }

  DragnetConfig.FeedConfig[] getFeedConfigs() {
    return config.getFeedConfigs();
  }

  String getFilteredFeedName(final String name) {
    final String extension = FilenameUtils.getExtension(name);
    return name.replace("." + extension, "-filtered." + extension);
  }

  private void info(final String s) {
    log(log, s);
    //System.out.println(s);
  }

  void handleError(final Throwable e) {
    log(errorLog, e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
  }

  private void log(Object o) {
    log(log, o);
  }

  private void log(File log, Object o) {
    final String msg = df.format(new Date()) + " - " + Service.class.getSimpleName() + ": " + o + "\n";

    try {
      final BufferedWriter out = new BufferedWriter(new FileWriter(log, true));
      out.append(msg);
      out.flush();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void start() throws IOException, FeedException {

    final InetAddress inetAddress = guessAddress();
    if (inetAddress == null) {
      handleError(new IOException("Unable to find a valid inet address."));
      return;
    }


    final SimpleWebServer server = new SimpleWebServer(inetAddress.getHostAddress(), config.getPort(), config.getWebRoot(), true);
    log("Starting webserver at address: " + inetAddress.getHostAddress());
    //ServerRunner.executeInstance(server);
    server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
  }

  private InetAddress guessAddress() throws IOException {
    final InetAddressValidator validator = InetAddressValidator.getInstance();
    final Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
    while (ifaces.hasMoreElements()) {
      final NetworkInterface iface = ifaces.nextElement();
      final Enumeration<InetAddress> inetAddresses = iface.getInetAddresses();
      while (inetAddresses.hasMoreElements()) {
        final InetAddress inetAddress = inetAddresses.nextElement();
        if (validator.isValidInet4Address(inetAddress.getHostAddress())) {
          if (inetAddress.isReachable(5) && !inetAddress.isLoopbackAddress()) {
            return inetAddress;
          }
        }
      }
    }
    return null;
  }

  public static void main(String[] args) throws InterruptedException, IOException, FeedException {
    //LoggerImpl.turnOff(Aggregator.class);
    //LoggerImpl.turnOff(FeedPersister.class);
    LoggerImpl.turnOff(GoogleGroupsDateScraper.class);
    File webroot = new File("/tmp");
    if (args.length > 0) {
      webroot = new File(args[0]);
    }
    final Service service = new Service(new ServiceConfig(8080, webroot));
    service.start();
  }

  File getWebroot() {
    return config.getWebRoot();
  }

}
