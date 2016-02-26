package com.orionletizi.avi.dragnet.rss.filters;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.rometools.rome.feed.synd.SyndEntry;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoogleGroupsFilterTest {

  @Test
  public void testYoungEnough() throws Exception {

    final FeedFilter mockFilter = mock(FeedFilter.class);
    final GoogleGroupsFilter filter = new GoogleGroupsFilter(mockFilter);

    final SyndEntry entry = mock(SyndEntry.class);
    //String url = "https://groups.google.com/forum/#!msg/terraform-tool/6Fxnl_bejX4/0O-d17UwhHcJ";
    String url = "https://groups.google.com/d/topic/cloud-computing/I556wc0FB9U";
    //when(entry.getLink()).thenReturn("https://groups.google.com/forum/#!topic/cloud-computing-use-cases/mYgrgTh1DP8");
    when(entry.getLink()).thenReturn(url);

    filter.filter(entry);
  }

}