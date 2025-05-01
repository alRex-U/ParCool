package com.alrex.parcool.common.zipline.impl;

import com.alrex.parcool.api.compatibility.Vec3Wrapper;
import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class StraightZipline extends Zipline {
    public StraightZipline(Vec3Wrapper point1, Vec3Wrapper point2) {
        super(point1, point2);
    }

    public Vec3Wrapper getMidPoint(double t) {
        return getMidPointOffsetFromStart(t).add(getStartPos());
    }

    @Override
    public Vec3Wrapper getMidPointOffsetFromStart(float t) {
        return getOffsetToEndFromStart().scale(t);
    }

    public Vec3Wrapper getMidPointOffsetFromStart(double t) {
        return getOffsetToEndFromStart().scale(t);
    }

    private final float slope = (float) (getOffsetToEndFromStart().y() * MathHelper.fastInvSqrt(
            getOffsetToEndFromStart().x() * getOffsetToEndFromStart().x()
                    + getOffsetToEndFromStart().z() * getOffsetToEndFromStart().z()
    ));

    @Override
    public float getSlope(float t) {
        return slope;
    }

    @Override
    public float getParameter(Vec3Wrapper position) {
        return (float) getParameterD(position);
    }

    public double getParameterD(Vec3Wrapper position) {
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
        float deltaXZ = movement * MathHelper.fastInvSqrt(slope * slope + 1);
        return currentT + deltaXZ / getHorizontalDistance();
    }

    @Override
    public double getSquaredDistanceApproximately(Vec3Wrapper position) {
        double t = getParameterD(position);
        Vec3Wrapper mostNearPoint = getMidPoint(t);
        return mostNearPoint.distanceToSqr(position);
    }

    @Override
    public boolean isPossiblyHangable(Vec3Wrapper position) {
        return new AxisAlignedBB(getStartPos().x(), getStartPos().y(), getStartPos().z(), getEndPos().x(), getEndPos().y(), getEndPos().z())
                .inflate(0.5)
                .contains(position);
    }
}
