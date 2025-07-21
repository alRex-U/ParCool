package com.alrex.parcool.common.zipline;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Zipline {
    public static final double MAXIMUM_HORIZONTAL_DISTANCE = 115.;
    public static final double MAXIMUM_VERTICAL_DISTANCE = MAXIMUM_HORIZONTAL_DISTANCE * 0.51;

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
        final double d = MAXIMUM_HORIZONTAL_DISTANCE * 0.52 + 1;
        List<ZiplineRopeEntity> entities = world.getEntitiesOfClass(
                ZiplineRopeEntity.class,
                player.getBoundingBox().inflate(d, Zipline.MAXIMUM_VERTICAL_DISTANCE + 1, d)
        );
        double catchRange = player.getBbWidth() * 0.6;
        double yDeltaMovement = player.getDeltaMovement().y();
        double yDistanceScale = MathHelper.clamp(catchRange / yDeltaMovement, 0.4d, 1d);
        Vector3d grabPos = player.position().add(0, player.getBbHeight() * 1.11 + MathHelper.clamp(yDeltaMovement * 0.4f, player.getBbHeight() * -0.4, player.getBbHeight() * 0.4), 0);
        for (ZiplineRopeEntity ziplineEntity : entities) {
            if (except == ziplineEntity)
                continue;
            if (ziplineEntity.getStartPos().equals(BlockPos.ZERO) && ziplineEntity.getEndPos().equals(BlockPos.ZERO))
                continue;
            Zipline zipline = ziplineEntity.getZipline();
            if (zipline.isPossiblyHangable(grabPos)) {
                double distSqr = zipline.getSquaredDistanceApproximately(grabPos, yDistanceScale);
                if (distSqr < catchRange * catchRange) {
                    return ziplineEntity;
                }
            }
        }
        return null;
    }

    public boolean conflictsWithSomething(World world) {
        int count = (int) Math.floor(getHorizontalDistance());
        for (int i = 1; i < count - 1; i++) {
            Vector3d midPoint = getMidPoint(((float) i / count));
            final double d = 0.2;
            if (!world.noCollision(new AxisAlignedBB(
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
    public double getSquaredDistanceApproximately(Vector3d position) {
        return getSquaredDistanceApproximately(position, 1);
    }

    public abstract double getSquaredDistanceApproximately(Vector3d position, double yDistanceScale);

    public abstract boolean isPossiblyHangable(Vector3d position);
}
