package com.alrex.parcool.compatibility;

import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.util.math.vector.Vector3d;

public class Vec3Wrapper extends Vector3d {
    public static final Vec3Wrapper ZERO = new Vec3Wrapper(Vector3d.ZERO);

    public Vec3Wrapper(double x, double y, double z) {
        super(x, y, z);
    }

    public Vec3Wrapper(Vector3d vec) {
        super(vec.x, vec.y, vec.z);
    }

    @Override
    public Vec3Wrapper normalize() {
        return new Vec3Wrapper(super.normalize());
    }

    @Override
    public Vec3Wrapper scale(double scale) {
        return new Vec3Wrapper(super.scale(scale));
    }

    @Override
    public Vec3Wrapper subtract(Vector3d vec) {
        return new Vec3Wrapper(super.subtract(vec));
    }

    @Override
    public Vec3Wrapper subtract(double x, double y, double z) {
        return new Vec3Wrapper(super.subtract(x, y, z));
    }

    @Override
    public Vec3Wrapper add(Vector3d vec) {
        return new Vec3Wrapper(super.add(vec));
    }

    @Override
    public Vec3Wrapper add(double x, double y, double z) {
        return new Vec3Wrapper(super.add(x, y, z));
    }

    @Override
    public Vec3Wrapper multiply(double x, double y, double z) {
        return new Vec3Wrapper(super.multiply(x, y, z));
    }

    @Override
    public Vec3Wrapper multiply(Vector3d vec) {
        return new Vec3Wrapper(super.multiply(vec));
    }

    @Override
    public Vec3Wrapper yRot(float value) {
        return new Vec3Wrapper(super.yRot(value));
    }

    @Override
    public Vec3Wrapper reverse() {
        return new Vec3Wrapper(super.reverse());
    }
}
