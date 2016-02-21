package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.orionletizi.avi.dragnet.rss.filters.vendor.*;
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

public class DragnetFilterTest {

  private SyndEntry entry;
  private List<SyndContent> contents;
  private SyndContent content;
  private FeedFilter filter;

  @Before
  public void before() throws Exception {
    entry = mock(SyndEntry.class);
    contents = new ArrayList<>();
    content = mock(SyndContent.class);
    contents.add(content);
    when(entry.getContents()).thenReturn(contents);
  }

  @Test
  public void testDragnet() throws Exception {
    filter = new DragnetFilter();

    before();
    testAPIC();

    before();
    testAvi();

    before();
    testAWS();

    before();
    testCloudFoundry();

    before();
    testMesos();

    before();
    testOpenShift();

    before();
    testOpenStack();
  }

  @Test
  public void testAWS() throws Exception {
    filter = new AmazonWebServices();
    doAWSTest();
  }

  private void doAWSTest() {
    assertNull(filter.filter(entry));

    when(entry.getTitle()).thenReturn("The title");

    assertNull(filter.filter(entry));

    when(entry.getTitle()).thenReturn("Amazon");
    assertNull(filter.filter(entry));

    when(entry.getTitle()).thenReturn("Amazon web services");
    assertNull(filter.filter(entry));

    when(content.getValue()).thenReturn("Elastic Load Balancer");
    when(entry.getContents()).thenReturn(contents);

    assertEquals(entry, filter.filter(entry));

    when(content.getValue()).thenReturn("elb");
    assertEquals(entry, filter.filter(entry));

    when(content.getValue()).thenReturn("f5");
    assertEquals(entry, filter.filter(entry));

    when(content.getValue()).thenReturn("");
    assertNull(filter.filter(entry));

    when(entry.getTitle()).thenReturn("aws f5");
    assertEquals(entry, filter.filter(entry));

    when(entry.getTitle()).thenReturn("alkdfjo jskfjle");
    when(content.getValue()).thenReturn("Elastic Load Balancer AWS");
    assertEquals(entry, filter.filter(entry));
  }

  @Test
  public void testAPIC() throws Exception {
    filter = new APIC();
    doAPICTest();
  }

  private void doAPICTest() {
    assertNegative();

    setTitle("Something about apic");
    assertNegative();

    setContent("something about f5 in the content");
    assertPositive();

    when(content.getValue()).thenReturn("something about load balancing");
    assertPositive();

    when(content.getValue()).thenReturn("something about Citrix in the content");
    assertPositive();

    when(entry.getTitle()).thenReturn("Something about A10 in the title");
    when(content.getValue()).thenReturn("Something about APIC in the content");
    assertPositive();

    when(entry.getTitle()).thenReturn("Something that doesn't match");
    when(content.getValue()).thenReturn("Something about APIC and a load balancer in the content");
    assertPositive();

    when(content.getValue()).thenReturn("Something that doesn't match");
    assertNegative();
  }

  @Test
  public void testAvi() throws Exception {
    filter = new Avi();
    doAviTest();
  }

  private void doAviTest() {
    assertNegative();

    setTitle("AVI");
    assertNegative();

    setTitle("SOmething about Avi NEtworks in the title");
    assertPositive();

    setTitle("Something about Load Balancer in the title");
    assertPositive();

    setTitle("Something about reverse proxies");
    assertPositive();

    setTitle("Something about haproxy");
    assertPositive();
  }

  @Test
  public void testCloudFoundry() throws Exception {
    filter = new CloudFoundry();
    doCloudFoundryTest();
  }

  private void doCloudFoundryTest() {
    assertNegative();

    setTitle("Something about CloudFoundry");
    assertNegative();

    setContent("Somethign about a load balancer...");
    assertPositive();
    setContent("Something about f5 in the content");
  }

  @Test
  public void testMesos() throws Exception {
    filter = new Mesos();
    doMesosTest();
  }

  private void doMesosTest() {
    assertNegative();

    setTitle("Something about mesos");
    assertNegative();

    setContent("something about load balancing in the content");
    assertPositive();

    setContent("something about servicediscovery");
    assertPositive();

    setContent("something about service discovery");
    assertPositive();
  }

  @Test
  public void testOpenShift() throws Exception {
    filter = new OpenShift();
    doOpenShiftTest();
  }

  private void doOpenShiftTest() {
    assertNegative();

    setTitle("Something OpenShift in the title");
    assertNegative();

    setContent("Something about F5 in the content");
    assertPositive();

    setContent("Someting about loadbalancers in the content");
    assertPositive();

    setTitle("nothing that matches");
    assertNegative();
  }

  @Test
  public void testOpenStack() throws Exception {
    filter = new OpenStack();
    doOpenStackTest();
  }

  private void doOpenStackTest() {
    assertNegative();

    setTitle("Something about openstack in the title");
    assertNegative();

    setContent("something about lbaas in the content");
    assertPositive();

    setContent("something about octavia in the content");
    assertPositive();

    setContent("Somethign baout F5");
    assertPositive();

    setContent("Someting about a10");
    assertPositive();

    setContent("Something about radwarei in the content");
    assertPositive();
  }

  private void setTitle(final String s) {
    when(entry.getTitle()).thenReturn(s);
  }

  private void setContent(final String s) {
    when(content.getValue()).thenReturn(s);
  }


  private void assertNegative() {
    assertNull(filter.filter(entry));
  }

  private void assertPositive() {
    assertEquals(entry, filter.filter(entry));
  }

}