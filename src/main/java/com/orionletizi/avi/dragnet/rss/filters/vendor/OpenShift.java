package com.orionletizi.avi.dragnet.rss.filters.vendor;

import com.orionletizi.avi.dragnet.rss.filters.And;
import com.orionletizi.avi.dragnet.rss.filters.Or;

public class OpenShift extends AbstractVendorFilter {

  public OpenShift() {
    setFilter(new And()
        .add(pattern(".*open\\s*shift.*"))
        .add(new Or()
            .add(pattern(".*load\\s*balanc.*"))
            .add(pattern(".*f5.*"))));
  }

}
