package com.alrex.parcool.common.math;

import net.minecraft.world.phys.Vec3;

public record Vec2XZ(double x, double z) {
    public static Vec2XZ from(Vec3 vec) {
        return new Vec2XZ(vec.x, vec.z);
    }

    public Vec3 withY(double y) {
        return new Vec3(x, y, z);
    }
}
