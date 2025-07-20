package com.alrex.parcool.common.zipline.impl;

import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class StraightZipline extends Zipline {
    public StraightZipline(Vec3 point1, Vec3 point2) {
        super(point1, point2);
    }

    public Vec3 getMidPoint(double t) {
        return getMidPointOffsetFromStart(t).add(getStartPos());
    }

    @Override
    public Vec3 getMidPointOffsetFromStart(float t) {
        return getOffsetToEndFromStart().scale(t);
    }

    public Vec3 getMidPointOffsetFromStart(double t) {
        return getOffsetToEndFromStart().scale(t);
    }

    private final float slope = (float) (getOffsetToEndFromStart().y() * Mth.fastInvSqrt(
            getOffsetToEndFromStart().x() * getOffsetToEndFromStart().x()
                    + getOffsetToEndFromStart().z() * getOffsetToEndFromStart().z()
    ));

    @Override
    public float getSlope(float t) {
        return slope;
    }

    @Override
    public float getParameter(Vec3 position) {
        return (float) getParameterD(position);
    }

    public double getParameterD(Vec3 position) {
        double xOffset = getOffsetToEndFromStart().x();
        double yOffset = getOffsetToEndFromStart().y();
        double zOffset = getOffsetToEndFromStart().z();
        double baseXOffset = getStartPos().x() - position.x;
        double baseYOffset = getStartPos().y() - position.y;
        double baseZOffset = getStartPos().z() - position.z;
        return -(xOffset * baseXOffset + yOffset * baseYOffset + zOffset * baseZOffset) / (xOffset * xOffset + yOffset * yOffset + zOffset * zOffset);
    }

    @Override
    public double getMovedPositionByParameterApproximately(float currentT, float movement) {
        float deltaXZ = movement * Mth.invSqrt(slope * slope + 1);
        return currentT + deltaXZ / getHorizontalDistance();
    }

    @Override
    public double getSquaredDistanceApproximately(Vec3 position, double yDistanceScale) {
        double t = getParameterD(position);
        Vec3 mostNearPoint = getMidPoint(t);
        double xOffset = mostNearPoint.x - position.x;
        double zOffset = mostNearPoint.z - position.z;
        double yOffset = (mostNearPoint.y - position.y) * yDistanceScale;
        return xOffset * xOffset + zOffset * zOffset + yOffset * yOffset;
    }

    @Override
    public boolean isPossiblyHangable(Vec3 position) {
        return new AABB(getStartPos().x(), getStartPos().y(), getStartPos().z(), getEndPos().x(), getEndPos().y(), getEndPos().z())
                .inflate(1d)
                .contains(position);
    }
}
