package com.alrex.parcool.common.zipline;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Zipline {
    public static final double MAXIMUM_DISTANCE = 30.;

    protected Zipline(Vector3f point1, Vector3f point2) {
        if (point1.y() <= point2.y()) {
            this.startPos = point1;
            this.endPos = point2;
        } else {
            this.startPos = point2;
            this.endPos = point1;
        }
        endOffsetFromStart = new Vector3f(
                endPos.x() - startPos.x(),
                endPos.y() - startPos.y(),
                endPos.z() - startPos.z()
        );
    }

    private final Vector3f startPos;
    private final Vector3f endPos;
    private final Vector3f endOffsetFromStart;

    public Vector3f getStartPos() {
        return startPos;
    }

    public Vector3f getEndPos() {
        return endPos;
    }

    public Vector3f getOffsetToEndFromStart() {
        return endOffsetFromStart;
    }

    @Nullable
    public static ZiplineRopeEntity getHangableZipline(World world, PlayerEntity player) {
        List<ZiplineRopeEntity> entities = world.getEntitiesOfClass(
                ZiplineRopeEntity.class,
                player.getBoundingBox().inflate(MAXIMUM_DISTANCE * 0.52)
        );
        Vector3d grabPos = player.position().add(0, player.getBbHeight() * 1.11, 0);
        for (ZiplineRopeEntity ziplineEntity : entities) {
            if (ziplineEntity.getStartPos().equals(BlockPos.ZERO) && ziplineEntity.getEndPos().equals(BlockPos.ZERO))
                continue;
            Zipline zipline = ziplineEntity.getZipline();
            if (zipline.isPossiblyHangable(grabPos)) {
                double distSqr = zipline.getSquaredDistanceApproximately(grabPos);
                double catchRange = player.getBbWidth() * 0.35;
                if (distSqr < catchRange * catchRange) return ziplineEntity;
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
    public abstract Vector3d getMidPoint(float t);

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
