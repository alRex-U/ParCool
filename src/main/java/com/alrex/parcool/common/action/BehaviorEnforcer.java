package com.alrex.parcool.common.action;

import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Supplier;

public class BehaviorEnforcer {
    public static class ID implements Comparable<ID> {
        private static int idValue = 0;

        private static ID newID() {
            return new ID(idValue++);
        }

        private final int value;

        private ID(int value) {
            this.value = value;
        }

        @Override
        public int compareTo(ID o) {
            return Integer.compare(this.value, o.value);
        }
    }

    public static ID newID() {
        return ID.newID();
    }

    public interface Marker {
        boolean remain();
    }

    public static class Enforcer<T> {
        final Marker marker;
        final Supplier<T> behaviorSupplier;

        Enforcer(Marker marker, Supplier<T> supplier) {
            this.marker = marker;
            this.behaviorSupplier = supplier;
        }

        boolean remain() {
            return marker.remain();
        }

        T getBehavior() {
            return behaviorSupplier.get();
        }
    }

    private final TreeMap<ID, Marker> jumpCancelMarks = new TreeMap<>();
    private final TreeMap<ID, Marker> descendFromEdgeCancelMarks = new TreeMap<>();
    private final ConcurrentSkipListMap<ID, Marker> sneakCancelMarks = new ConcurrentSkipListMap<>();
    private final TreeMap<ID, Marker> sprintCancelMarks = new TreeMap<>();
    private final TreeMap<ID, Marker> fallFlyingCancelMarks = new TreeMap<>();
    private final TreeMap<ID, Marker> showNameCancelMarks = new TreeMap<>();
    @Nullable
    private Enforcer<Vector3d> movementEnforcer = null;
    @Nullable
    private Enforcer<Vector3d> positionEnforcer = null;

    public void addMarkerCancellingJump(ID id, Marker marker) {
        jumpCancelMarks.put(id, marker);
    }

    public void addMarkerCancellingSneak(ID id, Marker marker) {
        sneakCancelMarks.put(id, marker);
    }

    public void addMarkerCancellingDescendFromEdge(ID id, Marker marker) {
        descendFromEdgeCancelMarks.put(id, marker);
    }

    public void addMarkerCancellingSprint(ID id, Marker marker) {
        sprintCancelMarks.put(id, marker);
    }

    public void addMarkerCancellingFallFlying(ID id, Marker marker) {
        fallFlyingCancelMarks.put(id, marker);
    }

    public void addMarkerCancellingShowName(ID id, Marker marker) {
        showNameCancelMarks.put(id, marker);
    }

    public void setMarkerEnforceMovePoint(Marker marker, Supplier<Vector3d> movementSupplier) {
        movementEnforcer = new Enforcer<>(marker, movementSupplier);
    }

    public void setMarkerEnforcePosition(Marker marker, Supplier<Vector3d> movementSupplier) {
        positionEnforcer = new Enforcer<>(marker, movementSupplier);
    }

    public boolean cancelJump() {
        jumpCancelMarks.values().removeIf(it -> !it.remain());
        return !jumpCancelMarks.isEmpty();
    }

    public boolean cancelSneak() {
        sneakCancelMarks.values().removeIf(it -> !it.remain());
        return !sneakCancelMarks.isEmpty();
    }

    public boolean cancelDescendFromEdge() {
        descendFromEdgeCancelMarks.values().removeIf(it -> !it.remain());
        return !descendFromEdgeCancelMarks.isEmpty();
    }

    public boolean cancelSprint() {
        sprintCancelMarks.values().removeIf(it -> !it.remain());
        return !sprintCancelMarks.isEmpty();
    }

    public boolean cancelFallFlying() {
        fallFlyingCancelMarks.values().removeIf(it -> !it.remain());
        return !fallFlyingCancelMarks.isEmpty();
    }

    public boolean cancelShowingName() {
        showNameCancelMarks.values().removeIf(it -> !it.remain());
        return !showNameCancelMarks.isEmpty();
    }

    @Nullable
    public Vector3d getEnforcedMovePoint() {
        if (movementEnforcer != null && movementEnforcer.remain()) {
            return movementEnforcer.getBehavior();
        }
        movementEnforcer = null;
        return null;
    }

    @Nullable
    public Vector3d getEnforcedPosition() {
        if (positionEnforcer != null && positionEnforcer.remain()) {
            return positionEnforcer.getBehavior();
        }
        positionEnforcer = null;
        return null;
    }
}
