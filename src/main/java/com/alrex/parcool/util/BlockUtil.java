package com.alrex.parcool.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class BlockUtil {
    public static BlockPos getBlockPos(Vec3 vec3) {
        return getBlockPos(vec3.x, vec3.y, vec3.z);
    }

    public static BlockPos getBlockPos(double x, double y, double z) {
        return new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
    }
}
