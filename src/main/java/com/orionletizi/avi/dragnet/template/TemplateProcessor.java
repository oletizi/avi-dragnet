package com.orionletizi.avi.dragnet.template;

import freemarker.template.*;

import java.io.IOException;
import java.io.Writer;

public class TemplateProcessor {
  private Configuration cfg;

  public TemplateProcessor(final String resourcePath)
      throws IOException {
    final Version configVersion = Configuration.VERSION_2_3_23;
    cfg = new Configuration(configVersion);
    cfg.setDefaultEncoding("UTF-8");
    cfg.setClassForTemplateLoading(this.getClass(), resourcePath);
    cfg.setObjectWrapper(new DefaultObjectWrapper(configVersion));
  }

  public void process(Object aModel, String aTemplate, Writer aWriter)
      throws IOException, TemplateException {
    final Template template = cfg.getTemplate(aTemplate);
    template.process(aModel, aWriter);
  }
}