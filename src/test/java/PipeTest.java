import org.junit.Test;

import java.io.*;

public class PipeTest {

  @Test
  public void test() throws Exception {
    final PipedWriter writer = new PipedWriter();
    final PipedReader reader = new PipedReader();

    final PrintWriter out = new PrintWriter(writer);
    final BufferedReader in = new BufferedReader(reader);

    reader.connect(writer);

    new Thread(() -> {
      int b;

      try {
        String line;
        while ((line = in.readLine()) != null) {
          System.out.println(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();

    out.println("Here's line one...");
    out.println("Here's line two...");
    out.close();
  }

}
