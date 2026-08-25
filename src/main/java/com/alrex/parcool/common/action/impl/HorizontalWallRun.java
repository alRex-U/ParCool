package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.ParCoolAttributes;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.animation.system.util.EntityUtil;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.client.sound.HorizontalWallRunSoundInstance;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.InteractingWallDirection;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.util.VectorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class HorizontalWallRun extends ContinuableAction implements ActionExtension.LeaveFromWallListener {
    private static final BehaviorEnforcer.ID ID_FALL_FLY_CANCEL = BehaviorEnforcer.newID();
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<InteractingWallDirection> propertyDirection;
    private final SynchronizedProperty<Boolean> propertyLeftToWall;

    private short tickSinceCanceled = 0;

    public HorizontalWallRun(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(ParCoolActions.GRAPPLE, ParCoolActions.DIVE));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyDirection = SynchronizedProperty.newEnum(InteractingWallDirection.class),
                propertyLeftToWall = SynchronizedProperty.newBoolean()
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canContinue() {
        if (tickSinceCanceled < 3) {
            return false;
        }
        if (!ParCoolKeyBinds.HORIZONTAL_WALL_RUN.state().isDown()) return false;

        var wallDirection = parkourability.getAdditionalProperties().getDefaultWallInteraction();
        if (wallDirection == null) return false;

        var wallVec = wallDirection.asVec();
        var lookVec = VectorUtil.fromYawDegree(parkourability.player().getYRot());
        var leftToWall = propertyLeftToWall.get();

        return leftToWall != null && (lookVec.cross(wallVec).y < 0) == leftToWall;
    }

    @Override
    public boolean canStart() {
        if (tickSinceCanceled < 3) {
            return false;
        }
        if (!ParCoolKeyBinds.HORIZONTAL_WALL_RUN.state().isDown()) return false;

        var wallDirection = parkourability.getAdditionalProperties().getDefaultWallInteraction();
        if (wallDirection == null) return false;
        var wallVec = wallDirection.asVec();
        var lookVec = VectorUtil.fromYawDegree(parkourability.player().getYRot());
        if (Math.abs(lookVec.dot(wallVec)) > 0.7071 /* cos(pi/4) */) return false;
        propertyDirection.set(wallDirection);
        propertyLeftToWall.set(lookVec.cross(wallVec).y < 0);

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInLocalClient() {
        if (!(parkourability.player() instanceof LocalPlayer player)) return;
        parkourability.getBehaviorEnforcer().noFallFlyingMarks.add(ID_FALL_FLY_CANCEL, this::isDoing);
        var durationAttr = player.getAttribute(ParCoolAttributes.HORIZONTAL_WALL_RUN_DURATION.get());
        if (durationAttr == null) return;
        var duration = durationAttr.getValue();
        parkourability.getBehaviorEnforcer().setMarkerEnforcingDeltaMovement(this::isDoing, () -> {
            var wallDirection = propertyDirection.get();
            if (wallDirection == null) return null;
            return player.getDeltaMovement()
                    .add(wallDirection.asVec().scale(1 / 16d))
                    .multiply(1, getDoingTick() < duration ? 0 : Math.min(1f, (getDoingTick() - duration) / duration), 1);
        });
        Minecraft.getInstance().getSoundManager().play(new HorizontalWallRunSoundInstance(player, this));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onStartInClient() {
        var leftToWall = propertyLeftToWall.get();
        if (leftToWall == null) return;
        if (leftToWall) {
            PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().HORIZONTAL_WALL_RUN);
        } else {
            PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().HORIZONTAL_WALL_RUN, true);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onTickInLocalClient() {
        if (tickSinceCanceled < 255) tickSinceCanceled++;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onWorkingTickInLocalClient() {
        propertyDirection.set(parkourability.getAdditionalProperties().getDefaultWallInteraction());
    }

    @Nullable
    public Vec3 getRunningDirection(float partial) {
        var wallDirection = propertyDirection.get();
        if (wallDirection == null) return null;
        var leftToWall = propertyLeftToWall.get();
        if (leftToWall == null) return null;

        return wallDirection.asVec().yRot(Mth.HALF_PI * (leftToWall ? 1f : -1f));
    }

    @Override
    public void onWorkingTickInClient() {
        var direction = propertyDirection.get();
        if (direction == null) return;
        var player = parkourability.player();
        var pos = player.position();
        var blockPos = new BlockPos(
                Mth.floor(pos.x + direction.asVec().x),
                Mth.floor(pos.y()),
                Mth.floor(pos.z + direction.asVec().z)
        );
        var blockState = player.level().getBlockState(blockPos);
        if (blockState.isAir()) return;
        var random = player.level().random;
        var particleMove = EntityUtil.getPositionDifference(player).reverse().scale(2).add(direction.asVec().reverse().scale(0.3));
        var particlePos = pos.add(direction.asVec().scale(player.getBbWidth() * 0.5));

        player.level().addParticle(
                new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                particlePos.x + (random.nextDouble() - 0.5) * 0.35,
                particlePos.y + (random.nextDouble() - 0.5) * 0.35,
                particlePos.z + (random.nextDouble() - 0.5) * 0.35,
                particleMove.x, particleMove.y, particleMove.z
        );
    }

    @Override
    public void onLeaveFromWall() {
        tickSinceCanceled = 0;
    }
}
