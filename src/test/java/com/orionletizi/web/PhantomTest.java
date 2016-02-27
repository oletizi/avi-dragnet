package com.orionletizi.web;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.WriterOutputStream;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.net.URL;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class PhantomTest {

  private Phantom phantom;
  private StringWriter outString;
  private StringWriter errString;
  private WriterOutputStream out;
  private WriterOutputStream err;

  @Before
  public void before() throws Exception {
    phantom = new Phantom();
    outString = new StringWriter();
    errString = new StringWriter();

    out = new WriterOutputStream(outString);
    err = new WriterOutputStream(errString);

  }

  @Test
  public void testHello() throws Exception {

    final Process proc = phantom.execute("console.log('Hello, World!'); phantom.exit();", out, System.err);
    proc.waitFor();
    out.close();
    assertEquals("Hello, World!\n", outString.getBuffer().toString());
  }

  @Test
  public void testScrape() throws Exception {
    final URL url = ClassLoader.getSystemResource("js/scrape.js");
    final String javascript = IOUtils.toString(url.openStream());

    final Phantom phantom = new Phantom();
    final Process proc = phantom.execute(javascript, out, System.err);
    proc.waitFor();
    out.close();

    final String expected = "PhantomJS is a headless WebKit scriptable with a JavaScript API.";
    final String actual = outString.getBuffer().toString();
    assertTrue(actual.indexOf(expected) > 0);
  }

}