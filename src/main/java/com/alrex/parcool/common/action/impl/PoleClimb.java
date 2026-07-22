package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.ParCoolSoundEvents;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.InputUtil;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.InteractingWallDirection;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.util.EntityUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class PoleClimb extends ContinuableAction {

    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<InteractingWallDirection> propertyWallDirection;

    public PoleClimb(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(
                ParCoolActions.HANG_ON,
                ParCoolActions.HANG_DOWN,
                ParCoolActions.CLIMB_UP,
                ParCoolActions.SLIDE_DOWN,
                ParCoolActions.DIVE
        ));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyWallDirection = SynchronizedProperty.newEnum(InteractingWallDirection.class)
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canContinue() {
        if (!ParCoolKeyBinds.HANG.key().isDown()) return false;
        var currentWallDirection = parkourability.getAdditionalProperties().getDefaultWallInteraction();
        if (currentWallDirection == null) return false;
        if (!currentWallDirection.alongToAxis()) return false;

        if (checkPole(parkourability.player(), currentWallDirection)) {
            propertyWallDirection.set(currentWallDirection);
            return true;
        }
        return false;
    }

    @Override
    public boolean canStart() {
        if (!ParCoolKeyBinds.HANG.key().isDown()) return false;
        if (Math.abs(parkourability.player().getDeltaMovement().y) > 0.4) return false;
        var currentWallDirection = parkourability.getAdditionalProperties().getDefaultWallInteraction();
        if (currentWallDirection == null) return false;
        if (!currentWallDirection.alongToAxis()) return false;
        if (currentWallDirection.asVec().dot(EntityUtil.getHorizontalLookAngle(parkourability.player())) < 1. / Mth.SQRT_OF_TWO)
            return false;

        if (checkPole(parkourability.player(), currentWallDirection)) {
            propertyWallDirection.set(currentWallDirection);
            return true;
        }
        return false;
    }

    @Override
    public void onStartInLocalClient() {
        if (!(parkourability.player() instanceof LocalPlayer player)) return;
        parkourability.getBehaviorEnforcer().setMarkerEnforcingPosition(this::isDoing, () -> {
            var currentWallDirection = propertyWallDirection.get();
            if (currentWallDirection == null) return null;
            var wallVec = currentWallDirection.asVec();
            var moveVec = player.position().add(
                    wallVec.x * 0.01,
                    InputUtil.getInputVectorInWorld(player, player.input).dot(wallVec) * 0.12,
                    wallVec.z * 0.01
            );
            if (currentWallDirection.alongToAxis()) {
                if (currentWallDirection.getSignX() == 0) {
                    return new Vec3((moveVec.x + Math.floor(moveVec.x) + 0.5) / 2., moveVec.y, moveVec.z);
                } else {
                    return new Vec3(moveVec.x, moveVec.y, (moveVec.z + Math.floor(moveVec.z) + 0.5) / 2.);
                }
            }
            return moveVec;
        });
    }

    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().POLE_CLIMB);
        parkourability.player().playSound(ParCoolSoundEvents.POLE_CLIMB.get());
    }

    @Override
    public void onWorkingTick() {
        parkourability.player().fallDistance = 0;
    }

    @Nullable
    public Vec3 getWallVec() {
        var wallDirection = propertyWallDirection.get();
        return wallDirection != null ? wallDirection.asVec() : null;
    }

    private static boolean checkPole(Player player, InteractingWallDirection wallDirection) {
        var playerPos = player.position();
        var playerHalfWidth = player.getBbWidth() / 2. + 0.1;
        var level = player.level;
        var wallBlockPos = new BlockPos(
                Mth.floor(playerPos.x + wallDirection.asVec().x * playerHalfWidth),
                Mth.floor(playerPos.y + 0.1),
                Mth.floor(playerPos.z + wallDirection.asVec().z * playerHalfWidth)
        );
        if (wallBlockPos.getY() < level.getMinBuildHeight() || level.getMaxBuildHeight() < wallBlockPos.getY())
            return false;
        if (!level.isLoaded(wallBlockPos)) return false;
        var blockState = level.getBlockState(wallBlockPos);
        var block = blockState.getBlock();
        var playerHeight = player.getBbHeight();

        if (block instanceof CrossCollisionBlock) {
            var collisionProperties = new BooleanProperty[]{CrossCollisionBlock.EAST, CrossCollisionBlock.WEST, CrossCollisionBlock.SOUTH, CrossCollisionBlock.NORTH};
            for (int i = 0; i < playerHeight; i++) {
                var pos = wallBlockPos.above(i);
                if (level.getMaxBuildHeight() < pos.getY()) return false;
                blockState = level.getBlockState(pos);
                if (!(blockState.getBlock() instanceof CrossCollisionBlock) && ((i + 1) < playerHeight || !blockState.isAir()))
                    return false;

                byte xCollision = 0, zCollision = 0;
                for (int collisionIdx = 0; collisionIdx < collisionProperties.length; collisionIdx++) {
                    if (blockState.getValue(collisionProperties[collisionIdx])) {
                        if (collisionIdx < 2) {
                            if (xCollision != 0) return false;
                            xCollision = collisionIdx == 0 ? (byte) 1 : (byte) -1;
                        } else {
                            if (zCollision != 0) return false;
                            zCollision = collisionIdx == 2 ? (byte) 1 : (byte) -1;
                        }
                    }
                }
                if (xCollision * wallDirection.getSignX() < 0) return false;
                if (zCollision * wallDirection.getSignZ() < 0) return false;
            }
            return true;
        }
        if (block instanceof ChainBlock) {
            for (int i = 0; i < playerHeight; i++) {
                var pos = wallBlockPos.above(i);
                if (level.getMaxBuildHeight() < pos.getY()) return false;
                blockState = level.getBlockState(pos);
                if (!(blockState.getBlock() instanceof ChainBlock)) return false;
                if (blockState.getValue(ChainBlock.AXIS) != Direction.Axis.Y) return false;
            }
            return true;
        }
        return false;
    }

}
