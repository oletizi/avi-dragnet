package com.orionletizi.avi.dragnet.template;

import com.orionletizi.avi.dragnet.service.IndexRenderer;
import org.junit.Ignore;
import org.junit.Test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.orionletizi.util.Assertions.assertTrue;

public class TemplateProcessorTest {

  @Test
  @Ignore
  public void test() throws Exception {
    final TemplateProcessor processor = new TemplateProcessor("/template");
    final List<IndexRenderer.FeedDescriptor> feeds = new ArrayList<IndexRenderer.FeedDescriptor>();
    feeds.add(new IndexRenderer.FeedDescriptor() {
      @Override
      public String getName() {
        return "name";
      }

      @Override
      public String getLink() {
        return "link";
      }

      @Override
      public String getLocalRawFeedUrl() {
        return "raw feed url";
      }

      @Override
      public String getLocalFilteredFeedUrl() {
        return "local filtered url";
      }

      @Override
      public String getDescription() {
        return "description";
      }

      @Override
      public int getSize() {
        return 1;
      }

      @Override
      public int getFilteredSize() {
        return 1;
      }

      @Override
      public String getLastUpdated() {
        return "just now";
      }
    });


    final Map<String, List<IndexRenderer.FeedDescriptor>> model = new HashMap<>();

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