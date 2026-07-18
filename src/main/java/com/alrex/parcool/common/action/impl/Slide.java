package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.IRequestable;
import com.alrex.parcool.util.EntityUtil;
import com.alrex.parcool.util.VectorUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class Slide extends ContinuableAction implements IRequestable<Slide.RequestContext> {
    private static final int MAX_TICK = 20;
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Vec3> propertyMovingDirection;
    public Slide(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyMovingDirection = SynchronizedProperty.newVec3Horizontal()
        );
    }

    @Nullable
    public Vec3 getSlidingDirection() {
        return propertyMovingDirection.get();
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canStart() {
        return false;
    }

    @Override
    public boolean canContinue() {
        return getDoingTick() < MAX_TICK; //TODO : make this configurable
    }

    @Override
    public boolean canStart(RequestContext context) {
        propertyMovingDirection.set(VectorUtil.fromYawDegree(parkourability.player().getYRot()).scale(1.25 * EntityUtil.getHorizontalMaximumSpeed(parkourability.player())));
        return true;
    }

    @Override
    public void onStartInLocalClient() {
        final var movingDirection = propertyMovingDirection.get();
        if (movingDirection == null) return;
        parkourability.getBehaviorEnforcer().setMarkerEnforcingDeltaMovement(
                this::isDoing,
                () -> {
                    var scale = Mth.lerp(getDoingTick() / (double) MAX_TICK, 1., 0.7);
                    return new Vec3(movingDirection.x * scale, parkourability.player().getDeltaMovement().y, movingDirection.z * scale);
                }
        );
    }

    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().SLIDE);
    }

    @Override
    public void onWorkingTickInClient() {
        var movingDirection = propertyMovingDirection.get();
        if (movingDirection == null) return;
        var player = parkourability.player();
        var pos = player.position();
        var blockPos = player.getBlockPosBelowThatAffectsMyMovement();
        var blockState = player.level.getBlockState(blockPos);
        if (blockState.isAir()) return;
        var random = player.level.random;
        var particleMove = com.alrex.parcool.client.animation.system.util.EntityUtil.getPositionDifference(player).reverse().scale(0.1).add(0, random.nextDouble() * 0.1, 0);
        var particlePos = pos.add(movingDirection.yRot(Mth.HALF_PI).scale(player.getBbWidth() * (random.nextDouble() - 0.5)));

        player.level.addParticle(
                new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                particlePos.x, particlePos.y, particlePos.z,
                particleMove.x, particleMove.y, particleMove.z
        );
    }

    public record RequestContext() {
    }
}
