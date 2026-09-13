package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.Action;
import com.alrex.parcool.api.action.ActionEntry;
import com.alrex.parcool.api.action.SynchronizedDataHolder;
import com.alrex.parcool.api.action.SynchronizedProperty;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.IPlayerAnimatorHolder;
import com.alrex.parcool.client.animation.system.math.MathUtil;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.util.VectorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class LongJump extends Action {
    private static final int INPUT_ACCEPTANCE_TICK = 10;
    private static final int COOLDOWN = 30;

    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Float> propertyJumpDirectionYaw;
    private int cooldownTick = 0;

    public LongJump(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(
                ParCoolActions.HIDE_IN_BLOCK
        ));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyJumpDirectionYaw = SynchronizedProperty.newFloat()
        );
    }

    @Override
    public void onTickInLocalClient() {
        if (cooldownTick > 0) cooldownTick--;
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canStart() {
        if (cooldownTick > 0) return false;
        var shiftKey = ParCoolKeyBinds.SHIFT;
        if (shiftKey.state().isJustReleased()
                && shiftKey.state().getPreviousPressedDurationTick() < INPUT_ACCEPTANCE_TICK
                && parkourability.get(ParCoolActions.FAST_RUN).getNotDoingTick() < INPUT_ACCEPTANCE_TICK
        ) {
            var deltaMove = parkourability.player().getDeltaMovement();
            if (deltaMove.lengthSqr() < 1e-5) return false;
            var deltaMoveYaw = (float) VectorUtil.toYawDegree(deltaMove);
            var lookAngleYaw = parkourability.player().getYRot();
            var rot = MathUtil.rotLerp(
                    shiftKey.state().getPreviousPressedDurationTick() / (float) INPUT_ACCEPTANCE_TICK,
                    deltaMoveYaw, lookAngleYaw
            );
            propertyJumpDirectionYaw.set(rot);
            return true;
        }
        return false;
    }

    @Override
    public void onStartInLocalClient() {
        var player = parkourability.player();
        var jumpDirectionYaw = propertyJumpDirectionYaw.get();
        if (jumpDirectionYaw == null) return;
        var jumpDirection = VectorUtil.fromYawDegree(jumpDirectionYaw);
        player.jumpFromGround();
        player.setOnGround(false);
        player.setDeltaMovement(
                jumpDirection.x() * 0.7,
                player.getDeltaMovement().y() * 1.16667,
                jumpDirection.z() * 0.7
        );
        cooldownTick = COOLDOWN;
    }

    @Override
    public void onStartInClient() {
        if (parkourability.player() instanceof IPlayerAnimatorHolder holder) {
            holder.getParCoolPlayerAnimator().start(AnimationRegistries.get().animations().LONG_JUMP);
        }
        var jumpDirectionYaw = propertyJumpDirectionYaw.get();
        if (jumpDirectionYaw != null) {
            spawnJumpEffect(parkourability.player(), VectorUtil.fromYawDegree(jumpDirectionYaw));
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void spawnJumpEffect(Player player, Vec3 jumpDirection) {
        var level = player.level;
        var pos = player.position();
        var blockpos = new BlockPos(pos.add(0, -0.2, 0));
        if (!level.isLoaded(blockpos)) return;
        float width = player.getBbWidth();
        var blockstate = level.getBlockState(blockpos);
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            for (int i = 0; i < 20; i++) {
                var particlePos = new Vec3(
                        pos.x() + (jumpDirection.x() * -0.5 + player.getRandom().nextDouble() - 0.5D) * width,
                        pos.y() + 0.1D,
                        pos.z() + (jumpDirection.z() * -0.5 + player.getRandom().nextDouble() - 0.5D) * width
                );
                var particleSpeed = particlePos.subtract(pos).normalize().scale(2.5 + 8 * player.getRandom().nextDouble()).add(0, 1.5, 0);
                level.addParticle(
                        new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(blockpos),
                        particlePos.x(),
                        particlePos.y(),
                        particlePos.z(),
                        particleSpeed.x(),
                        particleSpeed.y(),
                        particleSpeed.z()
                );
            }
        }
    }
}
