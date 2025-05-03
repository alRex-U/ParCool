package com.alrex.parcool.compatibility;

import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public final class AxisWrapper {
   public static AxisWrapper XN = new AxisWrapper(Vector3f.XN);
   public static AxisWrapper XP = new AxisWrapper(Vector3f.XP);
   public static AxisWrapper YN = new AxisWrapper(Vector3f.YN);
   public static AxisWrapper YP = new AxisWrapper(Vector3f.YP);
   public static AxisWrapper ZN = new AxisWrapper(Vector3f.ZN);
   public static AxisWrapper ZP = new AxisWrapper(Vector3f.ZP);
   private Vector3f vector;

	public static AxisWrapper createXZ(Vec3Wrapper vec) {
		return new AxisWrapper(vec.x(), 0, vec.z());
	}

   public AxisWrapper(Vector3f vec) {
      vector = vec;
   }
   public AxisWrapper(float x, float y, float z) {
      vector = new Vector3f(x, y, z);
   }
   public AxisWrapper(double x, double y, double z) {
      vector = new Vector3f((float)x, (float)y, (float)z);
   }

   public Quaternion rotationDegrees(float angleDegree) {
      return vector.rotationDegrees(angleDegree);
   }

   public Quaternion rotation(float angle) {
      return vector.rotation(angle);
   }

   public static AxisWrapper fromVector(Vec3Wrapper midPointD) {
      return new AxisWrapper(midPointD.getX(), midPointD.getY(), midPointD.getZ());
   }

   public float x() {
      return vector.x();
   }

   public float y() {
      return vector.y();
   }

   public float z() {
      return vector.z();
   }
}
