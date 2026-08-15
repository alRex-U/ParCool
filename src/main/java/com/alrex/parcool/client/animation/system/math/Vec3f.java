package com.alrex.parcool.client.animation.system.math;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public record Vec3f(float x, float y, float z) {
    public static final Vec3f ZERO = new Vec3f(0f, 0f, 0f);
    public static final Vec3f XP = new Vec3f(1f, 0f, 0f);
    public static final Vec3f YP = new Vec3f(0f, 1f, 0f);
    public static final Vec3f ZP = new Vec3f(0f, 0f, 1f);
    public static final Vec3f XN = new Vec3f(-1f, 0f, 0f);
    public static final Vec3f YN = new Vec3f(0f, -1f, 0f);
    public static final Vec3f ZN = new Vec3f(0f, 0f, -1f);

    public Vec3f scale(float v) {
        return new Vec3f(x * v, y * v, z * v);
    }

    public Vec3f add(Vec3f v) {
        return new Vec3f(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public Vector3f toVector3f() {
        return new Vector3f(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec3f vec3f) {
            return x == vec3f.x && y == vec3f.y && z == vec3f.z;
        }
        return false;
    }

    public boolean isApproximatelyZero() {
        return Math.abs(x) < 1e-5 && Math.abs(y) < 1e-5 && Math.abs(z) < 1e-5;
    }
}
