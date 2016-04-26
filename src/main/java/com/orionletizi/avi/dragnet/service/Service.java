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
import java.util.concurrent.ScheduledExecutorService;
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
  private ServiceConfig config;

  private Service(final ServiceConfig config) throws IOException, FeedException {
    this.config = config;
    this.log = new File(config.getWebRoot(), "log.txt");
    this.errorLog = new File(config.getWebRoot(), "error-log.txt");

    final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(config.getFeedConfigs().length + 3);

    // set up index renderer
    info("Scheduling the index writer...");
    executor.scheduleWithFixedDelay(new IndexWriter(this, new IndexRenderer()), 0, 10, TimeUnit.SECONDS);

    // set up a persisting feed fetcher for each feed
    final DragnetConfig.FeedConfig[] dragnetFeeds = new DragnetConfig.FeedConfig[config.getFeedConfigs().length];
    for (int i = 0; i < config.getFeedConfigs().length; i++) {
      final DragnetConfig.FeedConfig feedConfig = config.getFeedConfigs()[i];
      final String feedName = feedConfig.getName();
      final String filteredFeedName = getFilteredFeedName(feedName);
      final BasicFeedConfig persisterConfig = new BasicFeedConfig(
          feedConfig.getFeedUrl(),
          entry -> entry,
          feedName,
          feedConfig.getRefreshPeriodMinutes(),
          true);

      info("scheduling feed persister for: " + feedName);
      executor.scheduleWithFixedDelay(() -> {
        try {
          new FeedPersister(config.getWebRoot(), persisterConfig, MAX_AGE_FILTER).fetch();
        } catch (IOException e) {
          log(errorLog, "Error fetching feed: " + persisterConfig.getFeedUrl() + "; exception: " + e);
        }
      }, 0, feedConfig.getRefreshPeriodMinutes(), TimeUnit.MINUTES);

      info("scheduling a filtering feed persistor for: " + feedName);
      final BasicFeedConfig filteredConfig = new BasicFeedConfig(
          new File(config.getWebRoot(), feedName).toURI().toURL(),
          feedConfig.getFilter(),
          filteredFeedName,
          feedConfig.getRefreshPeriodMinutes(),
          true
      );

      executor.scheduleWithFixedDelay(() -> {
        try {
          new FeedPersister(config.getWebRoot(), filteredConfig, MAX_AGE_FILTER).fetch();
        } catch (IOException e) {
          log(errorLog, e);
        }
      }, 0, filteredConfig.getRefreshPeriodMinutes(), TimeUnit.MINUTES);

      dragnetFeeds[i] = new BasicFeedConfig(
          new File(config.getWebRoot(), filteredFeedName).toURI().toURL(),
          event -> event,//config.getFilter(),
          filteredFeedName,
          feedConfig.getRefreshPeriodMinutes(),
          false);
    }
    // Set up the aggregator to aggregate and aggregate the persisted, filtered feeds
    this.aggregator = new Aggregator(new DragnetConfig() {
      @Override
      public DragnetConfig.FeedConfig[] getFeeds() {
        return dragnetFeeds;
      }

      @Override
      public File getOutpuFile() {
        return new File(config.getWebRoot(), "filtered.xml");
      }

    });

    // Schedule aggregator

    executor.scheduleWithFixedDelay(() ->
        {
          try {
            log("Refreshing aggregator feed...");
            aggregator.aggregate();
            log("Done refreshing aggregator feed.");
          } catch (Throwable e) {
            handleError(e);
          }
        }
        , 0, REFRESH_PERIOD_IN_MINUTES, TimeUnit.MINUTES);


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
