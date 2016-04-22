package com.orionletizi.avi.dragnet.rss;

import com.orionletizi.util.logging.Logger;
import com.orionletizi.util.logging.LoggerImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;
import org.apache.commons.cli.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Aggregator {
  private static final Logger logger = LoggerImpl.forClass(Aggregator.class);
  private static final ExecutorService executor = Executors.newSingleThreadExecutor();

  private final DragnetConfig config;

  public Aggregator(final DragnetConfig config) {
    this.config = config;
  }

  public void aggregate() throws IOException, FeedException {
    final SyndFeedInput input = new SyndFeedInput();
    final List<SyndEntry> aggregatedEntries = new ArrayList<>();
    final SyndFeedOutput feedWriter = new SyndFeedOutput();
    info("fetching feeds...");

    for (DragnetConfig.FeedConfig feedConfig : config.getFeeds()) {
      info("fetching feed: " + feedConfig.getFeedUrl());
      final URL url = feedConfig.getFeedUrl();
      final SyndFeed[] feed = {null};
      // put timeout on feed fetching... sometimes it's really slow
      final Future task = executor.submit(() -> {
        try {
          info("Fetching feed: " + url + "...");
          feed[0] = input.build(new XmlReader(url));
          info("Done fetching feed.");
        } catch (Throwable e) {
          handleError(e);
        }
      });

      try {
        info("Waiting for feed aggregate: " + url + "...");
        task.get(3, TimeUnit.SECONDS);
        info("successfully aggregated feed: " + url);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        handleError(e);
        // screw this feed. Move on to the next one
        info("Couldn't aggregate feed in time. Moving to the next feed.");
        continue;
      }

      if (feed[0] == null) {
        continue;
      }

      info("Aggregated entries before adding : " + feedConfig.getFeedUrl() + ": " + aggregatedEntries.size());
      info(feedConfig.getFeedUrl() + " contains " + feed[0].getEntries().size() + " entries");
      final FeedFilter filter = feedConfig.getFilter();
      for (SyndEntry entry : feed[0].getEntries()) {
        final SyndEntry filtered = filter.filter(entry);
        if (filtered != null) {
          info("Entry passed filter: " + filter);
          aggregatedEntries.add(filtered);
        }
      }
      info("Aggregated entries after: " + aggregatedEntries.size());
    }

    final SyndFeed aggregatedOut = new SyndFeedImpl();
    aggregatedOut.setTitle("Avi Aggregator");
    aggregatedOut.setDescription("Avi Aggregator");
    aggregatedOut.setFeedType("atom_1.0");
    aggregatedOut.setLink("http://dragnet.aviplayground.com:8080/filtered.xml");
    aggregatedOut.setEntries(aggregatedEntries);

    info("Writing filtered feed to " + config.getOutpuFile());
    feedWriter.output(aggregatedOut, config.getOutpuFile());

    info("Done reading feeds.");
  }

  private void handleError(final Throwable e) {
    e.printStackTrace();
  }

  private void info(final String s) {
    logger.info(s);
  }

  private static Options getOptions() {
    final Options options = new Options();
    options.addOption(Option.builder()
        .longOpt("filtered")
        .argName("file path")
        .desc("Where to write the filtered output. If not specified, STDOUT will be used.")
        .hasArg()
        .valueSeparator()
        .build());
    options.addOption(Option.builder()
        .longOpt("raw")
        .argName("file path")
        .desc("Where to write the unfiltered output. If not specified, STDERR will be used. ")
        .hasArg()
        .valueSeparator()
        .build());
    final Option help = new Option("h", "Prints this message.");
    help.setLongOpt("help");
    options.addOption(help);
    return options;
  }

  public static void main(String[] args) throws IOException, FeedException {
    final Options options = getOptions();
    final CommandLineParser parser = new DefaultParser();
    try {
      final CommandLine cmd = parser.parse(options, args);
      if (cmd.hasOption('h')) {
        usage();
        return;
      }

      PrintWriter filteredOutput = null;
      PrintWriter rawOutput = null;

      String filtered = cmd.getOptionValue("filtered", "-");
      if ("-".equals(filtered)) {
        filteredOutput = new PrintWriter(System.out);
      } else {
        try {
          filteredOutput = new PrintWriter(new FileWriter(filtered));
        } catch (IOException e) {
          System.out.println("Can't write to filtered output file: " + e.getMessage());
          System.out.println();
          usage();
          return;
        }
      }

      String raw = cmd.getOptionValue("raw", "-");
      if ("-".equals(raw)) {
        rawOutput = new PrintWriter(System.err);
      } else {
        try {
          rawOutput = new PrintWriter(new FileWriter(raw));
        } catch (IOException e) {
          System.out.println("Can't write to raw output file: " + e.getMessage());
          System.out.println();
          usage();
          return;
        }
      }

      throw new RuntimeException("Implement Me!");
//      new Aggregator(Arrays.asList(urls), rawOutput, filteredOutput).fetch();


    } catch (ParseException e) {
      usage();
    }
  }

  private static void usage() {
    new HelpFormatter().printHelp("java [vm options] " + Aggregator.class.getName(), getOptions());
  }
}
