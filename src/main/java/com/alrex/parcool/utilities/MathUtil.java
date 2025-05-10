package com.alrex.parcool.utilities;

import com.github.exopandora.shouldersurfing.math.Vec2f;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class MathUtil {
	public static float squaring(float value) {
		return value * value;
	}

	public static float lerp(float start, float end, float factor) {
		return start + (end - start) * factor;
	}

    public static float normalizeRadian(float angle) {
        return (float) (angle - 2 * Math.PI * Math.floor((angle + Math.PI) / (2. * Math.PI)));
    }

    public static float normalizeDegree(float angle) {
        return (float) (angle - 360f * Math.floor((angle + 180f) / 360f));
    }
	
	public static Vec3 rotateYDegrees(Vec3 vector, float baseAngle)
	{
		var angle = baseAngle * Mth.DEG_TO_RAD;
		return new Vec3(vector.x * Mth.cos(angle) - vector.z * Mth.sin(angle), vector.y, vector.x * Mth.sin(angle) + vector.z * Mth.cos(angle));
	}

	public static float toYaw(Vec3 vector) {
		return (float)Math.toDegrees(Math.atan2(-vector.x, vector.z));
	}

	public static boolean isZero(Vec3 vector) {
		return vector.x == 0 && vector.y == 0 && vector.z == 0;
	}

	public static boolean isZero(Vec2 vector) {
		return vector.x == 0 && vector.y == 0;
	}
}
