package com.orionletizi.avi.dragnet.service;

import com.orionletizi.avi.dragnet.rss.BasicFeedConfig;
import com.orionletizi.avi.dragnet.rss.Dragnet;
import com.orionletizi.avi.dragnet.rss.DragnetConfig;
import com.orionletizi.avi.dragnet.rss.filters.DragnetFilter;
import com.orionletizi.avi.dragnet.rss.filters.GoogleGroupsDateScraper;
import com.orionletizi.avi.dragnet.rss.filters.GoogleGroupsFilter;
import com.rometools.rome.io.FeedException;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.SimpleWebServer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Service {
  final static String DZONE = "http://feeds.dzone.com/home";
  final static String INFOQ = "http://www.infoq.com/feed?token=s8sWhq8NCl1T2XMizaXG4rD3eZujOkQj";
  final static String OREILLEY_RADAR = "http://feeds.feedburner.com/oreilly/radar/atom";
  final static String OREILLEY_FORUMS = "http://forums.oreilly.com/rss/forums/10-oreilly-forums/";
  final static String QUORA = "https://www.quora.com/rss";
  final static String SERVER_FAULT = "http://serverfault.com/feeds";
  final static String STACK_OVERFLOW = "http://stackoverflow.com/feeds/";

  final static String GGROUPS_AWS = "http://www.bing.com/search?q=site%3Agroups.google.com+((AWS+OR+%22amazon+web+services%22)+AND+(ELB+OR+F5+OR+%22load+balancer%22+OR+%22loadbalancer%22+OR+%22load+balancing%22+OR+%22loadbalancing%22))&format=rss";
  final static String GGROUPS_MESOS = "http://www.bing.com/search?q=site%3Agroups.google.com+mesos+AND+(%22load+balancing%22+OR+%22load+balancer%22+OR+%22loadbalancer%22+OR+%22service+discovery%22)&format=rss";
  final static String GGROUPS_NGINX_HAPROXY = "http://www.bing.com/search?q=site%3Agroups.google.com+%22reverse+proxy%22+OR+Nginx+OR+HAProxy&format=rss";
  final static String GGROUPS_OPENSHIFT = "http://www.bing.com/search?q=site%3Agroups.google.com+((%22OpenShift%22+OR+%22open+shift%22)+AND+(%22load+balancer%22+OR+%22loadbalancer%22+OR+%22load+balancing%22+OR+%22loadbalancing%22+OR+F5))&format=rss";
  final static String GGROUPS_OPENSTACK = "http://www.bing.com/search?q=site%3Agroups.google.com+(OpenStack+OR+%22open+stack%22)++AND+(LBaaS+OR+Octavia+OR+F5+OR+Citrix+OR+A10+OR+Radware)&format=rss";
  final static String GGROUPS_CLOUD_FOUNDRY = "http://www.bing.com/search?q=site%3Agroups.google.com+(%22cloud+foundry%22+OR+%22cloudfoundry%22)+AND+(%22load+balancer%22+OR+%22loadbalancer%22+OR+%22load+balancer%22+OR+%22loadbalancing%22+OR+F5)&format=rss";
  final static String GGROUPS_LOAD_BALANCER = "http://www.bing.com/search?q=site%3Agroups.google.com+%22load+balancer%22+OR+%22loadbalancer%22+OR+%22load+balancing%22+OR+%22loadbalancing%22&format=rss";

  private static DragnetConfig.FeedConfig[] feedConfigs;
  private static DragnetFilter dragnetFilter = new DragnetFilter();
  private static GoogleGroupsFilter googleGroupsFilter;

  static {
    try {
      final GoogleGroupsDateScraper scraper = new GoogleGroupsDateScraper(Duration.ofMinutes(1));
      final Period maxAge = Period.ofDays(30);
      googleGroupsFilter = new GoogleGroupsFilter(scraper, dragnetFilter, maxAge);
      feedConfigs = new DragnetConfig.FeedConfig[]{
          new BasicFeedConfig(new URL(DZONE), dragnetFilter, "dzone.xml", true),
          new BasicFeedConfig(new URL(INFOQ), dragnetFilter, "infoq.xml", true),
          new BasicFeedConfig(new URL(OREILLEY_RADAR), dragnetFilter, "oreilley-radar.xml", true),
          new BasicFeedConfig(new URL(OREILLEY_FORUMS), dragnetFilter, "oreilley-forums.xml", true),
          new BasicFeedConfig(new URL(QUORA), dragnetFilter, "quora.xml", true),
          new BasicFeedConfig(new URL(SERVER_FAULT), dragnetFilter, "server-fault.xml", true),
          new BasicFeedConfig(new URL(STACK_OVERFLOW), dragnetFilter, "stack-overflow.xml", true),
          new BasicFeedConfig(new URL(GGROUPS_AWS), googleGroupsFilter, "ggroups-aws.xml", true),
          new BasicFeedConfig(new URL(GGROUPS_MESOS), googleGroupsFilter, "ggroups-mesos.xml", true),
          new BasicFeedConfig(new URL(GGROUPS_NGINX_HAPROXY), googleGroupsFilter, "ggroups-nginx-haproxy.xml", true),
          new BasicFeedConfig(new URL(GGROUPS_OPENSHIFT), googleGroupsFilter, "ggroups-openshift.xml", true),
          new BasicFeedConfig(new URL(GGROUPS_OPENSTACK), googleGroupsFilter, "ggroups-openstack.xml", true),
          new BasicFeedConfig(new URL(GGROUPS_CLOUD_FOUNDRY), googleGroupsFilter, "ggroups-cloud-foundry.xml", true),
          new BasicFeedConfig(new URL(GGROUPS_LOAD_BALANCER), googleGroupsFilter, "ggroups-load-balancer.xml", true)
      };
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final long REFRESH_PERIOD_IN_MINUTES = 120;

  private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
  private final Dragnet dragnet;
  private final ArrayList<File> webroots;
  private final int port;
  private final File log;
  private final File errorLog;


  public Service(final int port, final File webroot) throws IOException, FeedException {
    this.log = new File(webroot, "log.txt");
    this.errorLog = new File(webroot, "error-log.txt");

    this.dragnet = new Dragnet(new DragnetConfig() {
      @Override
      public FeedConfig[] getFeeds() {
        return feedConfigs;
      }

      @Override
      public File getFilteredOutputFile() {
        return new File(webroot, "filtered.xml");
      }

      @Override
      public File getRawOutputFile() {
        return new File(webroot, "raw.xml");
      }

      @Override
      public File getWriteRoot() {
        return webroot;
      }
    });

    executor.scheduleAtFixedRate((Runnable) () -> {
      try {
        log("Refreshing dragnet feed...");
        dragnet.read();
        log("Done refreshing dragnet feed.");
      } catch (Throwable e) {
        handleError(e);
      }
    }, 0, REFRESH_PERIOD_IN_MINUTES, TimeUnit.MINUTES);

    this.port = port;
    this.webroots = new ArrayList<>();
    webroots.add(webroot);
    log("Webroot: " + webroot);
  }

  private void handleError(final Throwable e) {
    log(errorLog, e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
  }

  private void log(Object o) {
    log(log, o);
  }

  private void log(File log, Object o) {
    final String msg = df.format(new Date()) + ": " + o + "\n";

    try {
      final BufferedWriter out = new BufferedWriter(new FileWriter(log, true));
      out.append(msg);
      out.flush();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void start() throws IOException, FeedException {

    final InetAddress inetAddress = guessAddress();
    if (inetAddress == null) {
      handleError(new IOException("Unable to find a valid inet address."));
      return;
    }

    final SimpleWebServer server = new SimpleWebServer(inetAddress.getHostAddress(), port, webroots, true);
    log("Starting webserver at address: " + inetAddress.getHostAddress());
    //ServerRunner.executeInstance(server);
    server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
  }

  private InetAddress guessAddress() throws IOException {
    final InetAddressValidator validator = InetAddressValidator.getInstance();
    final Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
    while (ifaces.hasMoreElements()) {
      final NetworkInterface iface = ifaces.nextElement();
      //System.out.println("iface: " + iface.getName());
      final String name = iface.getName();
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
    File webroot = new File("/tmp");
    if (args.length > 0) {
      webroot = new File(args[0]);
    }
    final List<File> roots = new ArrayList<>();
    final Service service = new Service(8080, webroot);
    service.start();
  }
}
