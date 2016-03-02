package com.orionletizi.avi.dragnet.rss;

import com.orionletizi.util.SequenceGenerator;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FeedPersisterTest {

  private URL sampleFeed;
  private URL sampleFeed2;
  private FeedPersister persister;

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();
  private BasicFeedConfig feedConfig;
  private File workingDir;
  private int sequence;
  private String feedName;
  private SequenceGenerator sequenceGenerator;

  @Before
  public void before() throws Exception {
    workingDir = tmp.newFolder();
    feedName = "myFeed.xml";

    sampleFeed = ClassLoader.getSystemResource("rss/sample-feed.xml");
    assertNotNull(sampleFeed);
    sampleFeed2 = ClassLoader.getSystemResource("rss/sample-feed2.xml");
    sequence = 0;
    feedConfig = new BasicFeedConfig(sampleFeed, entry -> entry, feedName, 10, true);
    sequenceGenerator = () -> ++sequence;
    persister = new FeedPersister(workingDir, sequenceGenerator, feedConfig);
  }

  @Test
  public void testPersistence() throws Exception {
    assertEquals(0, sequence);

    // fetch entries with nothing persisted yet

    File expectedArchive = getExpectedArchiveFile(sequence + 1);
    File expectedPublished = new File(workingDir, feedName);
    assertFalse(expectedArchive.exists());
    assertFalse(expectedPublished.exists());
    persister.fetch();
    assertTrue("expected archive doesn't exist: " + expectedArchive, expectedArchive.exists());
    assertTrue("expected published feed doesn't exist: " + expectedPublished, expectedPublished.exists());

    final List<SyndEntry> sourceEntries = getEntries(sampleFeed);
    List<SyndEntry> archivedEntries = getEntries(expectedArchive);
    List<SyndEntry> publishedEntries = getEntries(expectedPublished);

    assertTrue(sourceEntries.size() > 0);
    assertEquals(sourceEntries.size(), archivedEntries.size());
    assertEquals(sourceEntries.size(), publishedEntries.size());

    // fetch entries with entries persisted, but no new entries

    expectedArchive = getExpectedArchiveFile(sequence + 1);
    assertFalse(expectedArchive.exists());
    persister.fetch();
    assertTrue(expectedArchive.exists());
    archivedEntries = getEntries(expectedArchive);

    assertEquals(sourceEntries.size(), archivedEntries.size());

    // fetch entries with entries persisted, plus some new entries

    feedConfig.setFeedUrl(sampleFeed2);
    expectedArchive = getExpectedArchiveFile(sequence + 1);
    assertFalse(expectedArchive.exists());
    persister = new FeedPersister(workingDir, sequenceGenerator, feedConfig);
    persister.fetch();
    assertTrue(expectedArchive.exists());
    archivedEntries = getEntries(expectedArchive);
    assertEquals("Archived entries isn't the union of the two sample feeds.",
        getEntries(sampleFeed).size() + getEntries(sampleFeed2).size(), archivedEntries.size());
  }

//  private void ls(final File dir) {
//    FileUtils.listFiles(dir, null, false).forEach(System.out::println);
//  }

  private File getExpectedArchiveFile(final long seq) {
    return new File(workingDir, "archive/myFeed" + "-" + seq + ".xml");
  }

  private List<SyndEntry> getEntries(final File source) throws IOException, FeedException {
    return getEntries(source.toURI().toURL());
  }


  private List<SyndEntry> getEntries(final URL source) throws IOException, FeedException {
    final SyndFeedInput input = new SyndFeedInput();
    final SyndFeed feed = input.build(new XmlReader(source));
    return feed.getEntries();
  }

}