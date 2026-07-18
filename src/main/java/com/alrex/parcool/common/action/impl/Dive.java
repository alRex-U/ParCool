package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.util.VectorUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class Dive extends ContinuableAction implements ActionExtension.JumpListener {
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<Float> propertyYSpeedOnBeginning;

    // Only for client
    private float ySpeedO;
    private float ySpeed;
    private boolean jumped;

    public Dive(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(ParCoolActions.RIDE_ZIPLINE));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyYSpeedOnBeginning = SynchronizedProperty.newFloat()
        );
    }

    public float getAnimationProgress(float partial) {
        final var offset = -2. / (1. + 1. / Math.E) + 0.5;
        var ySpeedOnBeginning = propertyYSpeedOnBeginning.get();
        if (ySpeedOnBeginning == null || ySpeedOnBeginning <= 0) return 1;
        var yDeltaMovement = Mth.lerp(partial, ySpeedO, ySpeed);
        return (float) Math.max(0, 2. / (1. + Math.exp(yDeltaMovement / ySpeedOnBeginning - 1.)) + offset);
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canContinue() {
        if (parkourability.get(ParCoolActions.SKYDIVE).isDoing()) return (ySpeed <= ySpeedO * 0.97f + 1e-4f);
        return (ySpeed <= ySpeedO + 1e-4f);
    }

    @Override
    public void onWorkingTickInClient() {
        ySpeedO = ySpeed;
        ySpeed = (float) (parkourability.player().position().y - parkourability.player().yo);
    }

    @Override
    public boolean canStart() {
        if (!jumped) return false;
        jumped = false;
        if (!parkourability.player().isSprinting()) return false;
        if (!checkEnoughSpace()) return false;
        propertyYSpeedOnBeginning.set(ySpeed = ySpeedO = (float) (parkourability.player().position().y - parkourability.player().yo));
        return true;
    }

    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().DIVE);
    }

    @Override
    public void onJump() {
        jumped = true;
    }

    private boolean checkEnoughSpace() {
        var player = parkourability.player();
        var world = player.level;
        double width = player.getBbWidth() * 1.5;
        double height = player.getBbHeight() * 1.5;
        double wideWidth = player.getBbWidth() * 2;
        var center = player.position();
        if (!world.isLoaded(new BlockPos(
                Mth.floor(center.x()),
                Mth.floor(center.y()),
                Mth.floor(center.z())
        ))) return false;
        var diveDirection = VectorUtil.fromYawDegree(player.getYHeadRot());
        for (int i = 0; i < 4; i++) {
            var centerPoint = center.add(diveDirection.scale(width * i));
            var box = new AABB(
                    centerPoint.x() - width,
                    centerPoint.y() + 0.05,
                    centerPoint.z() - width,
                    centerPoint.x() + width,
                    centerPoint.y() + height,
                    centerPoint.z() + width
            );
            if (!world.noCollision(player, box)) return false;
        }
        center = center.add(diveDirection.scale(4));
        if (world.noCollision(new AABB(
                center.x() - wideWidth,
                center.y() - 7,
                center.z() - wideWidth,
                center.x() + wideWidth,
                center.y() + height,
                center.z() + wideWidth
        ))) return true;

        var centerBlockPos = new BlockPos(
                Mth.floor(center.x()),
                Mth.floor(center.y() - 0.5),
                Mth.floor(center.z())
        );
        // check if water pool exists
        if (!world.isLoaded(centerBlockPos)) return false;
        int i = 0;
        int waterLevel = -1;
        for (; i < 6; i++) {
            var block = world.getBlockState(centerBlockPos.below(i)).getBlock();
            if (block == Blocks.AIR) continue;
            if (block == Blocks.WATER) {
                waterLevel = i;
                break;
            }
            return false;
        }
        if (waterLevel == -1) return false;
        boolean filledWithWater = true;
        for (; i < waterLevel + 3; i++) {
            var state = world.getBlockState(centerBlockPos.below(i));
            if (state.getBlock() != Blocks.WATER) {
                filledWithWater = false;
                break;
            }
        }
        return filledWithWater && world.noCollision(new AABB(
                center.x() - wideWidth,
                center.y() - 2.9,
                center.z() - wideWidth,
                center.x() + wideWidth,
                center.y() + height,
                center.z() + wideWidth
        ));
    }
}
