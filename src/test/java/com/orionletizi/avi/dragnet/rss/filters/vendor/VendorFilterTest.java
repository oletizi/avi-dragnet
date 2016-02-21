package com.orionletizi.avi.dragnet.rss.filters.vendor;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VendorFilterTest {

  private SyndEntry entry;
  private List<SyndContent> contents;
  private SyndContent content;

  @Before
  public void before() throws Exception {
    entry = mock(SyndEntry.class);
    contents = new ArrayList<>();
    content = mock(SyndContent.class);
    contents.add(content);
    when(entry.getContents()).thenReturn(contents);
  }

  @Test
  public void testAWS() throws Exception {

    final AmazonWebServices aws = new AmazonWebServices();
    assertNull(aws.filter(entry));

    when(entry.getTitle()).thenReturn("The title");

    assertNull(aws.filter(entry));

    when(entry.getTitle()).thenReturn("Amazon");
    assertNull(aws.filter(entry));

    when(entry.getTitle()).thenReturn("Amazon web services");
    assertNull(aws.filter(entry));

    when(content.getValue()).thenReturn("Elastic Load Balancer");
    when(entry.getContents()).thenReturn(contents);

    assertEquals(entry, aws.filter(entry));

    when(content.getValue()).thenReturn("elb");
    assertEquals(entry, aws.filter(entry));

    when(content.getValue()).thenReturn("f5");
    assertEquals(entry, aws.filter(entry));

    when(content.getValue()).thenReturn("");
    assertNull(aws.filter(entry));

    when(entry.getTitle()).thenReturn("aws f5");
    assertEquals(entry, aws.filter(entry));

    when(entry.getTitle()).thenReturn("alkdfjo jskfjle");
    when(content.getValue()).thenReturn("Elastic Load Balancer AWS");
    assertEquals(entry, aws.filter(entry));
  }

  @Test
  public void testAPIC() throws Exception {
    final APIC filter = new APIC();
    assertNegative(filter);

    when(entry.getTitle()).thenReturn("Something about apic");
    assertNegative(filter);

    when(content.getValue()).thenReturn("something about f5 in the content");
    assertPositive(filter);

    when(content.getValue()).thenReturn("something about load balancing");
    assertPositive(filter);

    when(content.getValue()).thenReturn("something about Citrix in the content");
    assertPositive(filter);

    when(entry.getTitle()).thenReturn("Something about A10 in the title");
    when(content.getValue()).thenReturn("Something about APIC in the content");
    assertPositive(filter);

    when(entry.getTitle()).thenReturn("Something that doesn't match");
    when(content.getValue()).thenReturn("Something about APIC and a load balancer in the content");
    assertPositive(filter);

    when(content.getValue()).thenReturn("Something that doesn't match");
    assertNegative(filter);
  }




  private void assertNegative(final FeedFilter filter) {
    assertNull(filter.filter(entry));
  }

  private void assertPositive(final FeedFilter filter) {
    assertEquals(entry, filter.filter(entry));
  }

}