package com.alrex.parcool.common.capability;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class Dodge implements IDodge{
    private int dodgingTime=0;
    private boolean dodging=false;
    private int coolTime=0;
    private DodgeDirection direction=null;
    @Override
    public boolean canDodge(ClientPlayerEntity player) {
        IStamina stamina;
        {
            LazyOptional<IStamina> staminaOptional=player.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
            if (!staminaOptional.isPresent())return false;
            stamina=staminaOptional.resolve().get();
        }
        return coolTime<=0 && player.isOnGround() && !player.isSneaking() && !stamina.isExhausted() && (KeyRecorder.keyBack.isDoubleTapped() || KeyRecorder.keyLeft.isDoubleTapped() || KeyRecorder.keyRight.isDoubleTapped());
    }

    @Nullable @Override
    public Vector3d getDodgeDirection(ClientPlayerEntity player) {
        Vector3d lookVec=player.getLookVec();
        lookVec=new Vector3d(lookVec.getX(),0, lookVec.getZ()).normalize();

        if (KeyBindings.getKeyBack().isKeyDown()){
            direction=DodgeDirection.Back;
            return lookVec.inverse();
        }
        if (KeyBindings.getKeyLeft().isKeyDown() && KeyBindings.getKeyRight().isKeyDown())return null;
        Vector3d vecToRight=lookVec.rotateYaw((float) Math.PI / -2);
        if (KeyBindings.getKeyLeft().isKeyDown()){
            direction=DodgeDirection.Left;
            return vecToRight.inverse();
        }else {
            direction=DodgeDirection.Right;
            return vecToRight;
        }
    }

    @Override
    public boolean isDodging() { return dodging; }

    @Override
    public void setDodging(boolean dodging) { this.dodging = dodging; if (dodging)coolTime=10; else direction=null;}

    @Override
    public int getDodgingTime() { return dodgingTime; }

    @Nullable @Override
    public DodgeDirection getDirection() { return direction; }

    @Override
    public boolean canContinueDodge(ClientPlayerEntity player) {
        return dodging && !player.isOnGround() && !player.isInWaterOrBubbleColumn() && !player.isElytraFlying() && !player.abilities.isFlying;
    }

    @Override
    public void updateDodgingTime() {
        if (coolTime>0)coolTime--;
        if (dodging){ dodgingTime++; }else { dodgingTime=0; }
    }

    @Override
    public int getStaminaConsumption() { return 100; }
}
