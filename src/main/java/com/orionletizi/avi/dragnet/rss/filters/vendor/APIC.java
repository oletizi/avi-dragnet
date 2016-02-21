package com.orionletizi.avi.dragnet.rss.filters.vendor;

import com.orionletizi.avi.dragnet.rss.filters.And;
import com.orionletizi.avi.dragnet.rss.filters.Or;

public class APIC extends AbstractVendorFilter {

  public APIC() {
    setFilter(
        new And()
            .add(pattern(".*apic.*"))
            .add(new Or()
                .add(pattern(".*load\\s*balanc.+"))
                .add(pattern(".*f5.*"))
                .add(pattern(".*citrix.*"))
                .add(pattern(".*a10.*"))));
  }

}
