package com.orionletizi.avi.dragnet.rss.filters.vendor;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AmazonWebServicesTest {

  @Test
  public void testBasics() throws Exception {

    final SyndEntry entry = mock(SyndEntry.class);

    final AmazonWebServices aws = new AmazonWebServices();
    assertNull(aws.filter(entry));

    when(entry.getTitle()).thenReturn("The title");

    assertNull(aws.filter(entry));

    when(entry.getTitle()).thenReturn("Amazon");
    assertNull(aws.filter(entry));

    when(entry.getTitle()).thenReturn("Amazon web services");
    assertNull(aws.filter(entry));

    final List<SyndContent> contents = new ArrayList<>();
    SyndContent content = mock(SyndContent.class);
    when(content.getValue()).thenReturn("Elastic Load Balancer");
    when(entry.getContents()).thenReturn(contents);

    contents.add(content);

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

}