package com.alrex.parcool.compatibility;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class AABBWrapper extends AxisAlignedBB {
    public AABBWrapper(BlockPos blockPos) {
        super(blockPos);
    }

    public AABBWrapper(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        super(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public AABBWrapper(Vec3Wrapper subtract, Vec3Wrapper vec3Wrapper) {
        super(subtract, vec3Wrapper);
    }

    @Override
    public AABBWrapper expandTowards(Vector3d vec) {
        return get(super.expandTowards(vec));
    }

    @Override
    public AABBWrapper expandTowards(double x, double y, double z) {
        return get(super.expandTowards(x, y, z));
    }

    @Override
    public AABBWrapper inflate(double p_186662_1_) {
        return get(super.inflate(p_186662_1_));
    }

    @Override
    public AABBWrapper inflate(double p_72314_1_, double p_72314_3_, double p_72314_5_) {
        return get(super.inflate(p_72314_1_, p_72314_3_, p_72314_5_));
    }

    public static AABBWrapper get(AxisAlignedBB boundingBox) {
        return new AABBWrapper(boundingBox.minX, boundingBox.minY, boundingBox.minZ,
                boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
    }
}
