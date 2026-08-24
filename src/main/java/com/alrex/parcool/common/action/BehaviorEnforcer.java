package com.alrex.parcool.common.action;

import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.TreeMap;
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

        public Enforcer(Marker marker, Supplier<T> supplier) {
            this.marker = marker;
            this.behaviorSupplier = supplier;
        }

        public boolean remain() {
            return marker.remain();
        }

        public T getBehavior() {
            return behaviorSupplier.get();
        }
    }

    public static class Marks {
        private final TreeMap<ID, Marker> marks = new TreeMap<>();

        private Marks() {
        }

        public void add(ID id, Marker marker) {
            marks.put(id, marker);
        }

        public boolean enforce() {
            marks.values().removeIf(it -> !it.remain());
            return !marks.isEmpty();
        }

        public boolean remain(ID id) {
            return marks.containsKey(id);
        }

        public boolean remainExcept(ID id) {
            return marks.size() > 1 || !marks.containsKey(id);
        }
    }

    public final Marks noJumpMarks = new Marks();
    public final Marks noDescendingFromEdgeMarks = new Marks();
    public final Marks noSneakMarks = new Marks();
    public final Marks noSprintMarks = new Marks();
    public final Marks noFallFlyingMarks = new Marks();
    public final Marks noShowNameMarks = new Marks();
    public final Marks noPhysicsMarks = new Marks();
    public final Marks sprintMarks = new Marks();
    public final Marks swimmingPoseMarks = new Marks();

    @Nullable
    private Enforcer<Vec3> positionEnforcer = null;
    @Nullable
    private Enforcer<Vec3> movementEnforcer = null;
    @Nullable
    private Enforcer<Vec3> deltaMovementEnforcer = null;
    @Nullable
    private Enforcer<Float> eyeHeightEnforcer = null;

    public void setMarkerEnforcingPosition(Marker marker, Supplier<Vec3> positionSupplier) {
        positionEnforcer = new Enforcer<>(marker, positionSupplier);
    }

    public void setMarkerEnforcingMovePoint(Marker marker, Supplier<Vec3> movementSupplier) {
        movementEnforcer = new Enforcer<>(marker, movementSupplier);
    }

    public void setMarkerEnforcingDeltaMovement(Marker marker, Supplier<Vec3> movementSupplier) {
        deltaMovementEnforcer = new Enforcer<>(marker, movementSupplier);
    }

    public void setMarkerEnforcingEyeHeight(Marker marker, Supplier<Float> eyeHeightSupplier) {
        eyeHeightEnforcer = new Enforcer<>(marker, eyeHeightSupplier);
    }

    @Nullable
    public Vec3 getEnforcedPosition() {
        if (positionEnforcer != null && positionEnforcer.remain()) {
            return positionEnforcer.getBehavior();
        }
        positionEnforcer = null;
        return null;
    }

    @Nullable
    public Vec3 getEnforcedMovePoint() {
        if (movementEnforcer != null && movementEnforcer.remain()) {
            return movementEnforcer.getBehavior();
        }
        movementEnforcer = null;
        return null;
    }

    @Nullable
    public Vec3 getEnforcedDeltaMovement() {
        if (deltaMovementEnforcer != null && deltaMovementEnforcer.remain()) {
            return deltaMovementEnforcer.getBehavior();
        }
        deltaMovementEnforcer = null;
        return null;
    }

    @Nullable
    public Float getEnforcedEyeHeight() {
        if (eyeHeightEnforcer != null && eyeHeightEnforcer.remain()) {
            return eyeHeightEnforcer.getBehavior();
        }
        eyeHeightEnforcer = null;
        return null;
    }
}
