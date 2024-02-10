package com.alrex.parcool.common.action;

import java.util.LinkedList;

public class CancelMarks {
    public interface Marker {
        public boolean remain();
    }

    LinkedList<Marker> jumpCancelMarks = new LinkedList<>();

    public void addMarkerCancellingJump(Marker marker) {
        jumpCancelMarks.add(marker);
    }

    public boolean cancelJump() {
        jumpCancelMarks.removeIf(it -> !it.remain());
        return !jumpCancelMarks.isEmpty();
    }
}
