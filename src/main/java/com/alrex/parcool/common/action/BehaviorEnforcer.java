package com.alrex.parcool.common.action;

import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.function.Supplier;

public class BehaviorEnforcer {
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

    private final LinkedList<Marker> jumpCancelMarks = new LinkedList<>();
    private final LinkedList<Marker> descendFromEdgeCancelMarks = new LinkedList<>();
    private final LinkedList<Marker> sneakCancelMarks = new LinkedList<>();
    @Nullable
    private Enforcer<Vector3d> movementEnforcer = null;

    public void addMarkerCancellingJump(Marker marker) {
        jumpCancelMarks.add(marker);
    }

    public void addMarkerCancellingSneak(Marker marker) {
        sneakCancelMarks.add(marker);
    }

    public void addMarkerCancellingDescendFromEdge(Marker marker) {
        descendFromEdgeCancelMarks.add(marker);
    }

    public void setMarkerEnforceMovePoint(Marker marker, Supplier<Vector3d> movementSupplier) {
        movementEnforcer = new Enforcer<>(marker, movementSupplier);
    }

    public boolean cancelJump() {
        jumpCancelMarks.removeIf(it -> !it.remain());
        return !jumpCancelMarks.isEmpty();
    }

    public boolean cancelSneak() {
        sneakCancelMarks.removeIf(it -> !it.remain());
        return !sneakCancelMarks.isEmpty();
    }

    public boolean cancelDescendFromEdge() {
        descendFromEdgeCancelMarks.removeIf(it -> !it.remain());
        return !descendFromEdgeCancelMarks.isEmpty();
    }

    @Nullable
    public Vector3d getEnforcedMovePoint() {
        if (movementEnforcer != null && movementEnforcer.remain()) {
            return movementEnforcer.behaviorSupplier.get();
        }
        movementEnforcer = null;
        return null;
    }
}
