package com.alrex.parcool.utilities;

import net.minecraft.util.math.vector.Vector3d;

public class VectorUtil {
	public static double toYawDegree(Vector3d vec) {
		return (Math.atan2(vec.getZ(), vec.getX()) * 180.0 / Math.PI - 90);
	}

	public static double toPitchDegree(Vector3d vec) {
		return -(Math.atan2(vec.getY(), Math.sqrt(vec.getX() * vec.getX() + vec.getZ() * vec.getZ())) * 180.0 / Math.PI);
	}
}
