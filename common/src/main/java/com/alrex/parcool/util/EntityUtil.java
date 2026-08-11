package com.alrex.parcool.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class EntityUtil {
    public static Vec3 getHorizontalLookAngle(Entity entity) {
        return VectorUtil.fromYawDegree(entity.getYRot());
    }

    public static float getHorizontalAccelerationOnGround(LivingEntity entity, float friction) {
        return entity.getSpeed() * (0.216f / (friction * friction * friction));
    }

    public static float getHorizontalMaximumSpeed(LivingEntity entity, float blockFriction) {
        return getHorizontalAccelerationOnGround(entity, blockFriction) / (1f - 0.91f * blockFriction);
    }

    public static float getHorizontalMaximumSpeed(LivingEntity entity) {
        return getHorizontalMaximumSpeed(entity, 0.6f);
    }

    public static float getHorizontalMaximumDeltaMovementValue(LivingEntity entity, float blockFriction) {
        return (0.91f * blockFriction) * getHorizontalMaximumSpeed(entity, blockFriction);
    }

    public static float getHorizontalMaximumDeltaMovementValue(LivingEntity entity) {
        return getHorizontalMaximumDeltaMovementValue(entity, 0.6f);
    }
}
