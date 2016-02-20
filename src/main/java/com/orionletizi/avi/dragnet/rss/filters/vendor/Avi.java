package com.orionletizi.avi.dragnet.rss.filters.vendor;

import com.orionletizi.avi.dragnet.rss.filters.Or;

public class Avi extends AbstractVendorFilter {

  public Avi() {
    setFilter(new Or()
        .add(pattern("avi network"))
        .add(pattern("load\\s*balanc"))
        .add(pattern("reverse\\s+proxy"))
        .add(pattern("haproxy")));
  }
}
