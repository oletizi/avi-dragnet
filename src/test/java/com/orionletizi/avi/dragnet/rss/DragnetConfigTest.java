package com.orionletizi.avi.dragnet.rss;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orionletizi.avi.dragnet.rss.filters.DragnetFilter;
import com.orionletizi.avi.dragnet.rss.filters.GoogleGroupsDateScraper;
import com.orionletizi.avi.dragnet.rss.filters.GoogleGroupsFilter;
import org.junit.Test;

import java.net.URL;
import java.time.Duration;
import java.time.Period;

import static junit.framework.TestCase.assertEquals;

public class DragnetConfigTest {
  final static String DZONE = "http://feeds.dzone.com/home";
  final static String INFOQ = "http://www.infoq.com/feed?token=s8sWhq8NCl1T2XMizaXG4rD3eZujOkQj";
  final static String OREILLEY_RADAR = "http://feeds.feedburner.com/oreilly/radar/atom";
  final static String OREILLEY_FORUMS = "http://forums.oreilly.com/rss/forums/10-oreilly-forums/";
  final static String QUORA = "https://www.quora.com/rss";
  final static String SERVER_FAULT = "http://serverfault.com/feeds";
  final static String STACK_OVERFLOW = "http://stackoverflow.com/feeds/";

  final static String GGROUPS_AWS = "http://www.bing.com/search?q=site%3Agroups.google.com+((AWS+OR+%22amazon+web+services%22)+AND+(ELB+OR+F5+OR+%22load+balancer%22+OR+%22loadbalancer%22+OR+%22load+balancing%22+OR+%22loadbalancing%22))&format=rss";
  final static String GGROUPS_MESOS = "http://www.bing.com/search?q=site%3Agroups.google.com+mesos+AND+(%22load+balancing%22+OR+%22load+balancer%22+OR+%22loadbalancer%22+OR+%22service+discovery%22)&format=rss";
  final static String GGROUPS_NGINX_HAPROXY = "http://www.bing.com/search?q=site%3Agroups.google.com+%22reverse+proxy%22+OR+Nginx+OR+HAProxy&format=rss";
  final static String GGROUPS_OPENSHIFT = "http://www.bing.com/search?q=site%3Agroups.google.com+((%22OpenShift%22+OR+%22open+shift%22)+AND+(%22load+balancer%22+OR+%22loadbalancer%22+OR+%22load+balancing%22+OR+%22loadbalancing%22+OR+F5))&format=rss";
  final static String GGROUPS_OPENSTACK = "http://www.bing.com/search?q=site%3Agroups.google.com+(OpenStack+OR+%22open+stack%22)++AND+(LBaaS+OR+Octavia+OR+F5+OR+Citrix+OR+A10+OR+Radware)&format=rss";
  final static String GGROUPS_CLOUD_FOUNDRY = "http://www.bing.com/search?q=site%3Agroups.google.com+(%22cloud+foundry%22+OR+%22cloudfoundry%22)+AND+(%22load+balancer%22+OR+%22loadbalancer%22+OR+%22load+balancer%22+OR+%22loadbalancing%22+OR+F5)&format=rss";
  final static String GGROUPS_LOAD_BALANCER = "http://www.bing.com/search?q=site%3Agroups.google.com+%22load+balancer%22+OR+%22loadbalancer%22+OR+%22load+balancing%22+OR+%22loadbalancing%22&format=rss";

  /**
   * The point of this test is both to test the serializability of the config stuff and, also, to generate a JSON
   * config that can be used as a starting point for the real configuration.
   *
   * @throws Exception
   */
  @Test
  public void testBasics() throws Exception {

    final DragnetFilter dragnetFilter = new DragnetFilter();
    final GoogleGroupsDateScraper scraper = new GoogleGroupsDateScraper(Duration.ofMinutes(1));
    final Period maxAge = Period.ofDays(30);
    final GoogleGroupsFilter googleGroupsFilter = new GoogleGroupsFilter(scraper, dragnetFilter, maxAge);


    final DragnetConfig.FeedConfig[] feedConfigs = {
        new BasicFeedConfig(new URL(DZONE), dragnetFilter, "dzone.xml", true),
        new BasicFeedConfig(new URL(INFOQ), dragnetFilter, "infoq.xml", true),
        new BasicFeedConfig(new URL(OREILLEY_RADAR), dragnetFilter, "oreilley-radar.xml", true),
        new BasicFeedConfig(new URL(OREILLEY_FORUMS), dragnetFilter, "oreilley-forums.xml", true),
        new BasicFeedConfig(new URL(QUORA), dragnetFilter, "quora.xml", true),
        new BasicFeedConfig(new URL(SERVER_FAULT), dragnetFilter, "server-fault.xml", true),
        new BasicFeedConfig(new URL(STACK_OVERFLOW), dragnetFilter, "stack-overflow.xml", true),
        new BasicFeedConfig(new URL(GGROUPS_AWS), googleGroupsFilter, "ggroups-aws.xml", true),
        new BasicFeedConfig(new URL(GGROUPS_MESOS), googleGroupsFilter, "ggroups-mesos.xml", true),
        new BasicFeedConfig(new URL(GGROUPS_NGINX_HAPROXY), googleGroupsFilter, "ggroups-nginx-haproxy.xml", true),
        new BasicFeedConfig(new URL(GGROUPS_OPENSHIFT), googleGroupsFilter, "ggroups-openshift.xml", true),
        new BasicFeedConfig(new URL(GGROUPS_OPENSTACK), googleGroupsFilter, "ggroups-openstack.xml", true),
        new BasicFeedConfig(new URL(GGROUPS_CLOUD_FOUNDRY), googleGroupsFilter, "ggroups-cloud-foundry.xml", true),
        new BasicFeedConfig(new URL(GGROUPS_LOAD_BALANCER), googleGroupsFilter, "ggroups-load-balancer.xml", true)
    };

    final ObjectMapper mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    final String serialized = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(feedConfigs);
    System.out.println(serialized);

    final BasicFeedConfig[] clone = mapper.readValue(serialized, BasicFeedConfig[].class);

    assertEquals(feedConfigs.length, clone.length);
  }
}