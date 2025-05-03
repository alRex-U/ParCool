package com.alrex.parcool.common.zipline.impl;

import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.compatibility.Vec3Wrapper;

import net.minecraft.util.math.AxisAlignedBB;

public class QuadraticCurveZipline extends Zipline {
    public QuadraticCurveZipline(Vec3Wrapper point1, Vec3Wrapper point2) {
        super(point1, point2);
    }

    @Override
    public Vec3Wrapper getMidPointOffsetFromStart(float t) {
        double x = getOffsetToEndFromStart().x() * t;
        double z = getOffsetToEndFromStart().z() * t;
        double y = getOffsetToEndFromStart().y() * t * t;
        return new Vec3Wrapper(x, y, z);
    }

    @Override
    public float getSlope(float t) {
        return (float) (2 * t * getOffsetToEndFromStart().y() / getHorizontalDistance());
    }

    @Override
    public float getParameter(Vec3Wrapper position) {
        double offsetX = getOffsetToEndFromStart().x();
        double offsetZ = getOffsetToEndFromStart().z();
        return (float) (((position.x() - getStartPos().x()) * offsetX + (position.z() - getStartPos().z()) * offsetZ) /
                (getHorizontalDistance() * getHorizontalDistance()));
    }

    // length along curve from point of t=0
    private static double getDistanceFrom0(double xzLen, double a) {
        double r = Math.sqrt(1 + 4 * a * a * xzLen * xzLen);
        return 0.5 * (xzLen * r + Math.log(Math.abs(2 * a * xzLen + r)) / (2 * a));
    }

    private static double getDistanceFrom0Derivative(double xzLen, double a) {
        return Math.sqrt(1 + 4 * a * a * xzLen * xzLen);
    }

    @Override
    public double getMovedPositionByParameterApproximately(float currentT, float movement) {
        //Movement along a quadratic curve is difficult to calculate mathematically precisely
        double xzLength = getHorizontalDistance();
        double a = getOffsetToEndFromStart().y() / (xzLength * xzLength);

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
        return new AxisAlignedBB(getStartPos().x(), getStartPos().y(), getStartPos().z(), getEndPos().x(), getEndPos().y(), getEndPos().z())
                .inflate(0.5)
                .contains(position);
    }

    public double getAccurateDistance(Vec3Wrapper position) {
        /*
        // calculate by minimalize squared distance for t

        float a_x= end.x()- start.x();
        float a_y=end.y()-start.y();
        float a_z=end.z()-start.z();

        // calculating critical point
        double[] criticalPoints=new double[3];
        int count=0;
        { // Cardano's formula
            double a=4*a_y*a_y;
            final double b=0;
            double c=2*(a_x+a_z+2*a_y*(start.y()-position.y()));
            double d=2*(a_x*(start.x()-position.x()+a_z*(start.z()-position.z())));
            double p=(-b*b+3*a*c)/(3*a*a);
            double q=(2*b*b*b-9*a*b*c+27*a*a*d)/(27*a*a*a);
            double r=q/2;
            double s=Math.sqrt((27*q*q+4*p*p)/(6*6*3));
            double U=Math.pow(r+s,1./3.);
            double V=Math.pow(r-s,1./3.);
            criticalPoints[count++]=-b/(3*a)-U-V;
        }

        // Identify form of squared distance function
        // ...

        // note : this approach may be a bit complex and take too much time for calculation many times in 1 tick
         */
        throw new UnsupportedOperationException("Not Implemented");
    }
}
