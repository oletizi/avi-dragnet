package com.orionletizi.avi.dragnet.rss.filters.vendor;

import com.orionletizi.avi.dragnet.rss.filters.And;
import com.orionletizi.avi.dragnet.rss.filters.Or;

public class AmazonWebServices extends AbstractVendorFilter {

  public AmazonWebServices() {
    setFilter(
        new And()
            .add(new Or()
                .add(pattern("aws"))
                .add(pattern("amazon\\s*web\\s*services")))
            .add(new Or()
                .add(pattern("elb"))
                .add(pattern("elastic\\s+load\\s*balanc"))
                .add(pattern("f5"))));
  }
}
