package com.alrex.parcool.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

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
    @Nullable
    public static Vector3d getStep(LivingEntity entity){
        final double d=0.3;
        World world=entity.world;
        double distance=entity.getWidth()/2;
        double baseLine=1.55;
        double stepX=0;
        double stepZ=0;

        AxisAlignedBB baseBoxSide=new AxisAlignedBB(
                entity.getPosX()-d,
                entity.getPosY(),
                entity.getPosZ()-d,
                entity.getPosX()+d,
                entity.getPosY()+baseLine,
                entity.getPosZ()+d
        );
        AxisAlignedBB baseBoxTop=new AxisAlignedBB(
                entity.getPosX()-d,
                entity.getPosY()+baseLine,
                entity.getPosZ()-d,
                entity.getPosX()+d,
                entity.getPosY()+entity.getHeight(),
                entity.getPosZ()+d
        );
        if (!world.hasNoCollisions(baseBoxSide.expand( distance,0,0)) && world.hasNoCollisions(baseBoxTop.expand( distance,0,0))){ stepX++;}
        if (!world.hasNoCollisions(baseBoxSide.expand(-distance,0,0)) && world.hasNoCollisions(baseBoxTop.expand(-distance,0,0))){ stepX--;}
        if (!world.hasNoCollisions(baseBoxSide.expand(0,0, distance)) && world.hasNoCollisions(baseBoxTop.expand(0,0, distance))){ stepZ++;}
        if (!world.hasNoCollisions(baseBoxSide.expand(0,0,-distance)) && world.hasNoCollisions(baseBoxTop.expand(0,0,-distance))){ stepZ--;}
        if (stepX==0 && stepZ==0)return null;

        return new Vector3d(stepX,0,stepZ);
    }
    public static double getWallHeight(LivingEntity entity){
        Vector3d wall=getWall(entity);
        if (wall==null)return 0;
        World world=entity.world;
        final double v=0.1;
        final double d=0.3;
        int loopNum=(int) Math.round(entity.getHeight()/v);
        double x1=entity.getPosX()+d+(wall.getX()>0 ?  1:0);
        double y1=entity.getPosY();
        double z1=entity.getPosZ()+d+(wall.getZ()>0 ?  1:0);
        double x2=entity.getPosX()-d+(wall.getX()<0 ? -1:0);
        double z2=entity.getPosZ()-d+(wall.getZ()<0 ? -1:0);
        boolean canReturn=false;
        for (int i=0;i<loopNum;i++){
            AxisAlignedBB box=new AxisAlignedBB(
                    x1,y1 + v*i,z1,x2,y1 + v*(i+1),z2
            );

            if (!world.hasNoCollisions(box)) { canReturn=true; }else { if (canReturn) return v*i; }
        }
        return entity.getHeight();
    }
    public static boolean existsGrabbableWall(LivingEntity entity){
        final double d=0.3;
        World world=entity.getEntityWorld();
        double distance=entity.getWidth()/2;
        double baseLine=entity.getEyeHeight()+(entity.getHeight()-entity.getEyeHeight())/2;

        AxisAlignedBB baseBoxSide=new AxisAlignedBB(
                entity.getPosX()-d,
                entity.getPosY()+baseLine-entity.getHeight()/6,
                entity.getPosZ()-d,
                entity.getPosX()+d,
                entity.getPosY()+baseLine,
                entity.getPosZ()+d
        );
        AxisAlignedBB baseBoxTop=new AxisAlignedBB(
                entity.getPosX()-d,
                entity.getPosY()+baseLine,
                entity.getPosZ()-d,
                entity.getPosX()+d,
                entity.getPosY()+entity.getHeight(),
                entity.getPosZ()+d
        );

        if (!world.hasNoCollisions(baseBoxSide.expand( distance,0,0)) && world.hasNoCollisions(baseBoxTop.expand( distance,0,0)))return true;
        if (!world.hasNoCollisions(baseBoxSide.expand(-distance,0,0)) && world.hasNoCollisions(baseBoxTop.expand(-distance,0,0)))return true;
        if (!world.hasNoCollisions(baseBoxSide.expand(0,0, distance)) && world.hasNoCollisions(baseBoxTop.expand(0,0, distance)))return true;
        if (!world.hasNoCollisions(baseBoxSide.expand(0,0,-distance)) && world.hasNoCollisions(baseBoxTop.expand(0,0,-distance)))return true;

        return false;
    }
}
