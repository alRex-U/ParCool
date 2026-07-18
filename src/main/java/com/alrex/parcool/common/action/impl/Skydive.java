package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class Skydive extends ContinuableAction {
    private static final byte TRANSITION_TICK = 5;
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Byte> propertyMovingForwardTick;
    private final SynchronizedProperty<Byte> propertyMovingLeftTick;

    // Only for client
    private byte oldMovingForwardTick;
    private byte oldMovingLeftTick;

    public Skydive(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyMovingForwardTick = SynchronizedProperty.newByte(),
                propertyMovingLeftTick = SynchronizedProperty.newByte()
        );
    }

    @Override
    public boolean canContinue() {
        return !ParCoolKeyBinds.JUMP.state().isJustPressed();
    }

    @Override
    public boolean canStart() {
        var player = parkourability.player();
        if (player.position().y - player.yo >= 0) return false;
        propertyMovingForwardTick.set((byte) 0);
        propertyMovingLeftTick.set((byte) 0);
        return ParCoolKeyBinds.JUMP.state().isJustPressed();
    }

    @Override
    public void onStartInLocalClient() {
        parkourability.getBehaviorEnforcer().setMarkerEnforcingDeltaMovement(this::isDoing, () -> {
            var deltaMove = parkourability.player().getDeltaMovement();
            return new Vec3(deltaMove.x, deltaMove.y * 0.97, deltaMove.z);
        });
    }

    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().SKYDIVE);
    }

    @Override
    public void onWorkingTickInClient() {
        var oldMovingForwardTick = propertyMovingForwardTick.get();
        if (oldMovingForwardTick != null) this.oldMovingForwardTick = oldMovingForwardTick;
        var oldMovingLeftTick = propertyMovingLeftTick.get();
        if (oldMovingLeftTick != null) this.oldMovingLeftTick = oldMovingLeftTick;
    }

    @Override
    public void onWorkingTickInLocalClient() {
        updateMovementProperties();
    }

    public float getBlendingFactorLeanForward(float partial) {
        var movingTick = propertyMovingForwardTick.get();
        if (movingTick == null) return 0f;
        return Mth.clamp(Mth.lerp(partial, oldMovingForwardTick, movingTick) / TRANSITION_TICK, 0, 1);
    }

    public float getBlendingFactorLeanBackward(float partial) {
        var movingTick = propertyMovingForwardTick.get();
        if (movingTick == null) return 0f;
        return Mth.clamp(-Mth.lerp(partial, oldMovingForwardTick, movingTick) / TRANSITION_TICK, 0, 1);
    }

    public float getBlendingFactorLeanLeft(float partial) {
        var movingTick = propertyMovingLeftTick.get();
        if (movingTick == null) return 0f;
        return Mth.clamp(Mth.lerp(partial, oldMovingLeftTick, movingTick) / TRANSITION_TICK, 0, 1);
    }

    public float getBlendingFactorLeanRight(float partial) {
        var movingTick = propertyMovingLeftTick.get();
        if (movingTick == null) return 0f;
        return Mth.clamp(-Mth.lerp(partial, oldMovingLeftTick, movingTick) / TRANSITION_TICK, 0, 1);
    }

    private void updateMovementProperties() {
        var input = ((LocalPlayer) parkourability.player()).input;
        byte tickMovingForward;
        {
            var tmp = propertyMovingForwardTick.get();
            tickMovingForward = (tmp != null) ? tmp : 0;
        }
        byte tickMovingLeft;
        {
            var tmp = propertyMovingLeftTick.get();
            tickMovingLeft = (tmp != null) ? tmp : 0;
        }
        if (input.forwardImpulse > 1e-4) {
            if (tickMovingForward < TRANSITION_TICK) tickMovingForward++;
        } else if (input.forwardImpulse < -1e-4) {
            if (tickMovingForward > -TRANSITION_TICK) tickMovingForward--;
        } else {
            if (tickMovingForward > 0) tickMovingForward--;
            else if (tickMovingForward < 0) tickMovingForward++;
        }
        if (input.leftImpulse > 1e-4) {
            if (tickMovingLeft < TRANSITION_TICK) tickMovingLeft++;
        } else if (input.leftImpulse < -1e-4) {
            if (tickMovingLeft > -TRANSITION_TICK) tickMovingLeft--;
        } else {
            if (tickMovingLeft > 0) tickMovingLeft--;
            else if (tickMovingLeft < 0) tickMovingLeft++;
        }
        propertyMovingForwardTick.set(tickMovingForward);
        propertyMovingLeftTick.set(tickMovingLeft);
    }

}
