package com.orionletizi.avi.dragnet.service;

import com.orionletizi.avi.dragnet.template.TemplateProcessor;
import com.orionletizi.util.logging.Logger;
import com.orionletizi.util.logging.LoggerImpl;
import com.rometools.rome.feed.atom.Feed;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class IndexRenderer {
  private static final Logger logger = LoggerImpl.forClass(IndexRenderer.class);
  private final TemplateProcessor processor;

  public IndexRenderer() throws IOException {
    processor = new TemplateProcessor("/template");
  }

  public void render(final List<FeedDescriptor> feeds, final Writer out) throws IOException, TemplateException {
    final List<Feed> wrapped = new ArrayList<>();

    final Map<String, List<FeedDescriptor>> model = new HashMap<>();
    info("Adding feeds to the model: " + feeds.size());
    model.put("feeds", feeds);
    processor.process(model, "index.ftl", out);
  }

  private void info(final Object msg) {
    logger.info(msg);
  }

}
