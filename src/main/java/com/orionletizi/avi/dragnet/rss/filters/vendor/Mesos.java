package com.orionletizi.avi.dragnet.rss.filters.vendor;

import com.orionletizi.avi.dragnet.rss.filters.And;
import com.orionletizi.avi.dragnet.rss.filters.Or;

public class Mesos extends AbstractVendorFilter {
  public Mesos() {
    setFilter(
        new And()
            .add(pattern("mesos"))
            .add(new Or()
                .add(pattern("load\\s*balanc"))
                .add(pattern("service\\s+discovery"))));
  }
}
