package com.orionletizi.avi.dragnet.service;

import com.orionletizi.avi.dragnet.rss.Dragnet;
import com.rometools.rome.io.FeedException;
import fi.iki.elonen.SimpleWebServer;
import fi.iki.elonen.util.ServerRunner;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Service {
  private static final DateFormat df = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
  private static final long REFRESH_PERIOD_IN_MINUTES = 1;

  private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
  private final Dragnet dragnet;
  private final ArrayList<File> webroots;
  private final int port;
  private final File log;
  private final File errorLog;


  public Service(final int port, final File webroot) throws IOException, FeedException {
    this.log = new File(webroot, "log.txt");
    this.errorLog = new File(webroot, "error-log.txt");
    this.dragnet = new Dragnet(new File(webroot, "raw.xml"), new File(webroot, "filtered.xml"));
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
    final SimpleWebServer server = new SimpleWebServer("localhost", port, webroots, false);
    info("Starting webserver...");
    ServerRunner.executeInstance(server);
  }

  private static void info(final String s) {
    System.out.println(Service.class.getSimpleName() + ": " + s);
  }

  public static void main(String[] args) throws InterruptedException, IOException, FeedException {
    final List<File> roots = new ArrayList<>();
    final Service service = new Service(8080, new File("/tmp"));
    service.start();
//    final CountDownLatch latch = new CountDownLatch(1);
//    latch.await();
  }
}
