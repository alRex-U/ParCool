package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChargeJump extends ContinuableAction implements ActionExtension.JumpListener {
    private static final byte CHARGE_DURATION = 10;

    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Boolean> propertyInputActive;
    private final SynchronizedProperty<Boolean> propertyJumpTriggered;

    // Only for client
    private boolean jumped = false;
    private byte oldChargingTick = 0;
    private byte chargingTick = 0;

    public ChargeJump(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyInputActive = SynchronizedProperty.newBoolean(),
                propertyJumpTriggered = SynchronizedProperty.newBoolean()
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public void onTickInClient() {
        oldChargingTick = chargingTick;
        if (parkourability.player().isLocalPlayer()) {
            propertyInputActive.set(isInputActive());
        }
        if (parkourability.player().onGround()) {
            if (propertyInputActive.getOrDefaultIfNull(Boolean.FALSE)) {
                if (chargingTick < CHARGE_DURATION) chargingTick++;
            } else {
                if (0 < chargingTick) chargingTick--;
            }
        } else {
            chargingTick = 0;
        }
    }

    @Override
    public boolean canStart() {
        propertyJumpTriggered.set(Boolean.FALSE);
        return isCharging();
    }

    @Override
    public boolean canContinue() {
        if (jumped) {
            jumped = false;
            return false;
        }
        var localPlayer = (LocalPlayer) parkourability.player();
        return isCharging()
                && Mth.abs(localPlayer.input.forwardImpulse) < 1e-4
                && Mth.abs(localPlayer.input.leftImpulse) < 1e-4;
    }

    @Override
    public void onJump() {
        if (isDoing() && parkourability.player().level().isClientSide()) {
            jumped = true;
            propertyJumpTriggered.set(Boolean.TRUE);
            var deltaMove = parkourability.player().getDeltaMovement();
            parkourability.player().setDeltaMovement(deltaMove.x, deltaMove.y + 0.16 * chargingTick / CHARGE_DURATION, deltaMove.z);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().JUMP_CHARGING);
        parkourability.player().playSound(ParCoolSoundEvents.CHARGE_JUMP.get());
    }

    @Override
    public void onStop() {
        oldChargingTick = chargingTick = 0;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStopInClient() {
        if (propertyJumpTriggered.getOrDefaultIfNull(false))
            PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().CHARGE_JUMP);
    }

    @Override
    protected void takeCost(StaminaConsumption.Type type) {
        if (type == StaminaConsumption.Type.FINISH) {
            if (propertyJumpTriggered.getOrDefaultIfNull(false)) {
                super.takeCost(type);
            }
        } else {
            super.takeCost(type);
        }
    }

    private boolean isInputActive() {
        if (parkourability.player() instanceof LocalPlayer localPlayer) {
            return localPlayer.isShiftKeyDown()
                    && Mth.abs(localPlayer.input.forwardImpulse) < 1e-4
                    && Mth.abs(localPlayer.input.leftImpulse) < 1e-4
                    && ParCoolKeyBinds.SHIFT.state().getPreviousNotPressedDurationTick() > 5;
        }
        return false;
    }

    public boolean isCharging() {
        return chargingTick > 0 || oldChargingTick > 0;
    }

    public float getChargeProgress(float partial) {
        return Mth.lerp(partial, oldChargingTick, chargingTick) / CHARGE_DURATION;
    }
}
