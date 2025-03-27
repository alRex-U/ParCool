package com.alrex.parcool.utilities;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ZiplineUtil {
    public static final double MAXIMUM_DISTANCE = 30.;
    // t is auxiliary variable in this class
    // start.y should not be higher than end.y
    // assert(start.y <= end.y)

    // return middle point of zipline
    // the x is start.x + (end.x - start.x) * t
    // also same about z
    // y is decided by calculated x and z
    public static Vector3f getMidPoint(Vector3f start, Vector3f end, float t) {
        Vector3f offset = getMidPoint(new Vector3f(
                end.x() - start.x(),
                end.y() - start.y(),
                end.z() - start.z()
        ), t);
        return new Vector3f(
                offset.x() + start.x(),
                offset.y() + start.y(),
                offset.z() + start.y()
        );
    }

    public static Vector3f getMidPoint(Vector3f endOffsetFromStart, float t) {
        float x = endOffsetFromStart.x() * t;
        float z = endOffsetFromStart.z() * t;
        float y = endOffsetFromStart.y() * t * t;
        return new Vector3f(x, y, z);
    }

    public static Vector3d getMidPointD(Vector3d start, Vector3d end, double t) {
        Vector3d offset = getMidPointD(end.subtract(start), t);
        return offset.add(start);
    }

    public static Vector3d getMidPointD(Vector3d endOffsetFromStart, double t) {
        double x = endOffsetFromStart.x() * t;
        double z = endOffsetFromStart.z() * t;
        double y = endOffsetFromStart.y() * t * t;
        return new Vector3d(x, y, z);
    }

    // return slope of zipline
    // equals dy/dt
    // maybe helpful for calculating acceleration
    public static float getSlope(Vector3f start, Vector3f end, float t) {
        return getSlope(
                new Vector3f(
                        end.x() - start.x(),
                        end.y() - start.y(),
                        end.z() - start.z()
                ),
                t
        );
    }

    public static float getSlope(Vector3f endOffsetFromStart, float t) {
        return 2 * endOffsetFromStart.y() * MathHelper.sqrt(endOffsetFromStart.x() * endOffsetFromStart.x() + endOffsetFromStart.z() * endOffsetFromStart.z()) * t;
    }

    public static Vector3f getActualZiplinePointAsFloat(BlockPos pos) {
        return new Vector3f(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
    }

    public static Vector3d getActualZiplinePointAsDouble(BlockPos pos) {
        return new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    @Nullable
    public static ZiplineRopeEntity getHangableZipline(World world, PlayerEntity player) {
        List<ZiplineRopeEntity> entities = world.getEntitiesOfClass(
                ZiplineRopeEntity.class,
                player.getBoundingBox().inflate(MAXIMUM_DISTANCE * 0.52)
        );
        Vector3d grabPos = player.position().add(0, player.getBbHeight() * 1.11, 0);
        for (ZiplineRopeEntity zipline : entities) {
            Vector3d start = getActualZiplinePointAsDouble(zipline.getStartPos());
            Vector3d end = getActualZiplinePointAsDouble(zipline.getEndPos());
            if (possiblyHangable(start, end, grabPos)) {
                double distSqr = getAboutSquaredDistance(start, end, grabPos);
                double catchRange = player.getBbWidth() * 0.35;
                if (distSqr < catchRange * catchRange) return zipline;
            }
        }
        return null;
    }

    // return t
    public static float getParameterFromX(float startX, float endX, float x) {
        return (x - startX) / (endX - startX);
    }

    public static float getParameterFromZ(float startZ, float endZ, float z) {
        return (z - startZ) / (endZ - startZ);
    }

    //
    public static Vector3f getMovedPosition(Vector3f start, Vector3f end, float currentT, float speed) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    // return not accurate distance
    public static double getAboutSquaredDistance(Vector3f start, Vector3f end, Vector3d position) {
        double offsetX = end.x() - start.x();
        double offsetZ = end.z() - start.z();
        double t = ((start.x() - position.x()) * offsetX + (start.z() - position.z()) * offsetZ) /
                (offsetX * offsetX + offsetZ * offsetZ);
        Vector3f simplifiedNearestPoint = getMidPoint(start, end, (float) t);
        return position.distanceToSqr(
                simplifiedNearestPoint.x(),
                simplifiedNearestPoint.y(),
                simplifiedNearestPoint.z()
        );
    }

    public static double getAboutSquaredDistance(Vector3d start, Vector3d end, Vector3d position) {
        double offsetX = end.x() - start.x();
        double offsetZ = end.z() - start.z();
        double t = ((position.x() - start.x()) * offsetX + (position.z() - start.z()) * offsetZ) /
                (offsetX * offsetX + offsetZ * offsetZ);
        Vector3d simplifiedNearestPoint = getMidPointD(start, end, t);
        return position.distanceToSqr(
                simplifiedNearestPoint.x(),
                simplifiedNearestPoint.y(),
                simplifiedNearestPoint.z()
        );
    }

    public static boolean possiblyHangable(Vector3f start, Vector3f end, Vector3d position) {
        return new AxisAlignedBB(start.x(), start.y(), start.z(), end.x(), end.y(), end.z())
                .inflate(0.5)
                .contains(position);
    }

    public static boolean possiblyHangable(Vector3d start, Vector3d end, Vector3d position) {
        return new AxisAlignedBB(start, end)
                .inflate(0.5)
                .contains(position);
    }

    public static double getAccurateDistance(Vector3f start, Vector3f end, Vector3d position) {
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
