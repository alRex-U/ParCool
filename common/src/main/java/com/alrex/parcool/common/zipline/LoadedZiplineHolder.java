package com.alrex.parcool.common.zipline;

import java.util.Set;
import java.util.TreeSet;

public class LoadedZiplineHolder {
    private final TreeSet<Zipline> livingZiplines = new TreeSet<>();

    public void clear() {
        livingZiplines.clear();
    }

    public void notifyZiplineAlive(Zipline zipline) {
        livingZiplines.add(zipline);
    }

    public boolean checkAlive(Zipline zipline) {
        return livingZiplines.contains(zipline);
    }

    public Set<Zipline> getLivingZiplines() {
        return livingZiplines;
    }
}
