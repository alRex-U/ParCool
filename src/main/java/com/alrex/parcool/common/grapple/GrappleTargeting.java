package com.alrex.parcool.common.grapple;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public final class GrappleTargeting {
    private static final double IDEAL_ELEVATION = Math.toRadians(50);
    private static final double IDEAL_RANGE_BASE = 10.0;
    private static final double IDEAL_RANGE_PER_SPEED = 12.0;

    private static final double SURFACE_OFFSET = 0.09;

    private GrappleTargeting() {
    }

    public record Result(Vec3 point, BlockPos blockPos, Direction face, boolean assisted) {
    }

    @Nullable
    public static Result findDirect(Player player, double maxRange, double minDistance) {
        return cast(player.level(), player, player.getEyePosition(), player.getLookAngle().normalize(), maxRange, minDistance, false);
    }

    @Nullable
    public static Result find(
            Player player,
            double maxRange,
            double assistAngleDegrees,
            int rings,
            int samplesPerRing,
            double minDistance
    ) {
        Level level = player.level();
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();

        Result direct = cast(level, player, eye, look, maxRange, minDistance, false);
        if (direct != null) return direct;
        if (assistAngleDegrees <= 0 || rings < 1 || samplesPerRing < 1) return null;

        Vec3 right = look.cross(new Vec3(0, 1, 0));
        if (right.lengthSqr() < 1.0e-6) right = new Vec3(1, 0, 0);
        right = right.normalize();
        Vec3 up = right.cross(look).normalize();

        Vec3 perfectPoint = perfectSwingPoint(player, maxRange);
        Result best = null;
        double bestScore = Double.MAX_VALUE;

        double maxAngle = Math.toRadians(assistAngleDegrees);
        for (int ring = 1; ring <= rings; ring++) {
            double angle = maxAngle * ring / rings;
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);

            double azimuthOffset = Math.PI * ring / samplesPerRing;
            for (int i = 0; i < samplesPerRing; i++) {
                double azimuth = azimuthOffset + 2 * Math.PI * i / samplesPerRing;
                Vec3 direction = look.scale(cos)
                        .add(right.scale(Math.cos(azimuth) * sin))
                        .add(up.scale(Math.sin(azimuth) * sin));

                Result candidate = cast(level, player, eye, direction, maxRange, minDistance, true);
                if (candidate == null) continue;
                double score = candidate.point().distanceToSqr(perfectPoint);
                if (score < bestScore) {
                    bestScore = score;
                    best = candidate;
                }
            }
        }
        return best;
    }

    public static Vec3 perfectSwingPoint(Player player, double maxRange) {
        Vec3 look = player.getLookAngle();
        Vec3 horizontalLook = new Vec3(look.x, 0, look.z);
        horizontalLook = horizontalLook.lengthSqr() < 1.0e-6
                ? new Vec3(0, 0, 1)
                : horizontalLook.normalize();

        Vec3 movement = player.getDeltaMovement();
        double speed = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
        double range = Math.min(IDEAL_RANGE_BASE + speed * IDEAL_RANGE_PER_SPEED, maxRange * 0.8);

        return player.getEyePosition()
                .add(horizontalLook.scale(range * Math.cos(IDEAL_ELEVATION)))
                .add(0, range * Math.sin(IDEAL_ELEVATION), 0);
    }

    @Nullable
    private static Result cast(
            Level level,
            Player player,
            Vec3 origin,
            Vec3 direction,
            double maxRange,
            double minDistance,
            boolean assisted
    ) {
        Vec3 end = origin.add(direction.scale(maxRange));
        HitResult hit = level.clip(new ClipContext(origin, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) return null;
        if (origin.distanceToSqr(hit.getLocation()) < minDistance * minDistance) return null;

        Direction face = blockHit.getDirection();
        Vec3 point = hit.getLocation().add(
                face.getStepX() * SURFACE_OFFSET,
                face.getStepY() * SURFACE_OFFSET,
                face.getStepZ() * SURFACE_OFFSET
        );
        return new Result(point, blockHit.getBlockPos(), face, assisted);
    }
}
