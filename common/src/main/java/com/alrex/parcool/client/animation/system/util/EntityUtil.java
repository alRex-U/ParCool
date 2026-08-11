package com.alrex.parcool.client.animation.system.util;

import com.alrex.parcool.util.VectorUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class EntityUtil {
    public static Vec3 getHorizontalPositionDifference(Entity entity) {
        var pos = entity.position();
        return new Vec3(pos.x - entity.xo, 0, pos.z - entity.zo);
    }

    public static Vec3 getPositionDifference(Entity entity) {
        return entity.position().subtract(entity.xo, entity.yo, entity.zo);
    }

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
}
