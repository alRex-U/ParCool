package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.ParCoolAttributes;
import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.LogicalMovement;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.util.EntityUtil;
import com.alrex.parcool.util.MathUtil;
import com.alrex.parcool.util.VectorUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import javax.annotation.Nullable;

public class Breakfall extends Action implements ActionExtension.LandListener {
    private final SynchronizedDataHolder holder;
    // Local -> Server
    private final SynchronizedProperty<BreakfallType> propertyInputBreakfallType;
    // Server -> Local
    private final SynchronizedProperty<BreakfallType> propertyWorkingBreakfallType;
    private final SynchronizedProperty<Float> propertyFallDist;
    private final SynchronizedProperty<Boolean> propertyLandingPerfect;

    // For Server
    private int inputTick = 0;

    public Breakfall(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
        holder = SynchronizedDataHolder.create(entry,
                propertyInputBreakfallType = SynchronizedProperty.newEnum(BreakfallType.class),
                propertyWorkingBreakfallType = SynchronizedProperty.newEnum(BreakfallType.class),
                propertyFallDist = SynchronizedProperty.newFloat(),
                propertyLandingPerfect = SynchronizedProperty.newBoolean()
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

    @OnlyIn(Dist.CLIENT)
    @Nullable
    @Override
    public ParCoolKeyBinds.IStateProvider getKeyBind() {
        return ParCoolKeyBinds.BREAKFALL;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onTickInLocalClient() {
        propertyInputBreakfallType.set(input().isActive() ?
                (ParCoolKeyBinds.getMovementInput(LogicalMovement.FORWARD).isDown() ? BreakfallType.ROLL : BreakfallType.TAP)
                : BreakfallType.NONE
        );
    }

    @Override
    public void onTickInServer() {
        if (propertyInputBreakfallType.getOrDefaultIfNull(BreakfallType.NONE) != BreakfallType.NONE) {
            inputTick++;
        } else {
            inputTick = 0;
        }
    }

    @Override
    public void onLand(LivingFallEvent event) {
        if (parkourability.player().level.isClientSide()) return;
        var breakfallType = propertyInputBreakfallType.get();
        if (breakfallType == null || breakfallType == BreakfallType.NONE) return;
        var attr = parkourability.player().getAttribute(ParCoolAttributes.BREAKFALL_DAMAGE_REDUCTION.get());
        if (attr == null) return;
        if (isPossible() && !MinecraftForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStart(parkourability.player(), this))) {
            int perfectInputAcceptanceTick = ParCool.getConfig().server().breakfallJustTimeInputAcceptanceTick.get();
            double damageReduction;
            if (perfectInputAcceptanceTick <= 0) {
                damageReduction = attr.getValue();
                propertyLandingPerfect.set(false);
            } else {
                boolean perfect = inputTick <= perfectInputAcceptanceTick;
                damageReduction = attr.getValue() * (perfect ? 1.2 : 0.8);
                propertyLandingPerfect.set(perfect);
            }
            damageReduction = Mth.clamp(damageReduction, 0, 1);
            event.setDamageMultiplier(event.getDamageMultiplier() * (float) (1. - damageReduction));
            if (event.getDistance() < 15 * damageReduction) {
                event.setCanceled(true);
            }

            propertyFallDist.set(event.getDistance());
            propertyWorkingBreakfallType.set(breakfallType);
            startExplicitly();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInClient() {
        switch (propertyWorkingBreakfallType.getOrDefaultIfNull(BreakfallType.NONE)) {
            case TAP:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().BREAKFALL_NO_MOVE);
                break;
            case ROLL:
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().BREAKFALL_FORWARD);
        }
        boolean perfect = propertyLandingPerfect.getOrDefaultIfNull(false);
        if (ParCool.getConfig().client().enableActionSounds.get()) {
            if (perfect) {
                parkourability.player().playSound(
                        ParCoolSoundEvents.BREAKFALL_PERFECT.get(), 1f, 1f
                );
            } else {
                parkourability.player().playSound(
                        ParCoolSoundEvents.BREAKFALL.get(),
                        MathUtil.mapLinear(propertyFallDist.getOrDefaultIfNull(0f), 3f, 10f, 0f, 1f),
                        1f
                );
            }
        }
        if (perfect) {
            spawnPerfectLandingParticles();
        }
    }

    @OnlyIn(Dist.CLIENT)
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

    @OnlyIn(Dist.CLIENT)
    public void spawnPerfectLandingParticles() {
        var player = parkourability.player();
        var pos = player.position();
        var rand = player.getRandom();
        for (int i = 0; i < 12; i++) {
            player.level.addParticle(ParticleTypes.END_ROD,
                    pos.x(),
                    pos.y() + player.getBbHeight() / 2,
                    pos.z(),
                    (rand.nextDouble() - 0.5) * 0.5,
                    (rand.nextDouble() - 0.5) * 0.5,
                    (rand.nextDouble() - 0.5) * 0.5
            );
        }
    }

    public enum BreakfallType {
        NONE, ROLL, TAP
    }
}
