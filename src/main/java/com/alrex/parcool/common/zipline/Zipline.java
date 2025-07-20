package com.alrex.parcool.common.zipline;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Zipline {
    public static final double MAXIMUM_HORIZONTAL_DISTANCE = 72.;
    public static final double MAXIMUM_VERTICAL_DISTANCE = MAXIMUM_HORIZONTAL_DISTANCE * 0.51;

    protected Zipline(Vec3 point1, Vec3 point2) {
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

    public Vec3 getOffsetToEndFromStart() {
        return endOffsetFromStart;
    }

    public double getHorizontalDistance() {
        return horizontalDistance;
    }

    @Nullable
    public static ZiplineRopeEntity getHangableZipline(Level world, Player player) {
        return getHangableZipline(world, player, null);
    }

    @Nullable
    public static ZiplineRopeEntity getHangableZipline(Level world, Player player, @Nullable ZiplineRopeEntity except) {
        final double d = MAXIMUM_HORIZONTAL_DISTANCE * 0.52 + 1;
        List<ZiplineRopeEntity> entities = world.getEntitiesOfClass(
                ZiplineRopeEntity.class,
                player.getBoundingBox().inflate(d, Zipline.MAXIMUM_VERTICAL_DISTANCE + 1, d)
        );
        double catchRange = player.getBbWidth() * 0.6;
        double yDeltaMovement = player.getDeltaMovement().y();
        double yDistanceScale = Mth.clamp(catchRange / yDeltaMovement, 0.4d, 1d);
        var grabPos = player.position().add(0, player.getBbHeight() * 1.11 + Mth.clamp(yDeltaMovement * 0.4f, player.getBbHeight() * -0.4, player.getBbHeight() * 0.4), 0);
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

    public abstract boolean isPossiblyHangable(Vec3 position);
}
