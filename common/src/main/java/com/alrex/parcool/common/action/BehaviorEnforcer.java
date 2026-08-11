package com.alrex.parcool.common.action;

import net.minecraft.world.entity.Pose;
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

    private final TreeMap<ID, Marker> enforceNoJumpMarks = new TreeMap<>();
    private final TreeMap<ID, Marker> enforceNoDescendingFromEdgeMarks = new TreeMap<>();
    private final TreeMap<ID, Marker> enforceNoSneakMarks = new TreeMap<>();
    private final TreeMap<ID, Marker> enforceNoSprintMarks = new TreeMap<>();
    private final TreeMap<ID, Marker> enforceNoFallFlyingMarks = new TreeMap<>();
    private final TreeMap<ID, Marker> enforceNoShowNameMarks = new TreeMap<>();
    private final TreeMap<ID, Marker> enforceNoPhysicsMarks = new TreeMap<>();
    private final TreeMap<ID, Marker> enforceSprintMarks = new TreeMap<>();

    @Nullable
    private Enforcer<Vec3> positionEnforcer = null;
    @Nullable
    private Enforcer<Vec3> movementEnforcer = null;
    @Nullable
    private Enforcer<Vec3> deltaMovementEnforcer = null;
    @Nullable
    private Enforcer<Float> eyeHeightEnforcer = null;
    @Nullable
    private Enforcer<Pose> forcedPoseEnforcer = null;

    public void addMarkerEnforcingNoJump(ID id, Marker marker) {
        enforceNoJumpMarks.put(id, marker);
    }

    public void addMarkerEnforcingNoSneak(ID id, Marker marker) {
        enforceNoSneakMarks.put(id, marker);
    }

    public void addMarkerEnforcingNoDescendingFromEdge(ID id, Marker marker) {
        enforceNoDescendingFromEdgeMarks.put(id, marker);
    }

    public void addMarkerEnforcingNoSprint(ID id, Marker marker) {
        enforceNoSprintMarks.put(id, marker);
    }

    public void addMarkerEnforcingNoFallFlying(ID id, Marker marker) {
        enforceNoFallFlyingMarks.put(id, marker);
    }

    public void addMarkerEnforcingNoShowName(ID id, Marker marker) {
        enforceNoShowNameMarks.put(id, marker);
    }

    public void addMarkerEnforcingSprint(ID id, Marker marker) {
        enforceSprintMarks.put(id, marker);
    }

    public void addMarkerEnforcingNoPhysics(ID id, Marker marker) {
        enforceNoPhysicsMarks.put(id, marker);
    }

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

    public void setMarkerEnforcingForcedPose(Marker marker, Supplier<Pose> forcedPoseSupplier) {
        forcedPoseEnforcer = new Enforcer<>(marker, forcedPoseSupplier);
    }

    public boolean enforceNoJump() {
        enforceNoJumpMarks.values().removeIf(it -> !it.remain());
        return !enforceNoJumpMarks.isEmpty();
    }

    public boolean enforceNoSneak() {
        enforceNoSneakMarks.values().removeIf(it -> !it.remain());
        return !enforceNoSneakMarks.isEmpty();
    }

    public boolean enforceNoDescendingFromEdge() {
        enforceNoDescendingFromEdgeMarks.values().removeIf(it -> !it.remain());
        return !enforceNoDescendingFromEdgeMarks.isEmpty();
    }

    public boolean enforceNoSprint() {
        enforceNoSprintMarks.values().removeIf(it -> !it.remain());
        return !enforceNoSprintMarks.isEmpty();
    }

    public boolean enforceNoFallFlying() {
        enforceNoFallFlyingMarks.values().removeIf(it -> !it.remain());
        return !enforceNoFallFlyingMarks.isEmpty();
    }

    public boolean enforceNoShowingName() {
        enforceNoShowNameMarks.values().removeIf(it -> !it.remain());
        return !enforceNoShowNameMarks.isEmpty();
    }

    public boolean enforceSprint() {
        enforceSprintMarks.values().removeIf(it -> !it.remain());
        return !enforceSprintMarks.isEmpty();
    }

    public boolean enforceNoPhysics() {
        enforceNoPhysicsMarks.values().removeIf(it -> !it.remain());
        return !enforceNoPhysicsMarks.isEmpty();
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

    @Nullable
    public Pose getEnforcedForcedPose() {
        if (forcedPoseEnforcer != null && forcedPoseEnforcer.remain()) {
            return forcedPoseEnforcer.getBehavior();
        }
        forcedPoseEnforcer = null;
        return null;
    }
}
