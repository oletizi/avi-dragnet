package com.orionletizi.avi.dragnet.template;

import com.orionletizi.avi.dragnet.service.FeedDescriptor;
import org.junit.Ignore;
import org.junit.Test;

import java.io.StringWriter;
import java.util.*;

import static com.orionletizi.util.Assertions.assertTrue;

public class TemplateProcessorTest {

  @Test
  @Ignore
  public void test() throws Exception {
    final TemplateProcessor processor = new TemplateProcessor("/template");
    final List<FeedDescriptor> feeds = new ArrayList<>();
    feeds.add(new FeedDescriptor("name", "description", 1, "http://thelink/", "localrawfeedurl", "localFilteredFeedUrl", 1, "now"));


    final Map<String, List<FeedDescriptor>> model = new HashMap<>();

    model.put("feeds", feeds);
    final StringWriter writer = new StringWriter();
    processor.process(model, "index.ftl", writer);
    writer.close();
    final String output = writer.getBuffer().toString();
    System.out.println(output);

    assertTrue(output.indexOf("Link: link") > 0);
    assertTrue(output.indexOf("Size: 1") > 0);
    assertTrue(output.indexOf("description") > 0);
    assertTrue(output.indexOf("Last updated: just now") > 0);
  }

}