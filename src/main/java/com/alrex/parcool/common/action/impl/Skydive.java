package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.IRequestable;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Skydive extends ContinuableAction implements IRequestable<Skydive.RequestContext> {

    public record RequestContext() {
    }

    private static final byte TRANSITION_TICK = 10;
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Byte> propertyMovingForwardTick;
    private final SynchronizedProperty<Byte> propertyMovingLeftTick;

    public Skydive(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyMovingForwardTick = SynchronizedProperty.newByte(),
                propertyMovingLeftTick = SynchronizedProperty.newByte()
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
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
    public boolean canStart(RequestContext requestContext) {
        propertyMovingForwardTick.set((byte) 0);
        propertyMovingLeftTick.set((byte) 0);
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInLocalClient() {
        parkourability.getBehaviorEnforcer().setMarkerEnforcingDeltaMovement(this::isDoing, () -> {
            var deltaMove = parkourability.player().getDeltaMovement();
            return new Vec3(deltaMove.x, deltaMove.y * 0.97, deltaMove.z);
        });
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().SKYDIVE);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onWorkingTickInLocalClient() {
        updateMovementProperties();
    }

    public float getBlendingFactorLeanForward() {
        return Mth.clamp(propertyMovingForwardTick.getOrDefaultIfNull((byte) 0) / (float) TRANSITION_TICK, 0f, 1f);
    }

    public float getBlendingFactorLeanBackward() {
        return Mth.clamp(-propertyMovingForwardTick.getOrDefaultIfNull((byte) 0) / (float) TRANSITION_TICK, 0f, 1f);
    }

    public float getBlendingFactorLeanLeft() {
        return Mth.clamp(propertyMovingLeftTick.getOrDefaultIfNull((byte) 0) / (float) TRANSITION_TICK, 0f, 1f);
    }

    public float getBlendingFactorLeanRight() {
        return Mth.clamp(-propertyMovingLeftTick.getOrDefaultIfNull((byte) 0) / (float) TRANSITION_TICK, 0f, 1f);
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
