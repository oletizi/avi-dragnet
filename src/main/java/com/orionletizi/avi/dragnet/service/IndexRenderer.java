package com.orionletizi.avi.dragnet.service;

import com.orionletizi.avi.dragnet.template.TemplateProcessor;
import com.rometools.rome.feed.synd.SyndFeed;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class IndexRenderer {
  private final TemplateProcessor processor;

  public IndexRenderer() throws IOException {
    processor = new TemplateProcessor("/template");
  }

  public void render(final List<SyndFeed> feeds, final Writer out) throws IOException, TemplateException {
    final List<Feed> wrapped = new ArrayList<>();

    final Map<String, List<Feed>> model = new HashMap<>();
    for (SyndFeed feed : feeds) {
      wrapped.add(new Feed() {
        @Override
        public String getName() {
          return feed.getTitle();
        }

        @Override
        public String getLink() {
          return feed.getLink();
        }

        @Override
        public String getDescription() {
          return feed.getDescription();
        }

        @Override
        public int getSize() {
          return feed.getEntries().size();
        }

        @Override
        public String getLastUpdated() {
          return feed.getPublishedDate() == null ? "unknown" : feed.getPublishedDate().toString();
        }
      });
    }


    model.put("feeds", wrapped);
    processor.process(model, "index.ftl", out);
  }

  public interface Feed {
    String getName();

    String getLink();

    String getDescription();

    int getSize();

    String getLastUpdated();
  }
}
