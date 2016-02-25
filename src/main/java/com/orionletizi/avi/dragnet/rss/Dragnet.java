package com.orionletizi.avi.dragnet.rss;

import com.orionletizi.avi.dragnet.rss.filters.DragnetFilter;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Dragnet {


  private final DragnetConfig config;

  public Dragnet(final DragnetConfig config) {
    this.config = config;
  }

  public void read() throws IOException, FeedException {
    final DragnetFilter dragnet = new DragnetFilter();
    final SyndFeedInput input = new SyndFeedInput();
    final List<SyndEntry> filteredEntries = new ArrayList<>();
    final List<SyndEntry> rawEntries = new ArrayList<>();
    final SyndFeedOutput feedWriter = new SyndFeedOutput();
    info("fetching feeds...");

    for (DragnetConfig.FeedConfig feedConfig : config.getFeeds()) {
      info("fetching feed: " + feedConfig.getFeedUrl());
      final URL url = feedConfig.getFeedUrl();

      final SyndFeed feed = input.build(new XmlReader(url));
      if (feedConfig.shouldWrite()) {
        // write this source feed to local disk
        final File outfile = new File(config.getWriteRoot(), feedConfig.getName());
        info("Writing to " + outfile);
        feedWriter.output(feed, outfile);
      }

      rawEntries.addAll(feed.getEntries());
      for (SyndEntry entry : feed.getEntries()) {
        final SyndEntry filtered = dragnet.filter(entry);
        if (filtered != null) {
          filteredEntries.add(filtered);
        }
      }
    }

    final SyndFeed filteredOut = new SyndFeedImpl();
    filteredOut.setTitle("Avi Dragnet");
    filteredOut.setDescription("Avi Dragnet");
    filteredOut.setFeedType("atom_1.0");
    filteredOut.setLink("http://some.website.com/");
    filteredOut.setEntries(filteredEntries);

    final SyndFeed rawout = new SyndFeedImpl();
    rawout.setTitle("Avi Dragnet Raw Aggregate Feed");
    rawout.setDescription("Raw aggregate of all Avi Dragnet sources with no filtering applied");
    rawout.setFeedType("atom_1.0");
    rawout.setLink("http://some.other.website.com/");
    rawout.setEntries(rawEntries);

    info("Writing filtered feed to " + config.getFilteredOutputFile());
    feedWriter.output(filteredOut, config.getFilteredOutputFile());

    info("Writing raw feed to " + config.getRawOutputFile());
    feedWriter.output(rawout, config.getRawOutputFile());

    info("Done reading feeds.");
  }

  private void info(final String s) {
    System.out.println(getClass().getSimpleName() + ": " + s);
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
//      new Dragnet(Arrays.asList(urls), rawOutput, filteredOutput).read();


    } catch (ParseException e) {
      usage();
    }
  }

  private static void usage() {
    new HelpFormatter().printHelp("java [vm options] " + Dragnet.class.getName(), getOptions());
  }
}
