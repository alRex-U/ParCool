package com.alrex.parcool.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class VectorUtil {
    public static Vec3 fromYawDegree(double degree) {
        return new Vec3(-Math.sin(Math.toRadians(degree)), 0, Math.cos(Math.toRadians(degree)));
    }

    public static boolean isZero(Vec3 vector) {
        return vector.x == 0 && vector.y == 0 && vector.z == 0;
    }

    public static boolean isZero(Vec2 vector) {
        return vector.x == 0 && vector.y == 0;
    }

    public static Vec3 calculateReflectVector(Vec3 reflectingVec, Vec3 reflectionNormal) {
        var t = -reflectingVec.dot(reflectionNormal) / reflectionNormal.lengthSqr();
        return reflectingVec.add(reflectionNormal.scale(2 * t));
    }

    public static Vec3 calculateViewVector(float xRotInDegrees, float yRotInDegrees) {
        var xRotRad = Math.toRadians(xRotInDegrees);
        var yRotRad = -Math.toRadians(yRotInDegrees);
        return new Vec3(Math.sin(yRotRad) * Math.cos(xRotRad), -Math.sin(xRotRad), Math.cos(yRotRad) * Math.cos(xRotRad));
    }

    public static Vec3 reverseIfInReverseDirection(Vec3 base, Vec3 target) {
        return base.dot(target) >= 0 ? target : target.reverse();
    }

    public static Vec3 lerp(double factor, Vec3 v1, Vec3 v2) {
        return new Vec3(
                Mth.lerp(factor, v1.x, v2.x),
                Mth.lerp(factor, v1.y, v2.y),
                Mth.lerp(factor, v1.z, v2.z)
        );
    }
}
