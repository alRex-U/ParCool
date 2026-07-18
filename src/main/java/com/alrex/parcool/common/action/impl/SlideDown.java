package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.action.InteractingWallDirection;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.util.EntityUtil;
import com.alrex.parcool.util.MathUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class SlideDown extends ContinuableAction implements ActionExtension.LeaveFromWallListener {
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<InteractingWallDirection> propertyDirection;

    private AnimationData currentAnimData = AnimationData.NONE;
    private AnimationData oldAnimData = AnimationData.NONE;
    private short tickSinceCanceled = 0;

    public SlideDown(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(
                ParCoolActions.CLIMB_UP,
                ParCoolActions.HANG_ON,
                ParCoolActions.POLE_CLIMB,
                ParCoolActions.DIVE,
                ParCoolActions.CASTAWAY
        ));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyDirection = SynchronizedProperty.newEnum(InteractingWallDirection.class)
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
        return canStart();
    }

    @Override
    public boolean canStart() {
        if (tickSinceCanceled < 3 || parkourability.player().getDeltaMovement().y >= -1e-4) {
            return false;
        }
        if (ParCoolKeyBinds.SLIDE_DOWN.key().isDown()) {
            var direction = parkourability.getAdditionalProperties().getDefaultWallInteraction();
            if (direction == null) return false;
            propertyDirection.set(direction);
            return true;
        }
        return false;
    }

    @Override
    public void onWorkingTickInClient() {
        oldAnimData = currentAnimData;
        currentAnimData = AnimationData.get(this, parkourability.player());
    }

    @Override
    public void onStartInLocalClient() {
        parkourability.getBehaviorEnforcer().setMarkerEnforcingMovePoint(
                this::isDoing, () -> {
                    if (!(parkourability.player() instanceof LocalPlayer player)) return null;
                    var direction = propertyDirection.get();
                    if (direction == null) return null;

                    var speed = player.getSpeed() * 0.2f;
                    var moveVec = player.input.getMoveVector().scale(speed);
                    var actualMoveVec = new Vec3(moveVec.x, 0, moveVec.y).yRot((float) Math.toRadians(-player.getYRot()));
                    if (direction.isProtrusion()) {
                        return player.position()
                                .add(new Vec3(
                                        direction.getSignX() > 0 ? Math.max(0, actualMoveVec.x) : Math.min(0, actualMoveVec.x),
                                        player.getDeltaMovement().y,
                                        direction.getSignZ() > 0 ? Math.max(0, actualMoveVec.z) : Math.min(0, actualMoveVec.z)
                                ));
                    } else if (direction.isOblique()) {
                        var directionVec = direction.asVec().yRot(Mth.HALF_PI);
                        return player.position()
                                .add(0, player.getDeltaMovement().y, 0)
                                .add(directionVec.scale(directionVec.dot(actualMoveVec)));
                    } else {
                        return player.position()
                                .add(direction.getSignX() * 0.2, player.getDeltaMovement().y, direction.getSignZ() * 0.2)
                                .add(actualMoveVec);
                    }
                }
        );
    }

    @Override
    public void onTickInLocalClient() {
        if (tickSinceCanceled < 255) tickSinceCanceled++;
    }

    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(AnimationRegistries.get().animations().SLIDE_DOWN);
    }

    @Nullable
    public Vec3 getWallVec(float partial) {
        var wallVec = propertyDirection.get();
        if (wallVec == null) return null;
        return wallVec.asVec();
    }

    public float getBlendFactorRightToWall(float partial) {
        return Mth.lerp(partial, oldAnimData.blendFactorRightToWall, currentAnimData.blendFactorRightToWall);
    }

    public float getBlendFactorLeftToWall(float partial) {
        return Mth.lerp(partial, oldAnimData.blendFactorLeftToWall, currentAnimData.blendFactorLeftToWall);
    }

    public float getBlendFactorBackToWall(float partial) {
        return Mth.lerp(partial, oldAnimData.blendFactorBackToWall, currentAnimData.blendFactorBackToWall);
    }

    @Override
    public void onWorkingTickInLocalClient() {
        parkourability.player().setDeltaMovement(parkourability.player().getDeltaMovement().multiply(0, 0.9, 0));
    }

    @Override
    public void onWorkingTickInServer() {
        parkourability.player().fallDistance *= 0.9f;
    }

    @Override
    public void onLeaveFromWall() {
        tickSinceCanceled = 0;
    }

    private record AnimationData(
            float blendFactorRightToWall,
            float blendFactorLeftToWall,
            float blendFactorBackToWall
    ) {
        static final AnimationData NONE = new AnimationData(0, 0, 0);

        public static AnimationData get(SlideDown slideDown, Player player) {
            var direction = slideDown.propertyDirection.get();
            if (direction == null) return NONE;
            var wallVec = direction.asVec();
            var horizontalLookVec = EntityUtil.getHorizontalLookAngle(player);
            var dotOfWallVecLookVec = (float) horizontalLookVec.dot(wallVec);

            return new AnimationData(
                    getBlendFactorRightToWall(horizontalLookVec, wallVec, dotOfWallVecLookVec),
                    getBlendFactorLeftToWall(horizontalLookVec, wallVec, dotOfWallVecLookVec),
                    getBlendFactorBackToWall(horizontalLookVec, wallVec, dotOfWallVecLookVec)
            );
        }

        private static float getBlendFactorLeftToWall(Vec3 horizontalLookVec, Vec3 wallVec, float dotOfWallVecLookVec) {
            if (wallVec.yRot(Mth.HALF_PI).dot(horizontalLookVec) < 0) return 0;
            return MathUtil.mapLinear(
                    -dotOfWallVecLookVec, -1, -0.7071f /*-cos(pi/4)*/, 0, 1
            );
        }

        private static float getBlendFactorRightToWall(Vec3 horizontalLookVec, Vec3 wallVec, float dotOfWallVecLookVec) {
            if (wallVec.yRot(Mth.HALF_PI).dot(horizontalLookVec) > 0) return 0;
            return MathUtil.mapLinear(
                    -dotOfWallVecLookVec, -1, -0.7071f /*-cos(pi/4)*/, 0, 1
            );
        }

        private static float getBlendFactorBackToWall(Vec3 horizontalLookVec, Vec3 wallVec, float dotOfWallVecLookVec) {
            return MathUtil.mapLinear(
                    -dotOfWallVecLookVec, 0.866f /*cos(pi/3)*/, 1, 0, 1
            );
        }
    }
}
