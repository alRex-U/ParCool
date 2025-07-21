package com.alrex.parcool.common.zipline.impl;

import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class StraightZipline extends Zipline {
    public StraightZipline(Vector3d point1, Vector3d point2) {
        super(point1, point2);
    }

    public Vector3d getMidPoint(double t) {
        return getMidPointOffsetFromStart(t).add(getStartPos());
    }

    @Override
    public Vector3d getMidPointOffsetFromStart(float t) {
        return getOffsetToEndFromStart().scale(t);
    }

    public Vector3d getMidPointOffsetFromStart(double t) {
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
    public float getParameter(Vector3d position) {
        return (float) getParameterD(position);
    }

    public double getParameterD(Vector3d position) {
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
    public double getSquaredDistanceApproximately(Vector3d position, double yDistanceScale) {
        double t = getParameterD(position);
        Vector3d mostNearPoint = getMidPoint(t);
        double xOffset = mostNearPoint.x - position.x;
        double zOffset = mostNearPoint.z - position.z;
        double yOffset = (mostNearPoint.y - position.y) * yDistanceScale;
        return xOffset * xOffset + zOffset * zOffset + yOffset * yOffset;
    }

    @Override
    public boolean isPossiblyHangable(Vector3d position) {
        return new AxisAlignedBB(getStartPos().x(), getStartPos().y(), getStartPos().z(), getEndPos().x(), getEndPos().y(), getEndPos().z())
                .inflate(1d)
                .contains(position);
    }
}
