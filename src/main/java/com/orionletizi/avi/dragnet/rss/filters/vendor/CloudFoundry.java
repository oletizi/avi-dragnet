package com.orionletizi.avi.dragnet.rss.filters.vendor;

import com.orionletizi.avi.dragnet.rss.FeedFilter;
import com.orionletizi.avi.dragnet.rss.filters.And;
import com.orionletizi.avi.dragnet.rss.filters.Or;

public class CloudFoundry extends AbstractVendorFilter {

  public CloudFoundry(final FeedFilter filter) {
    setFilter(new And()
        .add(pattern("cloud\\s*foundry"))
        .add(new Or()
            .add(pattern("load\\s*balanc"))
            .add(pattern("f5"))));
  }
}
