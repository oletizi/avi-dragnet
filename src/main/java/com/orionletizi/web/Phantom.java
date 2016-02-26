package com.orionletizi.web;

import java.io.*;

public class Phantom {

  private static final String cmd = "phantomjs";

  public Phantom() {

  }

  public int execute(final String javascript, final OutputStream out, final OutputStream err) throws IOException, InterruptedException {

    final File scriptFile = File.createTempFile("phantomjs-script", "js");
    final FileWriter scriptOut = new FileWriter(scriptFile);
    scriptOut.append(javascript);
    scriptOut.close();

    final Runtime runtime = Runtime.getRuntime();
    final Process proc = runtime.exec(new String[]{
        cmd,
        scriptFile.getAbsolutePath()
    });

    dumpStream(proc.getInputStream(), out);
    dumpStream(proc.getErrorStream(), err);

    return proc.waitFor();
  }

  private void dumpStream(final InputStream in, final OutputStream out) {
    new Thread(() -> {
      try {
        int i = -1;
        while ((i = in.read()) != -1) {
          out.write(i);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }
}
