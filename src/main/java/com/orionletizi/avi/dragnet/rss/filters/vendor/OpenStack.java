package com.orionletizi.avi.dragnet.rss.filters.vendor;

import com.orionletizi.avi.dragnet.rss.filters.And;
import com.orionletizi.avi.dragnet.rss.filters.Or;

public class OpenStack extends AbstractVendorFilter {
  public OpenStack() {
    setFilter(
        new And().add(pattern(".*open\\s*stack.*"))
            .add(new Or()
                .add(pattern(".*LBaaS.*"))
                .add(pattern(".*Octavia.*"))
                .add(pattern(".*f5.*"))
                .add(pattern(".*a10.*"))
                .add(pattern(".*radware.*"))));
  }
}
