package com.alrex.parcool.utilities;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.extern.AdditionalMods;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class VectorUtil {
	private static final float DEG_TO_RAD = (float)(Math.PI / 180D);

	public static double toYawDegree(Vector3d vec) {
		return (Math.atan2(vec.z(), vec.x()) * 180.0 / Math.PI - 90);
	}

	public static double toYawRadian(Vector3d vec) {
		return (Math.atan2(vec.z(), vec.x()) - Math.PI / 2.);
	}

	public static double toPitchDegree(Vector3d vec) {
		return -(Math.atan2(vec.y(), Math.sqrt(vec.x() * vec.x() + vec.z() * vec.z())) * 180.0 / Math.PI);
	}

	public static Vector3d fromYawDegree(double degree) {
		return new Vector3d(-Math.sin(Math.toRadians(degree)), 0, Math.cos(Math.toRadians(degree)));
	}
	
	public static Vector3d rotateYDegrees(Vector3d vector, float baseAngle)
	{
		float angle = baseAngle * DEG_TO_RAD;
		return new Vector3d(vector.x * MathHelper.cos(angle) - vector.z * MathHelper.sin(angle), vector.y, vector.x * MathHelper.sin(angle) + vector.z * MathHelper.cos(angle));
	}

	public static float toYaw(Vector3d vector) {
		return (float)Math.toDegrees(Math.atan2(-vector.x, vector.z));
	}

	public static boolean isZero(Vector3d vector) {
		return vector.x == 0 && vector.y == 0 && vector.z == 0;
	}

	public static boolean isZero(Vector2f vector) {
		return vector.x == 0 && vector.y == 0;
	}

	public static double getLength(Vector2f vector) {
		return Math.sqrt(vector.x * vector.x + vector.y * vector.y);
	}
}
