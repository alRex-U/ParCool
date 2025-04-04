package com.alrex.parcool.common.zipline;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Zipline {
    public static final double MAXIMUM_DISTANCE = 30.;

    protected Zipline(Vector3d point1, Vector3d point2) {
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

    private final Vector3d startPos;
    private final Vector3d endPos;
    private final Vector3d endOffsetFromStart;
    private final double horizontalDistance;

    public Vector3d getStartPos() {
        return startPos;
    }

    public Vector3d getEndPos() {
        return endPos;
    }

    public Vector3d getOffsetToEndFromStart() {
        return endOffsetFromStart;
    }

    public double getHorizontalDistance() {
        return horizontalDistance;
    }

    @Nullable
    public static ZiplineRopeEntity getHangableZipline(World world, PlayerEntity player) {
        return getHangableZipline(world, player, null);
    }

    @Nullable
    public static ZiplineRopeEntity getHangableZipline(World world, PlayerEntity player, @Nullable ZiplineRopeEntity except) {
        List<ZiplineRopeEntity> entities = world.getEntitiesOfClass(
                ZiplineRopeEntity.class,
                player.getBoundingBox().inflate(MAXIMUM_DISTANCE * 0.52)
        );
        Vector3d grabPos = player.position().add(0, player.getBbHeight() * 1.11, 0);
        for (ZiplineRopeEntity ziplineEntity : entities) {
            if (except == ziplineEntity)
                continue;
            if (ziplineEntity.getStartPos().equals(BlockPos.ZERO) && ziplineEntity.getEndPos().equals(BlockPos.ZERO))
                continue;
            Zipline zipline = ziplineEntity.getZipline();
            if (zipline.isPossiblyHangable(grabPos)) {
                double distSqr = zipline.getSquaredDistanceApproximately(grabPos);
                double catchRange = player.getBbWidth() * 0.5;
                if (distSqr < catchRange * catchRange) {
                    return ziplineEntity;
                }
            }
        }
        return null;
    }

    // t is auxiliary variable in this class
    // start.y should not be higher than end.y
    // assert(start.y <= end.y)

    // return middle point of zipline
    // the x is start.x + (end.x - start.x) * t
    // also same about z
    // y is decided by calculated x and z
    public Vector3d getMidPoint(float t) {
        return getMidPointOffsetFromStart(t).add(getStartPos());
    }

    public abstract Vector3d getMidPointOffsetFromStart(float t);

    // return slope of zipline
    // equals dy/d(t * sqrt(x^2 * z^2))
    // maybe helpful for calculating acceleration
    public abstract float getSlope(float t);

    // return t
    public abstract float getParameter(Vector3d position);

    //
    public abstract double getMovedPositionByParameterApproximately(float currentT, float movement);

    // return not accurate distance
    public abstract double getSquaredDistanceApproximately(Vector3d position);

    public abstract boolean isPossiblyHangable(Vector3d position);
}
