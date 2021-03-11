package com.alrex.parcool.common.capability;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class WallJump implements IWallJump{
    @Override
    public double getJumpPower() {
        return 0.3;
    }

    @Override
    public boolean canWallJump(ClientPlayerEntity player) {
        IStamina stamina;
        {
            LazyOptional<IStamina> staminaOptional = player.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
            if (!staminaOptional.isPresent()) return false;
            stamina = staminaOptional.resolve().get();
        }
        return !stamina.isExhausted() && !player.isOnGround() && !player.isElytraFlying() && !player.abilities.isFlying && KeyRecorder.keyJumpState.isPressed() && getWall(player)!=null;
    }
    @Override @Nullable
    public Vector3d getJumpDirection(ClientPlayerEntity player){
        Vector3d wall = getWall(player);
        if (wall==null)return null;

        Vector3d lookVec=player.getLookVec();
        Vector3d vec=new Vector3d(lookVec.getX(),0, lookVec.getZ()).normalize();

        Vector3d value;

        if (wall.dotProduct(vec)>0){//壁を向いている
            double dot=vec.inverse().dotProduct(wall);
            value=vec.add(wall.scale(2*dot/wall.length())); // Perfect.
        }else {//壁に背を向けている
            value=vec;
        }

        return value.normalize().add(wall.scale(-0.7));
    }
    @Nullable
    private Vector3d getWall(ClientPlayerEntity player){
        final double d=0.1;
        final double distance=1;
        double wallX=0;
        double wallZ=0;
        byte wallNumX=0;
        byte wallNumZ=0;

        AxisAlignedBB baseBox=new AxisAlignedBB(
                player.getPosX()-d,
                player.getPosY(),
                player.getPosZ()-d,
                player.getPosX()+d,
                player.getPosY()+player.getHeight(),
                player.getPosZ()+d
        );

        if (!player.world.hasNoCollisions(baseBox.expand( distance,0,0))){ wallX++;wallNumX++; }
        if (!player.world.hasNoCollisions(baseBox.expand(-distance,0,0))){ wallX--;wallNumX++; }
        if (!player.world.hasNoCollisions(baseBox.expand(0,0, distance))){ wallZ++;wallNumZ++; }
        if (!player.world.hasNoCollisions(baseBox.expand(0,0,-distance))){ wallZ--;wallNumZ++; }
        if (wallNumX==2 || wallNumZ==2 || (wallNumX==0 && wallNumZ==0))return null;

        return new Vector3d(wallX,0,wallZ);
    }
}
