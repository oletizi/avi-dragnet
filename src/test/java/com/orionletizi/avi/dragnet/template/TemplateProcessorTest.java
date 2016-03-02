package com.orionletizi.avi.dragnet.template;

import org.junit.Test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.orionletizi.util.Assertions.assertTrue;

public class TemplateProcessorTest {

  @Test
  public void test() throws Exception {
    final TemplateProcessor processor = new TemplateProcessor("/template");
    final List<Feed> feeds = new ArrayList<>();
    feeds.add(new Feed() {
      @Override
      public String getName() {
        return "name";
      }

      @Override
      public String getLink() {
        return "link";
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
      public String getLastUpdated() {
        return "just now";
      }
    });


    final Map<String, List<Feed>> model = new HashMap<>();

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

  public interface Feed {
    String getName();

    String getLink();

    String getDescription();

    int getSize();

    String getLastUpdated();
  }
}