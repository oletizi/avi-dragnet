package com.orionletizi.avi.dragnet.rss;

import com.rometools.rome.feed.synd.SyndEntry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Dedupe {

  public List<SyndEntry> dedupe(final List<SyndEntry> source) {
    final Set<DedupeEntry> set = new HashSet<>();
    for (SyndEntry entry : source) {
      set.add(new DedupeEntry(entry));
    }

    final List<SyndEntry> rv = new ArrayList<>();
    for (DedupeEntry entry : set) {
      rv.add(entry.entry);
    }

    rv.sort((o1, o2) -> o1.getPublishedDate().compareTo(o2.getPublishedDate()));
    return rv;
  }

  private static class DedupeEntry {

    private SyndEntry entry;

    public DedupeEntry(final SyndEntry entry) {
      this.entry = entry;
    }

    @Override
    public boolean equals(Object o) {
      boolean rv = false;
      if (o != null && o instanceof DedupeEntry) {
        final DedupeEntry that = (DedupeEntry) o;
        rv = this.entry.getUri().equals(that.entry.getUri());
      }
      return rv;
    }

    @Override
    public int hashCode() {
      return entry.getUri().hashCode();
    }
  }
}
