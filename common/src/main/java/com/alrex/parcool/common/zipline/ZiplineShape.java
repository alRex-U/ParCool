package com.alrex.parcool.common.zipline;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class ZiplineShape {

    protected ZiplineShape(Vec3 point1, Vec3 point2) {
        if (point1.y() <= point2.y()) {
            this.startPos = point1;
            this.endPos = point2;
        } else {
            this.startPos = point2;
            this.endPos = point1;
        }
        endOffsetFromStart = endPos.subtract(startPos);
        horizontalDistance = Math.hypot(endOffsetFromStart.x(), endOffsetFromStart.z());
    }

    private final Vec3 startPos;
    private final Vec3 endPos;
    private final Vec3 endOffsetFromStart;
    private final double horizontalDistance;

    public Vec3 getStartPos() {
        return startPos;
    }

    public Vec3 getEndPos() {
        return endPos;
    }

    public Vec3 getOffsetFromStartToEnd() {
        return endOffsetFromStart;
    }

    public double getHorizontalDistance() {
        return horizontalDistance;
    }

    public boolean conflictsWithSomething(Level world) {
        int count = (int) Math.floor(getHorizontalDistance());
        for (int i = 1; i < count - 1; i++) {
            Vec3 midPoint = getMidPoint(((float) i / count));
            final double d = 0.2;
            if (!world.noCollision(new AABB(
                    midPoint.subtract(d, d, d),
                    midPoint.add(d, d, d)
            ))) {
                return true;
            }
        }
        return false;
    }

    // t is auxiliary variable in this class
    // start.y should not be higher than end.y
    // assert(start.y <= end.y)

    // return middle point of zipline
    // the x is start.x + (end.x - start.x) * t
    // also same about z
    // y is decided by calculated x and z
    public Vec3 getMidPoint(float t) {
        return getMidPointOffsetFromStart(t).add(getStartPos());
    }

    public abstract Vec3 getMidPointOffsetFromStart(float t);

    // return slope of zipline
    // equals dy/d(t * sqrt(x^2 * z^2))
    // maybe helpful for calculating acceleration
    public abstract float getSlope(float t);

    // return t
    public abstract float getParameter(Vec3 position);

    //
    public abstract double getMovedPositionByParameterApproximately(float currentT, float movement);

    // return not accurate distance
    public double getSquaredDistanceApproximately(Vec3 position) {
        return getSquaredDistanceApproximately(position, 1);
    }

    public abstract double getSquaredDistanceApproximately(Vec3 position, double yDistanceScale);

    public abstract double getLength();

    public boolean isPossiblyHangAble(Vec3 position) {
        return new AABB(getStartPos().x(), getStartPos().y(), getStartPos().z(), getEndPos().x(), getEndPos().y(), getEndPos().z())
                .inflate(1d)
                .contains(position);
    }
}
