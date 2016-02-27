package com.orionletizi.avi.dragnet.rss.filters;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import com.orionletizi.web.Phantom;
import net.fortuna.ical4j.model.DateTime;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GoogleGroupsDateScraper {
  private static final DateFormat df = new SimpleDateFormat("MM/dd/yy");
  private Duration timeout;
  private final Set<Date> dates = new TreeSet<>();

  public GoogleGroupsDateScraper(final Duration timeout) {
    this.timeout = timeout;
  }

  public List<Date> getDates() {
    return new ArrayList<>(dates);
  }

  public void scrape(final String url) throws IOException, InterruptedException {

      String template = IOUtils.toString(ClassLoader.getSystemResource("js/ggroups-date.js").openStream());

      final String urlToken = "${entry.url}";
      final String waitToken = "${lookup.wait}";
      int waitTime = 3 * 1000;

    info("URL TOKEN: " + urlToken);
    info("url    : " + url);


      template = template.replace(urlToken, url);
      template = template.replace(waitToken, "" + waitTime);


      final PipedOutputStream out = new PipedOutputStream();
      final OutputStream err = System.err;

      final PipedInputStream in = new PipedInputStream();
      in.connect(out);

      final BufferedReader reader = new BufferedReader(new InputStreamReader(in));

      final Phantom phantom = new Phantom();
      final Process proc = phantom.execute(template, out, err);
      new Thread(() -> {
        String line = null;
        try {
          while ((line = reader.readLine()) != null) {
            info(line);
            if (line.startsWith("DATE:")) {
              // parse the date
              final String dateString = line.substring("DATE: ".length());
              Date date = null;
              try {
                date = df.parse(dateString);
              } catch (ParseException e) {
                // that one didn't work. try this one...
                try {
                  final List<DateGroup> dateGroups = new Parser().parse(dateString);
                  if (!dateGroups.isEmpty()) {
                    final DateGroup dateGroup = dateGroups.get(0);
                    final List<Date> theseDates = dateGroup.getDates();
                    if (!theseDates.isEmpty()) {
                      dates.add(new DateTime(theseDates.get(0)));
                    }
                  }
                } catch (Throwable t) {
                  t.printStackTrace();
                }
              }
              if (date != null) {
                dates.add(date);
              }
            } else if (line.startsWith("END DATE DIG")) {
              info("ALL DONE!!!");
              proc.destroyForcibly();
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }).start();
      proc.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
      if (proc.isAlive()) {
        proc.destroyForcibly();
      }
  }

  private void info(final String line) {
    System.out.println(getClass().getSimpleName() + ": " + line);
  }
}
