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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dragnet {

  public static URL CONVERSATIONS_URL;
  public static URL GOOGLE_GROUPS_URL;

  static {
    try {
      CONVERSATIONS_URL = new URL("http://www.inoreader.com/stream/user/1005620749/tag/Conversations");
      GOOGLE_GROUPS_URL = new URL("http://www.inoreader.com/stream/user/1005620749/tag/Google%20Groups");
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }


  private List<URL> feedUrls;
  private final PrintWriter rawOutput;
  private final PrintWriter filteredOutput;

  public Dragnet(final File rawFile, final File filteredFile) throws IOException, FeedException {
    this(Arrays.asList(new URL[]{CONVERSATIONS_URL, GOOGLE_GROUPS_URL}), new PrintWriter(new FileWriter(rawFile)), new PrintWriter(new FileWriter(filteredFile)));
  }

  public Dragnet(final List<URL> feedUrls, final PrintWriter rawOutput, final PrintWriter filteredOutput) throws IOException, FeedException {
    this.feedUrls = feedUrls;
    this.rawOutput = rawOutput;
    this.filteredOutput = filteredOutput;
  }

  public void read() throws IOException, FeedException {
    final DragnetFilter dragnet = new DragnetFilter();
    final SyndFeedInput input = new SyndFeedInput();
    final List<SyndEntry> filteredEntries = new ArrayList<>();
    final List<SyndEntry> rawEntries = new ArrayList<>();

    for (URL url : feedUrls) {
      final SyndFeed feed = input.build(new XmlReader(url));
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

    SyndFeedOutput out = new SyndFeedOutput();
    out.output(filteredOut, filteredOutput);
    out.output(rawout, rawOutput);
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
      URL[] urls = {
          CONVERSATIONS_URL,
          GOOGLE_GROUPS_URL
      };

      new Dragnet(Arrays.asList(urls), rawOutput, filteredOutput).read();


    } catch (ParseException e) {
      usage();
    }
  }

  private static void usage() {
    new HelpFormatter().printHelp("java [vm options] " + Dragnet.class.getName(), getOptions());
  }
}
