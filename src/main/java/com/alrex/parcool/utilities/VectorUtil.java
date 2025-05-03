package com.alrex.parcool.utilities;

import com.alrex.parcool.compatibility.Vec3Wrapper;

public class VectorUtil {
	public static double toYawDegree(Vec3Wrapper vec) {
		return (Math.atan2(vec.z(), vec.x()) * 180.0 / Math.PI - 90);
	}

	public static double toYawRadian(Vec3Wrapper vec) {
		return (Math.atan2(vec.z(), vec.x()) - Math.PI / 2.);
	}

	public static double toPitchDegree(Vec3Wrapper vec) {
		return -(Math.atan2(vec.y(), Math.sqrt(vec.x() * vec.x() + vec.z() * vec.z())) * 180.0 / Math.PI);
	}

	public static Vec3Wrapper fromYawDegree(double degree) {
		return new Vec3Wrapper(-Math.sin(Math.toRadians(degree)), 0, Math.cos(Math.toRadians(degree)));
	}
}
