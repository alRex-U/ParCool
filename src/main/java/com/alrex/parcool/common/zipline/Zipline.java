package com.alrex.parcool.common.zipline;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.compatibility.AABBWrapper;
import com.alrex.parcool.compatibility.LevelWrapper;
import com.alrex.parcool.compatibility.PlayerWrapper;
import com.alrex.parcool.compatibility.Vec3Wrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Zipline {
    public static final double MAXIMUM_DISTANCE = 60.;

    protected Zipline(Vec3Wrapper point1, Vec3Wrapper point2) {
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

    private final Vec3Wrapper startPos;
    private final Vec3Wrapper endPos;
    private final Vec3Wrapper endOffsetFromStart;
    private final double horizontalDistance;

    public Vec3Wrapper getStartPos() {
        return startPos;
    }

    public Vec3Wrapper getEndPos() {
        return endPos;
    }

    public Vec3Wrapper getOffsetToEndFromStart() {
        return endOffsetFromStart;
    }

    public double getHorizontalDistance() {
        return horizontalDistance;
    }

    @Nullable
    public static ZiplineRopeEntity getHangableZipline(LevelWrapper level, PlayerWrapper player) {
        return getHangableZipline(level, player, null);
    }

    @Nullable
    public static ZiplineRopeEntity getHangableZipline(LevelWrapper level, PlayerWrapper player, @Nullable ZiplineRopeEntity except) {
        List<ZiplineRopeEntity> entities = level.getEntitiesOfClass(
                ZiplineRopeEntity.class,
                player.getBoundingBox().inflate(MAXIMUM_DISTANCE * 0.52)
        );
        Vec3Wrapper grabPos = player.position().add(0, player.getBbHeight() * 1.11, 0);
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

    public boolean conflictsWithSomething(World world) {
        int count = (int) Math.floor(getHorizontalDistance());
        for (int i = 1; i < count - 1; i++) {
            Vec3Wrapper midPoint = getMidPoint(((float) i / count));
            final double d = 0.2;
            if (!world.noCollision(new AABBWrapper(
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
    public Vec3Wrapper getMidPoint(float t) {
        return getMidPointOffsetFromStart(t).add(getStartPos());
    }

    public abstract Vec3Wrapper getMidPointOffsetFromStart(float t);

    // return slope of zipline
    // equals dy/d(t * sqrt(x^2 * z^2))
    // maybe helpful for calculating acceleration
    public abstract float getSlope(float t);

    // return t
    public abstract float getParameter(Vec3Wrapper position);

    //
    public abstract double getMovedPositionByParameterApproximately(float currentT, float movement);

    // return not accurate distance
    public abstract double getSquaredDistanceApproximately(Vec3Wrapper position);

    public abstract boolean isPossiblyHangable(Vec3Wrapper position);
}
