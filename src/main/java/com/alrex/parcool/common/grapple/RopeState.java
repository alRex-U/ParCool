package com.alrex.parcool.common.grapple;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RopeState {
    private static final double EDGE_CLEARANCE = 0.13;

    private static final double RAY_SHRINK = 0.03;

    private static final double MIN_FREE_LENGTH = 0.6;

    private static final double GUARANTEED_FREE_LENGTH = 2.5;

    private static final double MIN_HINGE_SEPARATION = 0.3;
    private static final int SLIDE_ITERATIONS = 18;
    private static final int SLIDE_PASSES = 2;
    private static final int MAX_WRAPS_PER_TICK = 6;

    private static final double STILL_WRAPPED_OPENING = 0.35;

    private static final double TAUT_MARGIN = 0.25;
    private static final double DEGENERATE = 1.0e-4;

    private static final double MAX_EDGE_LENGTH = 24;
    private static final int MAX_EDGE_GROWTH_PER_SOLVE = 6;
    private static final double EDGE_END_EPSILON = 1.0e-3;

    private static final double SUPPORT_PROBE = 0.28;

    private static final class Hinge {
        private Vec3 start;
        private final Vec3 direction;
        private double length;

        private final Vec3 outward;
        private double parameter;

        private Hinge(Vec3 start, Vec3 direction, double length, Vec3 outward, double parameter) {
            this.start = start;
            this.direction = direction;
            this.length = length;
            this.outward = outward;
            this.parameter = parameter;
        }

        private Vec3 position() {
            return start.add(direction.scale(parameter));
        }
    }

    private final Vec3 anchor;
    private final ArrayList<Hinge> hinges = new ArrayList<>(4);

    public int contactCount() {
        return hinges.size();
    }

    private final ArrayList<Vec3> positions = new ArrayList<>(4);
    private final List<Vec3> positionsView = Collections.unmodifiableList(positions);

    private double totalLength;
    private double wrappedLength = 0;

    public RopeState(Vec3 anchor, double length) {
        this.anchor = anchor;
        this.totalLength = length;
    }

    public Vec3 anchor() {
        return anchor;
    }

    public List<Vec3> bends() {
        return positionsView;
    }

    public Vec3 pivot() {
        return hinges.isEmpty() ? anchor : hinges.get(hinges.size() - 1).position();
    }

    public double length() {
        return totalLength;
    }

    public double wrappedLength() {
        return wrappedLength;
    }

    public double freeLength() {
        return Math.max(MIN_FREE_LENGTH, totalLength - wrappedLength);
    }

    public void setFreeLength(double newFreeLength) {
        this.totalLength = wrappedLength + Math.max(MIN_FREE_LENGTH, newFreeLength);
    }

    public void update(Level level, Entity owner, Vec3 playerPos, int maxHinges, boolean allowWrap) {
        update(level, owner, playerPos, maxHinges, allowWrap, true);
    }

    public void update(Level level, Entity owner, Vec3 playerPos, int maxHinges, boolean allowWrap, boolean allowRelease) {
        dropUnsupported(level);
        slide(level, playerPos);

        boolean taut = freeLength() - pivot().distanceTo(playerPos) < TAUT_MARGIN;

        if (allowRelease) release(level, owner, playerPos, taut);

        if (allowWrap && taut) {
            int added = 0;
            while (hinges.size() < maxHinges && added < MAX_WRAPS_PER_TICK && wrapOnce(level, owner, playerPos)) {
                added++;
            }
            if (added > 0) slide(level, playerPos);
        }

        refresh();

        double needed = Math.max(GUARANTEED_FREE_LENGTH, pivot().distanceTo(playerPos));
        totalLength = Math.max(totalLength, wrappedLength + needed);
    }

    private void slide(Level level, Vec3 playerPos) {
        if (hinges.isEmpty()) return;
        for (int pass = 0; pass < SLIDE_PASSES; pass++) {
            for (int i = 0; i < hinges.size(); i++) {
                Hinge hinge = hinges.get(i);
                Vec3 previous = i == 0 ? anchor : hinges.get(i - 1).position();
                Vec3 next = i == hinges.size() - 1 ? playerPos : hinges.get(i + 1).position();
                hinge.parameter = solveContact(hinge, previous, next);
                growWhileClamped(level, hinge, previous, next);
            }
        }
    }

    private static void growWhileClamped(Level level, Hinge hinge, Vec3 from, Vec3 to) {
        for (int i = 0; i < MAX_EDGE_GROWTH_PER_SOLVE; i++) {
            boolean atStart = hinge.parameter <= EDGE_END_EPSILON;
            boolean atEnd = hinge.parameter >= hinge.length - EDGE_END_EPSILON;
            if (!atStart && !atEnd) return;
            if (!grow(level, hinge, atStart)) return;
            hinge.parameter = solveContact(hinge, from, to);
        }
    }

    private static boolean grow(Level level, Hinge hinge, boolean atStart) {
        if (hinge.length >= MAX_EDGE_LENGTH) return false;
        Vec3 probe = atStart
                ? hinge.start.subtract(hinge.direction.scale(0.5))
                : hinge.start.add(hinge.direction.scale(hinge.length + 0.5));
        if (!isFreeSpace(level, probe)) return false;
        if (isFreeSpace(level, probe.subtract(hinge.outward.scale(SUPPORT_PROBE)))) return false;

        if (atStart) hinge.start = hinge.start.subtract(hinge.direction);
        hinge.length += 1;
        return true;
    }

    private static double solveContact(Hinge hinge, Vec3 from, Vec3 to) {
        double low = 0;
        double high = hinge.length;
        for (int i = 0; i < SLIDE_ITERATIONS; i++) {
            double third = (high - low) / 3;
            double first = low + third;
            double second = high - third;
            if (pathLength(hinge, first, from, to) < pathLength(hinge, second, from, to)) {
                high = second;
            } else {
                low = first;
            }
        }
        return (low + high) * 0.5;
    }

    private static double pathLength(Hinge hinge, double parameter, Vec3 from, Vec3 to) {
        Vec3 point = hinge.start.add(hinge.direction.scale(parameter));
        return from.distanceTo(point) + point.distanceTo(to);
    }

    private void release(Level level, Entity owner, Vec3 playerPos, boolean taut) {
        if (hinges.isEmpty()) return;
        int last = hinges.size() - 1;
        Hinge hinge = hinges.get(last);
        Vec3 position = hinge.position();
        Vec3 previous = last == 0 ? anchor : hinges.get(last - 1).position();

        if (!isVisible(level, owner, previous, playerPos)) return;

        Vec3 toPrevious = previous.subtract(position);
        Vec3 toPlayer = playerPos.subtract(position);
        if (taut && toPrevious.lengthSqr() > DEGENERATE && toPlayer.lengthSqr() > DEGENERATE) {
            double opening = toPrevious.normalize().add(toPlayer.normalize()).dot(hinge.outward);
            if (opening > STILL_WRAPPED_OPENING) return;
        }
        hinges.remove(last);
    }

    private void dropUnsupported(Level level) {
        for (int i = hinges.size() - 1; i >= 0; i--) {
            Hinge hinge = hinges.get(i);
            if (isFreeSpace(level, hinge.position().subtract(hinge.outward.scale(SUPPORT_PROBE)))) {
                hinges.remove(i);
            }
        }
    }

    private boolean wrapOnce(Level level, Entity owner, Vec3 playerPos) {
        Vec3 pivot = pivot();
        if (pivot.distanceToSqr(playerPos) < DEGENERATE) return false;

        HitResult hit = clip(level, owner, pivot, playerPos);
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) return false;

        BlockPos support = blockHit.getBlockPos();
        VoxelShape shape = level.getBlockState(support).getCollisionShape(level, support);
        if (shape.isEmpty()) return false;
        AABB box = shape.bounds().move(support);

        List<Hinge> candidates = new ArrayList<>(12);
        List<Double> scores = new ArrayList<>(12);
        collectEdges(box, pivot, playerPos, candidates, scores);

        Hinge best = null;
        double bestScore = Double.MAX_VALUE;
        Hinge partial = null;
        double partialScore = Double.MAX_VALUE;
        double remaining = pivot.distanceTo(playerPos);

        for (int i = 0; i < candidates.size(); i++) {
            double score = scores.get(i);
            if (score >= bestScore) continue;
            Hinge candidate = candidates.get(i);
            Vec3 contact = candidate.position();
            if (contact.distanceToSqr(pivot) < MIN_HINGE_SEPARATION * MIN_HINGE_SEPARATION) continue;
            if (!isFreeSpace(level, contact)) continue;
            if (!isVisible(level, owner, pivot, contact)) continue;

            if (isVisible(level, owner, contact, playerPos)) {
                bestScore = score;
                best = candidate;
            } else if (score < partialScore
                    && contact.distanceTo(playerPos) < remaining - MIN_HINGE_SEPARATION) {
                partialScore = score;
                partial = candidate;
            }
        }

        Hinge chosen = best != null ? best : partial;
        if (chosen == null) return false;

        hinges.add(chosen);
        return true;
    }

    private static void collectEdges(
            AABB box,
            Vec3 from,
            Vec3 to,
            List<Hinge> candidates,
            List<Double> scores
    ) {
        for (int axis = 0; axis < 3; axis++) {
            for (int first = 0; first <= 1; first++) {
                for (int second = 0; second <= 1; second++) {
                    Hinge hinge = buildEdge(box, axis, first, second);
                    if (hinge == null) continue;
                    hinge.parameter = solveContact(hinge, from, to);
                    candidates.add(hinge);
                    scores.add(pathLength(hinge, hinge.parameter, from, to));
                }
            }
        }
    }

    @Nullable
    private static Hinge buildEdge(AABB box, int axis, int firstSign, int secondSign) {
        double[] min = {box.minX, box.minY, box.minZ};
        double[] max = {box.maxX, box.maxY, box.maxZ};
        int firstAxis = (axis + 1) % 3;
        int secondAxis = (axis + 2) % 3;

        double length = max[axis] - min[axis];
        if (length < DEGENERATE) return null;

        double[] start = new double[3];
        double[] outward = new double[3];
        start[axis] = min[axis];
        start[firstAxis] = firstSign == 0 ? min[firstAxis] : max[firstAxis];
        start[secondAxis] = secondSign == 0 ? min[secondAxis] : max[secondAxis];
        outward[axis] = 0;
        outward[firstAxis] = firstSign == 0 ? -1 : 1;
        outward[secondAxis] = secondSign == 0 ? -1 : 1;

        Vec3 outwardVector = new Vec3(outward[0], outward[1], outward[2]).normalize();
        Vec3 startVector = new Vec3(start[0], start[1], start[2]).add(outwardVector.scale(EDGE_CLEARANCE));
        Vec3 direction = new Vec3(axis == 0 ? 1 : 0, axis == 1 ? 1 : 0, axis == 2 ? 1 : 0);
        return new Hinge(startVector, direction, length, outwardVector, 0);
    }

    private void refresh() {
        positions.clear();
        wrappedLength = 0;
        Vec3 previous = anchor;
        for (Hinge hinge : hinges) {
            Vec3 position = hinge.position();
            wrappedLength += previous.distanceTo(position);
            positions.add(position);
            previous = position;
        }
    }

    private static boolean isFreeSpace(Level level, Vec3 point) {
        return level.noCollision(new AABB(point, point).inflate(0.01));
    }

    private static boolean isVisible(Level level, Entity owner, Vec3 from, Vec3 to) {
        Vec3 delta = to.subtract(from);
        double length = delta.length();
        if (length <= 2 * RAY_SHRINK) return true;
        Vec3 direction = delta.scale(1 / length);
        return clip(level, owner,
                from.add(direction.scale(RAY_SHRINK)),
                to.subtract(direction.scale(RAY_SHRINK))
        ).getType() == HitResult.Type.MISS;
    }

    public static boolean isSegmentClear(Level level, Entity owner, Vec3 from, Vec3 to) {
        return isVisible(level, owner, from, to);
    }

    private static HitResult clip(Level level, Entity owner, Vec3 from, Vec3 to) {
        return level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, owner));
    }
}
