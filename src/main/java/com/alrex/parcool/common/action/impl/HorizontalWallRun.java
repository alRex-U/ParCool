package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.ParCoolAttributes;
import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.AnimationRegistries;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ActionExtension;
import com.alrex.parcool.common.action.InteractingWallDirection;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.util.VectorUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class HorizontalWallRun extends ContinuableAction implements ActionExtension.LeaveFromWallListener {
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<InteractingWallDirection> propertyDirection;
    private final SynchronizedProperty<Boolean> propertyLeftToWall;

    private short tickSinceCanceled = 0;

    public HorizontalWallRun(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(ParCoolActions.DIVE));
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

    @Override
    public void onStartInLocalClient() {
        var player = parkourability.player();
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
    }

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

    @Override
    public void onTickInLocalClient() {
        if (tickSinceCanceled < 255) tickSinceCanceled++;
    }

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
    public void onLeaveFromWall() {
        tickSinceCanceled = 0;
    }
}
