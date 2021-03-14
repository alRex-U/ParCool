package com.alrex.parcool.common.capability;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public class GrabCliff implements IGrabCliff{
    private boolean grabbing=false;
    private int grabbingTime=0;
    private int notGrabbingTime=0;
    @Override
    public boolean canGrabCliff(ClientPlayerEntity player) {
        IStamina stamina;
        {
            LazyOptional<IStamina> staminaOptional = player.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
            if (!staminaOptional.isPresent()) return false;
            stamina = staminaOptional.resolve().get();
        }
        return !stamina.isExhausted() && KeyBindings.getKeyGrabWall().isKeyDown() && existsGrabbableWall(player);
    }

    @Override
    public boolean canJumpOnCliff(ClientPlayerEntity player) {
        return grabbing && grabbingTime > 10 && KeyRecorder.keyJumpState.isPressed();
    }

    @Override
    public boolean isGrabbing() { return grabbing; }
    @Override
    public void setGrabbing(boolean grabbing) { this.grabbing = grabbing; }

    private boolean existsGrabbableWall(PlayerEntity player){
        final double d=0.3;
        World world=player.getEntityWorld();
        double distance=player.getWidth()/2;
        double baseLine=player.getEyeHeight()+(player.getHeight()-player.getEyeHeight())/2;

        AxisAlignedBB baseBoxSide=new AxisAlignedBB(
                player.getPosX()-d,
                player.getPosY()+baseLine-player.getHeight()/6,
                player.getPosZ()-d,
                player.getPosX()+d,
                player.getPosY()+baseLine,
                player.getPosZ()+d
        );
        AxisAlignedBB baseBoxTop=new AxisAlignedBB(
                player.getPosX()-d,
                player.getPosY()+baseLine,
                player.getPosZ()-d,
                player.getPosX()+d,
                player.getPosY()+player.getHeight(),
                player.getPosZ()+d
        );

        if (!world.hasNoCollisions(baseBoxSide.expand( distance,0,0)) && world.hasNoCollisions(baseBoxTop.expand( distance,0,0)))return true;
        if (!world.hasNoCollisions(baseBoxSide.expand(-distance,0,0)) && world.hasNoCollisions(baseBoxTop.expand(-distance,0,0)))return true;
        if (!world.hasNoCollisions(baseBoxSide.expand(0,0, distance)) && world.hasNoCollisions(baseBoxTop.expand(0,0, distance)))return true;
        if (!world.hasNoCollisions(baseBoxSide.expand(0,0,-distance)) && world.hasNoCollisions(baseBoxTop.expand(0,0,-distance)))return true;

        return false;
    }

    @Override
    public int getGrabbingTime() { return grabbingTime; }
    @Override
    public int getNotGrabbingTime() { return notGrabbingTime; }

    @Override
    public void updateTime() {
        if (grabbing){
            grabbingTime++;
            notGrabbingTime=0;
        }else {
            notGrabbingTime++;
            grabbingTime=0;
        }
    }
}
