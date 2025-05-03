package com.alrex.parcool.common.zipline.impl;

import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.compatibility.Vec3Wrapper;

import net.minecraft.util.math.AxisAlignedBB;

public class GeneralQuadraticCurveZipline extends Zipline {

    public GeneralQuadraticCurveZipline(Vec3Wrapper point1, Vec3Wrapper point2, double lowestPointOffset) {
        super(point1, point2);
        double straightDistance = Math.hypot(getHorizontalDistance(), getOffsetToEndFromStart().y());
        double yOffsetAtVertex = Math.abs(lowestPointOffset);

        tAtVertex = Math.abs(getOffsetToEndFromStart().y()) < 0.005 ?
                0.5 :
                (Math.sqrt(yOffsetAtVertex * (yOffsetAtVertex + getOffsetToEndFromStart().y())) - yOffsetAtVertex) / getOffsetToEndFromStart().y();
        distOfXZToVertex = tAtVertex * getHorizontalDistance();
        getMidPointOffsetFromStart$a = Math.abs(tAtVertex - 0.5) < 0.005 ?
                4 * yOffsetAtVertex :
                getOffsetToEndFromStart().y() / (1 - 2 * tAtVertex);
        getMovedPositionByParameterApproximately$a = getOffsetToEndFromStart().y() / (getHorizontalDistance() * getHorizontalDistance());
        getDistanceFrom0$offset = getDistance(-distOfXZToVertex, getMovedPositionByParameterApproximately$a);
    }

    private final double tAtVertex;
    private final double distOfXZToVertex;

    private final double getMidPointOffsetFromStart$a;

    @Override
    public Vec3Wrapper getMidPointOffsetFromStart(float t) {
        return new Vec3Wrapper(
                getOffsetToEndFromStart().x() * t,
                getMidPointOffsetFromStart$a * t * (t - 2 * tAtVertex),
                getOffsetToEndFromStart().z() * t
        );
    }

    @Override
    public float getSlope(float t) {
        return (float) (2 * (t - tAtVertex) * getMidPointOffsetFromStart$a / getHorizontalDistance());
    }

    @Override
    public float getParameter(Vec3Wrapper position) {
        double offsetX = getOffsetToEndFromStart().x();
        double offsetZ = getOffsetToEndFromStart().z();
        return (float) (((position.x() - getStartPos().x()) * offsetX + (position.z() - getStartPos().z()) * offsetZ) /
                (getHorizontalDistance() * getHorizontalDistance()));
    }

    private double getDistance(double xzLen, double a) {
        double r = Math.sqrt(1 + 4 * a * a * xzLen * xzLen);
        return 0.5 * (xzLen * r + Math.log(Math.abs(2 * a * xzLen + r)) / (2 * a));
    }

    private final double getDistanceFrom0$offset;

    // length along curve from point of t=0
    private double getDistanceFrom0(double xzLen, double a) {
        return getDistance(xzLen - distOfXZToVertex, a) - getDistanceFrom0$offset;
    }

    private double getDistanceFrom0Derivative(double xzLen, double a) {
        xzLen = xzLen - distOfXZToVertex;
        return Math.sqrt(1 + 4 * a * a * xzLen * xzLen);
    }

    private final double getMovedPositionByParameterApproximately$a;

    @Override
    public double getMovedPositionByParameterApproximately(float currentT, float movement) {
        //Movement along a quadratic curve is difficult to calculate mathematically precisely
        //note : Catenary curve is possible to arc length parameterize
        // so maybe this can be approximate by that way


        double xzLength = getHorizontalDistance();
        double a = getMovedPositionByParameterApproximately$a;

        // use linear interpolation for avoiding division by zero
        if (Math.abs(a) < 0.005) {
            return (movement / xzLength) + currentT;
        }

        double destination = getDistanceFrom0(currentT * xzLength, a) + movement;
        //Newton's method
        double oldInterim;
        double interim = currentT * xzLength;
        for (int i = 0; i < 20; i++) {
            oldInterim = interim;
            interim -= (getDistanceFrom0(interim, a) - destination) / getDistanceFrom0Derivative(interim, a);
            if (Math.abs(oldInterim - interim) < 0.001) {
                return interim / xzLength;
            }
        }
        return interim / xzLength;
    }

    @Override
    public double getSquaredDistanceApproximately(Vec3Wrapper position) {
        float t = getParameter(position);
        Vec3Wrapper simplifiedNearestPoint = getMidPoint(t);
        return position.distanceToSqr(simplifiedNearestPoint);
    }

    @Override
    public boolean isPossiblyHangable(Vec3Wrapper position) {
        return new AxisAlignedBB(getStartPos().x(), getMidPoint((float) tAtVertex).y(), getStartPos().z(), getEndPos().x(), getEndPos().y(), getEndPos().z())
                .inflate(0.5)
                .contains(position);
    }
}
