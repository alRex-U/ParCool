package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.ParCoolAttributes;
import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.SynchronizedDataHolder;
import com.alrex.parcool.api.action.SynchronizedProperty;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.LogicalMovement;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.architectury.event.ParCoolActionArchEvent;
import com.alrex.parcool.util.EntityUtil;
import com.alrex.parcool.util.MathUtil;
import com.alrex.parcool.util.VectorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.phys.Vec3;

public class Breakfall extends Action implements ActionExtension.LandListener {
    private final SynchronizedDataHolder holder;
    // Local -> Server
    private final SynchronizedProperty<BreakfallType> propertyInputBreakfallType;
    // Server -> Local
    private final SynchronizedProperty<BreakfallType> propertyWorkingBreakfallType;
    private final SynchronizedProperty<Float> propertyFallDist;

    public Breakfall(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
        holder = SynchronizedDataHolder.create(entry,
                propertyInputBreakfallType = SynchronizedProperty.newEnum(BreakfallType.class),
                propertyWorkingBreakfallType = SynchronizedProperty.newEnum(BreakfallType.class),
                propertyFallDist = SynchronizedProperty.newFloat()
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return holder;
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onTickInLocalClient() {
        propertyInputBreakfallType.set(ParCoolKeyBinds.BREAKFALL.state().isDown() ?
                (ParCoolKeyBinds.getMovementInput(LogicalMovement.FORWARD).isDown() ? BreakfallType.ROLL : BreakfallType.TAP)
                : BreakfallType.NONE
        );
    }

    @Override
    public void onLand(LivingFallEvent event) {
        var breakfallType = propertyInputBreakfallType.get();
        if (breakfallType == null || breakfallType == BreakfallType.NONE) return;
        var attr = parkourability.player().getAttribute(ParCoolAttributes.BREAKFALL_DAMAGE_REDUCTION.get());
        if (attr == null) return;
        if (isPossible() && ParCoolActionArchEvent.TryToStart.EVENT.invoker().onTryToStart(parkourability.player(), this).isTrue()) {
            var damageReduction = attr.getValue();
            event.setDamageMultiplier(event.getDamageMultiplier() * (float) (1. - damageReduction));
            if (event.getDistance() < 15 * damageReduction) {
                event.setCanceled(true);
            }

            propertyFallDist.set(event.getDistance());
            propertyWorkingBreakfallType.set(breakfallType);
            startExplicitly();
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onStartInClient() {
        switch (propertyWorkingBreakfallType.getOrDefaultIfNull(BreakfallType.NONE)) {
            case TAP:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().BREAKFALL_NO_MOVE);
                break;
            case ROLL:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().BREAKFALL_FORWARD);
        }
        parkourability.player().playSound(
                ParCoolSoundEvents.BREAKFALL.get(),
                MathUtil.mapLinear(propertyFallDist.getOrDefaultIfNull(0f), 3f, 10f, 0f, 1f),
                1f
        );
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onStartInLocalClient() {
        switch (propertyWorkingBreakfallType.getOrDefaultIfNull(BreakfallType.NONE)) {
            case TAP:
                parkourability.getBehaviorEnforcer().setMarkerEnforcingDeltaMovement(
                        () -> this.getTickSinceStarted() < 10,
                        () -> parkourability.player().getDeltaMovement().multiply(0, 1, 0)
                );
                return;
            case ROLL:
                var deltaMove = VectorUtil.fromYawDegree(parkourability.player().getYRot()).scale(1.25 * EntityUtil.getHorizontalMaximumSpeed(parkourability.player()));
                parkourability.getBehaviorEnforcer().setMarkerEnforcingDeltaMovement(
                        () -> this.getTickSinceStarted() < 10,
                        () -> new Vec3(deltaMove.x, parkourability.player().getDeltaMovement().y, deltaMove.z)
                );
        }
    }

    public enum BreakfallType {
        NONE, ROLL, TAP
    }
}
