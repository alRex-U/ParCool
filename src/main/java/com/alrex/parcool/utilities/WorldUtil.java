package com.alrex.parcool.utilities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class WorldUtil {
    @Nullable
    public static Vector3d getWall(LivingEntity entity){
        final double d=0.3;
        double distance=entity.getWidth()/2;
        double wallX=0;
        double wallZ=0;
        byte wallNumX=0;
        byte wallNumZ=0;

        AxisAlignedBB baseBox=new AxisAlignedBB(
                entity.getPosX()-d,
                entity.getPosY(),
                entity.getPosZ()-d,
                entity.getPosX()+d,
                entity.getPosY()+entity.getHeight(),
                entity.getPosZ()+d
        );

        if (!entity.world.hasNoCollisions(baseBox.expand( distance,0,0))){ wallX++;wallNumX++; }
        if (!entity.world.hasNoCollisions(baseBox.expand(-distance,0,0))){ wallX--;wallNumX++; }
        if (!entity.world.hasNoCollisions(baseBox.expand(0,0, distance))){ wallZ++;wallNumZ++; }
        if (!entity.world.hasNoCollisions(baseBox.expand(0,0,-distance))){ wallZ--;wallNumZ++; }
        if (wallNumX==2 || wallNumZ==2 || (wallNumX==0 && wallNumZ==0))return null;

        return new Vector3d(wallX,0,wallZ);
    }
}
